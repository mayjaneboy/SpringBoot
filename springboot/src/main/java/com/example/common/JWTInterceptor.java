package com.example.common;

import com.auth0.jwt.JWT;
import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;


//拦截器实现
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 从网络请求头中拿到token
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            // 如果没拿到，再从参数里再拿一次
            token = request.getParameter("token");
        }

        // 2. 认证token
        if (StrUtil.isBlank(token)) {
            System.out.println("Received Token: " + token);
            throw new CustomException("401", "您无权限操作0");
        }

        Account account = null;
        try {
            // 拿到 token 中的载荷数据
            String audience = JWT.decode(token).getAudience().get(0);
            String[] split = audience.split("-");
            String userId = split[0];
            String role = split[1];

            // 根据 用token解析出来的userId去对应的表查询用户信息
            if ("ADMIN".equals(role)) {
                account = adminService.selectById(userId);
            } else if ("USER".equals(role)) {
                account = userService.selectById(userId);
            }
        } catch (Exception e) {
            throw new CustomException("401", "您无权限操作1");
        }

        if (account == null) {
            throw new CustomException("401", "您无权限操作2");
        }

        try {
            // 验证签名
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new CustomException("401", "您无权限操作3");
        }
        return true;
    }
}