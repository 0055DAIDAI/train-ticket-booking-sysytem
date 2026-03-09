package org.example.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.LoginInfo;
import org.example.pojo.Result;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginInfo> login(@RequestBody LoginRequest request) {
        log.info("用户登录:username={},password={}", request.getUsername(), request.getPassword());
        try {
            LoginInfo loginInfo = userService.login(request.getUsername(), request.getPassword());
            if (loginInfo != null){
                userService.updateLastLogin(loginInfo.getId(), LocalDateTime.now());
            }
            return Result.success(loginInfo);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 登录请求参数封装
     */
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
