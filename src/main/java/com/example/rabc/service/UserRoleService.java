package com.example.rabc.service;

import java.util.List;

public interface UserRoleService {
    //业务逻辑  先删除该用户原有角色 再批量插入新角色 然后清理权限缓存
    void assignRoles(Long userId, List<Long> roleIds);
    
}
