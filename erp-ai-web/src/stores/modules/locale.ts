import { defineStore } from 'pinia'
import { ref } from 'vue'

interface LocaleState {
  language: string
  loadedLocales: string[]
  availableLanguages: Array<{
    code: string
    label: string
  }>
}

function getInitialLocale(): string {
  const saved = localStorage.getItem('app-language')
  if (saved && ['zh-CN', 'en-US'].includes(saved)) return saved

  const browserLang = navigator.language
  if (browserLang.startsWith('zh')) return 'zh-CN'
  if (browserLang.startsWith('en')) return 'en-US'

  return 'zh-CN'
}

export const useLocaleStore = defineStore('locale', () => {
  const language = ref<string>(getInitialLocale())
  const loadedLocales = ref<string[]>(['zh-CN'])
  const availableLanguages = ref<Array<{ code: string; label: string }>>([
    { code: 'zh-CN', label: '中文' },
    { code: 'en-US', label: 'English' }
  ])

  return {
    language,
    loadedLocales,
    availableLanguages
  }
})

export type { LocaleState }
