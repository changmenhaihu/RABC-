package com.example.rabc.service.serviceimpl;

import com.example.rabc.convert.OperateLogConvert;
import com.example.rabc.dto.OperateLogDTO;
import com.example.rabc.entity.SysoperateLog;
import com.example.rabc.service.SysOperateLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OperateLogAsyncSaver {
    @Resource
    private SysOperateLogService sysOperateLogService;
    // 注入统一转换器
    @Resource
    private OperateLogConvert operateLogConvert;
    @Async("operateLogAsyncPool")
    public void asyncSaveLog(OperateLogDTO dto){
        try {
            SysoperateLog entity = operateLogConvert.dtoToEntity(dto);
            sysOperateLogService.saveOperateLog(entity);
            log.info("降级异步入库操作日志成功，logId:{}",dto.getLogId());
        }catch (DuplicateKeyException e){
            log.warn("降级入库时日志已存在，logId:{}",dto.getLogId());
        }catch (Exception e){
            log.error("降级保存日志失败,logId:{}",dto.getLogId(),e);
        }
    }

}
