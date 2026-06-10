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
 * ErpNumberInput 整数/数字录入框 Props
 */
export interface ErpNumberInputProps {
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
  /** 最小值 */
  min?: number
  /** 最大值 */
  max?: number
  /** 步长，默认 1 */
  step?: number
  /** 是否显示增减按钮 */
  controls?: boolean
  /** 增减按钮位置 */
  controlsPosition?: 'right' | ''
  /** 数值精度（小数位数），默认 0 表示整数 */
  precision?: number
  /** 尺寸 */
  size?: 'large' | 'default' | 'small'
}

/**
 * ErpNumberInput 整数/数字录入框 Emits
 */
export interface ErpNumberInputEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  validate: [result: boolean]
}

/**
 * ErpNumberInput 整数/数字录入框 Exposed 方法
 */
export interface ErpNumberInputExpose {
  /** 执行校验，返回是否通过 */
  validate: () => Promise<boolean>
  /** 重置值为 null */
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
export type FieldTypeComponentMapping = Record<
  string,
  | 'ErpInput'
  | 'ErpTextarea'
  | 'ErpNumberInput'
  | 'ErpPasswordInput'
  | 'ErpSwitch'
  | 'ErpRadioGroup'
  | 'ErpCheckboxGroup'
  | 'ErpSelect'
  | 'ErpTreeSelect'
  | 'ErpDatePicker'
  | 'ErpTimePicker'
  | 'ErpDateTimePicker'
  | 'ErpMonthPicker'
  | 'ErpFileUpload'
  | 'ErpImageUpload'
  | 'ErpRichEditor'
  | 'ErpColorPicker'
  | 'ErpRate'
  | 'ErpTagInput'
  | 'ErpDictSelect'
  | 'ErpDictMultiSelect'
  | 'ErpDictCascade'
>

// ============================================================
// ErpPasswordInput — 密码输入框 (§8.1-20)
// ============================================================
export interface ErpPasswordInputProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  maxLength?: number
  showPassword?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpPasswordInputEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  validate: [result: boolean]
}
export interface ErpPasswordInputExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpSwitch — 开关组件 (§8.1-10)
// ============================================================
export interface ErpSwitchProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  activeText?: string
  inactiveText?: string
  activeValue?: any
  inactiveValue?: any
  size?: 'large' | 'default' | 'small'
}
export interface ErpSwitchEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpSwitchExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpRadioGroup — 单选组件 (§8.1-11)
// ============================================================
export interface RadioOption {
  label: string
  value: any
  disabled?: boolean
}
export interface ErpRadioGroupProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  options?: RadioOption[]
  buttonStyle?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpRadioGroupEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpRadioGroupExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpCheckboxGroup — 多选组件 (§8.1-12)
// ============================================================
export interface CheckboxOption {
  label: string
  value: any
  disabled?: boolean
}
export interface ErpCheckboxGroupProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  options?: CheckboxOption[]
  maxCount?: number
  buttonStyle?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpCheckboxGroupEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpCheckboxGroupExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpSelect — 通用下拉选择器
// ============================================================
export interface SelectOption {
  label: string
  value: any
  disabled?: boolean
}
export interface ErpSelectProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  options?: SelectOption[]
  multiple?: boolean
  filterable?: boolean
  clearable?: boolean
  remote?: boolean
  remoteMethod?: (query: string) => Promise<SelectOption[]>
  size?: 'large' | 'default' | 'small'
}
export interface ErpSelectEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  validate: [result: boolean]
}
export interface ErpSelectExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpTreeSelect — 树形选择组件 (§8.1-13)
// ============================================================
export interface TreeNodeData {
  label: string
  value: any
  disabled?: boolean
  children?: TreeNodeData[]
}
export interface ErpTreeSelectProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  treeData?: TreeNodeData[]
  multiple?: boolean
  filterable?: boolean
  clearable?: boolean
  checkStrictly?: boolean
  showCheckbox?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpTreeSelectEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpTreeSelectExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpDatePicker — 日期选择器 (§8.1-6)
// ============================================================
export interface ErpDatePickerProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  format?: string
  valueFormat?: string
  clearable?: boolean
  editable?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpDatePickerEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  validate: [result: boolean]
}
export interface ErpDatePickerExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpDateTimePicker — 日期时间选择器 (§8.1-8)
// ============================================================
export interface ErpDateTimePickerProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  format?: string
  valueFormat?: string
  clearable?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpDateTimePickerEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  validate: [result: boolean]
}
export interface ErpDateTimePickerExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpColorPicker — 颜色选择组件 (§8.1-17)
// ============================================================
export interface ErpColorPickerProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  showAlpha?: boolean
  predefine?: string[]
  size?: 'large' | 'default' | 'small'
}
export interface ErpColorPickerEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpColorPickerExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpRate — 星级评分组件 (§8.1-18)
// ============================================================
export interface ErpRateProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  maxStars?: number
  showScore?: boolean
  allowHalf?: boolean
  texts?: string[]
  size?: 'large' | 'default' | 'small'
}
export interface ErpRateEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpRateExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpTagInput — 标签输入组件 (§8.1-19)
// ============================================================
export interface ErpTagInputProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  presetTags?: string[]
  allowFreeInput?: boolean
  maxTags?: number
  size?: 'large' | 'default' | 'small'
}
export interface ErpTagInputEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpTagInputExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpFileUpload — 附件上传组件 (§8.1-14)
// ============================================================
export interface UploadFile {
  name: string
  url?: string
  size?: number
  status?: 'ready' | 'uploading' | 'success' | 'fail'
  uid?: number
}
export interface ErpFileUploadProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  accept?: string
  maxSize?: number
  maxCount?: number
  multiple?: boolean
  action?: string
  size?: 'large' | 'default' | 'small'
}
export interface ErpFileUploadEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpFileUploadExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpImageUpload — 图片上传组件 (§8.1-15)
// ============================================================
export interface ErpImageUploadProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  accept?: string
  maxSize?: number
  maxCount?: number
  multiple?: boolean
  action?: string
  listType?: 'text' | 'picture' | 'picture-card'
  size?: 'large' | 'default' | 'small'
}
export interface ErpImageUploadEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpImageUploadExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpRichEditor — 富文本编辑组件 (§8.1-16)
// ============================================================
export interface ErpRichEditorProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  height?: number
  toolbar?: string
  size?: 'large' | 'default' | 'small'
}
export interface ErpRichEditorEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpRichEditorExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpDictSelect — 通用字典下拉选择器 (§8.2-1)
// ============================================================
export interface ErpDictSelectProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  dictCode?: string
  strictMode?: boolean
  clearable?: boolean
  filterable?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpDictSelectEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpDictSelectExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpDictMultiSelect — 字典多选下拉组件 (§8.2-2)
// ============================================================
export interface ErpDictMultiSelectProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  dictCode?: string
  maxCount?: number
  clearable?: boolean
  size?: 'large' | 'default' | 'small'
}
export interface ErpDictMultiSelectEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpDictMultiSelectExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

// ============================================================
// ErpDictCascade — 字典级联下拉组件 (§8.2-3)
// ============================================================
export interface ErpDictCascadeProps {
  modelValue: any
  disabled?: boolean
  loading?: boolean
  fieldConfig?: FieldConfig
  rules?: ValidatorRule[]
  placeholder?: string
  dictCodeList?: string[]
  size?: 'large' | 'default' | 'small'
}
export interface ErpDictCascadeEmits {
  'update:modelValue': [value: any]
  change: [value: any]
  linkage: [event: ErpInputLinkageEvent]
  validate: [result: boolean]
}
export interface ErpDictCascadeExpose {
  validate: () => Promise<boolean>
  reset: () => void
}

/**
 * 字段值收集器统一接口 — 所有录入组件通过此接口向上层表单暴露值收集能力
 *
 * 支持两种收集方式：
 * 1. v-model 双向绑定（实时同步值变化）
 * 2. collect() 手动批量收集（如表单提交时统一收值）
 */
export interface IFieldCollector {
  /** 字段名 */
  readonly fieldName: string
  /** 响应式当前值 */
  readonly value: import('vue').Ref<any>
  /** 响应式错误信息列表 */
  readonly errors: import('vue').Ref<string[]>
  /** 手动收集当前字段值 */
  collect(): any
  /** 以编程方式设置字段值 */
  setValue(val: any): void
  /** 重置值为空并清除错误 */
  reset(): void
  /** 执行校验，返回是否通过 */
  validate(): Promise<boolean>
}

/**
 * useFieldCollector 构造选项
 */
export interface FieldCollectorOptions {
  /** 字段配置 */
  fieldConfig: import('./list-table').FieldConfig
  /** 外部 v-model 绑定值 */
  modelValue?: any
  /** v-model 值更新回调 */
  onUpdate?: (value: any) => void
}
