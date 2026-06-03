import request from '@/utils/request'
import type { LoginDTO, LoginResponse, CaptchaResponse, UserInfoResponse } from '@/api/types/auth'

export function loginApi(data: LoginDTO): Promise<LoginResponse> {
  return request.post('/api/auth/login', data)
}

export function logoutApi(): Promise<void> {
  return request.post('/api/auth/logout')
}

export function getCaptchaApi(): Promise<CaptchaResponse> {
  return request.get('/api/auth/captcha')
}

export function refreshTokenApi(refreshToken: string): Promise<LoginResponse> {
  return request.post('/api/auth/token/refresh', { refreshToken })
}

export function verifyTokenApi(): Promise<boolean> {
  return request.post('/api/auth/token/verify')
}

export function getUserInfoApi(): Promise<UserInfoResponse> {
  return request.get('/api/auth/info')
}
