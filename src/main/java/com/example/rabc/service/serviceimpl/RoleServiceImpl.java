package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.common.BusinessException;
import com.example.rabc.dto.UpdateRoleDTO;
import com.example.rabc.entity.Role;
import com.example.rabc.entity.RolePermission;
import com.example.rabc.mapper.RoleMapper;
import com.example.rabc.mapper.RolePermissionMapper;
import com.example.rabc.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Override
    public List<Role> listAll(){
        return roleMapper.selectList(null);
    }
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void addRole(String roleName,String roleKey){
        //1.判空
        if (roleName == null || roleName.isBlank()){
            throw new BusinessException("角色名不能为空");
        }
        //2.查重
        Long count = roleMapper.selectCount(
                new LambdaQueryWrapper<Role>().eq(Role::getRoleName,roleName)
        );
        if (count>0){
            throw new BusinessException("角色名已存在");
        }
        //3.插入
        Role role = new Role();
        role.setRoleName(roleName);
        role.setRoleKey(roleKey);
        roleMapper.insert(role);
    }
    @Override
    public void updateRole(UpdateRoleDTO dto){
        if (dto.getId()==null){
            throw new BusinessException("roleId 不能为空");
        }
        Role role = roleMapper.selectById(dto.getId());
        if (role == null){
            throw new BusinessException("角色不存在");
        }
        role.setRoleName(role.getRoleName());
        role.setRoleKey(role.getRoleKey());
        roleMapper.updateById(role);
   }
   @Override
   @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId){
        if (roleId == null){
            throw new BusinessException("roleId不能为空");
        }
        //判断角色是否存在
       Role role = roleMapper.selectById(roleId);
       if (role == null) {
           return false;
       }
    //先删除角色权限关联表
       rolePermissionMapper.delete(
               new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId,roleId)
       );
       // 再删除角色
       roleMapper.deleteById(roleId);
       //再删除缓存
       Set<String> keys = redisTemplate.keys("perm:user:*");
       if ( !keys.isEmpty()){
           redisTemplate.delete(keys);
       }
       return true;
   }
}
