package com.example.rabc.aspect;

import com.alibaba.fastjson2.JSON;
import com.example.rabc.annotation.OperateLog;
import com.example.rabc.dto.OperateLogDTO;
import com.example.rabc.service.serviceimpl.OperateLogSender;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class OperateLogAspect {

    @Resource
    private OperateLogSender operateLogSender;
    //切点：拦截带有@OperateLog的方法
    @Pointcut("@annotation(com.example.rabc.annotation.OperateLog")
    public void logPointCut(){}

    @Around("logPointCut()")
    public Object around (ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        OperateLogDTO dto = new OperateLogDTO();

        //默认成功
        dto.setIsSuccess(1);
        dto.setCreateTime(LocalDateTime.now());

        //获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) joinPoint.getSignature();
        HttpServletRequest request =null;
        if (attributes!=null){
            request = attributes.getRequest();
        }

        //获取注解信息
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperateLog logAnno = method.getAnnotation(OperateLog.class);
        dto.setModule(logAnno.module().getModuleName());
        dto.setDesc(logAnno.desc());

        //请求基础信息
        if (request!=null) {
            dto.setRequestUrl(request.getRequestURI());
            dto.setRequestMethod(request.getMethod());
            dto.setIp(getIp(request));

            //从AuthFilter存入request属性中拿取用户信息
            Long userId = (Long) request.getAttribute("userId");
            String username = (String) request.getAttribute("username");
            dto.setUserId(userId);
            dto.setUserName(username);
        }
        //请求参数
        Object[] args = joinPoint.getArgs();
        try {
            dto.setRequestParams(JSON.toJSONString(args));
        }catch (Exception e){
            dto.setRequestParams("参数序列化失败");
        }

        Object result;
        try {
            //执行Controller方法
            result = joinPoint.proceed();
            //保存返回的结果
            dto.setResponseResult(JSON.toJSONString(result));
        }catch (Exception e){
             //捕获业务异常  标记失败
            dto.setIsSuccess(0);
            dto.setErrorMsg(e.getMessage());
            throw  e;}
       finally {
            //耗时
            long cost = System.currentTimeMillis() - start;
            dto.setCostTime(cost);
            //同步写入数据库‘  写日志不影响业务
            try {
            operateLogSender.sendOperateLog(dto);
            }catch (Exception e){
                log.error("保存操作日志失败,logId={}",dto.getLogId(), e);
            }

        }
        return result;
    }
    // 获取客户端 IP
    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }
}
