package com.example.service;

import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Resource
    UserMapper userMapper;
    /*`@Resource`是一个注解，用来进行依赖注入。
    依赖注入的意思是，Spring框架会自动找到合适的对象并赋值给这个变量，不需要手动创建。
    在这里，`@Resource`被用在`UserMapper userMapper`这个变量上，
    目的是让Spring把UserMapper接口的实现类自动注入到userMapper这个变量中。
    `UserMapper userMapper;` 声明了一个UserMapper类型的变量。\
    UserMapper是一个接口，本身不能直接实例化。
    但是在MyBatis中，通过动态代理机制，会自动生成这个接口的实现类，
    并且在运行时将这个实现类的实例注入到userMapper变量中。
    这样，就可以直接调用userMapper中的方法selectAll()，而无需关心具体的实现细节。*/


    public void update(User user) {
        userMapper.updateById(user);
    }

    public User selectById(String id) {
        return userMapper.selectById(id);
    }

    public List<User> selectAll(User user){
        return userMapper.selectAll(user);
    }

    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize,User user) {
        //开启分页查询
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList = userMapper.selectAll(user);
        return PageInfo.of(userList);
        //userMapper.selectAll()把数据查询出来，并作为PageInfo.of()的参数分页过后（以一个对象的数据类型）返回
    }

    public void add(User user) {
        //根据新的账号查询数据库，是否存在同样账号的数据
        User dbUser= userMapper.selectByUsername(user.getUsername());
        if(dbUser!=null){
            throw new CustomException("账号重复");
        }
        //设置默认密码
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword("123456");
        }
        if (StrUtil.isBlank(user.getName())) {
            user.setName(user.getUsername());
        }

        user.setRole("USER");
        userMapper.insert(user);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<User> list) {
        for (User user : list) {  //批量删除就是循环调用单个删除
            this.deleteById(user.getId());
        }
    }

    public User login(Account account) {
        //验证账号是否存在
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if(dbUser == null){
            throw new CustomException("账号不存在");
        }
        //验证密码是否正确
        if (!dbUser.getPassword().equals(account.getPassword())) {
            throw new CustomException("账号或密码错误");
        }

        String token = TokenUtils.createToken(dbUser.getId()+"-"+"USER",dbUser.getPassword());
        //创建token,并把#{id}-Admin和该管理员的密码作为参数传给TokenUtils.createToken(),该管理员的密码用于签名
        dbUser.setToken(token);//设置token
        return dbUser;
    }

    public void register(User user) {
        this.add(user);
    }


    public void updatePassword(Account account) {
        //判断新密码和旧密码是否一致
        if (!account.getNewPassword().equals(account.getConfirmPassword())) { throw new CustomException("500","两次输入的密码不一致"); }
        //校验原密码是否正确
        Account currentUser = TokenUtils.getCurrentUser();
        if (!account.getPassword().equals(currentUser.getPassword())) {
            throw new CustomException("500","原密码输入错误");
        }
        //开始更新密码
        User user = userMapper.selectById(currentUser.getId().toString());
        user.setPassword(account.getNewPassword());
        userMapper.updateById(user);
    }
}