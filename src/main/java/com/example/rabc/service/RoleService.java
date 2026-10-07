package com.example.rabc.service;

import com.example.rabc.dto.UpdateRoleDTO;
import com.example.rabc.dto.UpdateUserDTO;
import com.example.rabc.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> listAll();
    void addRole(String roleName,String roleKey);



    boolean deleteRole(Long roleId);
    void  updateRole(UpdateRoleDTO dto);
   
}
