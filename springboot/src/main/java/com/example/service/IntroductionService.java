package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import com.example.entity.Account;
import com.example.entity.Category;
import com.example.entity.Introduction;
import com.example.entity.User;
import com.example.mapper.CategoryMapper;
import com.example.mapper.IntroductionMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntroductionService {

    @Resource
    IntroductionMapper introductionMapper;
    @Resource
    CategoryMapper categoryMapper;
    @Resource
    UserMapper userMapper;
    /*`@Resource`是一个注解，用来进行依赖注入。
    依赖注入的意思是，Spring框架会自动找到合适的对象并赋值给这个变量，不需要手动创建。
    在这里，`@Resource`被用在`IntroductionMapper introductionMapper`这个变量上，
    目的是让Spring把IntroductionMapper接口的实现类自动注入到introductionMapper这个变量中。
    `IntroductionMapper introductionMapper;` 声明了一个IntroductionMapper类型的变量。\
    IntroductionMapper是一个接口，本身不能直接实例化。
    但是在MyBatis中，通过动态代理机制，会自动生成这个接口的实现类，
    并且在运行时将这个实现类的实例注入到introductionMapper变量中。
    这样，就可以直接调用introductionMapper中的方法selectAll()，而无需关心具体的实现细节。*/


    public void update(Introduction introduction) {
        introductionMapper.updateById(introduction);
    }


    public List<Introduction> selectAll(Introduction introduction){
        List<Introduction> introductions = introductionMapper.selectAll(introduction);
        for (Introduction dbintroduction:introductions){
            dbintroduction.setDescription(HtmlUtil.cleanHtmlTag(dbintroduction.getContent()));
        }
        return introductions;
    }

    public PageInfo<Introduction> selectPage(Integer pageNum, Integer pageSize, Introduction introduction) {

        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            introduction.setUserId(currentUser.getId());
        }
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Introduction> list = introductionMapper.selectAll(introduction);

//        for (Introduction dbIntroduction : list) {
//            Integer categoryId =  dbIntroduction.getCategoryId();
//            Integer userId = dbIntroduction.getUserId();
//            Category category = categoryMapper.selectById(categoryId);
//            User user = userMapper.selectById(userId.toString());
//            if (ObjectUtil.isNotEmpty(category)) {
//                dbIntroduction.setCategoryTitle(category.getTitle());
//            }
//            if (ObjectUtil.isNotEmpty(user)) {
//                dbIntroduction.setUsername(user.getUsername());
//            }
//        }

        return PageInfo.of(list);
    }

    public void add(Introduction introduction) {
        Account currentUser = TokenUtils.getCurrentUser();
        introduction.setUserId(currentUser.getId());
        introduction.setTime(DateUtil.now());
        introductionMapper.insert(introduction);
    }

    public void deleteById(Integer id) {
        introductionMapper.deleteById(id);
    }


    public Introduction selectById(Integer id) {
        Introduction introduction = introductionMapper.selectById(id);
        Integer categoryId =  introduction.getCategoryId();
        Integer userId = introduction.getUserId();
        Category category = categoryMapper.selectById(categoryId);
        User user = userMapper.selectById(userId.toString());
        if (ObjectUtil.isNotEmpty(category)) {
            introduction.setCategoryTitle(category.getTitle());
        }
        if (ObjectUtil.isNotEmpty(user)) {
            introduction.setUsername(user.getUsername());
            introduction.setUserAvatar(user.getAvatar());
        }

        return introduction;
    }
}