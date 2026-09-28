package com.example.rabc.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rabc.entity.User;
import com.example.rabc.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;



import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    //Redis前缀
    private  static  final String TOKEN_PREFIX = "token：";
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
      //登录成功生成token
        String token = UUID.randomUUID().toString().replace("-","");
        String redisKey = TOKEN_PREFIX + token;
        //存入Redis ，value存userId 设置过期
        redisTemplate.opsForValue().set(redisKey,user.getId(),TOKEN_EXPIRE_SECONDS,
                TimeUnit.SECONDS);
        return token;
    }

    @Override
    public boolean checkToken(String token) {
        String key = TOKEN_PREFIX+token;
        return redisTemplate.hasKey(key);
    }

    @Override
    public void logout(String token){
        String key = TOKEN_PREFIX+token;
        redisTemplate.delete(key);
    }
    @Override
    public User getUserById(Long userId) {
        return  userMapper.selectById(userId);
    }
   @Override
   public  Long  getUserIdByToken(String token){
        String key = TOKEN_PREFIX +token;
        return  (Long) redisTemplate.opsForValue().get(key);
   }
    @Override
    public void addUser(User user){
        //密码加密
        String encodepwd = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(10));
        user.setPassword(encodepwd);
        userMapper.insert(user);
    }
}
