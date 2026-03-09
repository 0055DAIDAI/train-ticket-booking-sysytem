package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.mapper.UserMapper;
import org.example.pojo.LoginInfo;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(User user) {
        // 1. 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查手机号是否已注册
        if (userMapper.findByPhone(user.getPhone()) != null) {
            throw new RuntimeException("手机号已被注册");
        }

        // 3. 检查身份证是否已注册
        if (userMapper.findByIdCard(user.getIdCard()) != null) {
            throw new RuntimeException("身份证号已被注册");
        }

        // 4. 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 5. 设置默认值
        user.setBalance(new java.math.BigDecimal("0.00"));
        user.setGender(user.getGender() == null ? 0 : user.getGender());
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 6. 插入数据库
        userMapper.insert(user);

        // 7. 返回用户信息（含生成的 userId）
        return user;
    }

    @Override
    public LoginInfo login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.info("用户不存在");
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.info("密码错误");
            throw new RuntimeException("密码错误");
        }


        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("name", user.getName());

        String token = JwtUtil.generateJwt(claims);
        return new LoginInfo(user.getId(), user.getUsername(), user.getName(),  token, user.getAvatar(), user.getGender(), user.getPhone(), user.getIdCard(), user.getBalance());
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("参数不能为空或金额必须大于0");
        }

        // 获取用户信息
        var user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新余额
        user.setBalance(user.getBalance().add(amount));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void updateLastLogin(Long id, LocalDateTime lastLogin) {
        if (id == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 获取用户信息
        var user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setLastLogin(LocalDateTime.now());
        userMapper.updateLastLogin(id, lastLogin);
    }

    @Override
    public User getUserInfo(long id){

        return userMapper.findById(id);

    }

}