package com.example.rabc.annotation;


import com.example.rabc.enums.OperateMoudleEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
       //业务模块   使用枚举
    OperateMoudleEnum module();
    //操作描述
    String desc();
}
