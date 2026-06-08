import request from '@/utils/request'

export interface CacheStatsVO {
  keyCount: number
  serverVersion: string
  usedMemory: number
  uptimeInSeconds: number
}

export interface CacheKeyVO {
  key: string
  type: string
  ttl: number
  size: number
}

/** 获取缓存统计信息 */
export function getCacheStatsApi(): Promise<CacheStatsVO> {
  return request.get('/api/system/cache/stats')
}

/** 扫描缓存Key列表 */
export function getCacheKeysApi(pattern: string, count?: number): Promise<CacheKeyVO[]> {
  return request.get('/api/system/cache/list', {
    params: { pattern: pattern || '*', count: count ?? 200 }
  })
}

/** 获取缓存Value */
export function getCacheValueApi(key: string): Promise<string> {
  return request.get(`/api/system/cache/${encodeURIComponent(key)}/value`)
}

/** 删除单个缓存Key */
export function deleteCacheKeyApi(key: string): Promise<void> {
  return request.delete(`/api/system/cache/${encodeURIComponent(key)}`)
}

/** 按模式批量删除缓存 */
export function deleteCacheByPatternApi(pattern: string): Promise<void> {
  return request.delete('/api/system/cache/batch', { params: { pattern } })
}
