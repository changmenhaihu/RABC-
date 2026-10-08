package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rabc.common.BusinessException;
import com.example.rabc.common.PageResult;
import com.example.rabc.dto.AddMenuDTO;
import com.example.rabc.dto.MenuQueryDTO;
import com.example.rabc.dto.UpdateMenuDTO;
import com.example.rabc.entity.SysMenu;
import com.example.rabc.mapper.SysMenuMapper;
import com.example.rabc.service.SysMenuService;
import com.example.rabc.vo.MenuTreeVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> listAll() {
        return sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSort));
    }

    @Override
    public PageResult<SysMenu> page(MenuQueryDTO q) {
        Page<SysMenu> page = new Page<>(q.getPageNum(), q.getPageSize());
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>()
                .like(StringUtils.hasText(q.getMenuName()), SysMenu::getMenuName, q.getMenuName())
                .eq(q.getStatus() != null, SysMenu::getStatus, q.getStatus())
                .orderByAsc(SysMenu::getSort);

        Page<SysMenu> pageResult = sysMenuMapper.selectPage(page, wrapper);
        // ✔修复原来错误：不要传PageResult类，传入total、pageNum、pageSize、数据列表
        return PageResult.of(pageResult);
    }

    @Override
    public List<MenuTreeVO> tree() {
        List<SysMenu> allMenus = listAll();
        return buildTree(allMenus);
    }

    private List<MenuTreeVO> buildTree(List<SysMenu> all) {
        Map<Long, List<SysMenu>> groupByParent = all.stream()
                .collect(Collectors.groupingBy(menu -> menu.getParentId() == null ? 0L : menu.getParentId()));
        return recursiveBuild(groupByParent, 0L);
    }

    private List<MenuTreeVO> recursiveBuild(Map<Long, List<SysMenu>> map, Long parentId) {
        List<SysMenu> list = map.getOrDefault(parentId, Collections.emptyList());
        List<MenuTreeVO> res = new ArrayList<>();
        for (SysMenu menu : list) {
            MenuTreeVO vo = MenuTreeVO.from(menu);
            vo.setChildren(recursiveBuild(map, menu.getId()));
            res.add(vo);
        }
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(AddMenuDTO dto) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        sysMenuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateMenuDTO dto) {
        // ✔删除无用强制类型转换
        SysMenu exist = sysMenuMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException("菜单不存在");
        }
        // 不能把父菜单设置为自己
        if (dto.getParentId() != null && dto.getParentId().equals(dto.getId())) {
            throw new BusinessException("父菜单不能选择自身");
        }
        BeanUtils.copyProperties(dto, exist);
        sysMenuMapper.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysMenu exist = sysMenuMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("菜单不存在");
        }
        // 判断是否存在子菜单
        Long childCount = sysMenuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子菜单，不能删除");
        }
        // 判断菜单是否被角色绑定
        if (sysMenuMapper.countRoleMenuBind(id) > 0) {
            throw new BusinessException("该菜单已绑定角色，无法删除");
        }
        sysMenuMapper.deleteById(id);
    }

    // ❗删掉原来笔误重复的 getMenuTreeBtyUserId 方法，拼写错误，空实现没用

    @Override
    public List<MenuTreeVO> getMenuTreeByUserId(Long userId) {
        List<SysMenu> menuList = sysMenuMapper.selectMenusByUserId(userId);
        return buildTree(menuList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        if (status == null || status < 0 || status > 1) {
            throw new BusinessException("状态只能是0或1");
        }
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        menu.setStatus(status);
        sysMenuMapper.updateById(menu);
    }
}
