package com.example.rabc.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    //当前页数据列表
    private List<T> records;
    //总条数
    private long total;
    //当前页码
    private long pageNum;
    //每页大小
    private long pageSize;
    //总页数
    private long pages;

    /**
     * 重载1：接收MyBatis‑Plus IPage对象（推荐service层直接使用）
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setPageNum(page.getCurrent());
        result.setTotal(page.getTotal());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    /**
     * 重载2：手动传入参数，兼容之前代码调用方式
     */
    public static <T> PageResult<T> of(long total, long pageNum, long pageSize, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        // 计算总页数，可选
        long pages = pageSize == 0 ? 0 : (total + pageSize -1)/pageSize;
        result.setPages(pages);
        return result;
    }
}
