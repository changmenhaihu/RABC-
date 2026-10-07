package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.common.BusinessException;
import com.example.rabc.entity.RolePermission;
import com.example.rabc.entity.UserRole;
import com.example.rabc.mapper.RolePermissionMapper;
import com.example.rabc.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolePermissionServiceImpl implements com.example.rabc.service.RolePermissionService {
   @Resource
    private RolePermissionMapper rolePermissionMapper;
   @Resource
    private UserRoleMapper userRoleMapper;
   @Resource
    private RedisTemplate<String,Object> redisTemplate;
   @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPerms(Long roleId,List<Long> permIds){
       if (roleId == null){
           throw new BusinessException("roleId 不能为空");
       }
       if (permIds == null){
           throw new BusinessException("permIds 不能为空");
       }
       //删除该角色原有权限
       rolePermissionMapper.delete(
               new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId,roleId));
       //批量插入新权限
       for (Long permId:permIds){
           RolePermission rp = new RolePermission();
           rp.setRoleId(roleId);
           rp.setPermId(permId);
           rolePermissionMapper.insert(rp);
       }
   //找出所有拥有这个角色的用户  清空他们的权限缓存
       List<Long> userIds = userRoleMapper.selectList(
               new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId,roleId)).stream().map(UserRole::getUserId).toList();

               for(Long userId:userIds){
                   redisTemplate.delete("perm:user:"+userId);
               }
   }
}
