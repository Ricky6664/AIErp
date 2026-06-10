import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'
import { TOKEN_KEY, REFRESH_TOKEN_KEY } from '@/router/constants'
import type { ApiResponse } from '@/types/api'

interface TokenVO {
  token: string
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

function isRefreshRequest(config: InternalAxiosRequestConfig): boolean {
  return config.url?.includes('/api/auth/token/refresh') ?? false
}

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
    config.headers.satoken = newToken
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
  userStore.refreshToken = ''
  userStore.userInfo = null
  userStore.permissions = []
  userStore.roles = []
  userStore.menuTree = []
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)

  isRefreshing = false

  ElMessage.warning('会话已过期，请重新登录')

  const router = useRouter()
  if (router.currentRoute.value?.path !== '/login') {
    const redirect = router.currentRoute.value?.fullPath || '/'
    router.replace({ path: '/login', query: { redirect } })
  }
}

export async function handleTokenRefresh(config: InternalAxiosRequestConfig): Promise<any> {
  if (isRefreshRequest(config)) {
    handleRefreshFailure()
    return Promise.reject(new Error('刷新Token失败，请重新登录'))
  }

  if (isRefreshing) {
    return addToQueue(config)
  }
  isRefreshing = true

  try {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
    if (!refreshToken) {
      throw new Error('无刷新Token')
    }

    const res = await refreshAxios.post<ApiResponse<TokenVO>>('/api/auth/token/refresh', {
      refreshToken
    })

    const { token, refreshToken: newRefreshToken } = res.data.data

    const userStore = useUserStore()
    userStore.token = token
    userStore.refreshToken = newRefreshToken
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(REFRESH_TOKEN_KEY, newRefreshToken)

    config.headers.Authorization = `Bearer ${token}`
    config.headers.satoken = token

    replayRequests(token)
    isRefreshing = false

    return axios(config)
  } catch {
    handleRefreshFailure()
    return Promise.reject(new Error('Token刷新失败'))
  }
}
