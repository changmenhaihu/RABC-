package com.example.rabc.vo;

import com.example.rabc.entity.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuTreeVO {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;

    private List<MenuTreeVO> children = new ArrayList<>();

    public static MenuTreeVO from (SysMenu menu){
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());
        vo.setStatus(menu.getStatus());
        return vo;
    }
}
