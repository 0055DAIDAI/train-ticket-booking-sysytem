// 文件路径：org/example/controller/UserController.java
package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.request.RechargeRequest;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户控制器：仅处理用户相关业务（注册、信息获取等）
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        registeredUser.setPassword(null); // 安全：不返回密码

        return Result.success(registeredUser);
    }



    /**
     * 用户充值
     */
    @PostMapping("/recharge")
    public Result<Void> recharge(@RequestBody RechargeRequest request) {
        try {
            userService.recharge(request.getUserId(), request.getAmount());
            return Result.success(null);
        } catch (Exception e) {
            log.error("用户充值失败:{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(Long userId) {
        User user = userService.getUserInfo(userId);
        user.setPassword(null); // 安全：不返回密码
        return Result.success(user);
    }


}