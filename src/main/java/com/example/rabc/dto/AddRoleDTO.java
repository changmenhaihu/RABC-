package com.example.rabc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddRoleDTO {
    @NotBlank(message = "角色名不能为空")
    private String roleName;
}
