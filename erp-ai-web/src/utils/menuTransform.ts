import type { RouteRecordRaw } from 'vue-router'
import type { MenuItem } from '@/api/types/menu'
import { resolveComponent } from '@/router/modules/dynamic'
import AppLayout from '@/layouts/AppLayout.vue'

/**
 * 将后端菜单树转换为Vue Router路由配置
 */
export function transformMenuToRoutes(menus: MenuItem[]): RouteRecordRaw[] {
  return menus
    .filter((menu) => menu.type !== 2)
    .sort((a, b) => a.sort - b.sort)
    .map((menu) => {
      const base = {
        path: menu.path,
        name: menu.path.replace(/\//g, '-').replace(/^-/, ''),
        meta: {
          title: menu.name,
          icon: menu.icon,
          keepAlive: menu.keepAlive ?? false,
          hideMenu: !menu.visible,
          permissions: menu.permissions ? [menu.permissions] : [],
          openType: menu.openType
        }
      }

      if (menu.type === 0) {
        return {
          ...base,
          component: AppLayout,
          children: menu.children?.length ? transformMenuToRoutes(menu.children) : [],
          redirect: menu.children?.[0]?.path ?? ''
        } as RouteRecordRaw
      }
      if (menu.type === 1 && menu.component) {
        return {
          ...base,
          component: resolveComponent(menu.component)
        } as RouteRecordRaw
      }
      return { ...base, redirect: '' } as RouteRecordRaw
    })
}
