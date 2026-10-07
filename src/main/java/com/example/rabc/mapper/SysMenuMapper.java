package com.example.rabc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rabc.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper {
//根据userId查询用户拥有的菜单
    List<SysMenu> selectMenuByUserId(@Param("userId") Long userId);
    //查询角色绑定的菜单id
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
    //查询菜单是否被角色绑定
    int countRoleMenuBind(@Param("menuId") Long menuId);
}
