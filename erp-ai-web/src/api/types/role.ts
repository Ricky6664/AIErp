/** 角色列表项 - 对应 SysRoleVO.ListVO */
export interface RoleListItem {
  id: number
  roleCode: string
  roleName: string
  roleDesc: string
  dataScope: string
  dataScopeName: string
  isEnabled: boolean
  sortOrder: number
  createTime: string
}

/** 角色分页查询参数 */
export interface RolePageQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  roleCode?: string
  roleName?: string
  isEnabled?: boolean
  dataScope?: string
}

/** 角色创建参数 - 对应 SysRoleDTO.CreateDTO */
export interface RoleCreateDTO {
  roleCode: string
  roleName: string
  roleDesc?: string
  dataScope?: string
  isEnabled?: boolean
  sortOrder?: number
}

/** 角色更新参数 - 对应 SysRoleDTO.UpdateDTO */
export interface RoleUpdateDTO {
  id: number
  roleCode?: string
  roleName?: string
  roleDesc?: string
  dataScope?: string
  isEnabled?: boolean
  sortOrder?: number
}

/** 角色分页结果 */
export interface RolePageResult {
  records: RoleListItem[]
  total: number
  pageNum: number
  pageSize: number
}

/** 角色详情 - 对应 SysRoleVO.DetailVO */
export interface RoleDetail extends RoleListItem {
  updateTime: string
}
