import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
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

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 