package com.bei.controller;

import com.bei.common.CommonResult;
import com.bei.common.param.LoginParam;
import com.bei.model.Employee;
import com.bei.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public CommonResult login(@RequestBody LoginParam loginParam) {

        String token = employeeService.login(loginParam.getUsername(), loginParam.getPassword());
        if (token == null) {
            return CommonResult.error("用户名或密码错误");
        }
        return CommonResult.success(token);
    }

    @PostMapping("test")
    public CommonResult test(HttpServletRequest request) {
        System.out.println(request.getHeader("Authentication"));
        return CommonResult.success(request.getHeader("Authentication"));
    }
}
