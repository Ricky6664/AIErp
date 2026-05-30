import request from '@/utils/request'
import type { LoginDTO, LoginResponse, UserInfoResponse } from '@/api/types/auth'

export function loginApi(data: LoginDTO): Promise<LoginResponse> {
  return request.post('/api/auth/login', data)
}

export function getUserInfoApi(): Promise<UserInfoResponse> {
  return request.get('/api/auth/info')
}
