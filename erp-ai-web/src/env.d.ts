/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** 应用标题 */
  readonly VITE_APP_TITLE: string
  /** 后端API基础URL */
  readonly VITE_API_BASE_URL: string
  /** 是否启用Mock数据 (true/false) */
  readonly VITE_USE_MOCK: string
  /** 当前环境标识 */
  readonly VITE_APP_ENV: 'development' | 'staging' | 'production'
  /** WebSocket地址 */
  readonly VITE_WS_URL: string
  /** 文件上传CDN地址 */
  readonly VITE_CDN_BASE_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
