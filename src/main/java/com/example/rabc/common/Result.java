package com.example.rabc.common;


import lombok.Data;
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private  T data;

    //成功静态方法
    public static  <T> Result<T> success(T data){
     Result<T> r = new Result<>();
     r.code = 200;
     r.msg = "success";
     r.data = data;
     return r;
    }
    public  static  <T> Result<T> success(T data, String msg){
        Result<T> result =new Result<>();
        result.code=200;
        result.msg=(msg);
        result.data=data;
        return result;
    }

    //失败静态方法
    public static  <T> Result<T> fail(Integer code, String msg){
        Result<T> r = new Result<>();
        r.code = 400;
        r.msg = (msg);
        return r;
    }


}
