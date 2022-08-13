package com.bei.controller.front;

import com.bei.common.BusinessException;
import com.bei.common.CommonResult;
import com.bei.dto.AdminUserDetail;
import com.bei.model.SetmealDish;
import com.bei.model.ShoppingCart;
import com.bei.service.SetmealDishService;
import com.bei.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/list")
    public CommonResult getShoppingList() {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(principal.getId());
        List<ShoppingCart> cartList = shoppingCartService.getShoppingCart(shoppingCart);
        return CommonResult.success(cartList);
    }

    @PostMapping("/add")
    @Transactional
    public CommonResult addItem(@RequestBody ShoppingCart cart) {
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCart(cart);
        ShoppingCart shoppingCart = shoppingCartList.size() > 0 ? shoppingCartList.get(0) : null;
        if (shoppingCart != null) {
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            int count = shoppingCartService.updateShoppingCart(shoppingCart);
            if (count != 1) {
                log.debug("添加购物车项目: [" + cart + "] 失败");
                throw new BusinessException("加入购物车失败");
            }
            return CommonResult.success("添加成功");
        }
        cart.setNumber(1);
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cart.setUserId(principal.getId());
        cart.setCreateTime(new Date());
        if (cart.getSetmealId() != null) {
            List<SetmealDish> setmealDishList = setmealDishService.getSetmeal(cart.getSetmealId());
            for (SetmealDish setmealDish : setmealDishList) {
                if (setmealDish.getIsDeleted() == 1) {
                    log.debug("尝试添加包含停售菜品的套餐, id: {}", cart.getSetmealId());
                    throw new BusinessException("本套餐包含的部分菜品已经停售");
                }
            }
        }
        int count = shoppingCartService.addShoppingCart(cart);
        if (count != 1) {
            log.debug("添加购物车项目: [" + cart + "] 失败");
            throw new BusinessException("加入购物车失败");
        }
        return CommonResult.success("添加成功");
    }

    @DeleteMapping("/clean")
    public CommonResult clean() {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int count = shoppingCartService.deleteItemByUser(principal.getId());
        if (count == 0) {
            log.debug("未找到需要删除项目, 用户id:{}", principal.getId());
            throw new BusinessException("购物车中没有物品，无需清空");
        }
        return CommonResult.success("清空成功");
    }
}
