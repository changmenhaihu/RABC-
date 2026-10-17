package com.example.rabc.common;

import com.example.rabc.enums.ResultCode;
import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    // ==================== 成功 ====================

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return success(data, ResultCode.SUCCESS.getMsg());
    }

    public static <T> Result<T> success(T data, String msg) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.msg = msg;
        r.data = data;
        return r;
    }

    // ==================== 失败 ====================

    /** 最常用：只给 msg，默认 code=400 */
    public static <T> Result<T> fail(String msg) {
        return fail(ResultCode.BAD_REQUEST, msg);
    }

    /** 用枚举默认 msg */
    public static <T> Result<T> fail(ResultCode rc) {
        return fail(rc.getCode(), rc.getMsg());
    }

    /** 用枚举 code + 自定义 msg */
    public static <T> Result<T> fail(ResultCode rc, String msg) {
        return fail(rc.getCode(), msg);
    }

    /** 兜底：直接指定 code + msg（一般不用，优先用枚举） */
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        r.data = null;
        return r;
    }
}