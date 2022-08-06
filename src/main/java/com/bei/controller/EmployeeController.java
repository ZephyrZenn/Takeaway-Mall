package com.bei.controller;

import com.bei.common.CommonResult;
import com.bei.common.param.EmployeeParam;
import com.bei.common.param.LoginParam;
import com.bei.dto.AdminUserDetail;
import com.bei.model.Employee;
import com.bei.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Employee employee = employeeService.getEmployeeByUsername(loginParam.getUsername());

        CommonResult result = CommonResult.success(token);
        result.add("userInfo", employee);
        return result;
    }

    @PostMapping("/logout")
    public CommonResult logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return CommonResult.success("退出成功");
    }

    @PostMapping("")
    public CommonResult addEmployee(@RequestBody EmployeeParam employeeParam) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        employeeService.addEmployee(employeeParam, principal.getId());
        return CommonResult.success("添加用户成功");
    }
}
