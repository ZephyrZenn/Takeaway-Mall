package com.bei.controller.front;

import com.alibaba.fastjson.JSON;
import com.bei.common.BusinessException;
import com.bei.common.CommonResult;
import com.bei.dto.AdminUserDetail;
import com.bei.model.SetmealDish;
import com.bei.model.ShoppingCart;
import com.bei.service.SetmealDishService;
import com.bei.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

//    @GetMapping("/list")
//    public CommonResult getShoppingList() {
//        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        ShoppingCart shoppingCart = new ShoppingCart();
//        shoppingCart.setUserId(principal.getId());
//        List<ShoppingCart> cartList = shoppingCartService.getShoppingCart(shoppingCart);
//        return CommonResult.success(cartList);
//    }
//
//    @PostMapping("/add")
//    @Transactional
//    public CommonResult addItem(@RequestBody ShoppingCart cart) {
//        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCart(cart);
//        ShoppingCart shoppingCart = shoppingCartList.size() > 0 ? shoppingCartList.get(0) : null;
//        if (shoppingCart != null) {
//            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
//            int count = shoppingCartService.updateShoppingCart(shoppingCart);
//            if (count != 1) {
//                log.debug("添加购物车项目: [" + cart + "] 失败");
//                throw new BusinessException("加入购物车失败");
//            }
//            return CommonResult.success("添加成功");
//        }
//        cart.setNumber(1);
//
//        cart.setUserId(principal.getId());
//        cart.setCreateTime(new Date());
//        if (cart.getSetmealId() != null) {
//            List<SetmealDish> setmealDishList = setmealDishService.getSetmeal(cart.getSetmealId());
//            for (SetmealDish setmealDish : setmealDishList) {
//                if (setmealDish.getIsDeleted() == 1) {
//                    log.debug("尝试添加包含停售菜品的套餐, id: {}", cart.getSetmealId());
//                    throw new BusinessException("本套餐包含的部分菜品已经停售");
//                }
//            }
//        }
//        int count = shoppingCartService.addShoppingCart(cart);
//        if (count != 1) {
//            log.debug("添加购物车项目: [" + cart + "] 失败");
//            throw new BusinessException("加入购物车失败");
//        }
//        return CommonResult.success("添加成功");
//    }

    @DeleteMapping("/clean")
    public CommonResult clean() {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redisTemplate.delete("cart:" + principal.getId());
        int count = shoppingCartService.deleteItemByUser(principal.getId());
        redisTemplate.opsForZSet().remove("schedule:", principal.getId());
//        if (count == 0) {
//            log.debug("未找到需要删除项目, 用户id:{}", principal.getId());
//            throw new BusinessException("购物车中没有物品，无需清空");
//        }
        return CommonResult.success("清空成功");
    }

    @PostMapping("/add")
    @Transactional
    public CommonResult addItemRedis(@RequestBody ShoppingCart cart) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = "cart:" + principal.getId();
        String foodId = String.valueOf(cart.getDishId() != null ? cart.getDishId() : cart.getSetmealId());
        if (cart.getSetmealId() != null) {
            List<SetmealDish> setmealDishList = setmealDishService.getSetmeal(cart.getSetmealId());
            for (SetmealDish setmealDish : setmealDishList) {
                if (setmealDish.getIsDeleted() == 1) {
                    log.debug("尝试添加包含停售菜品的套餐, id: {}", cart.getSetmealId());
                    throw new BusinessException("本套餐包含的部分菜品已经停售");
                }
            }
        }
        if (redisTemplate.opsForHash().hasKey(key, foodId)) {
            String item = String.valueOf(redisTemplate.opsForHash().get(key, foodId));
            ShoppingCart sCart = JSON.parseObject(item, ShoppingCart.class);
            sCart.setNumber(sCart.getNumber() + 1);
            redisTemplate.opsForHash().put(key, foodId, JSON.toJSONString(sCart));
            return CommonResult.success(foodId);
        }
        cart.setNumber(1);
        cart.setUserId(principal.getId());
        cart.setCreateTime(new Date());
        if (redisTemplate.opsForHash().keys(key).size() == 0) {
            addSchedule(String.valueOf(principal.getId()), 5);
        }
        redisTemplate.opsForHash().put(key, foodId, JSON.toJSONString(cart));

        return CommonResult.success(foodId);
    }

    @GetMapping("/list")
    public CommonResult getShoppingListRedis() {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = "cart:" + principal.getId();
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(principal.getId());
            List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCart(shoppingCart);
            for (ShoppingCart cart : shoppingCartList) {
                String foodId = String.valueOf(cart.getDishId() != null ? cart.getDishId() : cart.getSetmealId());
                redisTemplate.opsForHash().put(key, foodId, JSON.toJSONString(cart));
            }
            return CommonResult.success(shoppingCartList);
        }
        Set<Object> hashKeys = redisTemplate.opsForHash().keys(key);
        List<Object> list = redisTemplate.opsForHash().multiGet(key, hashKeys);
        List<ShoppingCart> shoppingCartList = list.stream()
                .map(o -> JSON.parseObject(String.valueOf(o), ShoppingCart.class))
                .collect(Collectors.toList());
        return CommonResult.success(shoppingCartList);
    }

    @PostMapping("/sub")
    public CommonResult subItem(@RequestBody ShoppingCart cart) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = "cart:" + principal.getId();
        String foodId = String.valueOf(cart.getDishId() != null ? cart.getDishId() : cart.getSetmealId());
        if (!redisTemplate.opsForHash().hasKey(key, foodId)) {
            log.debug("用户：{} 尝试删除不在购物车中的商品：{}", principal.getId(), foodId);
            return CommonResult.error("购物车中没有该商品");
        }
        Object item = redisTemplate.opsForHash().get(key, foodId);
        ShoppingCart sCart = JSON.parseObject(String.valueOf(item), ShoppingCart.class);
        sCart.setNumber(sCart.getNumber() - 1);
        if (sCart.getNumber() == 0) {
            redisTemplate.opsForHash().delete(key, foodId);
            if (redisTemplate.opsForHash().keys(key).size() == 0) {
                redisTemplate.opsForHash().delete(key);
            }
        } else {
            redisTemplate.opsForHash().put(key, foodId, JSON.toJSONString(sCart));
        }
        return CommonResult.success(foodId);
    }

    /**
     * 添加更新购物车数据调度
     * @param uid 用户id
     * @param delay 延迟时长
     * */
    private void addSchedule(String uid, int delay) {
        if (delay <= 0) {
            redisTemplate.opsForZSet().remove("schedule:", uid);
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plusMinutes(delay);
        ZoneOffset zoneOffset = ZoneOffset.ofHours(8);
        log.info("用户：{} 购物车的下一次更新在 {}", uid, next);
        redisTemplate.opsForZSet().add("schedule:", uid, next.toEpochSecond(zoneOffset));
    }
}
