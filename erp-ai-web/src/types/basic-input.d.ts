import type { FieldConfig, FieldLinkageRule } from './list-table'

/**
 * 字段校验规则
 */
export interface ValidatorRule {
  required?: boolean
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (value: unknown) => boolean | Promise<boolean>
  message?: string
  trigger?: 'blur' | 'change'
}

/**
 * 联动事件负载 — 当字段值变化触发联动规则时通过 linkage 事件发出
 */
export interface ErpInputLinkageEvent {
  /** 触发字段名 */
  field: string
  /** 当前字段值 */
  value: unknown
  /** 被触发的联动规则列表 */
  linkages: FieldLinkageRule[]
}

/**
 * ErpInput 单行文本输入框 Props
 */
export interface ErpInputProps {
  /** v-model 绑定值 */
  modelValue: any
  /** 是否禁用/只读 */
  disabled?: boolean
  /** 是否加载中 */
  loading?: boolean
  /** 字段配置（来自字段配置体系） */
  fieldConfig?: FieldConfig
  /** 校验规则列表 */
  rules?: ValidatorRule[]
  /** 占位提示文字 */
  placeholder?: string
  /** 最大字符长度 */
  maxLength?: number
  /** 是否可清空 */
  clearable?: boolean
  /** 是否显示字数统计 */
  showWordLimit?: boolean
  /** 尺寸 */
  size?: 'large' | 'default' | 'small'
}

/**
 * ErpInput 单行文本输入框 Emits
 */
export interface ErpInputEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  validate: [result: boolean]
}

/**
 * ErpInput 单行文本输入框 Exposed 方法
 */
export interface ErpInputExpose {
  /** 执行校验，返回是否通过 */
  validate: () => Promise<boolean>
  /** 重置值为空字符串 */
  reset: () => void
}

/**
 * ErpTextarea 多行文本输入框 Props
 */
export interface ErpTextareaProps {
  /** v-model 绑定值 */
  modelValue: any
  /** 是否禁用/只读 */
  disabled?: boolean
  /** 是否加载中 */
  loading?: boolean
  /** 字段配置（来自字段配置体系） */
  fieldConfig?: FieldConfig
  /** 校验规则列表 */
  rules?: ValidatorRule[]
  /** 占位提示文字 */
  placeholder?: string
  /** 最大字符长度 */
  maxLength?: number
  /** 是否显示字数统计 */
  showWordLimit?: boolean
  /** 是否可清空 */
  clearable?: boolean
  /** 行数 */
  rows?: number
  /** 是否可拖拽调整大小 */
  resize?: 'none' | 'both' | 'horizontal' | 'vertical'
  /** 尺寸 */
  size?: 'large' | 'default' | 'small'
}

/**
 * ErpTextarea 多行文本输入框 Emits
 */
export interface ErpTextareaEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  validate: [result: boolean]
}

/**
 * ErpTextarea 多行文本输入框 Exposed 方法
 */
export interface ErpTextareaExpose {
  /** 执行校验，返回是否通过 */
  validate: () => Promise<boolean>
  /** 重置值为空字符串 */
  reset: () => void
}

/**
 * ErpFieldRenderer 字段组件动态渲染器 Props
 *
 * 根据 fieldConfig.fieldType 动态渲染对应的录入组件：
 * - text → ErpInput
 * - textarea → ErpTextarea
 * - 未来扩展 number/select/date 等
 */
export interface ErpFieldRendererProps {
  /** v-model 绑定值 */
  modelValue: any
  /** 字段配置（fieldType 决定渲染哪个组件） */
  fieldConfig?: FieldConfig
  /** 是否禁用/只读 */
  disabled?: boolean
  /** 是否可见（联动规则控制，false 时隐藏字段） */
  visible?: boolean
  /** 是否加载中 */
  loading?: boolean
  /** 占位提示文字 */
  placeholder?: string
  /** 尺寸 */
  size?: 'large' | 'default' | 'small'
}

/**
 * ErpFieldRenderer 字段组件动态渲染器 Emits
 */
export interface ErpFieldRendererEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
}

/**
 * ErpFieldRenderer 字段组件动态渲染器 Exposed 方法
 */
export interface ErpFieldRendererExpose {
  /** 执行校验，返回是否通过 */
  validate: () => Promise<boolean>
  /** 重置值为空字符串 */
  reset: () => void
}

/**
 * fieldType → 组件名映射
 */
export type FieldTypeComponentMapping = Record<string, 'ErpInput' | 'ErpTextarea'>
