import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'
import type { MenuTreeNode } from '@/types/user'
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

    const childRoutes =
      node.children && node.children.length > 0 ? buildRoutes(node.children) : undefined

    // 路径保持绝对形式（以 / 开头），避免 Vue Router 嵌套时路径重复拼接
    // 种子数据中 routePath 已是完整绝对路径如 /system/user
    const routePath = node.routePath || ''
    const absPath = routePath.startsWith('/') ? routePath : `/${routePath}`

    // 有子菜单的父节点：如果自己没有页面组件，则自动重定向到第一个子页面
    if (!node.componentPath && childRoutes && childRoutes.length > 0) {
      const firstChild = childRoutes[0]
      const firstChildPath = (firstChild.path || '') as string
      routes.push({
        path: absPath,
        name: node.menuName,
        redirect: firstChildPath,
        meta: {
          title: node.menuName,
          icon: node.icon,
          permissions: node.permissionCode ? [node.permissionCode] : []
        },
        children: childRoutes
      } as RouteRecordRaw)
      continue
    }

    // 无子菜单且无组件的节点：跳过
    if (!node.componentPath && (!childRoutes || childRoutes.length === 0)) continue

    // 有组件的叶子节点或父节点
    const route: RouteRecordRaw = {
      path: absPath,
      name: node.menuName,
      component: node.componentPath ? resolveComponent(node.componentPath) : undefined,
      meta: {
        title: node.menuName,
        icon: node.icon,
        permissions: node.permissionCode ? [node.permissionCode] : []
      },
      children: childRoutes
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
      this.routes = dynamicRoutes
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
