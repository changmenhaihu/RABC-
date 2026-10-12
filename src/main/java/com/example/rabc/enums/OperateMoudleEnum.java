package com.example.rabc.enums;

import lombok.Getter;

@Getter
public enum OperateMoudleEnum {
    USER_MANAGE("用户管理"),
    ROLE_MANAGE("角色管理"),
    PERMISSION_MANAGE("权限管理"),
    MENU_MANAGE("菜单管理"),
    LOGIN_AUTH("登录认证");

    private final String moduleName;
    OperateMoudleEnum(String moduleName){
        this.moduleName = moduleName;
    }
}
