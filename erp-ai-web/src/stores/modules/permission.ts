import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'

interface PermissionState {
  routes: RouteRecordRaw[]
  isRoutesLoaded: boolean
  permissions: string[]
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    routes: [],
    isRoutesLoaded: false,
    permissions: []
  }),
  actions: {
    async generateRoutes(_menus: unknown[]) {
      /* 动态路由生成逻辑在路由守卫中实现 */
    },
    hasPermission(routePermissions: string[]): boolean {
      if (!routePermissions.length) return true
      return routePermissions.some((p) => this.permissions.includes(p))
    },
    resetPermission() {
      this.routes = []
      this.isRoutesLoaded = false
      this.permissions = []
    }
  }
})
