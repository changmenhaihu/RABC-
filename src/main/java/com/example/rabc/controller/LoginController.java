package com.example.rabc.controller;

import com.example.rabc.common.BusinessException;
import com.example.rabc.common.Result;
import com.example.rabc.dto.Logindto;
import com.example.rabc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public Result<String> login(@RequestBody Logindto logindto){
        String token = userService.login(logindto.getUsername(),
                logindto.getPassword());
        return Result.success(token);
    }
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        String token;
        if(authorization == null) {
           throw new BusinessException("未携带Authorization请求头");
        }
        if(authorization.startsWith("Bearer ")){
            token = authorization.substring(7);
        }else {
            token = authorization;
        } //可以优化为三元运算符 ?
        userService.logout(token);
       return Result.success("退出成功");
    }
}
