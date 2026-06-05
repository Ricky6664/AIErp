/**
 * MasterForm 主表单录入区 & FormField 表单字段渲染器 — 类型定义
 *
 * ## 配置项说明
 *
 * ### 表单布局 (FormLayoutConfig)
 * | 配置项 | 类型 | 默认值 | 说明 |
 * |--------|------|--------|------|
 * | mode | 'horizontal'\|'vertical'\|'inline' | 'horizontal' | 表单布局模式 |
 * | labelWidth | number\|string | '100px' | 标签宽度 |
 * | labelPosition | 'left'\|'right'\|'top' | 'right' | 标签位置 |
 * | columns | number | 1 | 栅格列数(24等分) |
 * | groups | FormGroupConfig[] | — | 分组配置 |
 * | disabled | boolean | false | 是否禁用整个表单 |
 * | size | 'small'\|'default'\|'large' | 'default' | 表单尺寸 |
 *
 * ### 表单字段配置 (FormFieldConfig)
 * | 配置项 | 类型 | 默认值 | 说明 |
 * |--------|------|--------|------|
 * | field | string | — | 字段名(必填) |
 * | fieldType | FormFieldType | — | 字段类型,决定渲染哪个录入组件(必填) |
 * | colSpan | number | 24 | 栅格列数(24=整行,12=半行,8=1/3行) |
 * | labelWidth | number\|string | — | 单字段标签宽度(覆盖表单级) |
 * | label | string | — | 标签文本(覆盖自动生成) |
 * | fullRow | boolean | false | 是否独占整行 |
 * | group | string | — | 所属分组key |
 * | tooltip | string | — | 提示文本(hover显示) |
 * | defaultValue | unknown | — | 字段默认值 |
 * | visible | boolean | true | 是否可见(联动引擎可控制) |
 * | readonly | boolean | false | 是否只读 |
 * | required | boolean | false | 是否必填 |
 * | placeholder | string | — | 占位提示文本 |
 * | rules | FieldValidationRule[] | — | 校验规则列表 |
 * | linkages | FieldLinkageRule[] | — | 联动规则列表 |
 * | componentProps | Record<string,unknown> | — | 透传录入组件的专属属性 |
 * | options | FormFieldOption[] | — | 下拉/选择类组件的静态选项 |
 * | optionsResolver | function | — | 异步选项加载器(远程搜索/懒加载) |
 *
 * ### 字段类型映射 (FormFieldType → 录入组件)
 * | fieldType | 对应组件 | 说明 |
 * |-----------|---------|------|
 * | text | ElInput | 文本输入 |
 * | number | ElInputNumber | 数字输入 |
 * | textarea | ElInput(textarea) | 多行文本 |
 * | password | ElInput(password) | 密码输入 |
 * | date | ElDatePicker(date) | 日期选择 |
 * | datetime | ElDatePicker(datetime) | 日期时间选择 |
 * | time | ElTimePicker | 时间选择 |
 * | select | ElSelect | 下拉选择 |
 * | multi-select | ElSelect(multiple) | 多选下拉 |
 * | radio | ElRadioGroup | 单选按钮组 |
 * | checkbox | ElCheckboxGroup | 多选复选框组 |
 * | switch | ElSwitch | 开关 |
 * | tree-select | ElTreeSelect | 树形选择 |
 * | cascader | ElCascader | 级联选择 |
 * | upload | ElUpload | 文件上传 |
 * | image | ElUpload(image) | 图片上传 |
 * | richtext | TinyMCE | 富文本编辑 |
 * | color | ElColorPicker | 颜色选择 |
 * | rate | ElRate | 评分 |
 * | slider | ElSlider | 滑块 |
 */

import type { FieldConfig, FieldValidationRule, FieldLinkageRule } from './list-table'

export type { FieldLinkageRule }

// ============================================================
// 基础枚举类型
// ============================================================

/** 表单布局模式 */
export type FormLayoutMode = 'horizontal' | 'vertical' | 'inline'

/** 标签位置 */
export type LabelPosition = 'left' | 'right' | 'top'

/** 字段类型到录入组件的映射(20种标准录入组件) */
export type FormFieldType =
  | 'text'
  | 'number'
  | 'textarea'
  | 'password'
  | 'date'
  | 'datetime'
  | 'time'
  | 'select'
  | 'multi-select'
  | 'radio'
  | 'checkbox'
  | 'switch'
  | 'tree-select'
  | 'cascader'
  | 'upload'
  | 'image'
  | 'richtext'
  | 'color'
  | 'rate'
  | 'slider'

// ============================================================
// 选项数据
// ============================================================

/** 选项数据(下拉/选择/单选/多选/级联等组件通用) */
export interface FormFieldOption {
  /** 选项标签 */
  label: string
  /** 选项值 */
  value: string | number | boolean
  /** 是否禁用 */
  disabled?: boolean
  /** 子选项(级联/树形使用) */
  children?: FormFieldOption[]
}

// ============================================================
// 表单布局配置
// ============================================================

/** 表单分组配置 */
export interface FormGroupConfig {
  /** 分组标识 */
  key: string
  /** 分组标题 */
  title: string
  /** 是否可折叠 */
  collapsible?: boolean
  /** 默认折叠 */
  defaultCollapsed?: boolean
  /** 排序号 */
  sortOrder?: number
}

/** 表单布局配置 */
export interface FormLayoutConfig {
  /** 布局模式,默认'horizontal' */
  mode?: FormLayoutMode
  /** 标签宽度,默认'100px' */
  labelWidth?: number | string
  /** 标签位置,默认'right' */
  labelPosition?: LabelPosition
  /** 栅格列数,默认1 */
  columns?: number
  /** 分组配置 */
  groups?: FormGroupConfig[]
  /** 是否禁用整个表单 */
  disabled?: boolean
  /** 表单尺寸,默认'default' */
  size?: 'small' | 'default' | 'large'
}

// ============================================================
// 表单字段配置(扩展FieldConfig — 增加表单布局&数据源属性)
// ============================================================

/** 表单字段配置 */
export interface FormFieldConfig extends FieldConfig {
  /** 字段类型映射(必填),决定渲染哪个录入组件 */
  fieldType: FormFieldType
  /** 栅格列数(24栅格),默认24(整行) */
  colSpan?: number
  /** 单字段标签宽度(覆盖FormLayoutConfig.labelWidth) */
  labelWidth?: number | string
  /** 标签文本(覆盖自动生成) */
  label?: string
  /** 是否独占整行(与colSpan互斥,优先使用colSpan) */
  fullRow?: boolean
  /** 所属分组key(对应FormGroupConfig.key) */
  group?: string
  /** 提示文本(hover Tooltip显示) */
  tooltip?: string
  /** 字段默认值 */
  defaultValue?: unknown
  /** 是否可见(联动引擎可动态控制) */
  visible?: boolean
  /** 透传录入组件的专属属性(如ElInput的maxlength、ElSelect的filterable等) */
  componentProps?: Record<string, unknown>
  /** 下拉/选择/单选/多选/级联类组件的静态选项数据源 */
  options?: FormFieldOption[]
  /** 异步选项加载器(用于远程搜索/懒加载,返回Promise<选项列表>) */
  optionsResolver?: (params: Record<string, unknown>) => Promise<FormFieldOption[]>
}

// ============================================================
// FormField 表单字段渲染器
// ============================================================

/** FormField 组件 Props */
export interface FormFieldProps {
  /** v-model双向绑定值 */
  modelValue: unknown
  /** 字段配置(类型/校验/联动/布局) */
  fieldConfig: FormFieldConfig
  /** 只读/禁用模式 */
  disabled?: boolean
  /** 占位提示 */
  placeholder?: string
  /** 表单布局配置(用于继承标签宽度等) */
  layoutConfig?: FormLayoutConfig
}

/** FormField 组件 Emits */
export interface FormFieldEmits {
  /** v-model值变更事件 */
  'update:modelValue': [value: unknown]
  /** 值确认变更事件 */
  change: [value: unknown]
  /** 获得焦点 */
  focus: [event: FocusEvent]
  /** 失去焦点 */
  blur: [event: FocusEvent]
}

/** FormField 组件 Slots */
export interface FormFieldSlots {
  /** 前缀插槽(输入框前) */
  prefix?: () => unknown
  /** 后缀插槽(输入框后) */
  suffix?: () => unknown
  /** 默认内容插槽 */
  default?: () => unknown
  /** 自定义标签插槽 */
  label?: () => unknown
  /** 自定义错误信息插槽 */
  error?: () => unknown
}

/** FormField 组件暴露方法 */
export interface FormFieldExpose {
  /** 聚焦当前字段 */
  focus: () => void
  /** 失焦当前字段 */
  blur: () => void
  /** 校验当前字段,返回是否通过 */
  validate: () => Promise<boolean>
  /** 清除当前字段校验状态 */
  clearValidate: () => void
  /** 重置当前字段为默认值 */
  resetField: () => void
}

// ============================================================
// MasterForm 主表单录入区
// ============================================================

/** MasterForm 组件 Props */
export interface MasterFormProps {
  /** v-model双向绑定表单数据(字段名→值映射) */
  modelValue: Record<string, unknown>
  /** 字段配置列表 */
  fieldConfigs: FormFieldConfig[]
  /** 表单布局配置 */
  layoutConfig?: FormLayoutConfig
  /** 是否禁用整个表单 */
  disabled?: boolean
  /** 是否显示校验错误 */
  showValidation?: boolean
  /** 视图编码(用于字段配置缓存key区分不同业务页面) */
  viewCode?: string
  /** 是否启用表单联动引擎 */
  enableLinkage?: boolean
  /** 是否启用表单校验引擎 */
  enableValidation?: boolean
}

/** MasterForm 组件 Emits */
export interface MasterFormEmits {
  /** v-model数据变更事件 */
  'update:modelValue': [data: Record<string, unknown>]
  /** 字段值变更事件 */
  change: [field: string, value: unknown]
  /** 字段获得焦点 */
  'field-focus': [field: string, event: FocusEvent]
  /** 字段失去焦点 */
  'field-blur': [field: string, event: FocusEvent]
  /** 表单校验完成事件(校验引擎触发) */
  validate: [valid: boolean, errors: Record<string, string[]>]
}

/** MasterForm 组件 Slots */
export interface MasterFormSlots {
  /** 表单前缀插槽(表单上方) */
  prefix?: () => unknown
  /** 表单后缀插槽(表单下方) */
  suffix?: () => unknown
  /** 默认内容插槽 */
  default?: () => unknown
  /** 表单操作按钮区(提交/重置等) */
  'form-actions'?: () => unknown
  /** 动态字段插槽(按字段名,如 field-remark 覆盖 remark 字段的渲染) */
  [fieldSlot: `field-${string}`]: (() => unknown) | undefined
}

/** MasterForm 组件暴露方法 */
export interface MasterFormExpose {
  /** 校验整个表单,返回是否全部通过 */
  validate: () => Promise<boolean>
  /** 清除校验状态(可指定字段名清除单个,不传清除全部) */
  clearValidate: (field?: string) => void
  /** 重置表单到初始默认值 */
  resetFields: () => void
  /** 获取当前表单数据 */
  getFormData: () => Record<string, unknown>
  /** 设置表单数据(覆盖合并) */
  setFormData: (data: Record<string, unknown>) => void
  /** 获取指定字段的值 */
  getFieldValue: (field: string) => unknown
  /** 设置指定字段的值 */
  setFieldValue: (field: string, value: unknown) => void
  /** 滚动到指定字段 */
  scrollToField: (field: string) => void
  /** 获取所有字段的校验错误(字段名→错误信息列表) */
  getErrors: () => Record<string, string[]>
}
