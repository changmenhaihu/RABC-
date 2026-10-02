package com.example.rabc.service;


import com.example.rabc.entity.User;
import com.example.rabc.vo.UserVO;

import java.util.List;

public interface UserService {
    //登录业务，校验账号和密码，生成token
    String login (String username,String password);

    //校验token是否有效
     boolean checkToken(String token) ;

    //根据用户id查询用户
    UserVO getUserById(Long userId);

    //退出登录 删除Redis中的token
    void logout(String token);
    //根据Token查询用户的id
    Long getUserIdByToken (String token);
    //新增用户
    void addUser(User user);

    List<String> getPermListByToken(String token);
}
