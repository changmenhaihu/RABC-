package com.example.rabc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rabc.entity.Role;
import com.example.rabc.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
