package com.example.rabc.service.serviceimpl;

import com.example.rabc.entity.Permission;
import com.example.rabc.mapper.PermissionMapper;
import com.example.rabc.service.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionMapper permissionMapper;
    public List<Permission> listAll() {
        return  permissionMapper.selectList(null);
     }
}
