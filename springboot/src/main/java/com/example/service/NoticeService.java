package com.example.service;

import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Resource
    NoticeMapper noticeMapper;
    /*`@Resource`是一个注解，用来进行依赖注入。
    依赖注入的意思是，Spring框架会自动找到合适的对象并赋值给这个变量，不需要手动创建。
    在这里，`@Resource`被用在`NoticeMapper noticeMapper`这个变量上，
    目的是让Spring把NoticeMapper接口的实现类自动注入到noticeMapper这个变量中。
    `NoticeMapper noticeMapper;` 声明了一个NoticeMapper类型的变量。\
    NoticeMapper是一个接口，本身不能直接实例化。
    但是在MyBatis中，通过动态代理机制，会自动生成这个接口的实现类，
    并且在运行时将这个实现类的实例注入到noticeMapper变量中。
    这样，就可以直接调用noticeMapper中的方法selectAll()，而无需关心具体的实现细节。*/


    public void update(Notice notice) {
        noticeMapper.updateById(notice);
    }


    public List<Notice> selectAll(Notice notice){
        return noticeMapper.selectAll(notice);
    }

    public PageInfo<Notice> selectPage(Integer pageNum, Integer pageSize,Notice notice) {
        //开启分页查询
        PageHelper.startPage(pageNum,pageSize);
        List<Notice> noticeList = noticeMapper.selectAll(notice);
        return PageInfo.of(noticeList);
        //noticeMapper.selectAll()把数据查询出来，并作为PageInfo.of()的参数分页过后（以一个对象的数据类型）返回
    }

    public void add(Notice notice) {
        noticeMapper.insert(notice);
    }

    public void deleteById(Integer id) {
        noticeMapper.deleteById(id);
    }


}