import type { RouteRecordRaw } from 'vue-router'
import type { MenuItemData } from './types'

export function filterMenuRoutes(routes: RouteRecordRaw[]): MenuItemData[] {
  return routes
    .filter((route) => !route.meta?.hideMenu)
    .map((route) => {
      const item: MenuItemData = {
        path: route.path,
        title: (route.meta?.title as string) || '',
        titleI18n: route.meta?.titleI18n as string | undefined,
        icon: route.meta?.icon as string | undefined,
        hideMenu: false,
        meta: route.meta,
        children: route.children ? filterMenuRoutes(route.children) : undefined
      }
      return item
    })
    .filter((item) => {
      if (item.children && item.children.length === 0) return false
      return true
    })
}

export const DEFAULT_OPEN_MENUS: string[] = ['/home']

export const SIDEBAR_LOGO = {
  collapsed: '/logo-mini.svg',
  expanded: '/logo-full.svg',
  title: 'ERP管理系统',
  link: '/home'
}
