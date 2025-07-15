import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: '/products',
        name: 'Products',
        component: () => import('../views/products/index.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('../views/orders/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('../views/users/index.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('adminToken')
  
  // 如果访问登录页，直接放行
  if (to.path === '/login') {
    if (token) {
      // 如果已登录，跳转到首页
      next('/')
    } else {
      next()
    }
    return
  }
  
  // 检查是否已登录
  if (!token) {
    // 未登录，跳转到登录页
    next('/login')
  } else {
    // 已登录，放行
    next()
  }
})

export default router 