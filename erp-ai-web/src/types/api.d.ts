export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}
