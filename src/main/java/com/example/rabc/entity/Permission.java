package com.example.rabc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String perm_key;
}
