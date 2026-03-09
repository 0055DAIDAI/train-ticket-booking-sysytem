package org.example.service;

import org.example.pojo.LoginInfo;
import org.example.pojo.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface UserService {

    /**
     * 用户注册
     * @param user 用户信息（包含 username, password, phone, idCard 等）
     * @return 注册后的用户对象
     */
    User register(User user);

    /**
     * 用户登录
     * @param username 用户名或手机号
     * @param password 明文密码
     * @return 登录成功的用户对象，失败返回 null
     */
    LoginInfo login(String username, String password);

    /**
     * 用户充值
     */
    void recharge(Long userId, BigDecimal amount);

    void updateLastLogin(Long id, LocalDateTime lastLogin);

    User getUserInfo(long id);
}