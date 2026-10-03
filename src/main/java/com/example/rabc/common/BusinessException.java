package com.example.rabc.common;

public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }
}
