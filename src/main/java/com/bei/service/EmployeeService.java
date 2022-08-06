package com.bei.service;

import com.bei.common.param.EmployeeParam;
import com.bei.model.Employee;

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
    void addEmployee(EmployeeParam employeeParam, Long uid);
}
