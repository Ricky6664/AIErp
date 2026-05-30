/** 后端菜单项数据结构 */
export interface MenuItem {
  id: number
  parentId: number
  name: string
  path: string
  component?: string
  icon?: string
  iconType?: 'element' | 'svg' | 'external'
  sort: number
  type: number
  permissions?: string
  visible: boolean
  keepAlive: boolean
  openType?: number
  children?: MenuItem[]
}

/** getInfo接口响应中的菜单部分 */
export interface MenuResponse {
  menus: MenuItem[]
  permissions: string[]
}
