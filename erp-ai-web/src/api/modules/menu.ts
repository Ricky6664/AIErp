import request from '@/utils/request'
import type { MenuItem, MenuResponse } from '@/api/types/menu'

/** 获取用户菜单和权限信息 */
export function getMenuList(): Promise<MenuResponse> {
  return request.get('/api/system/user/getInfo')
}

/** 扁平化菜单树(用于权限标识查找) */
export function flattenMenuTree(menus: MenuItem[]): MenuItem[] {
  const result: MenuItem[] = []
  function traverse(items: MenuItem[]) {
    items.forEach((item) => {
      result.push(item)
      if (item.children?.length) {
        traverse(item.children)
      }
    })
  }
  traverse(menus)
  return result
}

/** 从菜单树中查找权限标识列表 */
export function extractPermissions(menus: MenuItem[]): string[] {
  const flat = flattenMenuTree(menus)
  return flat.filter((item) => item.permissions).map((item) => item.permissions!)
}
