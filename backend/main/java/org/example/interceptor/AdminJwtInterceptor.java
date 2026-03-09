
// src/main/java/org/example/interceptor/AdminJwtInterceptor.java
package org.example.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AdminJwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取到请求路径
        String requestURI = request.getRequestURI();
        // 判断是否是管理员登录请求，如果是login请求，则放行
        if (requestURI.contains("/admin/login") || requestURI.contains("/admin/register")){
            log.info("管理员登录请求，放行");
            return true;
        }

        // 获取请求头的token
        String token = request.getHeader("token");
        // 判断token是否存在，若不存在则返回401错误
        if (token == null || token.isEmpty()) {
            log.info("请求头token为空");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 如果token存在，则验证token是否正确
        try {
            JwtUtil.parseJwt(token);
        } catch (Exception e) {
            log.info("token验证失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 验证成功，放行
        log.info("token验证成功");
        return true;
    }
}