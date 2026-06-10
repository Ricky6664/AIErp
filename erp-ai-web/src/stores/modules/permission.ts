import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'
import type { MenuTreeNode } from '@/types/user'
import { resolveComponent } from '@/router/modules/dynamic'

/** P01-P15 示范页面菜单树 */
const DEMO_MENU_TREE: MenuTreeNode = {
  id: -9000,
  parentId: 0,
  menuName: '示范页面',
  menuType: 'menu',
  icon: 'Grid',
  sortOrder: 9999,
  children: [
    {
      id: -9001,
      parentId: -9000,
      menuName: 'P01 首页仪表盘',
      menuType: 'menu',
      routePath: '/demo/pages/p01',
      componentPath: 'demo/pages/P01DashboardDemo',
      icon: 'DataBoard',
      sortOrder: 1
    },
    {
      id: -9002,
      parentId: -9000,
      menuName: 'P02 工作台',
      menuType: 'menu',
      routePath: '/demo/pages/p02',
      componentPath: 'demo/pages/P02WorkbenchDemo',
      icon: 'DataBoard',
      sortOrder: 2
    },
    {
      id: -9003,
      parentId: -9000,
      menuName: 'P03 主从列表页',
      menuType: 'menu',
      routePath: '/demo/pages/p03',
      componentPath: 'demo/pages/P03MasterListDemo',
      icon: 'List',
      sortOrder: 3
    },
    {
      id: -9004,
      parentId: -9000,
      menuName: 'P04 单一列表页',
      menuType: 'menu',
      routePath: '/demo/pages/p04',
      componentPath: 'demo/pages/P04SimpleListDemo',
      icon: 'List',
      sortOrder: 4
    },
    {
      id: -9005,
      parentId: -9000,
      menuName: 'P05 树形列表页',
      menuType: 'menu',
      routePath: '/demo/pages/p05',
      componentPath: 'demo/pages/P05TreeListDemo',
      icon: 'Grid',
      sortOrder: 5
    },
    {
      id: -9006,
      parentId: -9000,
      menuName: 'P06 主从表单页',
      menuType: 'menu',
      routePath: '/demo/pages/p06',
      componentPath: 'demo/pages/P06MasterFormDemo',
      icon: 'Edit',
      sortOrder: 6
    },
    {
      id: -9007,
      parentId: -9000,
      menuName: 'P07 单一表单页',
      menuType: 'menu',
      routePath: '/demo/pages/p07',
      componentPath: 'demo/pages/P07SimpleFormDemo',
      icon: 'Edit',
      sortOrder: 7
    },
    {
      id: -9008,
      parentId: -9000,
      menuName: 'P08 看板页',
      menuType: 'menu',
      routePath: '/demo/pages/p08',
      componentPath: 'demo/pages/P08KanbanDemo',
      icon: 'Grid',
      sortOrder: 8
    },
    {
      id: -9009,
      parentId: -9000,
      menuName: 'P09 查询页',
      menuType: 'menu',
      routePath: '/demo/pages/p09',
      componentPath: 'demo/pages/P09QueryDemo',
      icon: 'Search',
      sortOrder: 9
    },
    {
      id: -9010,
      parentId: -9000,
      menuName: 'P10 报表页',
      menuType: 'menu',
      routePath: '/demo/pages/p10',
      componentPath: 'demo/pages/P10ReportDemo',
      icon: 'Document',
      sortOrder: 10
    },
    {
      id: -9011,
      parentId: -9000,
      menuName: 'P11 大屏页',
      menuType: 'menu',
      routePath: '/demo/pages/p11',
      componentPath: 'demo/pages/P11ScreenDemo',
      icon: 'Monitor',
      sortOrder: 11
    },
    {
      id: -9012,
      parentId: -9000,
      menuName: 'P12 画像页',
      menuType: 'menu',
      routePath: '/demo/pages/p12',
      componentPath: 'demo/pages/P12ProfileDemo',
      icon: 'User',
      sortOrder: 12
    },
    {
      id: -9013,
      parentId: -9000,
      menuName: 'P13 配置页',
      menuType: 'menu',
      routePath: '/demo/pages/p13',
      componentPath: 'demo/pages/P13ConfigDemo',
      icon: 'Setting',
      sortOrder: 13
    },
    {
      id: -9014,
      parentId: -9000,
      menuName: 'P14 AI对话页',
      menuType: 'menu',
      routePath: '/demo/pages/p14',
      componentPath: 'demo/pages/P14AIDialogDemo',
      icon: 'ChatDotRound',
      sortOrder: 14
    },
    {
      id: -9015,
      parentId: -9000,
      menuName: 'P15 打印模板设计器',
      menuType: 'menu',
      routePath: '/demo/pages/p15-print',
      componentPath: 'demo/pages/P15PrintDesignerDemo',
      icon: 'Printer',
      sortOrder: 15
    },
    {
      id: -9016,
      parentId: -9000,
      menuName: 'P15 数据报表设计器',
      menuType: 'menu',
      routePath: '/demo/pages/p15-report',
      componentPath: 'demo/pages/P15ReportDesignerDemo',
      icon: 'DataAnalysis',
      sortOrder: 16
    },
    {
      id: -9017,
      parentId: -9000,
      menuName: 'P15 审批流程设计器',
      menuType: 'menu',
      routePath: '/demo/pages/p15-approval',
      componentPath: 'demo/pages/P15ApprovalDesignerDemo',
      icon: 'Stamp',
      sortOrder: 17
    }
  ]
}

/** 示范组件菜单 */
const DEMO_COMPONENT_MENU: MenuTreeNode = {
  id: -9100,
  parentId: 0,
  menuName: '示范组件',
  menuType: 'menu',
  icon: 'Grid',
  sortOrder: 10000,
  children: [
    {
      id: -9101,
      parentId: -9100,
      menuName: '§8.1 基础录入组件 (20个)',
      menuType: 'menu',
      routePath: '/demo/components/basic-inputs',
      componentPath: 'demo/components/BasicInputsDemo',
      icon: 'Edit',
      sortOrder: 1
    },
    {
      id: -9102,
      parentId: -9100,
      menuName: '§8.2 字典下拉组件 (3个)',
      menuType: 'menu',
      routePath: '/demo/components/dict',
      componentPath: 'demo/components/DictComponentsDemo',
      icon: 'Menu',
      sortOrder: 2
    },
    {
      id: -9103,
      parentId: -9100,
      menuName: '§6.2 列表表格功能 (12项)',
      menuType: 'menu',
      routePath: '/demo/components/list-table',
      componentPath: 'demo/components/ListTableFeaturesDemo',
      icon: 'List',
      sortOrder: 3
    },
    {
      id: -9104,
      parentId: -9100,
      menuName: '§6.3 录入表格功能 (10项)',
      menuType: 'menu',
      routePath: '/demo/components/edit-table',
      componentPath: 'demo/components/EditTableFeaturesDemo',
      icon: 'Edit',
      sortOrder: 4
    }
  ]
}

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
      // 始终追加演示页面和组件演示到菜单树末尾
      const mergedTree = [...menuTree, DEMO_MENU_TREE, DEMO_COMPONENT_MENU]
      const dynamicRoutes = buildRoutes(mergedTree)
      this.routes = dynamicRoutes
      this.permissions = collectPermissions(mergedTree)
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
