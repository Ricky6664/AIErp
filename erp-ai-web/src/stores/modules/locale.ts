import { defineStore } from 'pinia'
import { ref } from 'vue'
import { i18n, epLocale } from '@/i18n'
import dayjs from 'dayjs'

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

const localeModules = import.meta.glob<{ default: Record<string, unknown> }>(
  '../../i18n/locales/*.ts'
)

export const useLocaleStore = defineStore('locale', () => {
  const language = ref<string>(getInitialLocale())
  const loadedLocales = ref<string[]>(['zh-CN'])
  const availableLanguages = ref<Array<{ code: string; label: string }>>([
    { code: 'zh-CN', label: '中文' },
    { code: 'en-US', label: 'English' }
  ])

  async function loadLocaleMessages(locale: string): Promise<void> {
    const modulePath = `../../i18n/locales/${locale}.ts`
    const loader = localeModules[modulePath]
    if (!loader) {
      console.warn(`[locale] No locale module found for "${locale}"`)
      return
    }
    const module = await loader()
    i18n.global.setLocaleMessage(locale, module.default as Record<string, unknown>)
    loadedLocales.value.push(locale)
  }

  async function setLanguage(locale: string): Promise<void> {
    if (!loadedLocales.value.includes(locale)) {
      await loadLocaleMessages(locale)
    }
    i18n.global.locale.value = locale
    language.value = locale
    localStorage.setItem('app-language', locale)
    document.documentElement.lang = locale

    if (locale === 'zh-CN') {
      const { default: elMsg } = await import('element-plus/dist/locale/zh-cn.mjs')
      epLocale.value = elMsg
      await import('dayjs/locale/zh-cn')
      dayjs.locale('zh-cn')
    } else {
      const { default: elMsg } = await import('element-plus/dist/locale/en.mjs')
      epLocale.value = elMsg
      await import('dayjs/locale/en')
      dayjs.locale('en')
    }
  }

  return {
    language,
    loadedLocales,
    availableLanguages,
    setLanguage,
    loadLocaleMessages
  }
})

export type { LocaleState }
