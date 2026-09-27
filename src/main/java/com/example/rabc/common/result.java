package com.example.rabc.common;


import lombok.Data;
@Data
public class result<T> {
    private Integer code;
    private String msg;
    private  T data;

    //成功静态方法
    public static  <T> result<T> sucess(T data){
     result<T> r = new result<>();
     r.code = 200;
     r.msg = "sucess";
     r.data = data;
     return r;
    }

    //失败静态方法
    public static  <T> result<T> fail(String msg){
        result<T> r = new result<>();
        r.code = 400;
        r.msg = "msg";
        r.data = null;
        return r;
    }


}
