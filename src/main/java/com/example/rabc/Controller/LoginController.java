package com.example.rabc.Controller;

import com.example.rabc.common.result;
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
    public result<String> login(@RequestBody Logindto logindto){
        String token = userService.login(logindto.getUsername(),
                logindto.getPassword());
        return result.success(token);
    }
    @PostMapping("/logout")
    public result<?> logout(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        String token;
        if(authorization == null) {
            return result.fail("未携带Authorization请求头");
        }
        if(authorization.startsWith("Bearer ")){
            token = authorization.substring(7);
        }else {
            token = authorization;
        }
        userService.logout(token);
        return  result.success("退出成功");
    }
}
