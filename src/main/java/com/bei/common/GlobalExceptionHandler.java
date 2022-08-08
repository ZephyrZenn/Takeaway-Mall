package com.bei.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * 参数校验失败处理
     * */
    @ExceptionHandler(BindException.class)
    public CommonResult handleBindException(BindException e, HttpServletRequest request) {
        String msg = formatBindException(e);
        log.warn(formatException(e, request, msg, false));
        return CommonResult.error("参数校验错误");
    }

    @ExceptionHandler(SQLException.class)
    public CommonResult handleSQLException(SQLException e, HttpServletRequest request) {
        String msg = formatException(e, request, null, false);
        log.warn(msg);
        if (e.getMessage().contains("Duplicate entry")) {
            String s = e.getMessage().split(" ")[2];
            return CommonResult.error( s + "已存在");
        }
        return CommonResult.error("数据库错误");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public CommonResult handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.warn(formatException(e, request, null, false));
        return CommonResult.error(e.getMessage());
    }

//    /**
//     * 非法操作异常
//     * */
//    @ExceptionHandler(IllegalOper)
//    public CommonResult handleIllegalOperationException(Exception e, HttpServletRequest request) {
//        log.warn(formatException(e, request, null, false));
//        return CommonResult.error("非法操作");
//    }

    public static String formatException(Exception e, HttpServletRequest request, String message, boolean stackRequired) {
        StringBuilder sb = new StringBuilder();
        sb.append("[出现异常]")
                .append("\n<请求>  ").append(request.getMethod()).append("  ").append(request.getRequestURI())
                .append("\n<类型>  ").append(e.getClass())
                .append("\n<信息>  ").append(message != null ? message : e.getMessage());
        if (stackRequired) {
            sb.append("\n<堆栈>\n");
            for (StackTraceElement traceElement : e.getStackTrace()) {
                sb.append("\tat ").append(traceElement).append("\n");
            }
        }
        return sb.toString();
    }

    public static String formatBindException(org.springframework.validation.BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError error : fieldErrors) {
            //提示：error.getField()得到的是校验失败的字段名字，error.getDefaultMessage()得到的是校验失败的原因
            sb.append(error.getField())
                    .append("=[")
                    .append(error.getDefaultMessage())
                    .append("]  ");
        }
        return sb.toString();

    }
}
