package com.example.rabc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rabc.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 唯一保留的自定义方法：多表 JOIN，BaseMapper 搞不定
    @Select("""
            SELECT DISTINCT p.perm_key
            FROM user_role ur, role_permission rp, permission p
            WHERE ur.role_id = rp.role_id
              AND rp.perm_id = p.id
              AND ur.user_id = #{userId}
            """)
    List<String> selectPermKeyByUserId(@Param("userId") Long userId);
}