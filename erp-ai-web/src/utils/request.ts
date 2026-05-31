import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/modules/user'
import { useAppStore } from '@/stores/modules/app'
import { handleTokenRefresh } from './request/tokenRefresh'
import { addPending, removePending, isWhitelisted } from './request/cancelRequest'

import type { ApiResponse } from '@/types/api'

declare module 'axios' {
  interface InternalAxiosRequestConfig {
    retry?: number
    retryDelay?: number
    __retryCount?: number
    silent?: boolean
    metadata?: {
      requestKey: string
      startTime: number
      skipCancel?: boolean
    }
  }
}

NProgress.configure({ showSpinner: false })

let requestCount = 0

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

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    const appStore = useAppStore()
    config.headers['Accept-Language'] = appStore.language

    const requestKey = `${config.method?.toUpperCase()}:${config.url}:${JSON.stringify(config.params ?? '')}:${JSON.stringify(config.data ?? '')}`
    config.metadata = { requestKey, startTime: Date.now() }

    if (!isWhitelisted(config)) {
      addPending(config)
    }
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
  (response: AxiosResponse<ApiResponse<any>>) => {
    removePending(response.config)
    endLoading(response.config)

    const { code, data, message } = response.data

    // 1. 业务成功：code === 0，返回解包后的数据
    if (code === 0) {
      return data
    }

    // 2. Token过期：code === 20001，触发刷新流程
    if (code === 20001) {
      return handleTokenRefresh(response.config)
    }

    // 3. 权限不足：code === 40001 或 40003
    if (code === 40001 || code === 40003) {
      ElMessage.error('权限不足，请联系管理员')
      return Promise.reject(new Error(message))
    }

    // 4. 其他业务错误：显示后端返回的message
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  async (error: AxiosError) => {
    const config = error.config as InternalAxiosRequestConfig | undefined

    if (
      config &&
      !axios.isCancel(error) &&
      error.name !== 'CanceledError' &&
      error.code !== 'ERR_CANCELED'
    ) {
      removePending(config)
    }

    if (
      config &&
      config.retry &&
      (!config.method || config.method.toLowerCase() === 'get') &&
      !axios.isCancel(error) &&
      error.name !== 'CanceledError' &&
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

    if (axios.isCancel(error) || error.name === 'CanceledError' || error.code === 'ERR_CANCELED') {
      return Promise.reject(error)
    }

    if (error.response) {
      const status = error.response.status as number
      switch (status) {
        case 401:
          return handleTokenRefresh(config!)
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(`请求失败(${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (!error.response) {
      ElMessage.error('网络连接异常，请检查网络')
    }
    return Promise.reject(error)
  }
)

export default service
