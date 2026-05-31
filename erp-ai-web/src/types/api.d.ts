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
