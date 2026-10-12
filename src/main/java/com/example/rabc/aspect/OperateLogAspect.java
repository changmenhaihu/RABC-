package com.example.rabc.aspect;

import com.alibaba.fastjson2.JSON;
import com.example.rabc.annotation.OperateLog;
import com.example.rabc.entity.SysoperateLog;
import com.example.rabc.service.SysOperateLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
public class OperateLogAspect {
    @Resource
    private SysOperateLogService sysOperateLogService;

    //切点：拦截带有@OperateLog的方法
    @Pointcut("@annotation(com.example.rabc.annotation.OperateLog")
    public void logPointCut(){}

    @Around("logPointCut()")
    public Object around (ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        SysoperateLog sysoperateLog =new SysoperateLog();

        //默认成功
        sysoperateLog.setIsSuccess(1);
        sysoperateLog.setCreateTime(LocalDateTime.now());

        //获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) joinPoint.getSignature();
        HttpServletRequest request = attributes.getRequest();

        //获取注解信息
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperateLog logAnno = method.getAnnotation(OperateLog.class);
        sysoperateLog.setModule(logAnno.module().getModuleName());
        sysoperateLog.setDesc(logAnno.desc());

        //请求基础信息
        sysoperateLog.setRequestUrl(request.getRequestURI());;
        sysoperateLog.setRequestMethod(request.getMethod());
        sysoperateLog.setIp(sysoperateLog.getIp());

        //从AuthFilter存入request属性中拿取用户信息
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        sysoperateLog.setUserId(userId);
        sysoperateLog.setUserName(username);

        //请求参数
        Object[] args = joinPoint.getArgs();
        try {
            sysoperateLog.setRequestParams(JSON.toJSONString(args));
        }catch (Exception e){
            sysoperateLog.setRequestParams("参数序列化失败");
        }

        Object result;
        try {
            //执行Controller方法
            result = joinPoint.proceed();
            //保存返回的结果
            sysoperateLog.setResponseResult(JSON.toJSONString(request));
        }catch (Exception e){
             //捕获业务异常  标记失败
            sysoperateLog.setIsSuccess(0);
            sysoperateLog.setErrorMsg(e.getMessage());
            throw  e;}
       finally {
            //耗时
            long cost = System.currentTimeMillis() - start;
            sysoperateLog.setCostTime(cost);
            //同步写入数据库‘
            sysOperateLogService.saveOperateLog(sysoperateLog);
        }
        return result;
    }
    //获取客户端ip
    private String getIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null ||ip.isEmpty()){
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
