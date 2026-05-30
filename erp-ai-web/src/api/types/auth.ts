export interface LoginDTO {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
}

export interface UserInfoResponse {
  userInfo: import('@/types/user').UserInfoVO
  permissions: string[]
  roles: string[]
}
