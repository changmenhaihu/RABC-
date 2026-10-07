package com.example.rabc.common;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    //1.处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e){
        return Result.fail(e.getCode(),e.getMessage());
    }
    //2.处理@Valid 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e){
        //取出第一个字段错误信息
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg  =  fieldError !=null ? fieldError.getDefaultMessage():"参数校验失败";
        return Result.fail(400,"参数校验失败"+e.getMessage());
    }
    //3.兜底：处理所有未捕获的异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e){
        e.printStackTrace();  //控制台打印堆栈 方便排错
        return Result.fail(500,"服务器内部异常");
    }
}
