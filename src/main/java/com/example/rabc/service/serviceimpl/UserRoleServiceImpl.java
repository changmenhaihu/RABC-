package com.example.rabc.service.serviceimpl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.entity.UserRole;
import com.example.rabc.mapper.UserRoleMapper;
import com.example.rabc.service.UserRoleService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Resource
    private UserRoleMapper useerRoleMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    //加上事务 删除和查找必须同时成功或失败
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds){
        //删除用户原有角色
        useerRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getId,userId));
        //批量插入新角色
        if (roleIds != null &&!roleIds.isEmpty()){
            for (Long roleId :roleIds){
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                useerRoleMapper.insert(userRole);
            }
        }
        //删除用户的权限缓存
        redisTemplate.delete("perm:user:"+userId);
    }
}
