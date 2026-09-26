package com.example.rabc.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.entity.User;
import jakarta.annotation.Resource;
import com.example.rabc.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController

public class UserController {
    @Resource
    private UserMapper userMapper;
    private  final  Map<String,Long> tokens = new HashMap<>();

    @PostMapping("/login")
    public  String login(@RequestBody Map<String,String> body){
        String username = body.get("username");
        String password = body.get("password");

        //构造查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User  user = (User) userMapper.selectOne(wrapper);
        if(user == null || ! user.getPassword().equals(password)){
            return "登录失败";
        }
        String token=UUID.randomUUID().toString();
        tokens.put(token, user.getId());   // ✅ 保存 token 和用户 ID
       // System.out.println("生成 token = " + token + "，tokens 现有 = " + tokens);
        return token;
    }
   @GetMapping("/me")
    public  String me(@RequestHeader("Authorization") String token){
       //System.out.println("收到 token = [" + token + "]，tokens 现有 = " + tokens);
       if(token == null) return  "未登录";
       Long userID = tokens.get(token);
        if (userID == null)  return  "未登录";
        User user = (User) userMapper.selectById(userID);
        if (user == null)  return "用户不存在";
        return  "你是用户id = "+userID;
   }
}
