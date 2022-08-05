package com.bei.service;

import com.bei.model.Employee;

public interface EmployeeService {

    Employee getEmployeeByUsername(String username);

    String login(String username, String password);
}
