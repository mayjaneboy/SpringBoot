<template>
<div>
  <div class="card">
    Hello Graduate!
  </div>

  <div class="card" style="margin-top: 10px" v-if="data.user.role === 'USER'">
    <div style="font-size: 20px;margin-bottom: 20px">系统公告</div>
    <el-timeline style="max-width: 600px">
      <el-timeline-item :timestamp="item.time" placement="top" v-for = "item in data.noticeData">
        <el-card>
          <h4>{{item.title}}</h4>
          <p>{{ item.content }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>

  <div v-else style="margin-top: 10px;display:flex;grid-gap: 10px">
    <div class="card" style="height: 400px;width: 50%" id="pie"></div>
    <div class="card" style="height: 400px;width: 50%" id="bar"></div>
  </div>
  <div class="card" style="height: 400px;width: 100%;margin-top: 10px" id="line"></div>
</div>
</template>

<script setup>

import {reactive,onMounted} from "vue";
import {ElMessage} from "element-plus";
import request from "@/utils/request.js";
import * as echarts from "echarts";  //引入echarts

const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}"),
  noticeData:[]
})

const loadNotice = () => {
  request.get('/notice/selectAll').then(res =>{
    if (res.code ==='200'){
      data.noticeData = res.data
    if (data.noticeData.length>3){data.noticeData = data.noticeData.slice(0,3)}  //阶段多余的系统公告
    }
    else {ElMessage.error(res.msg)}
  })
}
loadNotice()

const loadPie = () => {
  request.get('/echarts/pie').then(res =>{
    if (res.code === '200'){
      var chartDom = document.getElementById('pie');
      var myChart = echarts.init(chartDom);
      pieOptions.series[0].data = res.data
      myChart.setOption(pieOptions);
    }
  })
}
onMounted(() =>{
  loadPie()
  loadBar()
  loadLine()
})

// 饼图
let pieOptions = {
  title: {
    text: '不同分类下用户发布旅游攻略帖子的数量', // 主标题
    subtext: '统计维度：攻略分类', // 副标题
    left: 'center'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '数量占比', // 鼠标移上去显示内容
      type: 'pie',
      radius: '50%',
      center: ['50%', '60%'],
      data: [
        {value: 1048, name: '瑞幸咖啡'}, // 示例数据：name表示维度，value表示对应的值
        {value: 735, name: '雀巢咖啡'},
        {value: 580, name: '星巴克咖啡'},
        {value: 484, name: '栖巢咖啡'},
      ]
    }
  ]
}

const loadBar =()=>{
  request.get('/echarts/bar').then(res =>{
    if (res.code === '200'){
      let chartDom = document.getElementById('bar');
      let myChart = echarts.init(chartDom);
      barOptions.xAxis.data = res.data.xList
      barOptions.series[0].data = res.data.vList
      myChart.setOption(barOptions);
    }
  })
}

const loadLine =()=>{
  request.get('/echarts/line').then(res =>{
    if (res.code === '200'){
      let chartDom = document.getElementById('line');
      let myChart = echarts.init(chartDom);
      lineOptions.xAxis.data = res.data.xList
      lineOptions.series[0].data = res.data.vList
      myChart.setOption(lineOptions);
    }
  })
}

// 柱状图
let barOptions = {
  title: {
    text: '不同用户发布帖子数量Top5', subtext: '统计维度：用户昵称', // 主副标题
    left: 'center'
  },
  grid : {   // ---------------------------增加这个属性，bottom就是距离底部的距离
    top: '20%',
    bottom : '20%'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'], // 示例数据：统计的维度（横坐标）
    name: '用户昵称',
    axisLabel: {
      show: true, // 是否显示刻度标签，默认显示
      interval: 0, // 坐标轴刻度标签的显示间隔，在类目轴中有效；默认会采用标签不重叠的策略间隔显示标签；可以设置成0强制显示所有标签；如果设置为1，表示『隔一个标签显示一个标签』，如果值为2，表示隔两个标签显示一个标签，以此类推。
      rotate: -60, // 刻度标签旋转的角度，在类目轴的类目标签显示不下的时候可以通过旋转防止标签之间重叠；旋转的角度从-90度到90度
      inside: false, // 刻度标签是否朝内，默认朝外
      margin: 6, // 刻度标签与轴线之间的距离
    },
  },
  yAxis: {
    type: 'value',
    name: '攻略数量',
  },
  tooltip: {
    trigger: 'item',
  },
  series: [
    {
      data: [120, 200, 150, 80, 70, 110, 130], // 示例数据：横坐标维度对应的值（纵坐标）
      type: 'bar',
      itemStyle: {
        normal: {
          color: function () {
            return "#" + Math.floor(Math.random() * (256 * 256 * 256 - 1)).toString(16);
          }
        },
      },
    }
  ]
};
// 平滑折线图
let lineOptions = {
  title: {
    text: '最近一周每天平台用户发布的帖子数量',
    subtext: '统计维度：最近一周',
    left: 'center'
  },
  legend: {
    data: [],
    template:""
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  tooltip: {
    trigger: 'item'
  },
  xAxis: {
    name: '日期',
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    name: '攻略数量',
    type: 'value'
  },
  series: [
    {
      name: '攻略数量',
      data: [820, 932, 901, 934, 1290, 1330, 1320],
      type: 'line',
      smooth: true,
      markLine: {
        data: [{ type: 'average', name: '最近一周场攻略发布数量平均值' }]
      },
      markPoint: {
        data: [
          { type: 'max', name: '最大值' },
          { type: 'min', name: '最小值' }
        ]
      },
    },
  ]
};
</script>