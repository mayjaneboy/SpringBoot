package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Category;
import com.example.exception.CustomException;
import com.example.service.CategoryService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

@RequestMapping("/category")
public class CategoryController {
    @Resource
    CategoryService categoryService;

    @PostMapping("/add")
    public Result add(@RequestBody Category category) {
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            throw new CustomException("500","您的角色无权操作");
        }
        categoryService.add(category);
        return Result.success();
    }

    @PutMapping("/update")  //前后端发送接收请求的类型要一致 不一致会报错405
    public Result update(@RequestBody Category category) {  //@RequestBody用来接受前端传来的json参数，（get请求是接收不倒json的）
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            throw new CustomException("500","您的角色无权操作");
        }
        categoryService.update(category);
        return Result.success();  //这个接口不用返回数据
    }

    @DeleteMapping("/delete/{id}")  //前后端发送接收请求的类型要一致 不一致会报错405
    public Result delete(@PathVariable Integer id) {  //@PathVariable用来接受前端传来的路径参数，（get请求是接收不倒json的）
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            throw new CustomException("500","您的角色无权操作");
        }
        categoryService.deleteById(id);
        return Result.success();  //这个接口不用返回数据
    }


    @GetMapping("/selectAll")  // 完整请求路径：  /category/selectAll
    /*@RequestMapping是一个通用的注解，可以处理所有HTTP方法的请求，
    比如GET、POST、PUT、DELETE等,可以通过 method 参数显式指定支持的 HTTP 方法
    而`@GetMapping`是专门处理HTTP GET请求的注解。
    另外，`@RequestMapping`可以定义在类级别和方法级别，
    而`@GetMapping`通常只用于方法级别。*/
    public Result selectAll(Category category) {
        List<Category> categoryList = categoryService.selectAll(category);
        return Result.success(categoryList);
    }



    /*分页查询
     * pageNum当前页码
     * pageSize每页数据的个数*/
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Category category) {
        //参数列表里的Category category可以接受多个 Category对象的属性 作为参数，且不和前面的参数冲突
        PageInfo<Category> pageInfo = categoryService.selectPage(pageNum, pageSize, category);
        //pageInfo是数据类型为实体类Category的对象
        return Result.success(pageInfo);
        //pageInfo作为参数传给Result.success（），将pageInfo赋给Result对象的data，得到的是Result类型的对象，对应Category.vue里的res
        //res.data是Category类型的，但为什么res.data会有res.data.list,Category类型的对象没有list属性啊
    }
}