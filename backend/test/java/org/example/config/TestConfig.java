// TestConfig.java
package org.example.config;

import org.example.interceptor.AdminJwtInterceptor;
import org.example.interceptor.JwtInterceptor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public AdminJwtInterceptor adminJwtInterceptor() {
        return new AdminJwtInterceptor() {
            @Override
            public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                                     jakarta.servlet.http.HttpServletResponse response,
                                     Object handler) throws Exception {
                // 测试环境下总是允许通过
                return true;
            }
        };
    }

    @Bean
    @Primary
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor() {
            @Override
            public boolean preHandle(jakarta.servlet.http.HttpServletRequest request,
                                     jakarta.servlet.http.HttpServletResponse response,
                                     Object handler) throws Exception {
                // 测试环境下总是允许通过
                return true;
            }
        };
    }
}
