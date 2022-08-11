package com.bei.controller.backend;

import com.bei.common.CommonResult;
import com.bei.dto.param.EmployeeParam;
import com.bei.dto.param.LoginParam;
import com.bei.dto.param.StatusParam;
import com.bei.dto.AdminUserDetail;
import com.bei.model.Employee;
import com.bei.service.EmployeeService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult addEmployee(@RequestBody EmployeeParam employeeParam) {
        AdminUserDetail principal = (AdminUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int count = employeeService.addEmployee(employeeParam, principal.getId());
        if (count == 1) {
            log.debug("add employee: {} success", employeeParam.getUsername());
            return CommonResult.success("添加用户成功");
        }
        else {
            log.debug("add employee: {} failed", employeeParam.getUsername());
            return CommonResult.error("添加用户失败");
        }
    }

    @GetMapping("/page")
    public CommonResult getEmployeePage(int page, int pageSize, String name) {
        List<Employee> employeeList = employeeService.getEmployeePage(page, pageSize, name);
        PageInfo<Employee> pageInfo = new PageInfo<>(employeeList);
        return CommonResult.success(pageInfo);
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult updateEmployeeStatus(@RequestBody StatusParam param) {
        Long id = param.getId();
        Integer status = param.getStatus();
        int count = employeeService.updateEmployeeStatus(id, status);
        if (count == 1) {
            log.debug("update {} status to {} success", id, status);
            return CommonResult.success("修改用户状态成功");
        } else {
            log.debug("update {} status to {} failed", id, status);
            return CommonResult.error("修改用户状态失败");
        }
    }

    @GetMapping("/{id}")
    public CommonResult getEmployeeInfo(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return CommonResult.error("没有该用户的信息");
        }
        return CommonResult.success(employee);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public CommonResult updateEmployeeInfo(@PathVariable Long id, @RequestBody EmployeeParam employeeParam) {
        int count = employeeService.updateEmployee(id, employeeParam);
        if (count == 1) {
            log.debug("update id: {} information successfully", id);
            return CommonResult.success("信息更新成功");
        } else {
            log.debug("update id: {} information failed", id);
            return CommonResult.error("信息更新失败");
        }
    }
}
