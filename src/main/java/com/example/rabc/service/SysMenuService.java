package com.example.rabc.service;

import com.example.rabc.common.PageResult;
import com.example.rabc.dto.AddMenuDTO;
import com.example.rabc.dto.MenuQueryDTO;
import com.example.rabc.dto.UpdateMenuDTO;
import com.example.rabc.entity.SysMenu;
import com.example.rabc.vo.MenuTreeVO;

import java.util.List;

public interface SysMenuService {
    List<SysMenu> listAll();
    PageResult<SysMenu> page(MenuQueryDTO query);
    List<MenuTreeVO> tree();
    Long add(AddMenuDTO dto);
    void update(UpdateMenuDTO dto);
    void delete(Long id);
    List<MenuTreeVO> getMenuTreeByUserId(Long userId);
    void updateStatus(Long id, Integer status);


}
