package com.bei.dto.param;

import lombok.Data;

/**
 * 员工信息参数
 * */
@Data
public class EmployeeParam {
    private String name;
    private String username;
    private String phone;
    private String sex;
    private String idNumber;
}
