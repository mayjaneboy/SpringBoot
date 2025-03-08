package com.example.service;

import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Resource
    CategoryMapper categoryMapper;
    /*`@Resource`是一个注解，用来进行依赖注入。
    依赖注入的意思是，Spring框架会自动找到合适的对象并赋值给这个变量，不需要手动创建。
    在这里，`@Resource`被用在`CategoryMapper categoryMapper`这个变量上，
    目的是让Spring把CategoryMapper接口的实现类自动注入到categoryMapper这个变量中。
    `CategoryMapper categoryMapper;` 声明了一个CategoryMapper类型的变量。\
    CategoryMapper是一个接口，本身不能直接实例化。
    但是在MyBatis中，通过动态代理机制，会自动生成这个接口的实现类，
    并且在运行时将这个实现类的实例注入到categoryMapper变量中。
    这样，就可以直接调用categoryMapper中的方法selectAll()，而无需关心具体的实现细节。*/


    public void update(Category category) {
        categoryMapper.updateById(category);
    }


    public List<Category> selectAll(Category category){
        return categoryMapper.selectAll(category);
    }

    public PageInfo<Category> selectPage(Integer pageNum, Integer pageSize,Category category) {
        //开启分页查询
        PageHelper.startPage(pageNum,pageSize);
        List<Category> categoryList = categoryMapper.selectAll(category);
        return PageInfo.of(categoryList);
        //categoryMapper.selectAll()把数据查询出来，并作为PageInfo.of()的参数分页过后（以一个对象的数据类型）返回
    }

    public void add(Category category) {
        categoryMapper.insert(category);
    }

    public void deleteById(Integer id) {
        categoryMapper.deleteById(id);
    }


}