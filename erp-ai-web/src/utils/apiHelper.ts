import type { ApiResponse } from '@/types/api'

export function isSuccess<T>(response: ApiResponse<T>): boolean {
  return response.code === 0
}

export function getErrorMessage(response: ApiResponse): string {
  return response.message || '未知错误'
}

export function assertSuccess<T>(
  response: ApiResponse<T>
): asserts response is ApiResponse<T> & { data: T } {
  if (response.code !== 0) {
    throw new Error(response.message || '请求失败')
  }
}
