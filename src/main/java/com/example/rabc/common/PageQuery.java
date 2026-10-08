package com.example.rabc.common;

import lombok.Data;

@Data
public class PageQuery {
    private Integer pageNum = 1;
    private  Integer pageSize =10;

    public Integer getPageNum(){
        if (pageNum == null || pageNum <1){
            return  1;
        }
        return pageNum;
    }
    public Integer getPageSize(){
        if (pageSize == null || pageSize <1){
            return  10;
        }
        //限制最大每页数量  防止恶意请求
        return Math.min(pageSize,200);
    }
}
