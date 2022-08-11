package com.bei.service;

import com.bei.model.User;

public interface UserService {

    /**
     * 通过邮件查询用户
     * */
    User getUserByEmail(String email);

    /**
     * 登录,如果原先没有该用户就自动注册
     * @param email 邮箱
     * @param code 验证码
     * */
    String login(String email, String code);

    /**
     * 根据主键获取用户
     * */
    User getUser(Long id);
}
