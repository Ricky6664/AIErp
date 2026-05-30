import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title: string
    titleI18n?: string
    icon?: string
    keepAlive?: boolean
    hideMenu?: boolean
    hideTab?: boolean
    permissions?: string[]
    openType?: number
    affix?: boolean
  }
}
