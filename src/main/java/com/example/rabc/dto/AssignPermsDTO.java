package com.example.rabc.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermsDTO {
    private Long roleId;
    private List<Long> permIds;
}
