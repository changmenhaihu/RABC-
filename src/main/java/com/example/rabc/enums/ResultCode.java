package com.example.rabc.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据已存在"),
    INTERNAL_ERROR(500, "服务器内部异常");


    private final int code;
    private final String msg;

    /** 根据 code 反查枚举（可选，日志/网关排查时有用） */
    public static ResultCode fromCode(int code) {
        for (ResultCode rc : values()) {
            if (rc.code == code) {
                return rc;
            }
        }
        return INTERNAL_ERROR;
    }
}