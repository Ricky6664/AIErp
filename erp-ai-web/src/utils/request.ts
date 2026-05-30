import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/modules/user'
import { useAppStore } from '@/stores/modules/app'

declare module 'axios' {
  interface InternalAxiosRequestConfig {
    retry?: number
    retryDelay?: number
    __retryCount?: number
    silent?: boolean
  }
}

NProgress.configure({ showSpinner: false })

const pendingMap = new Map<string, AbortController>()
let requestCount = 0

function getRequestKey(config: InternalAxiosRequestConfig): string {
  const { url, method, params, data } = config
  return [url, method, JSON.stringify(params), JSON.stringify(data)].join('&')
}

function addPending(config: InternalAxiosRequestConfig): void {
  const key = getRequestKey(config)
  if (pendingMap.has(key)) {
    pendingMap.get(key)!.abort()
  }
  const controller = new AbortController()
  config.signal = controller.signal
  pendingMap.set(key, controller)
}

function removePending(config: InternalAxiosRequestConfig): void {
  const key = getRequestKey(config)
  pendingMap.delete(key)
}

function startLoading(config: InternalAxiosRequestConfig): void {
  if (config.silent || (config.__retryCount && config.__retryCount > 0)) return
  if (requestCount === 0) NProgress.start()
  requestCount++
}

function endLoading(config?: InternalAxiosRequestConfig): void {
  if (config?.silent) return
  requestCount--
  if (requestCount <= 0) {
    requestCount = 0
    NProgress.done()
  }
}

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []

function handleTokenExpired(): void {
  if (isRefreshing) {
    return
  }
  isRefreshing = true
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
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    const appStore = useAppStore()
    config.headers['Accept-Language'] = appStore.language

    addPending(config)
    startLoading(config)
    return config
  },
  (error) => {
    endLoading(error.config)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    removePending(response.config)
    endLoading(response.config)

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
        break
      default:
        ElMessage.error(msg || '请求失败')
    }
    return Promise.reject(new Error(msg || 'Error'))
  },
  async (error) => {
    const config = error.config as InternalAxiosRequestConfig | undefined

    if (config && !axios.isCancel(error) && error.code !== 'ERR_CANCELED') {
      removePending(config)
    }

    if (
      config &&
      config.retry &&
      (!config.method || config.method.toLowerCase() === 'get') &&
      !axios.isCancel(error) &&
      error.code !== 'ERR_CANCELED'
    ) {
      config.__retryCount = config.__retryCount || 0
      if (config.__retryCount < config.retry) {
        config.__retryCount++
        await new Promise((resolve) => setTimeout(resolve, config.retryDelay || 1000))
        return service(config)
      }
    }

    endLoading(config)

    if (axios.isCancel(error) || error.code === 'ERR_CANCELED') {
      return Promise.reject(error)
    }

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
