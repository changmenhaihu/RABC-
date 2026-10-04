package com.example.rabc.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private Integer code;
    public BusinessException(Integer code,String message){
        super(message);
        this.code =code;
    }
    public BusinessException(String message){
        this(400,message);
    }
}
