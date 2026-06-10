/**
 * 字段控件类型
 */
export type FieldControlType =
  | 'input'
  | 'textarea'
  | 'number'
  | 'select'
  | 'date'
  | 'dateRange'
  | 'datetime'
  | 'dateMonth'
  | 'switch'
  | 'radio'
  | 'checkbox'

/**
 * 字段校验规则
 */
export interface FieldValidationRule {
  required?: boolean
  min?: number
  max?: number
  pattern?: RegExp
  message?: string
  trigger?: 'blur' | 'change'
}

/**
 * 字段联动规则
 */
export interface FieldLinkageRule {
  /** 目标字段名 */
  targetField: string
  /** 联动条件：当前字段值满足条件时触发 */
  condition: (value: unknown, formData: Record<string, unknown>) => boolean
  /** 联动动作 */
  action: (targetField: string, formData: Record<string, unknown>) => void
}

/**
 * 字段配置
 */
export interface FieldConfig {
  /** 字段名（对应modelValue中的key） */
  field: string
  /** 字段标题（显示用） */
  title?: string
  /** 字段标签 */
  label: string
  /** 控件类型 */
  type: FieldControlType
  /** 占位提示 */
  placeholder?: string
  /** 选择类控件的选项 */
  options?: Array<{ label: string; value: string | number | boolean }>
  /** 校验规则 */
  rules?: FieldValidationRule[]
  /** 联动规则 */
  linkage?: FieldLinkageRule[]
  /** 默认值 */
  defaultValue?: unknown
  /** 列宽度（栅格，默认6即半行） */
  span?: number
  /** 是否可清空 */
  clearable?: boolean
  /** 是否禁用 */
  disabled?: boolean
}

/**
 * QueryPanel Props
 */
export interface QueryPanelProps {
  /** v-model绑定的查询条件对象 */
  modelValue: Record<string, unknown>
  /** 字段配置列表 */
  fieldConfig: FieldConfig[]
  /** 是否禁用 */
  disabled?: boolean
  /** 组件级默认占位提示（字段级placeholder优先） */
  placeholder?: string
  /** 是否显示展开/折叠（字段数超过阈值时生效） */
  collapsible?: boolean
  /** 折叠阈值（字段数超过此值显示展开按钮，默认8） */
  collapseThreshold?: number
}
