package com.example.rabc.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignRolesDTO {

    private Long userId;
    private List<Long> roleIds;
}
