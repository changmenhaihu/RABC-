package com.example.rabc.convert;

import com.alibaba.fastjson2.JSON;
import com.example.rabc.dto.OperateLogDTO;
import com.example.rabc.entity.SysoperateLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OperateLog 统一转换器
 *
 * 作用：
 *   1. JSON 字符串  -> OperateLogDTO
 *   2. OperateLogDTO -> SysoperateLog 实体
 *   3. JSON 字符串  -> SysoperateLog 实体（一步到位，替代原来重复写的 convertToEntity）
 *
 * ============================================================
 * 传输链路中被"修改"的字段（重点标记）：
 * ------------------------------------------------------------
 *  [生产端 - OperateLogSender]
 *   - logId        : 若为空，补 UUID（幂等标识）
 *   - createTime   : 若为空，由 AOP 侧设置（这里是兜底）
 *
 *  [切面 - OperateLogAspect]
 *   - isSuccess    : 默认 1（成功）；异常时置 0
 *   - costTime     : 由 AOP 统计方法耗时后写入
 *   - errorMsg     : 业务抛异常时写入
 *
 *  [转换器 - 本类]
 *   - createTime   : 入实体前若为 null，补当前时间
 *   - isSuccess    : 入实体前若为 null，默认 1
 *
 *  [消费者 - OperateLogConsumer]
 *   - 不修改字段，只做幂等校验
 * ============================================================
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OperateLogConvert {

    /** 供非 Spring 环境（如单元测试）使用 */
    OperateLogConvert INSTANCE = Mappers.getMapper(OperateLogConvert.class);

    // ============================================================
    // 1. JSON 字符串 -> DTO
    //    这一步不修改任何字段，原样反序列化
    // ============================================================
    default OperateLogDTO jsonToDto(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return JSON.parseObject(json, OperateLogDTO.class);
    }

    // ============================================================
    // 2. DTO -> 实体
    //    标出被修改或者默认值的字段：
    //      - createTime : 若为 null -> 补当前时间
    //      - isSuccess  : 若为 null -> 默认 1
    //    其他同名字段直接映射：
    //      logId, userId, userName, module, desc, requestUrl, requestMethod,
    //      ip, requestParams, responseResult, errorMsg, costTime
    // ============================================================
    @Mapping(target = "createTime", source = "createTime", qualifiedByName = "fillCreateTime")
    @Mapping(target = "isSuccess",  source = "isSuccess",  qualifiedByName = "fillIsSuccess")
    SysoperateLog dtoToEntity(OperateLogDTO dto);

    // ============================================================
    // 3. JSON 字符串 -> 实体（一步到位）
    //    消费者只需要调用这一个方法，替代原来重复的 convertToEntity
    // ============================================================
    default SysoperateLog jsonToEntity(String json) {
        OperateLogDTO dto = jsonToDto(json);
        return dto == null ? null : dtoToEntity(dto);
    }

    // ============================================================
    // 4. 批量转换（列表场景，如导入、补偿任务）
    // ============================================================
    List<SysoperateLog> dtoListToEntityList(List<OperateLogDTO> dtoList);

    // ============================================================
    // 字段默认值填充（标记为"传输中被修改"）
    // ============================================================

    /** 传输中修改的字段：createTime —— 若为空则补当前时间 */
    @Named("fillCreateTime")
    default LocalDateTime fillCreateTime(LocalDateTime createTime) {
        return createTime != null ? createTime : LocalDateTime.now();
    }

    /** 传输中修改的字段：isSuccess —— 若为空则默认为 1（成功） */
    @Named("fillIsSuccess")
    default Integer fillIsSuccess(Integer isSuccess) {
        return isSuccess != null ? isSuccess : 1;
    }
}