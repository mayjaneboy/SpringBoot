package com.example.exception;

//CustomException是自定义异常，用于处理全局异常不会捕获，但我们又希望去报错处理的情况
//自定义异常肯定是运行时出现的情况，所以可以通过继承RuntimeException来使这个类是一个用来处理异常的类
public class CustomException extends RuntimeException{
    private String msg;
    private String code;

    public CustomException(String code,String msg) {
        this.code=code;
        this.msg=msg;
    }//构造器
    public CustomException(String msg){
        this.msg=msg;
        this.code="500";
    }
    public CustomException(){

    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}