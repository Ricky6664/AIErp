export interface LoginDTO {
  username: string
  password: string
  captchaCode?: string
  captchaKey?: string
  rememberMe?: boolean
}

import type { MenuTreeNode } from '@/types/user'

export interface LoginResponse {
  token: string
  refreshToken?: string
  userId?: number
  username?: string
  nickname?: string
  avatar?: string
  passwordExpired?: boolean
  passwordExpireDate?: string
  menuTree?: MenuTreeNode[]
  permissions?: string[]
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

export interface LockStatusResponse {
  locked: boolean
  remainingSeconds: number
  remainingMinutes: number
}
