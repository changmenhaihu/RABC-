package com.example.rabc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    //当前数据列表
    private List<T> records;
    //总条数
    private long total;
    //当前页码
    private long pageNum;
    //每页大小
    private long pageSize;
    //总页数
    private long pages;
    //构造静态方法
    public static <T> PageResult<T> of (com.baomidou.mybatisplus.core.metadata.IPage<T> page){
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setPageNum(page.getCurrent());
        result.setTotal(page.getTotal());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }
}

