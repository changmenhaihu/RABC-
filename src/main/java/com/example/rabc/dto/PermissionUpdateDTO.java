package com.example.rabc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionUpdateDTO {
    @NotNull(message = "id不能为空")
    private Long id;

    @NotBlank(message = "权限标识permKey不能为空")
    private String permKey;

    @NotBlank(message = "权限名称permName不能为空")
    private String permName;

    private Long parentId;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private Integer sort;
}
