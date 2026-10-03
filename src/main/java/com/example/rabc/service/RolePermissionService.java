package com.example.rabc.service;

import java.util.List;

public interface RolePermissionService {
    void  assignPerms(Long roleId, List<Long> permIds);
}
