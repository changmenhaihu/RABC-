package com.example.rabc.service;


import com.baomidou.mybatisplus.spring.service.IService;
import com.example.rabc.entity.SysoperateLog;

public interface SysOperateLogService extends IService<SysoperateLog> {
    //保存日志
    void saveOperateLog(SysoperateLog log);
}
