package com.example.rabc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rabc.annotation.RequirePerm;
import com.example.rabc.common.PageResult;
import com.example.rabc.common.Result;
import com.example.rabc.entity.SysoperateLog;
import com.example.rabc.service.SysOperateLogService;
import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/log")
public class SysOperateLogController {

    @Resource
    private SysOperateLogService operateLogService;

    @GetMapping("/pages")
    @RequirePerm("log:list")
    public Result<PageResult<SysoperateLog>> page(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize
    ){
        // 1.构建分页对象
        Page<SysoperateLog> page = new Page<>(pageNum, pageSize);
        // 2.执行分页查询，无查询条件第二个参数填null
        IPage<SysoperateLog> pageData = operateLogService.page(page, null);

        // 3.组装PageResult
        PageResult<SysoperateLog> pageResult = new PageResult<>();
        pageResult.setRecords(pageData.getRecords());
        pageResult.setTotal(pageData.getTotal());
        pageResult.setPageNum(pageData.getCurrent());
        pageResult.setPageSize(pageData.getSize());
        pageResult.setPages(pageData.getPages());

        return Result.success(pageResult);
    }
}
