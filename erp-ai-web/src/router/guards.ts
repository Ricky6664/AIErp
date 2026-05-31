import type { Router } from 'vue-router'
import { usePermissionStore } from '@/stores/modules/permission'
import { useUserStore } from '@/stores/modules/user'
import { WHITE_LIST, LOGIN_PATH, HOME_PATH, TOKEN_KEY } from './constants'
import { cancelPendingRequests } from '@/utils/request/cancelRequest'

export function setupRouterGuards(router: Router) {
  router.beforeEach(async (to, from, next) => {
    // 路由切换时取消前一页面的pending请求
    if (from.path !== to.path) {
      cancelPendingRequests()
    }

    const token = localStorage.getItem(TOKEN_KEY)

    // 1. 白名单路由直接放行
    if (WHITE_LIST.includes(to.path)) {
      // 已登录访问登录页，跳转首页
      if (to.path === LOGIN_PATH && token) {
        return next({ path: HOME_PATH, replace: true })
      }
      return next()
    }

    // 2. 无Token → 跳转登录页(携带redirect参数)
    if (!token) {
      return next({ path: LOGIN_PATH, query: { redirect: to.fullPath }, replace: true })
    }

    // 3. 动态路由未加载 → 加载后重新导航
    const permissionStore = usePermissionStore()
    if (!permissionStore.isRoutesLoaded) {
      try {
        const userStore = useUserStore()
        await userStore.getInfo()
        await permissionStore.generateRoutes(userStore.menus)
        return next({ ...to, replace: true })
      } catch {
        localStorage.removeItem(TOKEN_KEY)
        return next({ path: LOGIN_PATH, replace: true })
      }
    }

    // 4. 权限校验
    const routePermissions = to.meta?.permissions as string[] | undefined
    if (routePermissions && !permissionStore.hasPermission(routePermissions)) {
      return next({ path: '/no-permission', replace: true })
    }

    // 5. 设置页面标题
    const title = to.meta?.title as string
    document.title = title ? `${title} - ERP管理系统` : 'ERP管理系统'

    // 6. 放行
    next()
  })
}
