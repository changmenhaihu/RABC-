package com.example.rabc.vo;

import lombok.Data;

@Data
public class PermissionVO {
    private Long id;
    private String permKey;
    private String permName;
    private Long parentId;
    private Integer status;
    private Integer sort;
}
