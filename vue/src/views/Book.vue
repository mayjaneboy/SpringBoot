<template>
  <div>


    <div class="card" style="margin-bottom: 5px">
      <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.name" placeholder="请输入标题查询" :prefix-icon="Search"></el-input>
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

        <el-table-column type="selection" width="55" />
        <el-table-column label="封面" width="100px">
          <template #default="scope">
            <el-image v-if="scope.row.img" :src="scope.row.img" :preview-src-list="[scope.row.img]" :preview-teleported="true"
                      style="width: 40px; height: 40px; border-radius: 5px; display: block" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名字" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="num" label="余量" />

        <el-table-column label="操作" width="200px">
          <template #default="scope">
            <el-button circle="true" icon="Edit" v-if="data.user.role === 'ADMIN' " @click="handleEdit(scope.row)"></el-button>
            <el-button circle="true" icon="Delete" v-if="data.user.role === 'ADMIN' " @click="del(scope.row.id)"></el-button>
            <el-button circle="true" icon="Edit" v-if="data.user.role === 'USER' " @click="show(scope.row)"></el-button>
            <el-button type="primary"  v-if="data.user.role === 'USER' " @click="borrow(scope.row)">借阅</el-button>
          </template>
        </el-table-column>
      </el-table>
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

    <el-dialog v-model="data.formVisible" title="图书信息" width="40%" destroy-on-close="true"><!--destroy-on-close="true"消除旧的表单验证-->
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px" style="padding:20px 30px 10px 0">

        <el-form-item prop="name" label="图书名字">
          <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入图书名字"/>
        </el-form-item>
        <el-form-item prop="author" label="图书作者">
          <el-input v-model="data.form.author" autocomplete="off" placeholder="请输入图书作者"/>
        </el-form-item>
        <el-form-item prop="price" label="图书价格">
          <el-input v-model="data.form.price" autocomplete="off" placeholder="请输入图书价格"/>
        </el-form-item>
        <el-form-item prop="num" label="图书余量">
          <el-input v-model="data.form.num" autocomplete="off" placeholder="请输入图书余量"/>
        </el-form-item>
        <el-form-item prop="img" label="图书封面">
          <el-upload
              action="http://localhost:9999/files/upload"
              :headers="{ token: data.user.token }"
              :on-success="handleFileSuccess"
              list-type="picture"
          >
            <el-button type="primary">上传封面</el-button>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>

    </el-dialog >

    <el-dialog v-model="data.showformVisible" title="图书信息" width="40%" destroy-on-close="true"><!--destroy-on-close="true"消除旧的表单验证-->
      <div>
        <h3>{{data.form.name}}</h3>
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
  name:null,
  pageNum:1,
  pageSize:5,
  total:0,
  tableData:[],
  form:{},
  formVisible:false,
  showformVisible:false,
  rules: {
    img:[
      {required:true,message:"请上传封面",trigger:'blur'}  //必填
    ],
    name:[
      {required:true,message:"请填写图书名字",trigger:'blur'}  //必填
    ],
    author:[
      {required:true,message:"请填写图书作者",trigger:'blur'}  //必填
    ],
    price:[
      {required:true,message:"请填写图书价格",trigger:'blur'}  //必填
    ],
    num:[
      {required:true,message:"请填写图书余量",trigger:'blur'}  //必填
    ],
  }
})

const load = () =>{
  request.get('/book/selectPage',{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
      name:data.name,
    }
  }).then(res =>{
    if (res.code === '200'){
      data.tableData = res.data?.list
      data.total = res.data?.total
    } else {ElMessage.error(res.msg)}
  })
}
load()

const handleFileSuccess = (res) => {
  data.form.img = res.data
}

const handleAdd = () =>{
  data.form = {}
  data.formVisible = true
}

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

const add = () => {
  request.post('/book/add',data.form).then(res => {
    if (res.code === '200'){
      ElMessage.success("新增成功")
      data.formVisible = false
      load()
    }else {ElMessage.error(res.msg)}
  })
}

const update = () => {
  request.put('/book/update',data.form).then(res => {
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
    request.delete('/book/delete/' + id).then(res => {
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
  data.name = null
  load()
}

const borrow =(row)=>{
  request.post('/record/add',{
    userId: data.user.id,
    bookId: row.id
  }).then(res =>{
    if (res.code === '200'){
      ElMessage.success('操作成功，等待管理员审核')
      load()
    }else {ElMessage.error(res.msg)}
  })
}
</script>