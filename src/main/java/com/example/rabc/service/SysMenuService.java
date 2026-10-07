package com.example.rabc.service;

import com.example.rabc.dto.AddMenuDTO;
import com.example.rabc.dto.MenuQueryDTO;
import com.example.rabc.dto.UpdateMenuDTO;
import com.example.rabc.entity.SysMenu;
import com.example.rabc.vo.MenuTreeVO;
import com.sun.source.tree.MethodTree;

import java.util.List;

public interface SysMenuService {
    List<SysMenu> listAll();
    PagedResult<SysMenu> page(MenuQueryDTO dto);
    List<MethodTree> tree();
    Long add(AddMenuDTO dto);
    void update(UpdateMenuDTO dto);
    void delete (Long id);
    List<MenuTreeVO> getMenTreeBtyUserId(Long userId);
    void updateStatus(Long id, Integer status);

}
