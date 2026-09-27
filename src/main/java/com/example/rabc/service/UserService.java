package com.example.rabc.service;


import com.example.rabc.entity.User;

public interface UserService {
    //登录业务，校验账号和密码，生成token
    String login (String username,String password);
    //根据token获取用户的id
    Long getUserIdByToken(String token);

    //校验token是否有效
     boolean checkToken(String token) ;

    //根据用户id查询用户
    User getUserById(Long userId);
}
