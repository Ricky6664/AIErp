import type { Router } from 'vue-router'
import { usePermissionStore } from '@/stores/modules/permission'
import { useUserStore } from '@/stores/modules/user'
import {
  WHITE_LIST,
  LOGIN_PATH,
  HOME_PATH,
  TOKEN_KEY,
  PASSWORD_EXPIRED_WHITE_LIST
} from './constants'
import { cancelPendingRequests } from '@/utils/request/cancelRequest'
import NProgress from 'nprogress'

export function setupRouterGuards(router: Router) {
  router.beforeEach(async (to, from, next) => {
    NProgress.start()

    if (from.path !== to.path) {
      cancelPendingRequests()
    }

    const token = localStorage.getItem(TOKEN_KEY)

    if (WHITE_LIST.includes(to.path)) {
      if (to.path === LOGIN_PATH && token) {
        return next({ path: HOME_PATH, replace: true })
      }
      return next()
    }

    if (!token) {
      return next({ path: LOGIN_PATH, query: { redirect: to.fullPath }, replace: true })
    }

    const userStore = useUserStore()
    if (userStore.passwordExpired && !PASSWORD_EXPIRED_WHITE_LIST.includes(to.path)) {
      return next({ path: '/change-password', replace: true })
    }

    const permissionStore = usePermissionStore()
    if (!permissionStore.isRoutesLoaded) {
      try {
        await userStore.getInfo()
        await permissionStore.generateRoutes(userStore.menuTree)
        // 将动态路由注册到 Layout 父路由下（跳过路径已存在的静态路由）
        const existingPaths = new Set(
          router.getRoutes().map((r) => {
            const p = r.path || ''
            return p.startsWith('/') ? p : `/${p}`
          })
        )
        for (const route of permissionStore.routes) {
          const fullPath = (route.path || '').startsWith('/') ? route.path : `/${route.path}`
          if (route.name && !router.hasRoute(route.name) && !existingPaths.has(fullPath)) {
            router.addRoute('Layout', route)
          }
        }
        return next({ ...to, replace: true })
      } catch {
        localStorage.removeItem(TOKEN_KEY)
        return next({ path: LOGIN_PATH, replace: true })
      }
    }

    const routePermissions = to.meta?.permissions as string[] | undefined
    if (routePermissions && !permissionStore.hasPermission(routePermissions)) {
      return next({ path: '/no-permission', replace: true })
    }

    next()
  })

  router.afterEach((to) => {
    const title = to.meta?.title as string
    document.title = title ? `${title} - ERP管理系统` : 'ERP管理系统'
    NProgress.done()
  })
}
