package com.bei.common.aop;

import com.alibaba.fastjson.JSON;
import com.bei.annotation.Cache;
import com.bei.annotation.CleanCache;
import com.bei.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class CacheAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(com.bei.annotation.Cache)")
    public void cachePt() {}

    @Pointcut("@annotation(com.bei.annotation.CleanCache)")
    public void cleanPt() {}

    @Around("cachePt()")
    public Object cacheAround(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Class[] parameterTypes = new Class[point.getArgs().length];
        Object[] args = point.getArgs();
        String params = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                params += JSON.toJSONString(args[i]);
                parameterTypes[i] = args[i].getClass();
            } else {
                parameterTypes[i] = null;
            }
        }
        Method method = point.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
        Cache cache = method.getAnnotation(Cache.class);
        long expire = cache.expire();
        String name = cache.name();
        String key = name + ":" + params;
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(value)) {
            log.info("缓存命中， {}, {}", className, methodName);
            return JSON.parseObject(value, CommonResult.class);
        }
        Object proceed = point.proceed();
        redisTemplate.opsForValue().set(key, JSON.toJSONString(proceed), Duration.ofMillis(expire));
        log.info("未命中缓存，保存本次返回结果, {}, {}", className, methodName);
        return proceed;
    }

    @Around("cleanPt()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Class[] parameterTypes = new Class[point.getArgs().length];
        Object[] args = point.getArgs();
        String params = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                params += JSON.toJSONString(args[i]);
                parameterTypes[i] = args[i].getClass();
            } else {
                parameterTypes[i] = null;
            }
        }
        Method method = point.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
        CleanCache annotation = method.getAnnotation(CleanCache.class);
        String name = annotation.name();
        Set<String> keys = redisTemplate.keys(name + "*");
        Object proceed = point.proceed();
        if (keys != null && keys.size() > 0) {
            log.info("数据库更新，清除缓存, {}, {}", className, methodName);
            redisTemplate.delete(keys);
        }
        return proceed;
    }
}
