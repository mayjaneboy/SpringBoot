package com.example.controller;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.entity.Category;
import com.example.entity.Introduction;
import com.example.entity.User;
import com.example.service.CategoryService;
import com.example.service.IntroductionService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/echarts")
public class EchaartsController {


    private final CategoryService categoryService;
    private final IntroductionService introductionService;
    private final UserService userService;

    public EchaartsController(CategoryService categoryService, IntroductionService introductionService, UserService userService) {
        this.categoryService = categoryService;
        this.introductionService = introductionService;
        this.userService = userService;
    }

    @GetMapping("/pie")
    public Result pie(){
        List<Map<String, Object>> list = new ArrayList<>();
        //查询出所有的分类信息
        List<Category> categories = categoryService.selectAll(new Category());
        //查询出所有的帖子信息
        List<Introduction> introductions = introductionService.selectAll(new Introduction());

        for (Category category : categories) {
            long count = introductions.stream().filter(x ->category.getId().equals(x.getCategoryId())).count();
            Map<String, Object> map = new HashMap<>();
            map.put("name",category.getTitle());
            map.put("value",count);
            list.add(map);
        }
        return Result.success(list);
    }

    @GetMapping("/bar")
    public Result bar(){
        Map<String,Object> resultMap = new HashMap<>();
        List<String> xList = new ArrayList<>();
        List<Long> vList = new ArrayList<>();

        Map<String,Object> map = new HashMap<>();  //map用于排序，并不真正返回

        //查询出所有用户
        List<User> users = userService.selectAll(new User());
        //查询出所有的帖子信息
        List<Introduction> introductions = introductionService.selectAll(new Introduction());
        for (User user : users) {
            long count = introductions.stream().filter(x ->user.getId().equals(x.getUserId())).count();
            map.put(user.getName(),count);
        }

        //对map进行排序，按照value来倒序
        LinkedHashMap<String, Long> collectMap = map.entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(e -> (Long) e.getValue())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (Long) e.getValue(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));


        for (String key : collectMap.keySet()) {
            xList.add(key);
            vList.add(collectMap.get(key));
        }

        //截断
        if (xList.size()>5 && vList.size()>5) {
            xList =  xList.subList(0,5);
            vList = vList.subList(0,5);
        }

        resultMap.put("xList",xList);
        resultMap.put("vList",vList);
        return Result.success(resultMap);
    }

    @GetMapping("/line")
    public Result line(){
        Map<String,Object> resultMap = new HashMap<>();
        List<Long> vList = new ArrayList<>();

        //获取最近多少天的年月日List
        Date today = new Date();
        DateTime start = DateUtil.offsetDay(today, -7);
        List<String> xList = DateUtil.rangeToList(start, today, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate).toList();

        //把所有帖子查出来
        List<Introduction> introductions = introductionService.selectAll(new Introduction());
        for (String day : xList) {
            Long count = introductions.stream().filter(x -> ObjectUtil.isNotEmpty(x.getTime())&&x.getTime().contains(day)).count();
            vList.add(count);
        }

        resultMap.put("xList",xList);
        resultMap.put("vList",vList);
        return Result.success(resultMap);
    }
}