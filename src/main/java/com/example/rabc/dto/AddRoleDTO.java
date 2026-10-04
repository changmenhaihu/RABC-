package com.example.rabc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddRoleDTO {
    @NotBlank(message = "角色名不能为空")
    private String roleName;
    @NotBlank(message = "角色标识不能为空")
    private String roleKey;
}
