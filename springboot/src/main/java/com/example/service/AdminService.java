package com.example.service;

import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Resource
    AdminMapper adminMapper;
    /*`@Resource`是一个注解，用来进行依赖注入。
    依赖注入的意思是，Spring框架会自动找到合适的对象并赋值给这个变量，不需要手动创建。
    在这里，`@Resource`被用在`AdminMapper adminMapper`这个变量上，
    目的是让Spring把AdminMapper接口的实现类自动注入到adminMapper这个变量中。
    `AdminMapper adminMapper;` 声明了一个AdminMapper类型的变量。\
    AdminMapper是一个接口，本身不能直接实例化。
    但是在MyBatis中，通过动态代理机制，会自动生成这个接口的实现类，
    并且在运行时将这个实现类的实例注入到adminMapper变量中。
    这样，就可以直接调用adminMapper中的方法selectAll()，而无需关心具体的实现细节。*/


    public void update(Admin admin) {
        adminMapper.updateById(admin);
    }

    public Admin selectById(String id) {
        return adminMapper.selectById(id);
    }
    public List<Admin> selectAll(Admin admin){
        return adminMapper.selectAll(admin);
    }

    public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize,Admin admin) {
        //开启分页查询
        PageHelper.startPage(pageNum,pageSize);
        List<Admin> adminList = adminMapper.selectAll(admin);
        return PageInfo.of(adminList);
        //adminMapper.selectAll()把数据查询出来，并作为PageInfo.of()的参数分页过后（以一个对象的数据类型）返回
    }

    public void add(Admin admin) {
        //根据新的账号查询数据库，是否存在同样账号的数据
        Admin dbAdmin= adminMapper.selectByUsername(admin.getUsername());
        if(dbAdmin!=null){
            throw new CustomException("账号重复");
        }
        //设置默认密码
        if (StrUtil.isBlank(admin.getPassword())) {
            admin.setPassword("admin");
        }
        admin.setRole("ADMIN");
        adminMapper.insert(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    public void deleteBatch(List<Admin> list) {
        for (Admin admin : list) {  //批量删除就是循环调用单个删除
            this.deleteById(admin.getId());
        }
    }

    public Admin login(Account account) {
        //验证账号是否存在
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if(dbAdmin == null){
            throw new CustomException("账号不存在");
        }
        //验证密码是否正确
        if (!dbAdmin.getPassword().equals(account.getPassword())) {
            throw new CustomException("账号或密码错误");
        }
        String token = TokenUtils.createToken(dbAdmin.getId()+"-"+"ADMIN",dbAdmin.getPassword());
        //创建token,并把#{id}-Admin和该管理员的密码作为参数传给TokenUtils.createToken(),该管理员的密码用于签名
        dbAdmin.setToken(token);//设置token

        return dbAdmin;
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
        Admin admin = adminMapper.selectById(currentUser.getId().toString());
        admin.setPassword(account.getNewPassword());
        adminMapper.updateById(admin);
    }

}