import { defineStore } from 'pinia'
import type { IAppState, DeviceType, ThemeType } from '@/types/app'

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

export const useAppStore = defineStore('app', {
  state: (): IAppState => ({
    sidebarCollapsed: localStorage.getItem('erp_app_sidebar') === 'true',
    device: (window.innerWidth < 768 ? 'mobile' : 'desktop') as DeviceType,
    theme: (localStorage.getItem('erp_app_theme') as ThemeType) || 'light',
    language: localStorage.getItem('erp_app_lang') || 'zh-CN',
    activeMenu: ''
  }),

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
    }
  },

  persist: {
    key: 'erp_app',
    pick: ['sidebarCollapsed', 'theme', 'language']
  }
})
