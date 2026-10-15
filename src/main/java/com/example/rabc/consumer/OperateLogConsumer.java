package com.example.rabc.consumer;

import com.example.rabc.convert.OperateLogConvert;
import com.example.rabc.entity.SysoperateLog;
import com.example.rabc.mapper.SysOperateLogMapper;
import com.example.rabc.service.SysOperateLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic = "${rockemq.topic}", consumerGroup = "sys_operate_log_consumer_group")

public class OperateLogConsumer implements RocketMQListener<String> {
    @Resource
    private SysOperateLogService sysOperateLogService;
    @Resource
    private SysOperateLogMapper sysOperateLogMapper;
    @Resource
    private OperateLogConvert operateLogConvert;

    public  void onMessage(String message){
        SysoperateLog entity;
        try {
            entity = operateLogConvert.jsonToEntity(message);
        }catch (Exception e){
            log.error("消息JSON解析失败:{}",message,e);
            return;
        }
        if (entity == null || entity.getLogId() == null || entity.getLogId().isBlank()){
            log.error("消息缺少logId,无法幂等处理,messgae={}",message);
            return;
        }
        String logId = entity.getLogId();
        //幂等校验
        Long count = sysOperateLogMapper.selectCountByLogId(logId);
        if (count > 0){
            log.warn("消息重复消费，logId={}已经存在，跳过入库",logId);
            return;
        }
       ///不存在  执行入库
        try {
            sysOperateLogService.saveOperateLog(entity);
            log.info("MQ消费日志入库成功logId:{}",logId);
        }catch (DuplicateKeyException e){
            // 并发场景  两个消费者同时查到不存在  考log_id 唯一索引兜底
            log.warn("并发重复消费,logId={}已存在,跳过入库",logId);
        }
    }
}
