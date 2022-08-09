package com.bei.service;

import com.bei.dto.param.EmployeeParam;
import com.bei.model.Employee;

import java.util.List;

public interface EmployeeService {
    /**
     * 根据用户名获取员工
     * @param username 用户名
     * */
    Employee getEmployeeByUsername(String username);
    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return token
     * */
    String login(String username, String password);

    /**
     * 添加用户，初始密码设为123456
     * @param employeeParam 新增用户的信息
     * @param uid 创建者的id
     * */
    int addEmployee(EmployeeParam employeeParam, Long uid);

    List<Employee> getEmployeePage(int page, int pageSize, String name);

    int updateEmployeeStatus(Long id, Integer status);

    Employee getEmployeeById(Long id);

    int updateEmployee(Long id, EmployeeParam employeeParam);
}
