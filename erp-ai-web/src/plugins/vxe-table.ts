/**
 * Vxe Table 全局注册插件
 * @param app - Vue 应用实例
 */

import type { App } from 'vue'
import VxeUI from 'vxe-pc-ui'
import VxeTable from 'vxe-table'

/**
 * Vxe Table 国际化配置类型
 */
export interface VxeI18nConfig {
  i18n: (key: string, args?: Record<string, unknown>) => string
}

/**
 * Vxe Table 默认参数配置类型
 */
export interface VxeDefaultConfig {
  table: {
    border: boolean
    resizable: boolean
    showOverflow: boolean | string
    emptyText?: string
  }
}

/**
 * VxeTable 全局注册插件
 * @param app - Vue 应用实例
 */
export function setupVxeTable(app: App): void {
  app.use(VxeUI).use(VxeTable)
}

/**
 * VxeTable 插件类型，供 main.ts 使用
 */
export type VxeTablePlugin = typeof setupVxeTable
