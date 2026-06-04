export interface LoginTrendItem {
  date: string
  count: number
}

export interface RoleDistributionItem {
  roleName: string
  userCount: number
}

export interface RecentLoginItem {
  username: string
  loginTime: string
  ip: string
  status: string
}

export interface WorkbenchData {
  userTotal: number
  roleTotal: number
  onlineCount: number
  todayLoginCount: number
  loginTrend: LoginTrendItem[]
  roleDistribution: RoleDistributionItem[]
  recentLogins: RecentLoginItem[]
}
