package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
//这些包一般是自动引进来的

@RestController//注解(很重要，小地球表示是可以对外传输数据的)
public class WebController {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    //接口
    @GetMapping("/")// 斜杠表示路由，全局唯一
    public Result hello() {
        return Result.success("hello bravo");
    }
    //定义接口的格式：@部分+路由+主体
    //接口代码发生改变之后要重新启动一次springboot项目
    //接口返回的数据类型是由我们自己定义的，比如string
    //为了避免各个接口返回的类型混乱，可以创建一个对象，把要返回的数据放在对象里返回

    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account dbAccount = null;
        if ("ADMIN".equals(account.getRole())){
            dbAccount = adminService.login(account);
        }else if ("USER".equals(account.getRole())){
            dbAccount = userService.login(account);
        }else {
            throw new CustomException("非法请求");
        }
        return  Result.success(dbAccount);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {

        if("ADMIN".equals(account.getRole())){adminService.updatePassword(account);}
        else if("USER".equals(account.getRole())){userService.updatePassword(account);};
        return Result.success();
    }
}