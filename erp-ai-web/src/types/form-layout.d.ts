/**
 * MasterForm 表单布局配置 — 类型定义
 *
 * ## 配置项说明
 *
 * ### 响应式栅格 (ResponsiveFormGridConfig)
 * | 配置项 | 类型 | 默认值 | 说明 |
 * |--------|------|--------|------|
 * | xs | number | 1 | 屏幕<768px时的栅格列数 |
 * | sm | number | 1 | 屏幕≥768px时的栅格列数 |
 * | md | number | 2 | 屏幕≥992px时的栅格列数 |
 * | lg | number | 3 | 屏幕≥1200px时的栅格列数 |
 * | xl | number | 4 | 屏幕≥1920px时的栅格列数 |
 *
 * ### 标签页布局 (FormTabLayoutConfig)
 * | 配置项 | 类型 | 说明 |
 * |--------|------|------|
 * | tabs | FormTabConfig[] | 标签页列表 |
 * | defaultActiveTab | string | 默认激活的标签页key |
 * | tabPosition | 'top'\|'left'\|'right' | 标签页位置,默认'top' |
 * | lazyRender | boolean | 是否懒加载未激活的标签页内容,默认true |
 *
 * ### 步骤向导布局 (FormStepLayoutConfig)
 * | 配置项 | 类型 | 说明 |
 * |--------|------|------|
 * | steps | FormStepConfig[] | 步骤列表 |
 * | currentStep | number | 当前步骤索引(从0开始) |
 * | showStepNav | boolean | 是否显示步骤导航,默认true |
 * | linear | boolean | 是否强制线性完成(不可跳过),默认true |
 *
 * ### 区域分区布局 (FormSectionLayoutConfig)
 * | 配置项 | 类型 | 说明 |
 * |--------|------|------|
 * | sections | FormSectionConfig[] | 分区列表 |
 * | sectionGutter | number | 分区间距,默认16px |
 * | showSectionDivider | boolean | 是否显示分区之间的分隔线,默认true |
 *
 * ### 行内布局 (FormRowLayoutConfig)
 * | 配置项 | 类型 | 说明 |
 * |--------|------|------|
 * | rows | FormRowConfig[] | 行配置列表(每行可自定义列数) |
 * | rowGutter | number | 行间距,默认0 |
 *
 * ### 扩展布局选项 (FormLayoutExtendedConfig)
 * | 配置项 | 类型 | 默认值 | 说明 |
 * |--------|------|--------|------|
 * | layoutType | 'grid'\|'tab'\|'step'\|'section' | 'grid' | 布局类型 |
 * | responsive | ResponsiveFormGridConfig | — | 响应式栅格配置 |
 * | tabLayout | FormTabLayoutConfig | — | 标签页布局(需layoutType='tab') |
 * | stepLayout | FormStepLayoutConfig | — | 步骤布局(需layoutType='step') |
 * | sectionLayout | FormSectionLayoutConfig | — | 分区布局(需layoutType='section') |
 * | rowLayout | FormRowLayoutConfig | — | 行内自定义布局 |
 * | gutter | number | 16 | 字段间距(px) |
 * | showGroupBorder | boolean | true | 是否显示分组边框 |
 * | defaultCollapseAll | boolean | false | 是否默认折叠所有分组 |
 * | dense | boolean | false | 紧凑模式(减小行间距与标签宽度) |
 * | formWidth | number\|string | '100%' | 表单整体宽度 |
 *
 * ### 布局预设常量 (FORM_LAYOUT_PRESETS)
 * | 预设名 | 说明 |
 * |--------|------|
 * | STANDARD | 标准表单(2列,label右对齐,标签宽100px,字段间距16px) |
 * | COMPACT | 紧凑表单(4列,label右对齐,标签宽80px,字段间距8px,dense模式) |
 * | DETAIL | 详情展示(1列,label右对齐,标签宽120px,全字段只读) |
 * | WIDE | 宽屏表单(3列,label右对齐,标签宽120px,字段间距16px) |
 * | FULL | 全宽表单(1列整行,label上对齐,标签宽auto,适合复杂字段) |
 * | QUERY | 查询表单(内联模式,4列响应式,标签宽80px,字段间距12px) |
 */

import type { FormLayoutMode, LabelPosition, FormGroupConfig, FormFieldConfig } from './master-form'

// ============================================================
// 响应式断点
// ============================================================

/** 响应式断点标识 */
export type ResponsiveBreakpoint = 'xs' | 'sm' | 'md' | 'lg' | 'xl'

/** 响应式栅格配置(每个断点对应的栅格列数) */
export interface ResponsiveFormGridConfig {
  /** 超小屏(<768px),默认1列 */
  xs?: number
  /** 小屏(≥768px),默认1列 */
  sm?: number
  /** 中屏(≥992px),默认2列 */
  md?: number
  /** 大屏(≥1200px),默认3列 */
  lg?: number
  /** 超大屏(≥1920px),默认4列 */
  xl?: number
}

// ============================================================
// 标签页布局
// ============================================================

/** 单个标签页配置 */
export interface FormTabConfig {
  /** 标签页标识 */
  key: string
  /** 标签页标题 */
  label: string
  /** 该标签页下的字段名列表(对应FormFieldConfig.field) */
  fields: string[]
  /** 是否禁用该标签页 */
  disabled?: boolean
  /** 标签图标(Element Plus icon 名称) */
  icon?: string
  /** 标签页badge提示 */
  badge?: string | number
}

/** 标签页布局配置 */
export interface FormTabLayoutConfig {
  /** 标签页列表 */
  tabs: FormTabConfig[]
  /** 默认激活的标签页key,默认取第一个 */
  defaultActiveTab?: string
  /** 标签页位置,默认'top' */
  tabPosition?: 'top' | 'left' | 'right'
  /** 是否懒加载未激活的标签页内容,默认true */
  lazyRender?: boolean
}

// ============================================================
// 步骤向导布局
// ============================================================

/** 单个步骤配置 */
export interface FormStepConfig {
  /** 步骤标识 */
  key: string
  /** 步骤标题 */
  title: string
  /** 步骤描述(ElSteps的description) */
  description?: string
  /** 该步骤下的字段名列表 */
  fields: string[]
  /** 步骤图标 */
  icon?: string
}

/** 步骤向导布局配置 */
export interface FormStepLayoutConfig {
  /** 步骤列表 */
  steps: FormStepConfig[]
  /** 当前步骤索引(0-based),默认0 */
  currentStep?: number
  /** 是否显示步骤导航条,默认true */
  showStepNav?: boolean
  /** 是否强制线性完成(不可跳过未完成的步骤),默认true */
  linear?: boolean
  /** 步骤导航样式,'simple'简洁 / 'dot'圆点 */
  stepStyle?: 'simple' | 'dot'
}

// ============================================================
// 区域分区布局
// ============================================================

/** 单个分区配置 */
export interface FormSectionConfig {
  /** 分区标识 */
  key: string
  /** 分区标题 */
  title: string
  /** 该分区下的字段名列表 */
  fields: string[]
  /** 是否可折叠,默认false */
  collapsible?: boolean
  /** 是否默认折叠,默认false */
  defaultCollapsed?: boolean
  /** 分区描述(折叠标题旁的提示文本) */
  description?: string
}

/** 区域分区布局配置 */
export interface FormSectionLayoutConfig {
  /** 分区列表 */
  sections: FormSectionConfig[]
  /** 分区间距(px),默认16 */
  sectionGutter?: number
  /** 是否显示分区之间的分隔线,默认true */
  showSectionDivider?: boolean
}

// ============================================================
// 行内自定义布局(复杂多字段行)
// ============================================================

/** 单行布局配置 */
export interface FormRowConfig {
  /** 该行的字段名列表 */
  fields: string[]
  /** 该行每字段的colSpan(默认均分,如4字段则每字段6) */
  colSpans?: number[]
  /** 该行的栅格列数(覆盖表单级columns),默认24 */
  columns?: number
  /** 行CSS类名 */
  className?: string
}

/** 行内自定义布局配置 */
export interface FormRowLayoutConfig {
  /** 行配置列表 */
  rows: FormRowConfig[]
  /** 行间距(px),默认0 */
  rowGutter?: number
}

// ============================================================
// 布局类型枚举(组合布局模式)
// ============================================================

/** 布局组合类型 */
export type FormLayoutType = 'grid' | 'tab' | 'step' | 'section'

// ============================================================
// 扩展表单布局配置(完整布局能力)
// ============================================================

/** 扩展表单布局配置(在FormLayoutConfig基础上增加高级布局能力) */
export interface FormLayoutExtendedConfig {
  // --- 布局类型 ---
  /** 布局组合类型,默认'grid'(普通栅格) */
  layoutType?: FormLayoutType

  // --- 基础配置(继承自FormLayoutConfig) ---
  /** 布局模式(行内/水平/垂直),默认'horizontal' */
  mode?: FormLayoutMode
  /** 标签宽度,默认'100px' */
  labelWidth?: number | string
  /** 标签位置,默认'right' */
  labelPosition?: LabelPosition
  /** 栅格列数(24等分),默认1;响应式时为基础默认值 */
  columns?: number
  /** 分组配置 */
  groups?: FormGroupConfig[]
  /** 是否禁用整个表单 */
  disabled?: boolean
  /** 表单尺寸,默认'default' */
  size?: 'small' | 'default' | 'large'

  // --- 响应式 ---
  /** 响应式栅格配置(按断点指定列数) */
  responsive?: ResponsiveFormGridConfig

  // --- 高级布局 ---
  /** 标签页布局(需配合layoutType='tab') */
  tabLayout?: FormTabLayoutConfig
  /** 步骤向导布局(需配合layoutType='step') */
  stepLayout?: FormStepLayoutConfig
  /** 区域分区布局(需配合layoutType='section') */
  sectionLayout?: FormSectionLayoutConfig
  /** 行内自定义布局(可精确控制每行字段,colSpan等) */
  rowLayout?: FormRowLayoutConfig

  // --- 外观 ---
  /** 字段间距(px),默认16 */
  gutter?: number
  /** 是否显示分组边框,默认true */
  showGroupBorder?: boolean
  /** 是否默认折叠所有分组,默认false */
  defaultCollapseAll?: boolean
  /** 紧凑模式(减小行间距与默认标签宽度),默认false */
  dense?: boolean
  /** 表单整体宽度,默认'100%' */
  formWidth?: number | string
  /** 是否在标签后显示冒号,默认false */
  labelSuffix?: boolean
}

// ============================================================
// 布局预设常量(类型声明)
// ============================================================

/** 布局预设键 */
export type FormLayoutPresetKey = 'STANDARD' | 'COMPACT' | 'DETAIL' | 'WIDE' | 'FULL' | 'QUERY'

/** 布局预设值映射 */
export type FormLayoutPresets = Record<
  FormLayoutPresetKey,
  Required<
    Pick<
      FormLayoutExtendedConfig,
      | 'layoutType'
      | 'mode'
      | 'labelWidth'
      | 'labelPosition'
      | 'columns'
      | 'size'
      | 'gutter'
      | 'dense'
    >
  >
>

/** 布局预设常量(declare,运行时由form-layout.ts提供实现) */
declare const FORM_LAYOUT_PRESETS: FormLayoutPresets

// ============================================================
// 表单布局更新工具类型
// ============================================================

/** 字段在布局中的位置信息 */
export interface FieldLayoutPosition {
  /** 字段名 */
  field: string
  /** 所在行索引 */
  rowIndex: number
  /** 行内位置索引 */
  colIndex: number
  /** 占用栅格列数 */
  colSpan: number
  /** 所在分组key */
  group?: string
  /** 所在标签页key */
  tab?: string
  /** 所在步骤key */
  step?: string
  /** 所在分区key */
  section?: string
}

/** 计算布局后的字段排列结果 */
export interface FormLayoutResult {
  /** 字段布局位置列表(按从左到右、从上到下排列) */
  fields: FieldLayoutPosition[]
  /** 总行数 */
  totalRows: number
  /** 每行的最大栅格数(24) */
  maxColumns: number
}

// ============================================================
// 布局配置合并工具类型
// ============================================================

/** 表单默认布局配置(可被FormLayoutConfig和单个FormFieldConfig覆盖) */
export type FormDefaultLayoutConfig = Required<
  Pick<
    FormLayoutExtendedConfig,
    | 'layoutType'
    | 'mode'
    | 'labelWidth'
    | 'labelPosition'
    | 'columns'
    | 'size'
    | 'gutter'
    | 'dense'
    | 'showGroupBorder'
    | 'defaultCollapseAll'
    | 'labelSuffix'
  >
>
