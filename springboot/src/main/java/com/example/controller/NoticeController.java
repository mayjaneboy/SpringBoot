package com.example.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Notice;
import com.example.exception.CustomException;
import com.example.service.NoticeService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
/*@RestController 是 @Controller和@ResponseBody的组合注解，
告知 Spring 该类用于处理HTTP请求(@Controller的作用)
不同于@Controller注解（返回JSP视图），
它用于标记一个类为 RESTful Web 服务的控制器，
所有方法的返回值（如对象、集合等）会自动转换为 JSON/XML 格式，
并通过 HTTP 响应体返回给客户端，无需手动处理。
适合构建前后端分离的 API 接口
*/

@RequestMapping("/notice")
public class NoticeController {
    @Resource
    NoticeService noticeService;

    @PostMapping("/add")
    public Result add(@RequestBody Notice notice) {
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            throw new CustomException("500","您的角色无权操作");
        }
        notice.setTime(DateUtil.now());
        noticeService.add(notice);
        return Result.success();
    }

    @PutMapping("/update")  //前后端发送接收请求的类型要一致 不一致会报错405
    public Result update(@RequestBody Notice notice) {  //@RequestBody用来接受前端传来的json参数，（get请求是接收不倒json的）
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            throw new CustomException("500","您的角色无权操作");
        }
        noticeService.update(notice);
        return Result.success();  //这个接口不用返回数据
    }

    @DeleteMapping("/delete/{id}")  //前后端发送接收请求的类型要一致 不一致会报错405
    public Result delete(@PathVariable Integer id) {  //@PathVariable用来接受前端传来的路径参数，（get请求是接收不倒json的）
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            throw new CustomException("500","您的角色无权操作");
        }
        noticeService.deleteById(id);
        return Result.success();  //这个接口不用返回数据
    }


    @GetMapping("/selectAll")  // 完整请求路径：  /notice/selectAll
    /*@RequestMapping是一个通用的注解，可以处理所有HTTP方法的请求，
    比如GET、POST、PUT、DELETE等,可以通过 method 参数显式指定支持的 HTTP 方法
    而`@GetMapping`是专门处理HTTP GET请求的注解。
    另外，`@RequestMapping`可以定义在类级别和方法级别，
    而`@GetMapping`通常只用于方法级别。*/
    public Result selectAll(Notice notice) {
        List<Notice> noticeList = noticeService.selectAll(notice);
        return Result.success(noticeList);
    }



    /*分页查询
     * pageNum当前页码
     * pageSize每页数据的个数*/
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Notice notice) {
        //参数列表里的Notice notice可以接受多个 Notice对象的属性 作为参数，且不和前面的参数冲突
        PageInfo<Notice> pageInfo = noticeService.selectPage(pageNum, pageSize, notice);
        //pageInfo是数据类型为实体类Notice的对象
        return Result.success(pageInfo);
        //pageInfo作为参数传给Result.success（），将pageInfo赋给Result对象的data，得到的是Result类型的对象，对应Notice.vue里的res
        //res.data是Notice类型的，但为什么res.data会有res.data.list,Notice类型的对象没有list属性啊
    }
}