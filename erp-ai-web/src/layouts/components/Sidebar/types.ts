import type { RouteRecordRaw } from 'vue-router'

/** 侧边栏组件Props */
export interface SidebarProps {
  isCollapsed: boolean
}

/** 菜单项数据结构(供SidebarItem使用) */
export interface MenuItemData {
  path: string
  title: string
  titleI18n?: string
  icon?: string
  iconType?: 'element' | 'svg' | 'external'
  children?: MenuItemData[]
  hideMenu?: boolean
  meta?: Record<string, any>
}

/** 将RouteRecordRaw转换为MenuItemData */
export function routeToMenuItem(route: RouteRecordRaw): MenuItemData {
  return {
    path: route.path,
    title: (route.meta?.title as string) || '',
    titleI18n: route.meta?.titleI18n as string | undefined,
    icon: route.meta?.icon as string | undefined,
    iconType: route.meta?.iconType as 'element' | 'svg' | 'external' | undefined,
    hideMenu: route.meta?.hideMenu as boolean | undefined,
    children: route.children?.map(routeToMenuItem),
    meta: route.meta
  }
}
