package com.example.rabc.Controller;

import com.example.rabc.common.result;
import com.example.rabc.dto.AssignPermsDTO;
import com.example.rabc.dto.AssignRolesDTO;
import com.example.rabc.entity.Role;
import com.example.rabc.service.RolePermissionService;
import com.example.rabc.service.RoleService;
import com.example.rabc.service.UserRoleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rbac")
public class rbacController {
    @Resource
    private RoleService roleService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RolePermissionService rolePermissionService;

    //查所有的角色
    @GetMapping("/role/list")
    public result<List<Role>> listRoles(){
        return result.success(roleService.listAll());
    }
    //给用户分配角色
    //请求示例   {"userId":1,"roleIds":[1,3]}
    @PostMapping("/role/assignPerms")
    public result<Void> assignPerms(@RequestBody AssignRolesDTO dto){
        userRoleService.assignRoles(dto.getUserId(),dto.getRoleIds());
        return result.success(null);
    }
    //给角色分配权限
    //请求示例  {“roleId":1,"permIds":[1,3]}
    @PostMapping("/role/assignPerms")
    public result<Void> assignPerms(@RequestBody AssignPermsDTO dto){
        rolePermissionService.assignPerms(dto.getRoleId(),dto.getPermIds());
        return result.success(null);
    }
}
