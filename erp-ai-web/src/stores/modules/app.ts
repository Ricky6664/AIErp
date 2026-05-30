import { defineStore } from 'pinia'
import type { IAppState, ThemeType } from '@/types/app'

export const useAppStore = defineStore('app', {
  state: (): IAppState => ({
    sidebarCollapsed: localStorage.getItem('erp_app_sidebar') === 'true',
    device: window.innerWidth < 768 ? 'mobile' : 'desktop',
    theme: (localStorage.getItem('erp_app_theme') as ThemeType) || 'light',
    language: localStorage.getItem('erp_app_lang') || 'zh-CN',
    activeMenu: ''
  }),

  persist: {
    key: 'erp_app',
    pick: ['sidebarCollapsed', 'theme', 'language']
  }
})
