package com.example.rabc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rabc.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface UserMapper extends BaseMapper<User> {
    @Select("""
            SELECT DISTINCT p.perm_key
            FROM user_role ur, role_permission rp, permission p
            WHERE ur.role_id = rp.role_id
              AND rp.perm_id = p.id
              AND ur.user_id = #{userId};
            """)
    List<String> selectPermKeyByUserId(@Param("userId") Long userId);
}
