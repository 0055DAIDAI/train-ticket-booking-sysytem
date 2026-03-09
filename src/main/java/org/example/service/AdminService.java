package org.example.service;

import org.example.pojo.Admin;
import org.example.pojo.AdminLoginInfo;

import java.time.LocalDateTime;

public interface AdminService {
    AdminLoginInfo login(String username, String password);

    Admin register(Admin admin);

    /**
     * 更新最后登录时间
     */
    void updateLastLogin(Long id, LocalDateTime lastLogin);

    /**
     * 根据ID获取管理员信息
     */
    Admin getAdminInfo(long id);

}
