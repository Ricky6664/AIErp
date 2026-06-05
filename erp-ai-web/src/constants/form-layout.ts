import type { FormLayoutPresets } from '../types/form-layout'

/**
 * FORM_LAYOUT_PRESETS — 表单布局预设常量
 *
 * 提供6种常用布局，通过预置值快速切换表单布局，无需逐项配置。
 * 各预设均可通过 FormLayoutExtendedConfig 的同名字段覆盖。
 *
 * ## 预设速览
 * | 预设 | 列数 | 标签宽 | 间距 | 适用场景 |
 * |------|:---:|:-----:|:---:|---------|
 * | STANDARD | 2 | 100px | 16px | 通用新建/编辑表单 |
 * | COMPACT | 4 | 80px | 8px | 批量录入、数据密集页面 |
 * | DETAIL | 1 | 120px | 16px | 详情只读展示 |
 * | WIDE | 3 | 120px | 16px | 宽屏表单、多字段录入 |
 * | FULL | 1 | auto | 16px | 全宽单列、复杂嵌套字段 |
 * | QUERY | 4 | 80px | 12px | 搜索查询栏 |
 */
export const FORM_LAYOUT_PRESETS: FormLayoutPresets = {
  /** 标准表单 — 2列、标签右对齐100px、间距16px、default尺寸 */
  STANDARD: {
    layoutType: 'grid' as const,
    mode: 'horizontal' as const,
    labelWidth: '100px',
    labelPosition: 'right' as const,
    columns: 2,
    size: 'default' as const,
    gutter: 16,
    dense: false
  },

  /** 紧凑表单 — 4列、标签右对齐80px、间距8px、dense模式、small尺寸 */
  COMPACT: {
    layoutType: 'grid' as const,
    mode: 'horizontal' as const,
    labelWidth: '80px',
    labelPosition: 'right' as const,
    columns: 4,
    size: 'small' as const,
    gutter: 8,
    dense: true
  },

  /** 详情展示 — 1列、标签右对齐120px、间距16px、default尺寸 */
  DETAIL: {
    layoutType: 'grid' as const,
    mode: 'horizontal' as const,
    labelWidth: '120px',
    labelPosition: 'right' as const,
    columns: 1,
    size: 'default' as const,
    gutter: 16,
    dense: false
  },

  /** 宽屏表单 — 3列、标签右对齐120px、间距16px、default尺寸 */
  WIDE: {
    layoutType: 'grid' as const,
    mode: 'horizontal' as const,
    labelWidth: '120px',
    labelPosition: 'right' as const,
    columns: 3,
    size: 'default' as const,
    gutter: 16,
    dense: false
  },

  /** 全宽表单 — 1列整行、标签上对齐、间距16px、default尺寸 */
  FULL: {
    layoutType: 'grid' as const,
    mode: 'vertical' as const,
    labelWidth: 'auto',
    labelPosition: 'top' as const,
    columns: 1,
    size: 'default' as const,
    gutter: 16,
    dense: false
  },

  /** 查询表单 — 4列、标签右对齐80px、间距12px、内联模式、small尺寸 */
  QUERY: {
    layoutType: 'grid' as const,
    mode: 'inline' as const,
    labelWidth: '80px',
    labelPosition: 'right' as const,
    columns: 4,
    size: 'small' as const,
    gutter: 12,
    dense: false
  }
}
