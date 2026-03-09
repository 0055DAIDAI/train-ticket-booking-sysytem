import { createRouter, createWebHistory } from "vue-router";
import { useAdminStore } from '@/stores/admin'
import { useUserStore } from '@/stores/index'

const routes = [
  {
    path: '/',
    name: 'index',
    component: () => import('../views/Home.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'home',
        component: () => import('../views/home/index.vue'),
      },
      {
        path: '/order',
        name: 'order',
        component: () => import('../views/order/index.vue'),
      },
      {
        path: '/user',
        name: 'user',
        component: () => import('../views/user/index.vue'),
      },
      {
        path: '/info',
        name: 'info',
        component: () => import('../views/info/index.vue'),
      },

            // 管理端：
            {
        path: 'admin/schedule',
        name: 'admin-schedule',
        component: () => import('@/views/admin/schedule.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'admin/route',
        name: 'admin-route',
        component: () => import('@/views/admin/route.vue'),
        meta: { requiresAdmin: true }
      },
      {
  path: 'admin/order',
  name: 'admin-order',
  component: () => import('@/views/admin/order.vue'),
  meta: { requiresAdmin: true }
},
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/register/index.vue'),
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404/index.vue'),
  },

]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const adminStore = useAdminStore()
  const userStore = useUserStore()
  // 恢复缓存
  adminStore.loadFromStorage?.()
  userStore.loadFromStorage?.()

  // 需要管理员权限
  if (to.meta?.requiresAdmin) {
    if (adminStore.isLoggedIn) return next()
    return next('/login')
  }

  // 需要登录的普通页面（可选）
  if (to.meta?.requiresAuth) {
    if (userStore.id) return next()
    return next('/login')
  }

  next()
})


export default router