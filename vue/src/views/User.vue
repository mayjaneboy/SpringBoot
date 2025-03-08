<template>
  <div>

    <div class="card" style="margin-bottom: 5px">
      <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.username" placeholder="请输入账号查询" :prefix-icon="Search"></el-input>
      <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.name" placeholder="请输入名称查询" :prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查 询</el-button>
      <el-button type="info" @click="reset">重 置</el-button>
    </div>
    <!--clearable是快速清除的那个叉号-->

    <div class="card" style="margin-bottom: 5px">
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
        <el-table-column type="selection" width="55" />
        <el-table-column label="头像" width="100px">
          <template #default="scope">
            <el-image v-if="scope.row.avatar" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]" :preview-teleported="true"
                      style="width: 40px; height: 40px; border-radius: 50%; display: block" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="账号" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button circle="true" icon="Edit" @click="handleEdit(scope.row)"></el-button>
            <el-button circle="true" icon="Delete" @click="del(scope.row.id)"></el-button>
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
      <!--@current-change是分页组件的一个事件，当前端界面分页时触发此事件，并调用“load”方法，而load方法会访问接口selectPage
      将新的页码和其他一些数据作为Controller层里的接口方法selectPage的参数，
      然后调用service层的方法，再调用Mapper层的方法查询新的数据返回到对象res，
      然后根据res更新前端的数据data并渲染出来
      -->
      <!--@size-change同理-->
    </div>

    <el-dialog v-model="data.formVisible" title="用户信息" width="40%" destroy-on-close="true"><!--destroy-on-close="true"消除旧的表单验证-->
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding:20px 30px 10px 0">
        <el-form-item prop="username" label="账号">
          <el-input v-model="data.form.username" autocomplete="off" placeholder="请输入账号"/>
        </el-form-item>
        <el-form-item prop="name" label="名称">
          <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称"/>
        </el-form-item>
        <el-form-item prop="phone" label="电话">
          <el-input v-model="data.form.phone" autocomplete="off" placeholder="请输入电话"/>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="data.form.email" autocomplete="off" placeholder="请输入邮箱"/>
        </el-form-item>
        <el-form-item prop="avatar" label="头像">
          <el-upload
              action="http://localhost:9999/files/upload"
              :headers="{ token: data.user.token }"
              :on-success="handleFileSuccess"
              list-type="picture"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog>


  </div>
</template>

<script setup>
import { reactive,ref } from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
  user:JSON.parse(localStorage.getItem('code_user') || '{}'),
  username: '',
  name: '',
  pageNum: 1,
  pageSize: 5,
  total: 0,
  tableData: [],
  formVisible:false,
  form:{},  //form是一个对象
  rules:{
    username:[
      {required:true,message:"请填写账号",trigger:'blur'}  //必填
    ],
    name:[
      {required:true,message:"请填写名称",trigger:'blur'}  //必填
    ],
    phone:[
      {required:true,message:"请填写手机号",trigger:'blur'}  //必填
    ],
    email:[
      {required:true,message:"请填写邮箱",trigger:'blur'}  //必填
    ],
  },
  rows:[],
  ids:[]
})

const formRef = ref()

const load = () => {
  request.get('/user/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      username: data.username,
      name: data.name
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data.list
      data.total = res.data.total
    } else {
      ElMessage.error(res.msg)
    }
  })
}
load()

const reset = () => {
  data.username = null
  data.name = null
  load()
}

const handleAdd =() => {
  data.form={}
  data.formVisible = true
}

const add = () =>{
  //  formRef是对表单的引用
  formRef.value.validate((valid) => {
    if (valid) {  //表单验证通过时
      request.post('user/add',data.form).then(res =>{
        if (res.code === '200') {
          data.formVisible = false
          ElMessage.success('新增成功')
          load()
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const handleEdit = (row) =>{
  data.form = JSON.parse(JSON.stringify(row))  //深度拷贝数据 先把row转换成字符串，再转换成JSON 使得在修改form时row的数据不会被修改
  data.formVisible = true
}

const update = () =>{
  //  formRef是对表单的引用
  formRef.value.validate((valid) => {
    if (valid) {  //表单验证通过时
      request.put('user/update',data.form).then(res =>{
        if (res.code === '200') {
          data.formVisible = false
          ElMessage.success('修改成功')
          load()
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const save =() =>{
  data.form.id?update():add()
}

const del = (id) => {
  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/user/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

const handleSelectionChange =(rows)=>{  //rows实际选择的数组
  data.rows = rows
  data.ids = data.rows.map(v => v.id)  //map可以把对象的数组转换成一个纯数字的数组
}

const deleteBatch =()=>{
  if(data.rows.length === 0){
    ElMessage.warning('请选择数据')
    return
  }

  ElMessageBox.confirm('删除后无法恢复，您确认删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/user/deleteBatch',{data:data.rows}).then(res => {
      if (res.code === '200') {
        ElMessage.success('批量删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

const exportData = () => {
  let idsStr = data.ids.join(",")  // 把数组转换成  字符串  [1,2,3]  ->  "1,2,3"
  let url = `http://localhost:9999/user/export?username=${data.username === null ? '' : data.username}`
      + `&name=${data.name === null ? '' : data.name}`+ `&ids=${idsStr}`
      + `&token=${data.user.token}`  //需要拼上token才能导出数据
  window.open(url)
}

const handleImportSuccess = (res) => {
  if (res.code === '200') {
    ElMessage.success('批量导入数据成功')
    load()
  } else {
    ElMessage.error(res.msg)
  }
}

const handleFileSuccess = (res) => {
  data.form.avatar = res.data
}
</script>