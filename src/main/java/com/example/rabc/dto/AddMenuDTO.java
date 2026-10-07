package com.example.rabc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMenuDTO {
    @NotNull(message = "父菜单Id不能为空")
    private Long parentId;

    @NotNull(message = "菜单名称不能为空")
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private  Integer sort = 0;
    private  Integer status = 1;
}
