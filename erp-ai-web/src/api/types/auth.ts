export interface LoginDTO {
  username: string
  password: string
  captchaCode?: string
  captchaKey?: string
  rememberMe?: boolean
}

export interface LoginResponse {
  token: string
  refreshToken?: string
  userId?: number
  username?: string
  passwordExpired?: boolean
  passwordExpireDate?: string
}

export interface CaptchaResponse {
  captchaKey: string
  captchaImage: string
}

export interface UserInfoResponse {
  userInfo: import('@/types/user').UserInfoVO
  permissions: string[]
  roles: string[]
  menuTree: import('@/types/user').MenuTreeNode[]
}
