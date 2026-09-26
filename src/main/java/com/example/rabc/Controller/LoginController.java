package com.example.rabc.Controller;

import com.example.rabc.dto.Logindto;
import com.example.rabc.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    private final UserService userService;
    //构造器注入
    public LoginController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/login")
    public String login(@RequestBody Logindto logindto){
        String token = userService.login(logindto.getUsername(),
                logindto.getPassword());
        if(token == null){
            return "登录失败";
        }
        return "登录成功："+token;
    }
}
