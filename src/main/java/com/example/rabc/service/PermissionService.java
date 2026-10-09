package com.example.rabc.service;

import com.example.rabc.entity.Permission;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PermissionService {
    List<Permission> listAll();

    Permission getById(Long id);

    void create(Permission permission);
    void  update(Permission permission);
    void delete(Long id);
    Page<Permission> page(int pageNum,int pageSize,String permKey,String permName,
                          Integer status);
}
