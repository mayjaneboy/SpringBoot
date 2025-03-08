<template>
  <div style="min-height: 1500px">
    <el-menu
        :default-active="activeIndex"
        class="el-menu-demo"
        mode="horizontal"
    >
      <el-menu-item index="1">系统首页</el-menu-item>
      <el-menu-item index="1" @click="logout">退出登录</el-menu-item>
    </el-menu>

    <div>
      <el-carousel height="350px">
        <el-carousel-item v-for="item in data.introductionData" :key="item" style="display: flex;justify-content: center;cursor:pointer" @click="navTo('/front/introductionDetail?id=' + item.id)">
          <img :src="item.img" alt="" style="height: 350px; ">
        </el-carousel-item>
      </el-carousel>
    </div>

    <div style="width: 80%;margin: 20px auto">
      <div style="font-size: 25px;border-left: 5px solid #2fb467;padding-left: 10px;height: 30px;line-height: 30px;margin-bottom: 20px;font-weight: bold">旅游攻略</div>
      <div>
        <el-row gutter="10">
        <el-col :span="6" v-for="item in data.introductionData"style="margin-bottom: 15px;">
          <img :src="item.img" alt="" style="width: 100%;height:310px;border-radius: 2px">
          <div style="font-size: 16px;font-weight:bold;margin-top: 3px;display: flex;">{{item.title}}</div>
          <div style="display: flex;align-items: center;margin-top: 3px;grid-gap: 10px">
            <img :src="item.userAvatar" alt="" style="width: 20px; height: 20px; border-radius: 50%">
            <div>{{item.username}}</div>
            <div>{{item.time}}</div>
          </div>
        </el-col>
        </el-row>
      </div>
    </div>

    <div style="width: 80%;margin: 20px auto">
      <div style="font-size: 25px;border-left: 5px solid #2fb467;padding-left: 10px;height: 30px;line-height: 30px;margin-bottom: 20px;font-weight: bold">旅游攻略</div>
      <div style="margin-top: 20px">
        <div style="display: flex;height: 300px;margin-top: 20px;grid-gap: 20px" v-for="item in data.introductionData">
          <div style="flex: 1;">
            <img :src="item.img" alt="" style="width: 100%;height: 300px;cursor: pointer" @click="navTo('/front/introductionDetail?id=' + item.id)">
          </div>
          <div style="flex: 3;">
            <div style="font-weight: bold;font-size: 20px;cursor: pointer">{{item.title}}</div>
            <div class="line9" style="margin-top: 13px;font-size: 16px;line-height: 25px">{{item.description}}</div>
            <div style="display: flex;align-items: center;margin-top: 3px;grid-gap: 10px">
              <img :src="item.userAvatar" alt="" style="width: 20px; height: 20px; border-radius: 50%">
              <div>{{item.username}}</div>
              <div>{{item.time}}</div>
            </div>
          </div>
        </div>
      </div>
    </div>


  </div>

</template>

<script setup>

import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import google from '@/assets/img/google.png';
import bing from '@/assets/img/bing.png';
import baidu from '@/assets/img/baidu.png';
import element from '@/assets/img/element.png';

//页面数据
const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}"),
  introductionData:[],
  carouselData:[google,bing,baidu,element,],
})

//退出当前登录
const logout = () => {
  localStorage.removeItem('code_user')
  location.href = '/login'
}

//拿到旅游攻略
const loadIntroduction =()=>{
  request.get('/introduction/selectAll').then(res=>{
    if (res.code === '200'){
      data.introductionData = res.data
    }else {
      ElMessage.error(res.msg)
    }
  })
}
loadIntroduction()

const navTo =(url)=>{
  location.href = url
}

</script>

<style>
.line9 {
  word-break: break-all;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 9;
  overflow: hidden;
}
</style>