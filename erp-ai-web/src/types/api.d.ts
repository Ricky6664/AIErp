export interface ApiResponse<T = unknown> {
  code: number
  data: T
  message: string
}

export interface PageResult<T = unknown> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface PageQuery {
  pageNum: number
  pageSize: number
}

export function isSuccess<T>(response: ApiResponse<T>): boolean
export function getErrorMessage(response: ApiResponse): string
export function assertSuccess<T>(
  response: ApiResponse<T>
): asserts response is ApiResponse<T> & { data: T }
