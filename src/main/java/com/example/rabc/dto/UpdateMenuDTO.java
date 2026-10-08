package com.example.rabc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMenuDTO {
    @NotNull(message = "菜单id不能为空")
    private Long id;
    private  Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private  Integer sort ;
    private  Integer status ;
}
