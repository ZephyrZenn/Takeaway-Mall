package com.bei.service.impl;

import com.bei.common.BusinessException;
import com.bei.mapper.UserMapper;
import com.bei.model.User;
import com.bei.model.UserExample;
import com.bei.service.UserService;
import com.bei.utils.JwtTokenUtil;
import com.bei.utils.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public User getUserByEmail(String email) {
        UserExample example = new UserExample();
        example.createCriteria().andEmailEqualTo(email);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() != 0) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public String login(String email, String code) {
        String realCode = redisTemplate.opsForValue().get("validation" + email);
        if (!code.equals(realCode)) {
            return null;
        }
        if (getUserByEmail(email) == null) {
            User user = new User();
            SnowflakeIdUtils snowflakeIdUtils = new SnowflakeIdUtils(1, 1);
            user.setId(snowflakeIdUtils.nextId());
            user.setStatus(1);
            user.setEmail(email);
            int count = userMapper.insertSelective(user);
            if (count != 1) {
                throw new BusinessException("注册新用户失败");
            }
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        redisTemplate.delete("validation" + email);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public User getUser(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
