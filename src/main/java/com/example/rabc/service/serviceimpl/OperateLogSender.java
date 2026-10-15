package com.example.rabc.service.serviceimpl;

import com.alibaba.fastjson2.JSON;
import com.example.rabc.convert.OperateLogConvert;
import com.example.rabc.dto.OperateLogDTO;
import com.example.rabc.service.SysOperateLogService;
import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OperateLogSender {
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private SysOperateLogService sysOperateLogService;
    @Resource
    private OperateLogConvert operateLogConvert;
    @Resource
    private OperateLogAsyncSaver operateLogAsyncSaver;
    @Value("${rocketmq.topic}")
    private String topic;
    //发送操作日志  优先MQ 失败后自动降级async异步入库

    public void sendOperateLog(OperateLogDTO dto){
        //生成全局唯一的logId 幂等标识
        if (dto.getLogId()==null||dto.getLogId().isBlank()){
            dto.setLogId(UUID.randomUUID().toString());
        }

        try {
            String json = JSON.toJSONString(dto);
            rocketMQTemplate.asyncSend(
                    topic,
                    MessageBuilder.withPayload(json).build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("操作日志发送MQ成功，logId:{},msgId:{}",
                                    dto.getLogId(),sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable throwable) {
                         log.error("MQ发送失败，触发async降级保存日志，logId：{}",
                                 dto.getLogId(),throwable);
                        }
                    }
            );
        }catch (Exception e){
            //MQ不可用  需要降级async异步入库
            log.error("MQ发送失败，触发async降级保存日志，logId:{},异常:{}",dto.getLogId(),e.getMessage());
            //异步发送方法
            operateLogAsyncSaver.asyncSaveLog(dto);
        }
    }

}
