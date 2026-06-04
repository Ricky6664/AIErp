/** 在线设备列表项 */
export interface OnlineDeviceItem {
  /** 会话Token标识（用于强制下线操作） */
  tokenId: string
  /** 会话Token值 */
  sessionTokenId?: string
  /** 用户ID */
  userId?: number
  /** 用户名 */
  username: string
  /** 设备类型：PC / Mobile / Tablet */
  deviceType: string
  /** 设备名称 */
  deviceName?: string
  /** 操作系统 */
  os?: string
  /** 浏览器 */
  browser?: string
  /** IP地址 */
  ipAddress?: string
  /** 登录时间 */
  loginTime?: string
  /** 最后活跃时间 */
  lastActiveTime: string
  /** 在线状态：online / offline / kicked */
  status: 'online' | 'offline' | 'kicked'
  /** 创建时间 */
  createdAt?: string
}

/** 在线设备分页查询参数 */
export interface OnlineDeviceQuery {
  pageNum: number
  pageSize: number
  username?: string
  deviceType?: string
  status?: string
}

/** 在线设备分页结果 */
export interface OnlineDevicePageResult {
  records: OnlineDeviceItem[]
  total: number
  pageNum: number
  pageSize: number
}
