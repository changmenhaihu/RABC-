package com.example.rabc.controller;


import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.Result;
import com.example.rabc.entity.Permission;
import com.example.rabc.service.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
