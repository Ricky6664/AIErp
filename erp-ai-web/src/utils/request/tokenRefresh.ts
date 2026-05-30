import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/modules/user'
import type { ApiResponse } from '@/types/api'

interface TokenVO {
  accessToken: string
  refreshToken: string
}

const refreshAxios = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string,
  timeout: 10000
})

let isRefreshing = false
let pendingRequests: ((token: string) => void)[] = []

function addToQueue(config: InternalAxiosRequestConfig): Promise<any> {
  return new Promise((resolve) => {
    pendingRequests.push((newToken: string) => {
      config.headers.Authorization = `Bearer ${newToken}`
      resolve(axios(config))
    })
  })
}

function replayRequests(newToken: string): void {
  pendingRequests.forEach((cb) => cb(newToken))
  pendingRequests = []
  isRefreshing = false
}

function handleRefreshFailure(): void {
  isRefreshing = false
  pendingRequests = []
  const userStore = useUserStore()
  userStore.logout()
  ElMessage.error('登录已过期，请重新登录')
}

export async function handleTokenRefresh(config: InternalAxiosRequestConfig): Promise<any> {
  if (isRefreshing) {
    return addToQueue(config)
  }
  isRefreshing = true

  try {
    const userStore = useUserStore()
    const res = await refreshAxios.post<ApiResponse<TokenVO>>('/auth/refresh-token', {
      refreshToken: localStorage.getItem('erp_refresh_token')
    })
    const { accessToken, refreshToken: newRefreshToken } = res.data.data
    userStore.token = accessToken
    localStorage.setItem('erp_refresh_token', newRefreshToken)
    config.headers.Authorization = `Bearer ${accessToken}`
    replayRequests(accessToken)
    return axios(config)
  } catch {
    handleRefreshFailure()
    return Promise.reject(new Error('Token刷新失败'))
  }
}
