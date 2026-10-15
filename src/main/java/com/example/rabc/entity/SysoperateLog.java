package com.example.rabc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operate_log")
public class SysoperateLog {
    @TableId(type = IdType.AUTO)

    private Long id;
    //幂等标识ID
    private String LogId;
    //操作人id
    private Long userId;
    private String userName;
    //模块名称
    private String module;

    //操作描述
    private String desc;
    //请求地址
    private String requestUrl;
    //请求方式
    private String requestMethod;
    //客户端ip
    private String ip;


    //请求参数
    private String requestParams;
    //响应结果
    private  String responseResult;

    //耗时
    private Long costTime;
    //是否成功  成功1，失败0
    private  Integer isSuccess;
    //创建时间
    private LocalDateTime createTime;
    private String errorMsg;

}
