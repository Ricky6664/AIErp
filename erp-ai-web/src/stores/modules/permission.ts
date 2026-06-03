import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'
import type { MenuTreeNode } from '@/types/user'
import { HOME_ROUTE } from '@/router/modules/static'
import { resolveComponent } from '@/router/modules/dynamic'

interface PermissionState {
  routes: RouteRecordRaw[]
  isRoutesLoaded: boolean
  permissions: string[]
}

function buildRoutes(menuTree: MenuTreeNode[]): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []
  for (const node of menuTree) {
    if (node.menuType === 'button') continue
    const children =
      node.children && node.children.length > 0 ? buildRoutes(node.children) : undefined
    const route: RouteRecordRaw = {
      path: node.routePath || '',
      name: node.menuName,
      component: node.componentPath ? resolveComponent(node.componentPath) : undefined,
      meta: {
        title: node.menuName,
        icon: node.icon,
        permissions: node.permissionCode ? [node.permissionCode] : []
      },
      children
    } as RouteRecordRaw
    routes.push(route)
  }
  return routes
}

function collectPermissions(menuTree: MenuTreeNode[]): string[] {
  const perms: string[] = []
  for (const node of menuTree) {
    if (node.permissionCode) {
      perms.push(node.permissionCode)
    }
    if (node.children && node.children.length > 0) {
      perms.push(...collectPermissions(node.children))
    }
  }
  return perms
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    routes: [],
    isRoutesLoaded: false,
    permissions: []
  }),
  actions: {
    generateRoutes(menuTree: MenuTreeNode[]) {
      const dynamicRoutes = buildRoutes(menuTree)
      this.routes = [HOME_ROUTE as RouteRecordRaw, ...dynamicRoutes]
      this.permissions = collectPermissions(menuTree)
      this.isRoutesLoaded = true
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
