package com.example.rabc.controller;

import com.example.rabc.annotation.OperateLog;
import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.BusinessException;
import com.example.rabc.common.Result;
import com.example.rabc.dto.AddRoleDTO;
import com.example.rabc.dto.AssignPermsDTO;
import com.example.rabc.dto.AssignRolesDTO;
import com.example.rabc.dto.UpdateRoleDTO;
import com.example.rabc.entity.Role;
import com.example.rabc.enums.OperateMoudleEnum;
import com.example.rabc.service.RolePermissionService;
import com.example.rabc.service.RoleService;
import com.example.rabc.service.UserRoleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
    @RequirePerm("role:list")
    @GetMapping("/role/list")
    public Result<List<Role>> listRoles(){
        return Result.success(roleService.listAll());
    }

    //给用户分配角色
    //请求示例   {"userId":1,"roleIds":[1,3]}

    @RequirePerm("user:assignRoles")
    @PostMapping("/user/assignRoles")
    @OperateLog(module=OperateMoudleEnum.USER_MANAGE,desc = "分配角色")
    public Result<Void> assignRoles(@Valid @RequestBody AssignRolesDTO dto){
        userRoleService.assignRoles(dto.getUserId(),dto.getRoleIds());
        return Result.success(null);
    }
    //给角色分配权限
    //请求示例  {“roleId":1,"permIds":[1,3]}
    @RequirePerm("role:assignPerm")
    @PostMapping("/role/assignPerms")
    @OperateLog(module=OperateMoudleEnum.ROLE_MANAGE,desc = "分配权限")
    public Result<Void> assignPerms(@RequestBody AssignPermsDTO dto){
        rolePermissionService.assignPerms(dto.getRoleId(),dto.getPermIds());
        return Result.success(null);
    }


    @RequirePerm("role:add")
    @PostMapping("/role/add")
    @OperateLog(module=OperateMoudleEnum.ROLE_MANAGE,desc = "增加角色")
    public Result<Void> addRoles(@Valid @RequestBody AddRoleDTO dto){
        roleService.addRole(dto.getRoleName(),dto.getRoleKey());
        return Result.success(null);
    }
    @RequirePerm("role:update")  //已经增加dto文件
    @PutMapping("/role/update")
    @OperateLog(module=OperateMoudleEnum.ROLE_MANAGE,desc = "更新角色")
    public Result<?> updateRole(@RequestBody UpdateRoleDTO dto){
        roleService.updateRole(dto);
        return Result.success(null);
    }
    @RequirePerm("role:delete")
    @DeleteMapping("/role/delete/{id}")
    @OperateLog(module=OperateMoudleEnum.ROLE_MANAGE,desc = "删除角色")
    public Result<?> deleteRole(@PathVariable Long id){
        boolean ok = roleService.deleteRole(id);
        if (!ok){
            throw new BusinessException("角色不存在");
        }
        return Result.success(null);
    }
    @RequirePerm("role:assignMenu")
    @PostMapping("/assignMenu")
    public Result<Void> assignMenu(@RequestParam Long roleId, @RequestBody List<Long> menuIdList) {
        roleService.assignMenus(roleId, menuIdList);
        return Result.success(null);
    }

}
