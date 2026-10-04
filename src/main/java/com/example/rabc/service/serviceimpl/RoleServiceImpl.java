package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.common.BusinessException;
import com.example.rabc.entity.Role;
import com.example.rabc.mapper.RoleMapper;
import com.example.rabc.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Override
    public List<Role> listAll(){
        return roleMapper.selectList(null);
    }

    @Override
    public void addRole(String rolename){
        //1.判空
        if (rolename == null || rolename.isBlank()){
            throw new BusinessException("角色名不能为空");
        }
        //2.查重
        Long count = roleMapper.selectCount(
                new LambdaQueryWrapper<Role>().eq(Role::getRoleName,rolename)
        );
        if (count>0){
            throw new BusinessException("角色名已存在");
        }
        //3.插入
        Role role = new Role();
        role.setRoleName(rolename);
        roleMapper.insert(role);
    }

}
