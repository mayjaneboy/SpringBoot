package com.example.utils;

import java.util.Date;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//JSON Web Token（JWT）是一种开放标准（RFC 7519），用于在网络应用间安全地传输信息。它以紧凑且自包含的方式，通过 JSON 对象在各方之间传递经过验证的信息。
//JWT 由三部分组成，用 . 分隔：Header（头部）、Payload（负载）(携带声明（如用户身份、权限、有效期等）)、Signature（签名）

@Component  //把TokenUtils注入到容器里面
//TokenUtils是用来操作JWT Token的工具类
public class TokenUtils {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;
    //getCurrentUser是类的成员函数，不能访问属于类的对象的数据adminService、userService，
    // 所以就需要定义static的staticadminService和staticuserService,但是它们之中是没有值的
    static AdminService staticAdminService;
    static UserService staticUserService;
    @PostConstruct  // springboot工程启动后会加载这段代码
    public void init() {
        staticAdminService = adminService;
        staticUserService = userService;
    }  //把非static的adminService和userService（有数据的值）赋给staticAdminService和staticUserService

    /**
     * 生成token
     */
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data) // 将 userId-role 保存到 token 里面,作为载荷  withAudience设置载荷
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1)) // withExpiresAt设置1天(当前日期+一天)后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥, HMAC256算法加密
    }
    //createToken生成一个JWT Token，一个分三段的字符串
    // eg:token: "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxLUFETUlOIiwiZXhwIjoxNzQxMDk0OTA3fQ.IC5p_qswxQoFGVvEWt8_b_TSmYjWfz2if0BKaTEJdLg"
    //JWT Token用于登录时，相当于一把钥匙，会存在LocalStorage里，用户拿着这把钥匙访问响应数据

    /**
     * 获取当前登录的用户信息
     */
    public static Account getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        // 拿到token 的载荷数据
        String audience = JWT.decode(token).getAudience().get(0);
        String[] split = audience.split("-");
        String userId = split[0];
        String role = split[1];
        // 根据token解析出来的userId去对应的表查询用户信息
        if ("ADMIN".equals(role)) {
            return staticAdminService.selectById(userId);
        } else if ("USER".equals(role)) {
            return staticUserService.selectById(userId);
        }
        return null;
    }
}