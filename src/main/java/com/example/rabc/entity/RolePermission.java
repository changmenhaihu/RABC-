package com.example.rabc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("RolePermission")
public class RolePermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private  Long  user_id;
    private  Long perm_id;
}
