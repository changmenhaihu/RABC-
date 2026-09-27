package com.example.rabc.Controller;


import com.example.rabc.common.result;
import com.example.rabc.entity.User;
import com.example.rabc.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Resource
    private UserService userService;

public UserController(UserService userService){
    this.userService = userService;
}

@GetMapping("/me")
public result<Long> me(@RequestHeader("Authorization") String authorization) {
    if (authorization == null) {
        return result.fail("未登录");
    }
    String token;
    if (authorization.startsWith("Bearer")) {
        token = authorization.substring(7);
    } else {
        token = authorization;
    }
    Long userId = userService.getUserIdByToken(token);
    if (userId == null) {
        return result.fail("未登录");
    }
    User user = userService.getUserById(userId);
    if (user == null) {
        return result.fail("用户不存在");
    }
    return result.sucess(userId);
}
}


