package com.bei.component;

import com.alibaba.fastjson.JSON;
import com.bei.common.BusinessException;
import com.bei.model.ShoppingCart;
import com.bei.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ScheduleTask {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Scheduled(cron = "0/5 * * ? * ?")
    private void flushShoppingCart() {
        while(true) {
            Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().rangeWithScores("schedule:", 0, 0);
            ZoneOffset zoneOffset = ZoneOffset.ofHours(8);
            if (set == null || set.size() == 0) return;
            log.info("开始尝试将购物车信息写入磁盘");
            ZSetOperations.TypedTuple<String> min = set.iterator().next();
            Double time = min.getScore();
            log.info(String.valueOf(time - LocalDateTime.now().toEpochSecond(zoneOffset)));
            if (time - LocalDateTime.now().toEpochSecond(zoneOffset) > 0) {return;}

            String uid = min.getValue();
            log.info("将用户：{}的购物车数据写入磁盘", uid);
            redisTemplate.opsForZSet().remove("schedule", uid);
            String key = "cart:" + uid;
            Set<Object> hashKeys = redisTemplate.opsForHash().keys(key);
            List<Object> list = redisTemplate.opsForHash().multiGet(key, hashKeys);
            List<ShoppingCart> shoppingCartList = list.stream()
                    .map(o -> JSON.parseObject(String.valueOf(o), ShoppingCart.class))
                    .collect(Collectors.toList());
            for (ShoppingCart cart : shoppingCartList) {
                int count = shoppingCartService.updateShoppingCart(cart);
                if (count != 1) {
                    count = shoppingCartService.addShoppingCart(cart);
                    if (count != 1) {
                        log.debug("购物车信息写入磁盘失败，param: {}", cart);
                        throw new BusinessException("购物车信息写入失败");
                    }
                }
            }
            addSchedule(uid, 5);
        }
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
