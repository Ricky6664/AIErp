// @ts-expect-error TS2305 — vue-i18n createI18n type export incompatible with TS 6.x
import { createI18n } from 'vue-i18n'
import { ref } from 'vue'
import { localeContextKey, buildLocaleContext } from 'element-plus'
import dayjs from 'dayjs'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'

function getInitialLocale(): string {
  const stored = localStorage.getItem('locale')
  if (stored) return stored

  const browserLang = navigator.language
  if (browserLang && browserLang.startsWith('zh')) return 'zh-CN'
  if (browserLang && browserLang.startsWith('en')) return 'en-US'

  return 'zh-CN'
}

export const initialLocale = getInitialLocale()

export const i18n = createI18n({
  legacy: false,
  locale: initialLocale,
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  },
  missing(_locale: string, key: string) {
    console.warn(`[i18n] Missing translation: "${key}" in locale "${_locale}"`)
  }
})

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const epLocale = ref<any>(null)

export function provideEpLocale(app: {
  provide: (key: symbol | string, value: unknown) => void
}): void {
  app.provide(localeContextKey, buildLocaleContext(epLocale))
}

export async function setLanguage(locale: string): Promise<void> {
  i18n.global.locale.value = locale
  localStorage.setItem('locale', locale)
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
