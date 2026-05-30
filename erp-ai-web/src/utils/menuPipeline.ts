import type { MenuItem } from '@/api/types/menu'

/** 判断图标是否为外部URL(http/https开头) */
export function isExternalIcon(icon: string): boolean {
  return /^(https?:\/\/|\/\/)/.test(icon)
}

/** 判断图标是否为SVG(svg:前缀) */
export function isSvgIcon(icon: string): boolean {
  return icon.startsWith('svg:')
}

/** 路径补全：确保路径以/开头 */
export function normalizePath(path: string, parentPath?: string): string {
  if (path.startsWith('/')) return path
  if (parentPath) {
    const base = parentPath.endsWith('/') ? parentPath : parentPath + '/'
    return base + path
  }
  return '/' + path
}

/** 菜单数据转换管道：路径补全 + 图标标准化 + 递归子菜单 */
export function processMenuData(menus: MenuItem[]): MenuItem[] {
  return menus.map((menu) => {
    const processed: MenuItem = { ...menu }

    // 1. 路径补全
    processed.path = normalizePath(menu.path)

    // 2. 图标字段标准化
    if (menu.icon) {
      if (isExternalIcon(menu.icon)) {
        processed.iconType = 'external'
      } else if (isSvgIcon(menu.icon)) {
        processed.iconType = 'svg'
        processed.icon = menu.icon.replace('svg:', '')
      } else {
        processed.iconType = 'element'
      }
    }

    // 3. 递归处理子菜单
    if (menu.children?.length) {
      processed.children = processMenuData(menu.children)
    }

    return processed
  })
}

/** 缓存转换结果，相同输入不重复计算 */
let cachedMenus: MenuItem[] | null = null
let cachedKey: string = ''

export function getCachedMenus(menus: MenuItem[]): MenuItem[] {
  const key = JSON.stringify(menus)
  if (key === cachedKey && cachedMenus) return cachedMenus
  cachedMenus = processMenuData(menus)
  cachedKey = key
  return cachedMenus
}

/** 清除缓存（退出登录时调用） */
export function clearMenuCache(): void {
  cachedMenus = null
  cachedKey = ''
}
