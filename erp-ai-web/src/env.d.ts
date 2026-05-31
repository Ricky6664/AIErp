/// <reference types="vite/client" />

declare module 'element-plus/dist/locale/zh-cn.mjs' {
  const locale: Record<string, unknown>
  export default locale
}

declare module 'element-plus/dist/locale/en.mjs' {
  const locale: Record<string, unknown>
  export default locale
}

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

declare module 'vue-i18n' {
  export interface DefineLocaleMessage {
    common: {
      confirm: string
      cancel: string
      save: string
      delete: string
      edit: string
      add: string
      search: string
      reset: string
      submit: string
      back: string
      close: string
      refresh: string
      export: string
      import: string
      yes: string
      no: string
      ok: string
      success: string
      error: string
      warning: string
      info: string
      loading: string
      noData: string
      pleaseSelect: string
      pleaseInput: string
      operate: string
      status: string
      createTime: string
      updateTime: string
      remark: string
      desc: string
      sort: string
      enable: string
      disable: string
      batchDelete: string
      more: string
      detail: string
      upload: string
      download: string
      preview: string
      copy: string
      copied: string
      tip: string
      confirmDelete: string
      saveSuccess: string
      deleteSuccess: string
      operateSuccess: string
      operateFailed: string
    }
    status: {
      enabled: string
      disabled: string
      active: string
      inactive: string
      locked: string
      deleted: string
      pending: string
      processing: string
      completed: string
      failed: string
      online: string
      offline: string
    }
    validation: {
      required: string
      maxLength: string
      minLength: string
      email: string
      phone: string
      url: string
      number: string
      integer: string
      positive: string
      max: string
      min: string
      duplicate: string
      formatError: string
    }
  }
}
