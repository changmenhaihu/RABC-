package com.example.rabc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rabc.entity.SysoperateLog;

public interface SysOperateLogMapper extends BaseMapper<SysoperateLog> {

    Long selectCountByLogId(String logId);
}
