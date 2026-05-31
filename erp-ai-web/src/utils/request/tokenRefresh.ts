import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'
import type { ApiResponse } from '@/types/api'

interface TokenVO {
  accessToken: string
  refreshToken: string
}

interface PendingRequest {
  resolve: (value: any) => void
  reject: (reason?: any) => void
  config: InternalAxiosRequestConfig
}

const refreshAxios = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string,
  timeout: 10000
})

let isRefreshing = false
const pendingQueue: PendingRequest[] = []

function addToQueue(config: InternalAxiosRequestConfig): Promise<any> {
  return new Promise((resolve, reject) => {
    const timer = setTimeout(() => {
      const idx = pendingQueue.findIndex((item) => item.config === config)
      if (idx !== -1) {
        pendingQueue.splice(idx, 1)
      }
      reject(new Error('请求排队超时'))
    }, 30000)
    pendingQueue.push({
      resolve: (v) => {
        clearTimeout(timer)
        resolve(v)
      },
      reject: (r) => {
        clearTimeout(timer)
        reject(r)
      },
      config
    })
  })
}

function replayRequests(newToken: string): void {
  const queue = [...pendingQueue]
  pendingQueue.length = 0

  queue.forEach(({ resolve, reject, config }) => {
    config.headers.Authorization = `Bearer ${newToken}`
    axios(config).then(resolve).catch(reject)
  })
}

function handleRefreshFailure(): void {
  pendingQueue.forEach(({ reject }) => {
    reject(new Error('登录已过期，请重新登录'))
  })
  pendingQueue.length = 0

  const userStore = useUserStore()
  userStore.token = ''
  userStore.userInfo = null
  userStore.permissions = []
  userStore.roles = []
  localStorage.removeItem('erp_user')
  localStorage.removeItem('erp_refresh_token')

  isRefreshing = false

  ElMessage.warning('会话已过期，请重新登录')

  const router = useRouter()
  if (router.currentRoute.value.path !== '/login') {
    router.replace('/login')
  }
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
    replayRequests(userStore.token)
    isRefreshing = false
    return axios(config)
  } catch (error) {
    handleRefreshFailure()
    return Promise.reject(error)
  }
}
