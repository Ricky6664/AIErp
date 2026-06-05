import type { FieldConfig } from './query-panel'

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
