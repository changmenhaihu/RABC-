package com.example.rabc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("Permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String permKey;
    private String perName;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updatetime;
    @TableLogic
    private Integer deleted;

}
