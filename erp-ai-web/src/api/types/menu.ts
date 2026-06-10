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

// ==================== 菜单管理后端 CRUD 类型 ====================

/** 菜单列表项 - 对应 SysMenuVO.ListVO */
export interface SysMenuListItem {
  id: number
  parentId: number
  menuName: string
  menuType: string
  menuTypeName?: string
  permissionCode?: string
  routePath?: string
  routeName?: string
  componentPath?: string
  icon?: string
  sortOrder: number
  isVisible: boolean
  isEnabled: boolean
  isKeepAlive: boolean
  isExternalLink: boolean
  createTime: string
  children?: SysMenuListItem[]
}

/** 菜单分页查询参数 */
export interface SysMenuPageQuery {
  pageNum: number
  pageSize: number
  menuName?: string
  menuType?: string
  parentId?: number
  isEnabled?: boolean
}

/** 菜单分页结果 */
export interface SysMenuPageResult {
  records: SysMenuListItem[]
  total: number
  pageNum: number
  pageSize: number
}

/** 菜单创建参数 - 对应 SysMenuDTO.CreateDTO */
export interface SysMenuCreateDTO {
  parentId?: number
  menuName: string
  menuType: string
  permissionCode?: string
  routePath?: string
  routeName?: string
  componentPath?: string
  icon?: string
  sortOrder?: number
  isVisible?: boolean
  isEnabled?: boolean
  isKeepAlive?: boolean
  isExternalLink?: boolean
  externalUrl?: string
}

/** 菜单更新参数 - 对应 SysMenuDTO.UpdateDTO */
export interface SysMenuUpdateDTO {
  id: number
  parentId?: number
  menuName?: string
  menuType?: string
  permissionCode?: string
  routePath?: string
  routeName?: string
  componentPath?: string
  icon?: string
  sortOrder?: number
  isVisible?: boolean
  isEnabled?: boolean
  isKeepAlive?: boolean
  isExternalLink?: boolean
  externalUrl?: string
}

/** 菜单详情 - 对应 SysMenuVO.DetailVO */
export interface SysMenuDetail extends SysMenuListItem {
  externalUrl?: string
  updateTime: string
}
