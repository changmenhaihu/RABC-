package com.example.rabc.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.Result;
import com.example.rabc.entity.Permission;
import com.example.rabc.service.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;
    @RequirePerm("perm:list")
    @GetMapping("/list")
    public Result<List<Permission>> listAll(){
        return Result.success(permissionService.listAll());
    }

    @RequirePerm("perm:list")
    @GetMapping("/getPermissionById")
    public Result<Permission> getById(@RequestParam Long id) {
        return Result.success(permissionService.getById(id));
    }
    @RequirePerm("perm:add")
    @PostMapping("/createpermission")
    public Result<Void> create(@RequestBody Permission permission){
        permissionService.create(permission);
        return Result.success(null);
    }
    @RequirePerm("perm:edit")
    @PutMapping("/updatePermission")
    public Result<Void> update(@RequestBody Permission permission){
        permissionService.update(permission);
        return Result.success(null);
    }

    @RequirePerm("perm:delete")
    @DeleteMapping("/deletePermission")
    public Result<Void> delete(@RequestParam Long id){
        permissionService.delete(id);
        return Result.success(null);
    }

    //权限查询分页     带条件筛选permKey permName  status
    @RequirePerm("perm:list")
    @GetMapping("/page")
    public Result<Page<Permission>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String permKey,
            @RequestParam(required = false) String permName,
            @RequestParam(required = false) Integer status
    ){
            Page<Permission> pageData = permissionService.page(pageNum,pageSize,permKey,permName,status);{
            return Result.success(pageData);
        }
    }

}
