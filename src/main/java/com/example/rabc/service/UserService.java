package com.example.rabc.service;


import com.example.rabc.dto.UpdateUserDTO;
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
    // 根据token查询权限列表
    List<String> getPermListByToken(String token);
    //查询所有的用户
    List<UserVO> listAll();
    //修改用户信息
    void updateUser(UpdateUserDTO dto);
    //删除用户
    boolean deleteUser(Long userId);
    //根据用户id获取拥有的角色
    List<Long> getRoleIdsByUserId(Long userId);


}
