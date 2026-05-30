import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL as string,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器（后续任务完善）
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 注入 Token
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器（后续任务完善）
service.interceptors.response.use(
  (response: AxiosResponse) => response.data,
  (error) => Promise.reject(error)
)

export default service
