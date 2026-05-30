/**
 * Vxe Table 全局注册插件
 * @param app - Vue 应用实例
 */

import type { App, Plugin } from 'vue'
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
  app.use(VxeUI as unknown as Plugin).use(VxeTable as unknown as Plugin)

  VxeUI.setup({
    table: {
      border: true,
      resizable: true,
      showOverflow: true,
      emptyText: '暂无数据',
      autoResize: true
    },
    pager: {
      pageSize: 20,
      pageSizes: [10, 20, 50, 100]
    }
  })
}

/**
 * VxeTable 插件类型，供 main.ts 使用
 */
export type VxeTablePlugin = typeof setupVxeTable
