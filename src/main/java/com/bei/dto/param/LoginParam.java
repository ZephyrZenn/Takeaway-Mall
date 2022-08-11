package com.bei.dto.param;

import lombok.Data;

@Data
public class LoginParam {
    private String username;
    private String password;

    private String email;
    private String code;

//    public void checkReq() {
//        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
//            throw new
//        }
//    }
}
