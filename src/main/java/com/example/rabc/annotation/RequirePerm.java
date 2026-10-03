package com.example.rabc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //注解只能贴在方法上
@Retention(RetentionPolicy.RUNTIME) //运行时才能读取

public @interface RequirePerm {
    String value();

}
