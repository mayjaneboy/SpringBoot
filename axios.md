**下载：在Vue项目的命令行下**

```
npm i axios -S
```

**用法：**' '内是路由

```
axios.get('http://localhost:9999/admin/selectAll').then(res => {
  console.log(res)
})
```

控制台出现如下，表明存在跨域问题：前端使用的端口是5173，后端的是9999，不能直接访问对方

```
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

**跨域问题解决方法：**在com.example/common文件夹下新建CorsConfig类，粘贴如下代码

```
package com.example.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 1 设置访问源地址
        corsConfiguration.addAllowedHeader("*"); // 2 设置访问源请求头
        corsConfiguration.addAllowedMethod("*"); // 3 设置访问源请求方法
        source.registerCorsConfiguration("/**", corsConfiguration); // 4 对接口配置跨域设置
        return new CorsFilter(source);
    }
}
```

这段代码的作用就是允许所有端口彼此访问

重启后端项目



```
axios.get('http://localhost:9999/admin/selectAll').then(res => {
  console.log(res)
})  //这种用法太过麻烦，可以采用如下方法简化
```

**封装统一的请求工具request.js：**

在vue/src/下新建目录utils，在utils下新建request.js文件，粘贴如下代码

```
import axios from "axios"; //引入axios
import {ElMessage} from "element-plus"; //引入element-plus弹窗提示组件

const request = axios.create({  //通过axios创建一个request对象
  baseURL: 'http://localhost:9999',  //设置统一地址
  timeout: 30000  // 设置后台接口超时时间 30000ms=30s
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
  config.headers['Content-Type'] = 'application/json;charset=utf-8';  //设置统一的请求头application/json，统一字符编码utf-8
  return config
}, error => {
  return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
  response => {
    let res = response.data;//首先拿到返回的数据data
    // 兼容服务端返回的字符串数据
    if (typeof res === 'string') {
      res = res ? JSON.parse(res) : res
    }  //数据类型转化：string转json
    return res;
  },
  error => {
    if (error.response.status === 404) {
      ElMessage.error('未找到请求接口')
    } else if (error.response.status === 500) {
      ElMessage.error('系统异常，请查看后端控制台报错')
    } else {
      console.error(error.message)
    }
    return Promise.reject(error)
  }
)

export default request //返回request
```

这样在.vue文件里就可以使用request对象发起请求访问后端，这种方法返回的是 对象object 类型

```
request.get('/admin/selectAll').then(res => {
  console.log(res)
})  //注意要导入request包 ：import request from "@/utils/request.js";
```

**用后端数据来渲染前端界面：**

在一个前端界面对应的anyController层文件中定义接口

```java
@GetMapping("/selectPage")
public Result selectPage(){   //接口名（selectPage）可以带有参数，同时service服务层中的的方法也要带有参数
    return Result.success(anyService.selectPage());  //Result.success()是当前端访问接口请求成功时，用来返回数据的包装类的对象（Result是专门用来向前端返回数据的包装类，其属性有请求状态码，请求失败时的返回信息和实际要用的数据，success()方法用于在请求成功时包装数据到Result类的对象中，并返回此对象。有多个重载）。在返回语句之前调用Service层的方法按照接口要实现的功能来获取目标数据。
    //Service层的方法从Mapper层获取数据，并对数据进行加工处理，其返回的数据类型取决于接口的功能要求。Mapper层实现对数据库中的数据的增删改查等操作。
}
```

