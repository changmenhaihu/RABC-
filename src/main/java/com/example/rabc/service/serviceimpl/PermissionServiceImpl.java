package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rabc.common.BusinessException;
import com.example.rabc.entity.Permission;
import com.example.rabc.entity.RolePermission;
import com.example.rabc.mapper.PermissionMapper;
import com.example.rabc.mapper.RolePermissionMapper;
import com.example.rabc.service.PermissionService;
import com.example.rabc.service.RolePermissionService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private  RolePermissionMapper rolePermissionMapper;
    @Resource
    private RolePermissionService rolePermissionService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public List<Permission> listAll() {
        return  permissionMapper.selectList(null);
     }
     @Override
    public Permission getById(Long id){
        if (id == null){
            throw new BusinessException("id 不能为空");
        }
        Permission permission = permissionMapper.selectById(id);
        if (permission == null){
            throw new BusinessException("权限不存在");
        }
        return permission;
     }

     @Override
     @Transactional(rollbackFor = Exception.class)
     public void create(Permission permission){
        if (!StringUtils.hasText(permission.getPermKey())){
            throw new BusinessException("permKey 不能为空");
        }
        if (!StringUtils.hasText(permission.getPermName())){
            throw new BusinessException("permName 不能为空");
        }
         Long count = permissionMapper.selectCount(
                 new LambdaQueryWrapper<Permission>().eq(Permission::getPermKey,
                         permission.getPermKey())
         );
         if (count> 0){ throw new BusinessException("权限标识已经存在");}
         if (permission.getStatus() == null){
             permission.setStatus(1);
         }
         //防止前端传id干扰自增
         permission.setId(null);
         permissionMapper.insert(permission); //新建权限不用清理缓存
     }

     @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Permission permission){
        if (permission.getId() == null){
            throw new BusinessException("id不能为空");
        }
        Permission existing = permissionMapper.selectById(permission.getId());
        if (existing == null) {
            throw new BusinessException("权限不存在");
        }
        // permKey唯一性校验
         boolean permKeyChanged = StringUtils.hasText(permission.getPermKey())&&
                 !permission.getPermKey().equals(existing.getPermKey());
        if (!permKeyChanged){
            Long count = permissionMapper.selectCount(
                    new LambdaQueryWrapper<Permission>()
                            .eq(Permission::getPermKey,permission.getPermKey())
                            .ne(Permission::getId,permission.getId())
            );
            if (count > 0){
                throw new BusinessException("权限标识已经存在");
            }
        }
        permissionMapper.updateById(permission);
        //只有permKey或者status发生变化  就清理缓存
         boolean statusChanged = permission.getStatus()!=null &&
                 !permission.getStatus().equals(existing.getStatus());
         if (permKeyChanged || statusChanged){
             clearCacheByPermId(permission.getId());
         }
     }
     //根据权限id清空所有相关联用户的权限缓存
    private void clearCacheByPermId(Long permId){
        //查询该权限关联的角色id
        List<Long> roleIds = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getPermId,permId)
        ).stream().map(RolePermission::getRoleId).distinct().toList();
        //遍历角色ID  清理每个角色的权限缓存
        for (Long roleId : roleIds){
            redisTemplate.delete("role:permission"+roleId);
        }
        //如果有权限单独缓存  也可以清理
        redisTemplate.delete("permission"+permId);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id){
        if (id == null ){
            throw new BusinessException("id不能为空");
        }
        Permission existing = permissionMapper.selectById(id);
        if (existing  == null){
            throw new BusinessException("权限不存在");
        }
        //删除角色-权限关联
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getPermId,id)
        );
        //逻辑删除权限（@TableLogic 自动处理）
        permissionMapper.deleteById(id);
        //最后清理缓存
        clearCacheByPermId(id);
    }
    //分页查询功能
    @Override
    public Page<Permission> page(int pageNum, int pageSize, String permKey, String permName,
                                 Integer status){
        Page<Permission> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(permKey),Permission::getPermKey,permKey)
                .like(StringUtils.hasText(permName),Permission::getPermName,permName)
                .eq(status != null,Permission::getStatus,status)
                .orderByDesc(Permission::getId);
        return permissionMapper.selectPage(page,wrapper);
    }
}
