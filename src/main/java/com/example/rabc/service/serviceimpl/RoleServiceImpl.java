package com.example.rabc.service.serviceimpl;

import com.example.rabc.entity.Role;
import com.example.rabc.mapper.RoleMapper;
import com.example.rabc.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Override
    public List<Role> listAll(){
        return roleMapper.selectList(null);
    }
}
