export type DeviceType = 'desktop' | 'mobile'
export type ThemeType = 'light' | 'dark'

export interface IAppState {
  sidebarCollapsed: boolean
  device: DeviceType
  theme: ThemeType
  language: string
  activeMenu: string
  systemName: string
  logoUrl: string
  defaultPageSize: number
  dateFormat: string
  dateTimeFormat: string
  themeColor: string
  watermarkEnabled: boolean
}
