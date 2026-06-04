import type { Router } from 'vue-router'
import { usePermissionStore } from '@/stores/modules/permission'
import { useUserStore } from '@/stores/modules/user'
import { WHITE_LIST, LOGIN_PATH, HOME_PATH, TOKEN_KEY } from './constants'
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

    const permissionStore = usePermissionStore()
    if (!permissionStore.isRoutesLoaded) {
      try {
        const userStore = useUserStore()
        await userStore.getInfo()
        await permissionStore.generateRoutes(userStore.menuTree)
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
