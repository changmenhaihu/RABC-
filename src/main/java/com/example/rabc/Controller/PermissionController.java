package com.example.rabc.Controller;


import com.example.rabc.common.result;
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
    @GetMapping("/list")
    public result<List<Permission>> listAll(){
        return result.success(permissionService.listAll());
    }
}
