package com.example.rabc.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperateLogDTO {
    // 幂等唯一标识，每条日志一个唯一ID，用于去重
    private String logId;
    private Long userId;
    private String UserName;
    private String module;
    private String desc;

    private String requestUrl;
    private String requestMethod;
    private String ip;

    private String requestParams;
    private String responseResult;

    private Integer isSuccess;
    private String errorMsg;
    private Long costTime;
    private LocalDateTime createTime;
}
/*幂等思路说明：
生产者生成全局唯一logId(UUID) 放到消息体中
消费者消费前，先根据logId查询数据库。如果已经存在直接跳过，不再入库。
实现幂等  规避MQ重复投递的问题

 */