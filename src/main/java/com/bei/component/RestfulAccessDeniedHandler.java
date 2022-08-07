package com.bei.component;

import com.alibaba.fastjson.JSON;
import com.bei.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户权限不足时返回结果
 * */
@Component
@Slf4j
public class RestfulAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(JSON.toJSON(CommonResult.error("当前用户权限不足")));
        log.debug("access deny exception: {}", accessDeniedException.getMessage());
        response.getWriter().flush();
    }
}
