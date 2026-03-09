// src/main/java/org/example/service/impl/AdminServiceImpl.java
package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.mapper.AdminMapper;
import org.example.pojo.Admin;
import org.example.pojo.AdminLoginInfo;
import org.example.service.AdminService;
import org.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminLoginInfo login(String username, String password) {
        Admin admin = adminMapper.findByAdminName(username);
        if (admin == null) {
            log.info("管理员不存在");
            throw new RuntimeException("管理员不存在");
        }
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            log.info("密码错误");
            throw new RuntimeException("密码错误");
        }

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("adminName", admin.getAdminName());

        String token = JwtUtil.generateJwt(claims);
        return new AdminLoginInfo(admin.getId(), admin.getAdminName(), token);
    }

    @Override
    public Admin register(Admin admin) {
        // 1. 检查用户名是否已存在
        if (adminMapper.findByAdminName(admin.getAdminName()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 密码加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        // 3. 设置默认值
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        admin.setLastLoginTime(LocalDateTime.now());

        // 4. 插入数据库
        // 4. 插入数据库
        int result = adminMapper.insert(admin);
        if (result <= 0) {
            throw new RuntimeException("注册失败");
        }
        log.info("管理员注册成功：{}", admin);
        // 5. 返回管理员信息
        return admin;
    }


    @Override
    public void updateLastLogin(Long id, LocalDateTime lastLogin) {
        if (id == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 获取管理员信息
        var admin = adminMapper.findById(id);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        admin.setLastLoginTime(LocalDateTime.now());
        adminMapper.updateLastLogin(id, lastLogin);
    }

    @Override
    public Admin getAdminInfo(long id) {
        return adminMapper.findById(id);
    }
}
