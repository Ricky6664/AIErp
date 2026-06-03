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

export interface IUserState {
  token: string
  refreshToken: string
  userInfo: UserInfoVO | null
  permissions: string[]
  roles: string[]
  menuTree: MenuTreeNode[]
}
