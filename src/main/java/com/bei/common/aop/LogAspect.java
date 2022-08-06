package com.bei.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/** 日志切面 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(public * com.bei.controller..*.*(..))")
    public void logPt() {}

    @Before("logPt()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("URL: {}", request.getRequestURL().toString());
        log.info("HTTP METHOD: {}", request.getMethod());
        log.info("IP: {}", request.getRemoteAddr());
        log.info("CLASS METHOD: {}", joinPoint.getSignature().getDeclaringTypeName());
        log.info("ARGS: {}", Arrays.toString(joinPoint.getArgs()));
    }
}
