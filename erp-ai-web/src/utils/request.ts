import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []

function handleTokenExpired(): void {
  if (isRefreshing) {
    return
  }
  isRefreshing = true
  // Token 刷新逻辑（后续任务 P0-002-001-002-003-003 完善）
  // 刷新成功后调用 onTokenRefreshed(newToken) 重放队列
}

function onTokenRefreshed(newToken: string): void {
  refreshSubscribers.forEach((cb) => cb(newToken))
  refreshSubscribers = []
  isRefreshing = false
}

function subscribeTokenRefresh(cb: (token: string) => void): void {
  refreshSubscribers.push(cb)
}

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Token 注入逻辑（后续任务完善）
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, data, msg } = response.data

    if (code === 0) {
      return data
    }

    switch (code) {
      case 401:
        handleTokenExpired()
        break
      case 403:
        ElMessage.error('权限不足')
        // TODO: 路由模块创建后添加 router.push('/403')
        break
      default:
        ElMessage.error(msg || '请求失败')
    }
    return Promise.reject(new Error(msg || 'Error'))
  },
  (error) => {
    if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (!error.response) {
      ElMessage.error('网络异常，请检查网络连接')
    } else {
      const status = error.response.status
      const messages: Record<number, string> = {
        400: '请求参数错误',
        404: '请求资源不存在',
        500: '服务器内部错误',
        502: '网关错误',
        503: '服务不可用'
      }
      ElMessage.error(messages[status] || `请求失败(${status})`)
    }
    return Promise.reject(error)
  }
)

export { onTokenRefreshed, subscribeTokenRefresh }
export default service
