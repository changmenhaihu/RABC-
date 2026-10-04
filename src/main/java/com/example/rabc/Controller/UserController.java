package com.example.rabc.Controller;

import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.BusinessException;
import com.example.rabc.common.result;
import com.example.rabc.dto.UpdateUserDTO;
import com.example.rabc.entity.User;
import com.example.rabc.filter.AuthFilter;
import com.example.rabc.service.UserService;
import com.example.rabc.vo.UserVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Resource
    private UserService userService;

@GetMapping("/me")
public result<UserVO> me(@RequestHeader("Authorization") String authorization,
                         HttpServletRequest request) {
    if (authorization == null || authorization.isBlank()) {
        throw new BusinessException("未登录");
    }
    //String token = authorization.startsWith("Bearer ")?
    //    authorization.substring(7):authorization;

    Long userId = (Long) request.getAttribute(AuthFilter.REQUEST_ATTR_USER_ID);
    if (userId == null) {
        throw new BusinessException("登录已过期 请重新登录");
    }
    UserVO user = userService.getUserById(userId);
    if (user == null) {
        throw new BusinessException("用户不存在");
    }
    return result.success(user);
}
    @RequirePerm("user:add")
    @PostMapping("user/add")
    public result<User> addUser(@RequestBody User user){
    userService.addUser(user);
    return result.success(null);
}

    @GetMapping("/user/list")
    public result<List<UserVO>> listUsers(){
    return result.success(userService.listAll());
    }
    @PutMapping("/user/update")
    public result<Void> updateUser(@Valid @RequestBody UpdateUserDTO dto){
    userService.updateUser(dto);
    return result.success(null);
    }
    @DeleteMapping("/user/{id}")
    public  result<List<Long>> deleteUser(@PathVariable Long id){
    return result.success(userService.getRoleIdsByUserId(id));
    }
    @GetMapping("/user/{id}/roles")
    public result<List<Long>> getUserRoles(@PathVariable Long id){
    return  result.success(userService.getRoleIdsByUserId(id));
    }
}


