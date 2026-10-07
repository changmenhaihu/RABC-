package com.example.rabc.controller;

import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.BusinessException;
import com.example.rabc.common.Result;
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
public Result<UserVO> me(@RequestHeader("Authorization") String authorization,
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
    return Result.success(user);
}
    @RequirePerm("user:add")
    @PostMapping("user/add")
    public Result<User> addUser(@Valid @RequestBody User user){
    userService.addUser(user);
    return Result.success(null);
}

    @RequirePerm("user:view")
    @GetMapping("/user/list")
    public Result<List<UserVO>> listUsers(){
    return Result.success(userService.listAll());
    }

    @RequirePerm("user:update")
    @PutMapping("/user/update")
    public Result<Void> updateUser(@Valid @RequestBody UpdateUserDTO dto){
    userService.updateUser(dto);
    return Result.success(null);
    }

    @RequirePerm("user:delete")
    @DeleteMapping("/user/delete/{id}")
    public Result<Void> deleteUser(@PathVariable Long id){
    boolean ok = userService.deleteUser(id);
    if(!ok){
        throw new BusinessException("用户不存在 删除失败");
    }
    return Result.success(null);
    }

    @RequirePerm("user:view")
    @GetMapping("/user/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id){
    return  Result.success(userService.getRoleIdsByUserId(id));
    }
}


