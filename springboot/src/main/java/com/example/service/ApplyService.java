package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Apply;
import com.example.mapper.ApplyMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplyService {

    @Resource
    ApplyMapper applyMapper;
    /*`@Resource`是一个注解，用来进行依赖注入。
    依赖注入的意思是，Spring框架会自动找到合适的对象并赋值给这个变量，不需要手动创建。
    在这里，`@Resource`被用在`ApplyMapper applyMapper`这个变量上，
    目的是让Spring把ApplyMapper接口的实现类自动注入到applyMapper这个变量中。
    `ApplyMapper applyMapper;` 声明了一个ApplyMapper类型的变量。\
    ApplyMapper是一个接口，本身不能直接实例化。
    但是在MyBatis中，通过动态代理机制，会自动生成这个接口的实现类，
    并且在运行时将这个实现类的实例注入到applyMapper变量中。
    这样，就可以直接调用applyMapper中的方法selectAll()，而无需关心具体的实现细节。*/


    public void update(Apply apply) {
        applyMapper.updateById(apply);
    }


    public List<Apply> selectAll(Apply apply){
        return applyMapper.selectAll(apply);
    }

    public PageInfo<Apply> selectPage(Integer pageNum, Integer pageSize,Apply apply) {
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())){
            apply.setUserId(currentUser.getId());
        }
        //开启分页查询
        PageHelper.startPage(pageNum,pageSize);
        List<Apply> applyList = applyMapper.selectAll(apply);
        return PageInfo.of(applyList);
        //applyMapper.selectAll()把数据查询出来，并作为PageInfo.of()的参数分页过后（以一个对象的数据类型）返回
    }

    public void add(Apply apply) {
        Account currentUser = TokenUtils.getCurrentUser();
        apply.setUserId(currentUser.getId());
        apply.setTime(DateUtil.now());
        apply.setStatus("待审核");
        applyMapper.insert(apply);
    }

    public void deleteById(Integer id) {
        applyMapper.deleteById(id);
    }


}