package com.example.rabc.controller;

import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.PageResult;
import com.example.rabc.common.Result;
import com.example.rabc.dto.AddMenuDTO;
import com.example.rabc.dto.MenuQueryDTO;
import com.example.rabc.dto.UpdateMenuDTO;
import com.example.rabc.entity.SysMenu;
import com.example.rabc.service.SysMenuService;
import com.example.rabc.vo.MenuTreeVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class SysMenuController {
    @Resource
    private SysMenuService sysMenuService;

    @RequirePerm("menu:list")
    @GetMapping("/list")
    public Result<List<SysMenu>> listAll() {
        return Result.success(sysMenuService.listAll());
    }

    @RequirePerm("menu:list")
    @GetMapping("/page")
    public Result<PageResult<SysMenu>> page(MenuQueryDTO query) {
        return Result.success(sysMenuService.page(query));
    }

    @RequirePerm("menu:list")
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        return Result.success(sysMenuService.tree());
    }

    @RequirePerm("menu:add")
    @PostMapping("/add")
    public Result<Long> add(@Valid @RequestBody AddMenuDTO dto) {
        return Result.success(sysMenuService.add(dto));
    }

    @RequirePerm("menu:update")
    @PutMapping("/update")
    public Result<Void> update(@Valid @RequestBody UpdateMenuDTO dto) {
        sysMenuService.update(dto);
        return Result.success(null);
    }

    @RequirePerm("menu:delete")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return Result.success(null);
    }

    @RequirePerm("menu:update")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        sysMenuService.updateStatus(id, status);
        return Result.success(null);
    }
}
