export interface MenuTreeNode {
  id: number
  parentId: number
  menuName: string
  menuType: 'menu' | 'button' | 'dir'
  permissionCode?: string
  componentPath?: string
  routePath?: string
  icon?: string
  sortOrder?: number
  children?: MenuTreeNode[]
}

export interface UserInfoVO {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  deptId: number
  deptName: string
}

/** 用户列表项 - 对应 SysUserVO.ListVO */
export interface UserListItem {
  id: number
  username: string
  realName: string
  nickname: string
  avatar: string
  email: string
  mobile: string
  gender: string
  status: string
  statusName: string
  isLocked: boolean
  employeeName: string
  lastLoginAt: string
  lastLoginIp: string
  createTime: string
}

/** 用户列表查询参数 */
export interface UserPageQuery {
  pageNum: number
  pageSize: number
  keyword?: string
  status?: string
  deptId?: number
}

export interface IUserState {
  token: string
  refreshToken: string
  userInfo: UserInfoVO | null
  permissions: string[]
  roles: string[]
  menuTree: MenuTreeNode[]
  passwordExpired: boolean
}
