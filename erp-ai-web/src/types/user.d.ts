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
  userInfo: UserInfoVO | null
  permissions: string[]
  roles: string[]
}
