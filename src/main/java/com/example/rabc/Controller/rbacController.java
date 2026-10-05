package com.example.rabc.Controller;

import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.BusinessException;
import com.example.rabc.common.result;
import com.example.rabc.dto.AddRoleDTO;
import com.example.rabc.dto.AssignPermsDTO;
import com.example.rabc.dto.AssignRolesDTO;
import com.example.rabc.entity.Role;
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
    public result<List<Role>> listRoles(){
        return result.success(roleService.listAll());
    }
    //给用户分配角色
    //请求示例   {"userId":1,"roleIds":[1,3]}


    @RequirePerm("user:assignRoles")
    @PostMapping("/user/assignRoles")
    public result<Void> assignRoles(@Valid @RequestBody AssignRolesDTO dto){
        userRoleService.assignRoles(dto.getUserId(),dto.getRoleIds());
        return result.success(null);
    }
    //给角色分配权限
    //请求示例  {“roleId":1,"permIds":[1,3]}
    @RequirePerm("role:assignPerm")
    @PostMapping("/role/assignPerms")
    public result<Void> assignPerms(@RequestBody AssignPermsDTO dto){
        rolePermissionService.assignPerms(dto.getRoleId(),dto.getPermIds());
        return result.success(null);
    }


    @RequirePerm("role:add")
    @PostMapping("/role/add")
    public result<Void> addRoles(@Valid @RequestBody AddRoleDTO dto){
        roleService.addRole(dto.getRoleName(),dto.getRoleKey());
        return result.success(null);
    }
    @RequirePerm("role:update")  //这里传递参数标准做法是要加一个DTO文件的  但是我又觉得可以简化 role表暂时没有太多信息，后面应该还是要加，role表里实体变多后
    @PutMapping("/role/update")
    public result<?> updateRole(@RequestBody Role role){
        roleService.updateRole(role);
        return result.success(null);
    }
    @RequirePerm("role:delete")
    @DeleteMapping("/role/delete/{id}")
    public result<?> deleteRole(@PathVariable Long id){
        boolean ok = roleService.deleteRole(id);
        if (!ok){
            throw new BusinessException("角色不存在");
        }
        return result.success(null);
    }
}
