/**
 * 录入数据表格（EntryTable）组件类型定义
 *
 * 基于 vxe-table 可编辑封装，支持单元格编辑、前端合计计算、
 * 字段配置集成、列持久化等功能。
 */

import type {
  FieldConfig,
  SummaryColumnConfig,
  SummaryConfig,
  RowConfig,
  ColumnPersistData
} from './list-table'

/** 编辑校验规则 */
export interface EditRule {
  /** 是否必填 */
  required?: boolean
  /** 最小值 */
  min?: number
  /** 最大值 */
  max?: number
  /** 正则校验 */
  pattern?: RegExp
  /** 错误提示 */
  message?: string
}

/** 编辑渲染器配置 */
export interface EditRenderConfig {
  /** 渲染器名称（input/number/select/date等） */
  name: string
  /** 渲染器属性 */
  props?: Record<string, unknown>
  /** 下拉选项（select类型时使用） */
  options?: { label: string; value: unknown }[]
}

/** 行拖拽排序配置 */
export interface DragConfig {
  /** 是否启用行拖拽排序 */
  enabled?: boolean
  /** 拖拽把手 CSS 选择器 */
  handle?: string
  /** 拖拽触发方式（icon=拖拽图标, row=整行拖拽） */
  trigger?: 'icon' | 'row'
  /** 拖拽类型（row=行拖拽, handle=把手拖拽） */
  type?: 'row' | 'handle'
  /** 是否显示拖拽状态提示 */
  showTip?: boolean
  /** 拖拽排序后是否自动更新行号 */
  autoRowNumber?: boolean
}

/** 行拖拽排序事件参数 */
export interface DragSortEventParams {
  /** 被拖拽的行数据 */
  row: Record<string, unknown>
  /** 旧行索引 */
  oldIndex: number
  /** 新行索引 */
  newIndex: number
  /** 排序后的完整数据 */
  newData: Record<string, unknown>[]
}
export interface EditTableColumn {
  /** 列字段名 */
  field: string
  /** 列标题 */
  title: string
  /** 列宽度 */
  width?: number | string
  /** 最小列宽度 */
  minWidth?: number | string
  /** 是否可编辑 */
  editable?: boolean
  /** 编辑渲染器配置 */
  editRender?: EditRenderConfig
  /** 编辑校验规则 */
  editRules?: EditRule[]
  /** 是否可见 */
  visible?: boolean
  /** 固定列 */
  fixed?: 'left' | 'right'
  /** 列对齐 */
  align?: 'left' | 'center' | 'right'
  /** 列数据类型 */
  type?: string
  /** 格式化函数 */
  formatter?: (value: unknown, row: Record<string, unknown>) => string
  /** 是否显示省略提示 */
  showOverflow?: boolean
  /** 自定义插槽名 */
  slot?: string
  /** 合计行列配置 */
  summary?: SummaryColumnConfig
}

/** 录入表格 Props */
export interface EditTableProps {
  /** 列配置 */
  columns: EditTableColumn[]
  /** v-model绑定数据（表格行数据） */
  modelValue?: Record<string, unknown>[]
  /** 字段配置映射（field_type映射+校验规则+联动规则） */
  fieldConfig?: Record<string, FieldConfig>
  /** 是否禁用所有编辑 */
  disabled?: boolean
  /** 占位提示文本 */
  placeholder?: string
  /** 表格高度 */
  height?: number | string
  /** 最大高度 */
  maxHeight?: number | string
  /** 行尺寸 */
  size?: 'mini' | 'small' | 'medium' | 'large'
  /** 斑马纹 */
  stripe?: boolean
  /** 边框模式 */
  border?: 'inner' | 'full' | 'none'
  /** 是否显示表头 */
  showHeader?: boolean
  /** 空数据提示文本 */
  emptyText?: string
  /** 视图编码（用于列配置持久化key区分） */
  viewCode?: string
  /** 编辑触发方式 */
  editTrigger?: 'click' | 'dblclick' | 'manual'
  /** 行拖拽排序配置 */
  dragConfig?: DragConfig
  /** 合计行配置 */
  summaryConfig?: SummaryConfig
  /** 行配置 */
  rowConfig?: RowConfig
}

/** 单元格变更事件参数 */
export interface EditChangeParams {
  /** 变更行数据 */
  row: Record<string, unknown>
  /** 变更字段 */
  field: string
  /** 新值 */
  value: unknown
  /** 行索引 */
  rowIndex: number
}

/** 录入表格 Emits */
export interface EditTableEmits {
  /** v-model更新事件（数据变更时触发） */
  'update:modelValue': [rows: Record<string, unknown>[]]
  /** 单元格值确认变更 */
  change: [params: EditChangeParams]
  /** 焦点事件 */
  focus: []
  /** 失焦事件 */
  blur: []
  /** 行拖拽排序事件 */
  'drag-sort': [params: DragSortEventParams]
}

/** 录入表格 Slots */
export interface EditTableSlots {
  /** 表格前缀插槽 */
  prefix?: () => unknown
  /** 表格后缀插槽 */
  suffix?: () => unknown
  /** 默认内容插槽 */
  default?: () => unknown
  /** 动态列插槽 */
  [columnSlot: string]:
    | ((params: { row: Record<string, unknown>; column: EditTableColumn }) => unknown)
    | (() => unknown)
    | undefined
}

/** 录入表格组件暴露方法 */
export interface EditTableExpose {
  /** vxe-grid 实例引用 */
  gridRef: unknown
  /** 刷新表格 */
  refresh: () => void
  /** 重置列配置 */
  resetColumns: () => void
  /** 获取当前数据 */
  getData: () => Record<string, unknown>[]
  /** 设置数据 */
  setData: (data: Record<string, unknown>[]) => void
  /** 校验所有可编辑单元格 */
  validate: () => Promise<boolean>
  /** 清除校验状态 */
  clearValidate: () => void
  /** 一键重置：重置列+清除校验 */
  resetAll: () => void
  /** 程序化行排序（拖拽排序回退/api调用） */
  reorder: (fromIndex: number, toIndex: number) => void
}
