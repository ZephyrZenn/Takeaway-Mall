package com.bei.common.param;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class LoginParam {
    private String username;
    private String password;

//    public void checkReq() {
//        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
//            throw new
//        }
//    }
}
