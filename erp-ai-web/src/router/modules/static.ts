import type { RouteRecordRaw } from 'vue-router'
import { REDIRECT_ROUTE } from './redirect'

// 登录页 - 无需鉴权，独立布局
export const LOGIN_ROUTE: RouteRecordRaw = {
  path: '/login',
  name: 'Login',
  component: () => import('@/views/login/index.vue'),
  meta: { title: '登录', titleI18n: 'login.title', hideMenu: true, hideTab: true }
}

// 根路由 - 重定向到首页
export const ROOT_ROUTE: RouteRecordRaw = {
  path: '/',
  name: 'Root',
  redirect: '/home',
  meta: { title: '根路径', hideMenu: true, hideTab: true }
}

// 首页 - 使用AdminLayout布局
export const HOME_ROUTE: RouteRecordRaw = {
  path: '/home',
  name: 'Home',
  component: () => import('@/layouts/AppLayout.vue'),
  children: [
    {
      path: '',
      name: 'HomePage',
      component: () => import('@/views/home/index.vue'),
      meta: {
        title: '首页',
        titleI18n: 'home.title',
        icon: 'HomeFilled',
        affix: true,
        keepAlive: true
      }
    }
  ]
}

// 404页面
export const ERROR_404: RouteRecordRaw = {
  path: '/404',
  name: 'Error404',
  component: () => import('@/views/error/404.vue'),
  meta: { title: '404', hideMenu: true, hideTab: true }
}

// 403页面
export const ERROR_403: RouteRecordRaw = {
  path: '/403',
  name: 'Error403',
  component: () => import('@/views/error/403.vue'),
  meta: { title: '403', hideMenu: true, hideTab: true }
}

// 无权限页面
export const NO_PERMISSION: RouteRecordRaw = {
  path: '/no-permission',
  name: 'NoPermission',
  component: () => import('@/views/error/no-permission.vue'),
  meta: { title: '无权限', hideMenu: true, hideTab: true }
}

// 开发调试页面
export const DEV_VIRTUAL_SCROLL: RouteRecordRaw = {
  path: '/dev/virtual-scroll',
  name: 'DevVirtualScroll',
  component: () => import('@/views/dev/virtual-scroll-demo.vue'),
  meta: { title: '虚拟滚动验证', hideMenu: false, hideTab: false }
}

// 用户管理工作台
export const USER_WORKBENCH: RouteRecordRaw = {
  path: '/user/workbench',
  name: 'UserWorkbench',
  component: () => import('@/views/user/workbench/index.vue'),
  meta: { title: '用户管理工作台', icon: 'DataBoard', keepAlive: true }
}

// 权限配置工作台
export const AUTH_CONFIG_WORKBENCH: RouteRecordRaw = {
  path: '/auth/config/workbench',
  name: 'AuthConfigWorkbench',
  component: () => import('@/views/auth/config/workbench/index.vue'),
  meta: { title: '权限配置工作台', icon: 'DataBoard', keepAlive: true }
}

// 登录日志页
export const LOGIN_LOG_PAGE: RouteRecordRaw = {
  path: '/auth/config/login-log',
  name: 'LoginLogList',
  component: () => import('@/views/auth/config/login-log/index.vue'),
  meta: { title: '登录日志', icon: 'Document', keepAlive: true }
}

// 静态路由集合 - 导出供router/index.ts使用
export const staticRoutes: RouteRecordRaw[] = [
  LOGIN_ROUTE,
  ROOT_ROUTE,
  HOME_ROUTE,
  DEV_VIRTUAL_SCROLL,
  USER_WORKBENCH,
  AUTH_CONFIG_WORKBENCH,
  LOGIN_LOG_PAGE,
  REDIRECT_ROUTE,
  ERROR_404,
  ERROR_403,
  NO_PERMISSION
]
