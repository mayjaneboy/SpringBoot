package com.example.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Admin;
import com.example.service.AdminService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

@RequestMapping("/admin")
public class AdminController {
    @Resource
    AdminService adminService;

    @PostMapping("/add")
    public Result add(@RequestBody Admin admin){  //@RequestBody用来接受前端传来的json参数，（get请求是接收不倒json的）
        adminService.add(admin);
        return Result.success();  //这个接口不用返回数据
    }

    @PutMapping("/update")  //前后端发送接收请求的类型要一致 不一致会报错405
    public Result update(@RequestBody Admin admin){  //@RequestBody用来接受前端传来的json参数，（get请求是接收不倒json的）
        adminService.update(admin);
        return Result.success();  //这个接口不用返回数据
    }

    @DeleteMapping("/delete/{id}")  //前后端发送接收请求的类型要一致 不一致会报错405
    public Result delete(@PathVariable Integer id){  //@PathVariable用来接受前端传来的路径参数，（get请求是接收不倒json的）
        adminService.deleteById(id);
        return Result.success();  //这个接口不用返回数据
    }

    @DeleteMapping("/deleteBatch")  //前后端发送接收请求的类型要一致 不一致会报错405
    public Result delete(@RequestBody List<Admin> list){  //@RequestBody用来接受前端传来的json数组，（get请求是接收不倒json的）
        adminService.deleteBatch(list);
        return Result.success();  //这个接口不用返回数据
    }


    @GetMapping("/selectAll")  // 完整请求路径：  /admin/selectAll
    /*@RequestMapping是一个通用的注解，可以处理所有HTTP方法的请求，
    比如GET、POST、PUT、DELETE等,可以通过 method 参数显式指定支持的 HTTP 方法
    而`@GetMapping`是专门处理HTTP GET请求的注解。
    另外，`@RequestMapping`可以定义在类级别和方法级别，
    而`@GetMapping`通常只用于方法级别。*/
    public Result selectAll(Admin admin){
        List<Admin> adminList = adminService.selectAll(admin);
        return Result.success(adminList);
    }

    /*分页查询
    * pageNum当前页码
    * pageSize每页数据的个数*/
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Admin admin){
        //参数列表里的Admin admin可以接受多个 Admin对象的属性 作为参数，且不和前面的参数冲突
        PageInfo<Admin> pageInfo = adminService.selectPage(pageNum, pageSize, admin);
        //pageInfo是数据类型为实体类Admin的对象
        return Result.success(pageInfo);
        //pageInfo作为参数传给Result.success（），将pageInfo赋给Result对象的data，得到的是Result类型的对象，对应Admin.vue里的res
        //res.data是Admin类型的，但为什么res.data会有res.data.list,Admin类型的对象没有list属性啊
    }


    /*数据导出接口*/
    @GetMapping("/export")
    public void exportData(Admin admin, HttpServletResponse response) throws Exception {
        String ids = admin.getIds();
        if (StrUtil.isNotBlank(ids)) {
            String[] idsArr = ids.split(",");
            admin.setIdsArr(idsArr);
        }
        //1.拿到所有数据
        List<Admin> list = adminService.selectAll(admin);
        //2.构建Writer对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //3.设置中文表头
        writer.addHeaderAlias("username","账号");
        writer.addHeaderAlias("name","名称");
        writer.addHeaderAlias("phone","电话");
        writer.addHeaderAlias("email","邮箱");

        //只把添加了alias的属性写出到writer，比如password没有被设置中文表头，就不会被导出
        writer.setOnlyAlias(true);
        //4.写出数据到到writer
        writer.write(list);
        //5.设置输出文件的名称以及输出流的头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");//这行代码是固定的
        String filename = URLEncoder.encode("管理员信息", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename +  ".xlsx");//设置浏览器响应格式
        //6.写出到输出流 并关闭 writer和os
        ServletOutputStream os = response.getOutputStream();
        writer.flush(os);//把writer里的数据输出到输出流os中
        writer.close();//关闭writer
        os.close();//关闭os
    }

    /*批量导入*/
    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();//获取输入流
        ExcelReader reader = ExcelUtil.getReader(inputStream);//构建reader
        //通过reader获取excle文件里的数据
        reader.addHeaderAlias("账号","username");
        reader.addHeaderAlias("名称","name");
        reader.addHeaderAlias("电话","phone");
        reader.addHeaderAlias("邮箱","email");
        List<Admin> list = reader.readAll(Admin.class);
        //将数据写入数据库
        for(Admin admin:list){
            adminService.add(admin);
        }
        return Result.success();
    }
}