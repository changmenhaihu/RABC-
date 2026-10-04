package com.example.rabc.service.serviceimpl;

import com.example.rabc.entity.Permission;
import com.example.rabc.mapper.PermissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionImpl {
    @Resource
    private PermissionMapper permissionMapper;
    public List<Permission> listAll() {
         return  permissionMapper.selectList(null);
     }
}
