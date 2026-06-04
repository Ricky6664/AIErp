export interface LoginMethodDistItem {
  loginMethod: string
  count: number
}

export interface DailyLoginStatItem {
  loginDate: string
  successCount: number
  failCount: number
}

export interface AuthConfigWorkbenchData {
  totalAuthMethods: number
  enabledAuthMethods: number
  totalPasswordPolicies: number
  enabledPasswordPolicies: number
  onlineDeviceCount: number
  todayLoginSuccessCount: number
  todayLoginFailCount: number
  ssoConfigCount: number
  loginMethodDistribution: LoginMethodDistItem[]
  dailyLoginStats: DailyLoginStatItem[]
}
