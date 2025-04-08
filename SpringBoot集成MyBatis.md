**粘贴以下代码到pom.xml 里面的<dependencies>和</dependencies>之间，添加 Mysql 依赖 和 MyBatis依赖，并按右上角maven图标加载依赖Load Maven Changes**

**pom.xml定义了Springboot项目的所有依赖项，springboot项目加载的时候会扫描这个文件里面声明的所有依赖项，然后下载**

```
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>
```

**SpringBoot配置数据库：粘贴以下代码到application.yml（Springboot项目的配置文件）中**

```
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver  //这是数据库的驱动名称
    username: root
    password: '123456'  //这两个是本地数据库的账号密码，要根据自己的修改
    url: jdbc:mysql://localhost:3306/code2025?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2b8&allowPublicKeyRetrieval=true
    //这个url中的code2025是本地数据库的名称，要根据自己的修改
```

**SpringBoot配置MyBatis：粘贴以下代码到application.yml中**

```
# 配置mybatis实体和xml映射
mybatis:
  ## 映射xml
  mapper-locations: classpath:mapper/*.xml  //application.yml是在resources目录下的，所以classpath表示的是resources这个目录，也就是在这个目录下创建文件夹mapper，项目会扫描在mapper里创建的所有.xml文件
  configuration:
    # 配置日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true //把数据库中按照下划线格式的内容改成Java中的驼峰书写方式
```

**在resources文件夹下创建mapper文件夹**

**在com.example下创建文件夹entity，代表实体类，每一个实体类中的属性和数据库中一张数据表的字段是一一对应的，比如与管理员表对应的类Admin（下面都以Admin为例）:**

```java
package com.example.entity;

public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
}
```

**在com.example下创建mapper文件夹，在这个mapper下创建接口，以Admin为例就是新建一个名为AdminMapper.java的接口文件，注意要选接口选项而不是普通的Java类**

```
package com.example.mapper;

import com.example.entity.Admin;

import java.util.List;

public interface AdminMapper {
    List<Admin> selectAll();
    //事实上，是在接口AdminMapper.java中声明selectAll()方法，再在AdminMapper.xml实现此方法。
    //注意要Admin导包和List导包
}
```

**在SpringbootApplication.java(项目的启动类，不同项目可能名字不同)中扫描com.example.mapper文件夹下的接口，做法就是添加代码：**

```
@MapperScan("com.example.mapper")
//("")里的部分对应接口文件夹
```

![image-20250228162522170](C:\Users\mayja\AppData\Roaming\Typora\typora-user-images\image-20250228162522170.png)

**Idea下载插件MyBatisX**

**在resources/mapper/下创建.xml文件，以Admin为例，创建AdminMapper.xml文件**

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mapper.AdminMapper">
    <select id="selectAll" resultType="com.example.entity.Admin">
        select * from `admin` order by id desc
    </select>
</mapper>
```

**在AdminMapper.xml中，<mapper namespace=" ">，双引号内对应的是AdminMapper.java接口，所以是<mapper namespace="com.example.mapper.AdminMapper">**

**select标签中id属性对应接口文件中的方法名selectAll，resultType代表要返回的数据是实体类Admin中的属性。select标签中放相应的查询语句。**

**AdminMapper.java接口文件声明接口的方法，AdminMapper.xml文件中实现对应接口的方法。**

**到此，接口文件中的方法虽然拿到了数据，但还不能用。因为对外是通过Controller层传递数据**

**在service文件夹下对应的AdminService.java文件中定义对应的方法selectAll()：**

```
package com.example.service;

import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
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

    public String admin(String name){
        if("admin".equals(name)){
            return "admin";
        }else {
            throw new CustomException("账号错误");
        }
    }

    public List<Admin> selectAll(){
    //注意List和Admin导包
        return adminMapper.selectAll();
    }
}
```

**到这里service层拿到了数据，再传递到controller层**

**在controller文件夹下创建AdminController.java**

```
package com.example.controller;

import com.example.common.Result;
import com.example.entity.Admin;
import com.example.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/selectAll")  // 完整请求路径：  /admin/selectAll
    /*@RequestMapping是一个通用的注解，可以处理所有HTTP方法的请求，
    比如GET、POST、PUT、DELETE等,可以通过 method 参数显式指定支持的 HTTP 方法
    而`@GetMapping`是专门处理HTTP GET请求的注解。
    另外，`@RequestMapping`可以定义在类级别和方法级别，
    而`@GetMapping`通常只用于方法级别。*/
    public Result selectAll(){
        List<Admin> adminList = adminService.selectAll();
        return Result.success(adminList);
    }
}
```

