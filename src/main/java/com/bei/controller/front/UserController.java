package com.bei.controller.front;

import com.bei.common.BusinessException;
import com.bei.common.CommonResult;
import com.bei.common.api.MailService;
import com.bei.dto.param.LoginParam;
import com.bei.model.AddressBook;
import com.bei.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/validate")
    public CommonResult sendCode(String email) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 6; i++) {
            builder.append(random.nextInt(10));
        }
        redisTemplate.opsForValue().set("validation" + email, builder.toString(), 600, TimeUnit.SECONDS);
//        mailService.sendSimpleTextMail(builder.toString(), "外卖系统登录验证码", email);
        log.info("验证码为:" + builder);
        return CommonResult.success("验证码发送成功");
    }

    @PostMapping("/login")
    public CommonResult login(@RequestBody LoginParam loginParam) {
        String token = userService.login(loginParam.getEmail(), loginParam.getCode());
        if (token == null) {
            throw new BusinessException("用户登录失败");
        }
        return CommonResult.success(token);
    }

    @PostMapping("/logout")
    public CommonResult logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return CommonResult.success("退出成功");
    }

}
