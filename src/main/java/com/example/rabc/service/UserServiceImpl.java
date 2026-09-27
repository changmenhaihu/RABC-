package com.example.rabc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.entity.User;
import com.example.rabc.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class UserServiceImpl implements UserService {
    private  final Map<String,Long> TOKEN_MAP = new ConcurrentHashMap<>();

    @Resource
    private UserMapper userMapper;

    public String login (String username , String password){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);
        //校验账号不存在或者密码不匹配
        if(user == null || !user.getPassword().equals(password)){
            return  null;
        }
        String token = UUID.randomUUID().toString();
        TOKEN_MAP.put(token,user.getId());
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        return  TOKEN_MAP.get(token);
    }

    @Override
    public boolean checkToken(String token) {
        return TOKEN_MAP.containsKey(token);
    }

    @Override
    public User getUserById(Long userId) {
        return  userMapper.selectById(userId);
    }
}
