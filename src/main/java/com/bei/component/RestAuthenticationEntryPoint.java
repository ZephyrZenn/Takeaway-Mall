package com.bei.component;

import com.alibaba.fastjson.JSON;
import com.bei.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败时，自定义返回信息
 * */
@Component
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        log.debug("authentication exception: {}", authException.getMessage());
        response.getWriter().println(JSON.toJSON(CommonResult.error("用户没有登录或token失效")));
        response.getWriter().flush();
    }
}
