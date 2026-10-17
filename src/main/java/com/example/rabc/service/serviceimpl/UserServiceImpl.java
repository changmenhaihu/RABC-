package com.example.rabc.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.rabc.common.BusinessException;
import com.example.rabc.dto.ChangePasswordDTO;
import com.example.rabc.dto.RegisterDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    //注册默认普通用户角色
    private static final Long DEFAULT_ROLE_ID = 2L;
    @Resource
    private PasswordEncoder passwordEncoder;

    private static final String USER_TOKENS_PREFIX = "user:tokens:";   // ← 新增

    @Override
    public String login (String username , String password){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(wrapper);
        //校验账号不存在或者密码不匹配
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new BusinessException("用户名或密码不能为空");
        }
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
      //一次SQL 直接查询权限标识
        List<String> permList = getPermKeysByUserId(user.getId());
       // 存进 TokenData
        //封装dto存入redis
        TokenData tokenData = new TokenData(user.getId(), user.getUsername(), permList);

        String token = UUID.randomUUID().toString().replace("-","");

        String redisKey  =  TOKEN_PREFIX + token;
        //存入整个dto对象  正向索引
        redisTemplate.opsForValue().set(redisKey,tokenData,TOKEN_EXPIRE_SECONDS,TimeUnit.SECONDS);
        // ② 反向：userId → Set<token>
        String userTokensKey = USER_TOKENS_PREFIX + user.getId();
        redisTemplate.opsForSet().add(userTokensKey, token);
        // 每次登录都刷新 Set 的过期时间
        redisTemplate.expire(userTokensKey, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        return token;
}


    @Override
    public void addUser(User user) {
    String encodePwd = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(10));
    user.setPassword(encodePwd);
    userMapper.insert(user);
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
        if (token == null || token.isBlank()) return;

        String tokenKey = TOKEN_PREFIX + token;

        // 先从正向里取出 userId，才能定位反向 Set
        Object value = redisTemplate.opsForValue().get(tokenKey);
        if (value instanceof TokenData td) {
            redisTemplate.opsForSet().remove(
                    USER_TOKENS_PREFIX + td.getUserId(), token);
        }

        // 删正向
        redisTemplate.delete(tokenKey);
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
        kickOutAllTokens(userId);
        return true;
    }
    @Override
    public  List<Long> getRoleIdsByUserId(Long userId){
        if (userId == null){
            throw new BusinessException("userId不能为空");
        }
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId,userId)
        ).stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void register(RegisterDTO dto){
        if (!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new BusinessException("两次密码不一致");
        }
        Long count  =userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername,dto.getUsername())
        );
        if (count>0){
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        //BCrypt加密
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        userMapper.insert(user);

        //分配默认角色
        UserRole ur = new UserRole();
        ur.setUserId(user.getId());
        ur.setRoleId(DEFAULT_ROLE_ID);
        userRoleMapper.insert(ur);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDTO dto){
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())){
            throw new BusinessException("两次新密码不一致");
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())){
            throw new BusinessException("新密码不能与旧密码相同");
        }
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (!passwordEncoder.matches(dto.getOldPassword(),user.getPassword())){
            throw new BusinessException("原密码错误");
        }
        String encode = passwordEncoder.encode(dto.getNewPassword());
        userMapper.update(null,
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getPassword, encode)
        );

        //强制下线  删除该用户所有token
        kickOutAllTokens(userId);
    }
    @Override
    public TokenData getTokenData(String token) {
        if (token == null || token.isBlank()) return null;
        String redisKey = TOKEN_PREFIX + token;
        Object value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) return null;

        // 顺带续期（滑动过期）
        redisTemplate.expire(redisKey, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return (TokenData) value;
    }
    /**
     * 强制下线：删除该用户所有有效 token
     */
    private void kickOutAllTokens(Long userId) {
        if (userId == null) return;

        String userTokensKey = USER_TOKENS_PREFIX + userId;
        Set<Object> tokens = redisTemplate.opsForSet().members(userTokensKey);

        if (tokens != null) {
            for (Object t : tokens) {
                redisTemplate.delete(TOKEN_PREFIX + t);
            }
        }

        // 反向 Set 本身也删掉
        redisTemplate.delete(userTokensKey);
    }

}
