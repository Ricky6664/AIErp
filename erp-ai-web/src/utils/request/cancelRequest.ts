import type { InternalAxiosRequestConfig } from 'axios'

// ============================================================
// 请求取消机制 - 配置定义 + pendingMap管理 + 白名单规则
// @author AI
// ============================================================

export interface CancelConfig {
  generateKey: (config: InternalAxiosRequestConfig) => string
  skipCancel: (config: InternalAxiosRequestConfig) => boolean
  cancelMessage: string
}

export const defaultConfig: CancelConfig = {
  generateKey: (config) => {
    const { method, url, params, data } = config
    return `${method?.toUpperCase()}:${url}:${JSON.stringify(params || {})}:${JSON.stringify(data || {})}`
  },
  skipCancel: (config) => {
    return config.metadata?.skipCancel === true
  },
  cancelMessage: '请求已取消（页面切换）'
}

export const pendingMap = new Map<string, AbortController>()

export function addPending(config: InternalAxiosRequestConfig): void {
  const key = defaultConfig.generateKey(config)
  if (pendingMap.has(key)) {
    pendingMap.get(key)!.abort()
  }
  const controller = new AbortController()
  config.signal = controller.signal
  pendingMap.set(key, controller)
}

export function removePending(config: InternalAxiosRequestConfig): void {
  const key = defaultConfig.generateKey(config)
  pendingMap.delete(key)
}

export function cancelPendingRequests(pageKey?: string): void {
  pendingMap.forEach((controller, key) => {
    if (pageKey === undefined || key.startsWith(pageKey)) {
      controller.abort()
      pendingMap.delete(key)
    }
  })
}

/** 白名单：这些请求不参与自动取消 */
export const CANCEL_WHITELIST_PATTERNS: (string | RegExp)[] = ['/auth/login', '/auth/refresh-token']

export function isWhitelisted(config: InternalAxiosRequestConfig): boolean {
  if (config.metadata?.skipCancel === true) {
    return true
  }
  if (config.responseType === 'blob') {
    return true
  }
  const url = config.url ?? ''
  return CANCEL_WHITELIST_PATTERNS.some((pattern) => {
    if (typeof pattern === 'string') {
      return url.includes(pattern)
    }
    return pattern.test(url)
  })
}
