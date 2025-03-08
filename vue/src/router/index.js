import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/',redirect: '/manager/home'},
    {path: '/manager',component: ()=>import('../views/Manager.vue'),
      children:[
        { path: 'home',meta:{name:'主页'},component:()=> import('../views/Home.vue'),},
        { path: 'admin',meta:{name:'管理员信息'},component:()=> import('../views/Admin.vue'),},
        { path: 'user',meta:{name:'用户信息'},component:()=> import('../views/User.vue'),},
        { path: 'person',meta:{name:'个人信息'},component:()=> import('../views/Person.vue'),},
        { path: 'UpdatePassword',meta:{name:'修改密码'},component:()=> import('../views/UpdatePassword.vue'),},
        { path: 'notice',meta:{name:'系统公告'},component:()=> import('../views/notice.vue'),},
        { path: 'introduction',meta:{name:'旅游攻略'},component:()=> import('../views/introduction.vue'),},
        { path: 'category',meta:{name:'攻略分类'},component:()=> import('../views/Category.vue'),},
        { path: 'apply',meta:{name:'请假申请'},component:()=> import('../views/apply.vue'),},
        { path: 'book',meta:{name:'图书预约'},component:()=> import('../views/book.vue'),},
        { path: 'record',meta:{name:'借阅记录'},component:()=> import('../views/record.vue'),},
      ]
    },
    {path: '/front/home',component: ()=>import('../views/Front.vue'),},
    {path: '/front/introductionDetail',component: ()=>import('../views/introductionDetail.vue'),},
    {path: '/login',component: ()=>import('../views/login.vue'),},
    {path: '/register',component: ()=>import('../views/Register.vue'),},
    {path: '/notFound',component: ()=>import('../views/404.vue'),},
    {path: '/:pathMatch(.*)',redirect:'/notFound'},
  ],
})

export default router