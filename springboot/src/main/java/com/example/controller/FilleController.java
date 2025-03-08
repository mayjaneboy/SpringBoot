package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.example.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.example.exception.CustomException;
import jakarta.servlet.ServletOutputStream;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//处理文件上传下载相关的接口
@RestController
@RequestMapping("/files")
public class FilleController {

    //文件上传
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 找到文件的位置
        String filePath = System.getProperty("user.dir") + "/files/"; //上传路径
        if (!FileUtil.isDirectory(filePath)) {FileUtil.mkdir(filePath);}//如果上传路径不是一个文件夹则创建该文件夹

        byte[] bytes = file.getBytes();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // 拿到文件的原始名称，和当前的毫秒数组成新的文件名（防止有两个相同名字的文件上传）

        FileUtil.writeBytes(bytes, filePath + fileName);// 写入文件

        String url = "http://localhost:9999/files/download/" + fileName;
        //上传之后返回文件的下载路径
        return Result.success(url);
    }


    //下载文件 http://localhost:9999/files/download/1.jpg
    @GetMapping("/download/{fileName}")//fileName是路径参数
    public void download(@PathVariable String fileName, HttpServletResponse response) throws Exception {
        //以流的形式下载，不需要返回Result对象，所以是函数返回类型是void  通过response把字符流数据写出去
        // 找到文件的位置
        String filePath = System.getProperty("user.dir") + "\\files\\";
        // 获取到当前项目code2025的绝对路径（C:\Users\mayja\Documents\小白做毕设\代码\code2025） 再加上  /files/
        String realPath = filePath + fileName;  //  fileName由前端传来

        boolean exist = FileUtil.exist(realPath);  //判断文件是否存在
        if (!exist) {throw new CustomException("文件不存在");}

        // 读取文件的字节流
        byte[] bytes = FileUtil.readBytes(realPath);

        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream");//以附件的形式去下载（一些简单文件会直接在浏览器里打开，这两行代码的作用就是直接下载并不打开）

        ServletOutputStream os = response.getOutputStream();
        // 输出流对象把文件写出到客户端(浏览器)
        os.write(bytes);
        os.flush();
        os.close();
    }

    /**
     * wang-editor编辑器文件上传接口
     */
    @PostMapping("/wang/upload")
    public Map<String, Object> wangEditorUpload(MultipartFile file) {
        String flag = System.currentTimeMillis() + "";
        String fileName = file.getOriginalFilename();
        try {
            String filePath = System.getProperty("user.dir") + "/files/";
            // 文件存储形式：时间戳-文件名
            FileUtil.writeBytes(file.getBytes(), filePath + flag + "-" + fileName);
            System.out.println(fileName + "--上传成功");
            Thread.sleep(1L);
        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }
        String http = "http://localhost:9999/files/download/";
        Map<String, Object> resMap = new HashMap<>();
        // wangEditor上传图片成功后， 需要返回的参数
        resMap.put("errno", 0);
        resMap.put("data", CollUtil.newArrayList(Dict.create().set("url", http + flag + "-" + fileName)));
        return resMap;
    }

}