package com.example.rabc.common;


import lombok.Data;
@Data
public class result<T> {
    private Integer code;
    private String msg;
    private  T data;

    //成功静态方法
    public static  <T> result<T> success(T data){
     result<T> r = new result<>();
     r.code = 200;
     r.msg = "success";
     r.data = data;
     return r;
    }
    public  static  <T> result<T> success(T data,String msg){
        result<T> result =new result<>();
        result.code=200;
        result.msg=(msg);
        result.data=data;
        return result;
    }

    //失败静态方法
    public static  <T> result<T> fail(Integer code,String msg){
        result<T> r = new result<>();
        r.code = 400;
        r.msg = (msg);
        return r;
    }


}
