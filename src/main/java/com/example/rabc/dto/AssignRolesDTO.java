package com.example.rabc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignRolesDTO {
    @NotNull(message = "useId不能为空")
    private Long userId;
    @NotNull(message = "roleIds 不能为空")
    private List<Long> roleIds;
}
