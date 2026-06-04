import { defineStore } from 'pinia'
import type { IAppState, DeviceType, ThemeType } from '@/types/app'
import { getSystemConfigApi } from '@/api/modules/system'

interface I18nInstance {
  global: {
    locale: {
      value: string
    }
  }
}

let i18n: I18nInstance | null = null

export function injectI18n(instance: I18nInstance): void {
  i18n = instance
}

const DEFAULT_SYSTEM_NAME = 'ERP管理系统'
const CACHE_KEY = 'app_config'

export const useAppStore = defineStore('app', {
  state: (): IAppState => {
    const cached = (() => {
      try {
        const raw = localStorage.getItem(CACHE_KEY)
        return raw ? JSON.parse(raw) : null
      } catch {
        return null
      }
    })()

    return {
      sidebarCollapsed: localStorage.getItem('erp_app_sidebar') === 'true',
      device: (window.innerWidth < 768 ? 'mobile' : 'desktop') as DeviceType,
      theme: (localStorage.getItem('erp_app_theme') as ThemeType) || 'light',
      language: localStorage.getItem('erp_app_lang') || 'zh-CN',
      activeMenu: '',
      systemName: cached?.systemName || DEFAULT_SYSTEM_NAME,
      logoUrl: cached?.logoUrl || '',
      defaultPageSize: Number(cached?.defaultPageSize) || 20,
      dateFormat: cached?.dateFormat || 'YYYY-MM-DD',
      dateTimeFormat: cached?.dateTimeFormat || 'YYYY-MM-DD HH:mm:ss',
      themeColor: cached?.themeColor || '#409EFF',
      watermarkEnabled: cached?.watermarkEnabled === true
    }
  },

  getters: {
    isMobile(state): boolean {
      return state.device === 'mobile'
    },

    sidebarStatus(state): 'closed' | 'opened' {
      return state.sidebarCollapsed ? 'closed' : 'opened'
    },

    currentTheme(state): ThemeType {
      return state.theme
    },

    locale(state): string {
      return state.language
    }
  },

  actions: {
    toggleSidebar(): void {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    setDevice(device: DeviceType): void {
      this.device = device
      if (device === 'mobile' && !this.sidebarCollapsed) {
        this.sidebarCollapsed = true
      }
    },

    setTheme(theme: ThemeType): void {
      this.theme = theme
      document.documentElement.setAttribute('data-theme', theme)
      document.documentElement.classList.toggle('dark', theme === 'dark')
      document.documentElement.classList.toggle('el-dark', theme === 'dark')
    },

    setLanguage(lang: string): void {
      this.language = lang
      if (i18n) {
        i18n.global.locale.value = lang
      }
    },

    setActiveMenu(path: string): void {
      this.activeMenu = path
    },

    async initAppConfig(): Promise<void> {
      try {
        const systemConfig = await getSystemConfigApi()
        if (systemConfig) {
          this.systemName = systemConfig.systemName || DEFAULT_SYSTEM_NAME
          this.logoUrl = systemConfig.logoUrl || ''
          this.defaultPageSize = Number(systemConfig.defaultPageSize) || 20
          this.dateFormat = systemConfig.dateFormat || 'YYYY-MM-DD'
          this.dateTimeFormat = systemConfig.dateTimeFormat || 'YYYY-MM-DD HH:mm:ss'
          this.themeColor = systemConfig.themeColor || '#409EFF'
          this.watermarkEnabled = systemConfig.watermarkEnabled === 'true'
        }
      } catch {
        // API unavailable, keep cached/defaults
      }

      const configToCache = {
        systemName: this.systemName,
        logoUrl: this.logoUrl,
        defaultPageSize: this.defaultPageSize,
        dateFormat: this.dateFormat,
        dateTimeFormat: this.dateTimeFormat,
        themeColor: this.themeColor,
        watermarkEnabled: this.watermarkEnabled
      }
      localStorage.setItem(CACHE_KEY, JSON.stringify(configToCache))

      document.documentElement.style.setProperty('--el-color-primary', this.themeColor)
      document.documentElement.style.setProperty('--app-system-primary', this.themeColor)
    }
  },

  persist: {
    key: 'erp_app',
    pick: ['sidebarCollapsed', 'theme', 'language']
  }
})
