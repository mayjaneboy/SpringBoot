<template>
  <div>


    <div class="card" style="margin-bottom: 5px">
      <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.title" placeholder="请输入标题查询" :prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查 询</el-button>
      <el-button type="info" @click="reset">重 置</el-button>
    </div>
    <!--clearable是快速清除的那个叉号-->

    <div class="card" style="margin-bottom: 5px" v-if="data.user.role === 'ADMIN' ">
      <el-button type="primary" @click="handleAdd">新 增</el-button>
      <el-button type="danger" @click ="deleteBatch">批量删除</el-button>
      <el-button type="success" @click = "exportData">批量导出</el-button>
      <el-upload
          style="display: inline-block; margin-left: 10px"
          action="http://localhost:9999/user/import"
          :show-file-list="false"
          :on-success="handleImportSuccess"
      >
        <el-button type="success">批量导入</el-button>
      </el-upload>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange"
                :header-cell-style="{ color: '#333', backgroundColor: '#eaf4ff' }">

        <el-table-column prop="title" label="公告标题" />
        <el-table-column prop="content" label="公告内容" />
        <el-table-column prop="time" label="发布日期" />
        <el-table-column label="操作" width="200px">
          <template #default="scope">
            <el-button circle="true" icon="Edit" @click="handleEdit(scope.row)" v-if="data.user.role === 'ADMIN' "></el-button>
            <el-button circle="true" icon="Delete" @click="del(scope.row.id)" v-if="data.user.role === 'ADMIN' "></el-button>
            <el-button circle="true" icon="Edit" @click="show(scope.row)" v-if="data.user.role === 'USER' "></el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--属性data="data.tableData"应该就是把列表上要渲染的数据和这个Vue文件里的data.tableData进行了绑定
      tableData里的数据是从数据库中查询出来的数据，那数据库中每个字段的数据是如何和前端列表中的每一列相对应的呢
      前端列表中的每一列又为什么要和实体类Admin中的属性相对应-->
    </div>

    <div class="card">
      <el-pagination
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[2, 5, 10, 20]"
          :total="data.total"
          @current-change="load"
          @size-change="load"
      />
    </div>

    <el-dialog v-model="data.formVisible" title="公告信息" width="40%" destroy-on-close="true"><!--destroy-on-close="true"消除旧的表单验证-->
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding:20px 30px 10px 0">

        <el-form-item prop="title" label="公告标题">
          <el-input v-model="data.form.title" autocomplete="off" placeholder="请输入公告标题"/>
        </el-form-item>
        <el-form-item prop="content" label="公告内容">
          <el-input type="textarea" v-model="data.form.content" autocomplete="off" placeholder="请输入公告内容"/>
        </el-form-item>

      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>

    </el-dialog >

    <el-dialog v-model="data.showformVisible" title="公告信息" width="40%" destroy-on-close="true"><!--destroy-on-close="true"消除旧的表单验证-->
      <div>
        <h3>{{data.form.title}}</h3>
        <p>{{data.form.content}}</p>
      </div>
    </el-dialog >

  </div>
</template>

<script setup>
import {Search} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const formRef = ref()

const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}"),
  title:null,
  pageNum:1,
  pageSize:5,
  total:0,
  tableData:[],
  form:{},
  formVisible:false,
  showformVisible:false,
  rules: {
    title:[
      {required:true,message:"请填写标题",trigger:'blur'}  //必填
    ],
    content:[
      {required:true,message:"请填写正文",trigger:'blur'}  //必填
    ],
  }
})

const load = () =>{
  request.get('/notice/selectPage',{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      title:data.title,
    }
  }).then(res =>{
    if (res.code === '200'){
      data.tableData = res.data?.list
      data.total = res.data?.total
    } else {ElMessage.error(res.msg)}
  })
}
load()

const handleAdd = () =>{
  data.form = {}
  data.formVisible = true
}

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

const add = () => {
  request.post('/notice/add',data.form).then(res => {
    if (res.code === '200'){
      ElMessage.success("新增成功")
      data.formVisible = false
      load()
    }else {ElMessage.error(res.msg)}
  })
}

const update = () => {
  request.put('/notice/update',data.form).then(res => {
    if (res.code === '200'){
      ElMessage.success('更新成功')
      data.formVisible = false
      load()
    }else {ElMessage.error(res.msg)}
  })
}

const save = () => {
  formRef.value.validate(valid =>{
    if (valid){
      data.form.id ? update():add()
    }
  })
}

const show = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.showformVisible = true
}

const del = (id) => {
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/notice/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

const reset = () => {
  data.title = null
  load()
}
</script>