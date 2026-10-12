package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.rabc.entity.SysoperateLog;
import com.example.rabc.mapper.SysOperateLogMapper;
import com.example.rabc.service.SysOperateLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysOperateLogServiceImpl extends ServiceImpl<SysOperateLogMapper, SysoperateLog> implements SysOperateLogService {
    @Resource
    private SysOperateLogMapper sysOperateLogMapper;
    @Override
    public void saveOperateLog(SysoperateLog log){
        sysOperateLogMapper.insert(log);
    }
}
