/** CDN配置 */
interface CdnConfig {
  /** 国内CDN基础URL */
  domesticBase: string
  /** 国外CDN基础URL */
  internationalBase: string
  /** 加载超时时间（ms） */
  timeout: number
}

const CDN_CONFIG: CdnConfig = {
  domesticBase: import.meta.env.VITE_EXTERNAL_CDN_BASE || 'https://fonts.loli.net',
  internationalBase: 'https://fonts.googleapis.com',
  timeout: 3000
}

const FALLBACK_URL: string = import.meta.env.VITE_CDN_FALLBACK_URL || 'https://fonts.loli.net'

/**
 * 判断当前是否为国内网络环境
 * 通过语言/时区简单判断，国内环境使用国内CDN
 */
function isDomestic(): boolean {
  const language = navigator.language || (navigator as { userLanguage?: string }).userLanguage || ''
  return language.includes('zh')
}

/** 获取默认CDN基础URL */
export function getCdnBase(): string {
  return isDomestic() ? CDN_CONFIG.domesticBase : CDN_CONFIG.internationalBase
}

/**
 * 带超时和回退的字体资源加载
 * 使用 <link rel="preload" as="style"> 预加载关键字体，避免 FOIT
 */
export function loadFontWithFallback(
  fontFamily: string,
  fontPath: string,
  fallbackPath?: string
): Promise<void> {
  const baseUrl: string = getCdnBase()
  const primaryUrl: string = baseUrl + fontPath

  return new Promise<void>((resolve) => {
    let resolved: boolean = false
    const finish = (): void => {
      if (!resolved) {
        resolved = true
        resolve()
      }
    }

    const link: HTMLLinkElement = document.createElement('link')
    link.rel = 'preload'
    link.as = 'style'
    link.href = primaryUrl
    link.onload = (): void => {
      applyStyleLink(primaryUrl, fontFamily)
      finish()
    }
    link.onerror = (): void => {
      const fallback: string = fallbackPath || FALLBACK_URL + fontPath
      applyStyleLink(fallback, fontFamily)
      finish()
    }

    document.head.appendChild(link)

    // 超时切换到备用CDN
    setTimeout(() => {
      if (!resolved) {
        const fallback: string = fallbackPath || FALLBACK_URL + fontPath
        applyStyleLink(fallback, fontFamily)
        finish()
      }
    }, CDN_CONFIG.timeout)
  })
}

/**
 * 将字体CSS链接应用到页面
 * 加载失败时回退到系统字体
 */
function applyStyleLink(href: string, fontFamily: string): void {
  const link: HTMLLinkElement = document.createElement('link')
  link.rel = 'stylesheet'
  link.href = href
  link.onerror = (): void => {
    document.documentElement.style.setProperty(
      `--font-family-${fontFamily}`,
      'system-ui, -apple-system, "Segoe UI", sans-serif'
    )
    link.remove()
  }
  document.head.appendChild(link)
}
