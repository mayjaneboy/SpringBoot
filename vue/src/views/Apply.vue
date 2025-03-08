<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.title" placeholder="请输入标题查询" :prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查 询</el-button>
      <el-button type="info" @click="reset">重 置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" @click="handleAdd" v-if="data.user.role === 'USER'">提交申请</el-button>
    </div>

    <el-dialog v-model="data.formVisible" title="请假信息" width="40%" destroy-on-close="true"><!--destroy-on-close="true"消除旧的表单验证-->
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding:20px 30px 10px 0">
        <el-form-item prop="title" label="标题" v-if="data.user.role === 'USER'">
          <el-input v-model="data.form.title" autocomplete="off" placeholder="请输入请假标题"/>
        </el-form-item>
        <el-form-item prop="content" label="请假内容" v-if="data.user.role === 'USER'">
          <el-input type="textarea" rows="10" v-model="data.form.content" autocomplete="off" placeholder="请输入请假内容"/>
        </el-form-item>
        <el-form-item prop="status" label="审核状态" v-if="data.user.role === 'ADMIN'">
          <el-radio-group v-model="data.form.status">
            <el-radio-button label="待审核" value="待审核" />
            <el-radio-button label="待议" value="待议" />
            <el-radio-button label="审核通过" value="审核通过" />
            <el-radio-button label="审核拒绝" value="审核拒绝" />
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="reason" label="审核说明" v-if="data.user.role === 'ADMIN' && data.form.status === '审核拒绝'">
          <el-input v-model="data.form.reason" autocomplete="off" placeholder="请输入拒绝说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog >

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange"
                :header-cell-style="{ color: '#333', backgroundColor: '#eaf4ff' }">

        <el-table-column prop="title" label="请假标题" />
        <el-table-column prop="content" label="请假说明" />
        <el-table-column prop="userName" label="请假人" />
        <el-table-column prop="time" label="申请时间" />
        <el-table-column prop="status" label="审核状态">
          <template #default="scope">
            <el-tag type="primary" v-if="scope.row.status === '待审核'">{{ scope.row.status }}</el-tag>
            <el-tag type="warning" v-if="scope.row.status === '待议'">{{ scope.row.status }}</el-tag>
            <el-tag type="success" v-if="scope.row.status === '审核通过'">{{ scope.row.status }}</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '审核拒绝'">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="拒绝原因" />
        <el-table-column label="操作" width="200px">
          <template #default="scope">
            <el-button :disabled="scope.row.status !== '待审核'" v-if="data.user.role === 'USER'" circle="true" icon="Edit" @click="handleEdit(scope.row)"></el-button>
            <el-button :disabled="scope.row.status !== '待审核'" v-if="data.user.role === 'USER'" circle="true" icon="Delete" @click="del(scope.row.id)"></el-button>
            <el-button circle="true" icon="Edit" @click="show(scope.row)"></el-button>
          </template>
          <template #default="scope" v-if="data.user.role === 'ADMIN'">
            <el-button :disabled="scope.row.status !== '待审核'" type="primary" @click="handleEdit(scope.row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="data.showformVisible" title="申请信息" width="40%" destroy-on-close="true"><!--destroy-on-close="true"消除旧的表单验证-->
      <div>
        <h3>{{data.form.title}}</h3>
        <p>{{data.form.content}}</p>
      </div>
    </el-dialog >

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

  </div>
</template>

<script setup>

import {Search} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('code_user') || "{}"),
  pageNum:1,
  pageSize:5,
  total:0,
  title:null,
  tableData:[],
  formVisible:false,
  form:{},
  showformVisible:false,
  rules:{
    title:[
      {required:true,message:"请填写标题",trigger:'blur'}  //必填
    ],
    content:[
      {required:true,message:"请填写内容",trigger:'blur'}  //必填
    ],
  },
})
const formRef = ref()

const load =()=>{
  request.get('/apply/selectPage',{
    params:{
      pageNum:data.pageNum,pageSize:data.pageSize,
      title:data.title
    }
  }).then(res => {
    if (res.code === '200'){
      data.tableData = res.data?.list
      data.total = res.data?.total
    }else {
      ElMessage.error(res.msg)
    }
  })
}
load()

const reset = ()=>{
  data.title = null
  load()
}

const handleAdd =()=>{
  data.form = {}
  data.formVisible = true
}

const handleEdit = (row) =>{
  data.form = row
  data.formVisible = true
}

const show = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.showformVisible = true
}

const add=()=>{
  request.post('/apply/add',data.form).then(res=>{
    if (res.code === '200'){
     ElMessage.success("提交成功，等待管理员审核")
    load()
    }else {
      ElMessage.success(res.data)
    }
  })
  data.formVisible = false
}

const update =()=>{
  request.put('/apply/update',data.form).then(res =>{
    if (res.code === '200'){
      ElMessage.success('更新成功')
      data.formVisible = false
      load()
    }else {
      ElMessage.error(res.msg)
    }
  })
}

const save=()=>{
  formRef.value.validate(valid=>{
    if (valid){
      data.form.id?update():add()
    }
  })
}

const del = (id) => { // 这里增加参数 id
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？', '删除确认', { type: 'warning' })
      .then(() => {
        return request.delete('/apply/delete/' + id)
      })
      .then(res => {
        if (res.code === '200') {
          ElMessage.success('删除成功')
          load() // 重新加载数据
        } else {
          ElMessage.error(res.msg)
        }
      })
      .catch(() => {
        // 用户取消删除，不需要做任何操作
      })
}
</script>