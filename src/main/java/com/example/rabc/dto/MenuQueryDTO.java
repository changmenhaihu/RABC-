package com.example.rabc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuQueryDTO extends PageQuery{
    private String menuName;
    private  Integer status;
}
