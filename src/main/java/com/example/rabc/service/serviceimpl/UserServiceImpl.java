package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.common.BusinessException;
import com.example.rabc.dto.TokenData;
import com.example.rabc.dto.UpdateUserDTO;
import com.example.rabc.entity.User;
import com.example.rabc.entity.UserRole;
import com.example.rabc.mapper.PermissionMapper;
import com.example.rabc.mapper.RolePermissionMapper;
import com.example.rabc.mapper.UserMapper;
import com.example.rabc.mapper.UserRoleMapper;
import com.example.rabc.service.UserService;
import com.example.rabc.vo.UserVO;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //把权限缓存到Redis
    private static final String PERM_PREFIX = "perm:user:";
    private static final long PERM_EXPIRE_SECONDS =30*60;

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
            throw new BusinessException("账号或者密码错误");
        }

        boolean passwordOk = BCrypt.checkpw(password,user.getPassword());
        if(!passwordOk){
            throw new BusinessException("账号或者密码错误");
        }
   //一次SQL 直接查询权限标识
        List<String> permList = getPermKeysByUserId(user.getId());
// 存进 TokenData
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
       Boolean hasKey =  redisTemplate.hasKey(redisKey);
       if (!hasKey){
           return false;
       }
       //每次校验通过都重新设置为2小时  滑动过期 只要使用就一直重置
        redisTemplate.expire(redisKey,TOKEN_EXPIRE_SECONDS,TimeUnit.SECONDS);
       return true;
    }

    @Override
    public UserVO getUserById(Long userId) {
    User user = userMapper.selectById(userId);
    if (user==null){
        return null;
    }
    UserVO vo =new UserVO();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setNickname(user.getNickname());
        return vo;
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

    //  权限缓存到列表里的方法
    @SuppressWarnings("unchecked")
    private List<String> getPermKeysByUserId(Long userId){
        String permKey = PERM_PREFIX + userId;
        Object cache = redisTemplate.opsForValue().get(permKey);
        if(cache!=null){
            return (List<String>) cache;
        }
        List<String> perms = userMapper.selectPermKeyByUserId(userId);
        redisTemplate.opsForValue().set(
                permKey,
                perms,
                PERM_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );
        return perms;
    }
    @Override
    public List<UserVO> listAll(){
        List<User> users = userMapper.selectList(null);
        return  users.stream().map(user -> {
            UserVO vo=new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            return vo;
        }).collect(Collectors.toList());
    }

    public void updateUser(UpdateUserDTO dto){
        if (dto.getId()==null){
            throw new BusinessException("userId 不能为空");
        }
        User user = userMapper.selectById(dto.getId());
        if (user==null){
            throw new BusinessException("用户不存在");
        }
        //只更新呢称
        user.setNickname(dto.getNickname());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId){
        if (userId == null){
            throw new BusinessException("userId 不能为空");
        }
        //先删除user_role 关联
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId,userId)
        );
        //再删除用户
        userMapper.deleteById(userId);
        //清理Redis缓存
        redisTemplate.delete("perm:user:"+userId);
        return true;
    }
    @Override
    public  List<Long> getRoleIdsByUserId(Long userId){
        if (userId == null){
            throw new BusinessException("userId不能为空");
        }
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId,userId)
        ).stream().map(UserRole::getId).collect(Collectors.toList());
    }
}
