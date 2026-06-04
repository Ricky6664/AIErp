/** 登录日志列表项 */
export interface LoginLogItem {
  id: number
  username: string
  loginTime: string
  logoutTime: string | null
  ipAddress: string
  ipLocation?: string
  loginMethod: string
  browser: string
  os: string
  status: 'success' | 'fail'
  onlineDuration?: string
}

/** 登录日志分页查询参数 */
export interface LoginLogQuery {
  pageNum: number
  pageSize: number
  username?: string
  status?: string
  startTime?: string
  endTime?: string
  ip?: string
}

/** 登录日志分页结果 */
export interface LoginLogPageResult {
  records: LoginLogItem[]
  total: number
  pageNum: number
  pageSize: number
}
