package com.example.rabc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.dto.TokenData;
import com.example.rabc.entity.Permission;
import com.example.rabc.entity.RolePermission;
import com.example.rabc.entity.User;
import com.example.rabc.entity.UserRole;
import com.example.rabc.mapper.PermissionMapper;
import com.example.rabc.mapper.RolePermissionMapper;
import com.example.rabc.mapper.UserMapper;
import com.example.rabc.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    //Redis前缀
    private  static  final String TOKEN_PREFIX = "token:";
    //token 过期时间 2小时
    private  static  final  long TOKEN_EXPIRE_SECONDS = 2*60*60;

    @Override
    public String login (String username , String password){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);
        //校验账号不存在或者密码不匹配
        if(user == null ){
            return  null;
        }

        boolean passwordOk = BCrypt.checkpw(password,user.getPassword());
        if(!passwordOk){
            return  null;
        }
        // 1. 查用户所有角色ID
        List<Long> roleIdList = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId())
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());

// 2. 查角色对应权限ID
        List<Long> permIdList = roleIdList.isEmpty()
                ? Collections.emptyList()
                : rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIdList)
        ).stream().map(RolePermission::getPermId).collect(Collectors.toList());

// 3. 查权限标识 permKey
        List<String> permList = permIdList.isEmpty()
                ? Collections.emptyList()
                : permissionMapper.selectByIds(permIdList)
                .stream()
                .map(Permission::getPermKey)
                .distinct()
                .collect(Collectors.toList());

// 4. 存进 TokenData
        TokenData tokenData = new TokenData();
        tokenData.setPermList(permList);
        String token = UUID.randomUUID().toString().replace("-","");
      //封装dto存入redis
        tokenData.setUserId(user.getId());
        tokenData.setUsername(user.getUsername());

        String redisKey  =  TOKEN_PREFIX + token;
        //存入整个dto对象
        redisTemplate.opsForValue().set(redisKey,tokenData,TOKEN_EXPIRE_SECONDS,TimeUnit.SECONDS);
        return token;
}
@Override
public  Long getUserIdByToken(String token){
    String redisKey = TOKEN_PREFIX + token;
    //读取DTO对象
    TokenData tokenData = (TokenData) redisTemplate.opsForValue().get(redisKey);
    if(tokenData == null){
        return  null;
    }
    return tokenData.getUserId();
    }

    @Override
    public void addUser(User user) {
    String encodePwd = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(10));
    user.setPassword(encodePwd);
    userMapper.insert(user);
    }

    @Override
    public  boolean checkToken(String token){
        String redisKey = TOKEN_PREFIX + token;
        return redisTemplate.hasKey(redisKey);
    }

    @Override
    public User getUserById(Long userId) {
    return userMapper.selectById(userId);
        }

    @Override
    public  void  logout (String token){
        String redisKey = TOKEN_PREFIX + token;
        redisTemplate.delete(redisKey);
    }
    @Override
    public List<String>  getPermListByToken(String token){
     String redisKey = TOKEN_PREFIX+ token;
     TokenData tokenData = (TokenData) redisTemplate.opsForValue().get(redisKey);
     if(tokenData == null || tokenData.getPermList() == null){
         return Collections.emptyList();
     }
     return tokenData.getPermList();
    }
}
