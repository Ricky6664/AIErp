import type { FieldConfig, FieldValidationRule, FieldLinkageRule } from '@/types/list-table'

/** 字段配置 API 响应 — 单个字段配置项 */
export interface FieldConfigItem extends FieldConfig {
  /** 字段名 */
  field: string
  /** 字段显示标题 */
  title?: string
  /** 字段类型映射（text/number/date/textarea/select等） */
  fieldType?: string
  /** 校验规则 */
  rules?: FieldValidationRule[]
  /** 联动规则 */
  linkages?: FieldLinkageRule[]
  /** 是否只读 */
  readonly?: boolean
  /** 是否必填 */
  required?: boolean
  /** 占位提示 */
  placeholder?: string
  /** 默认值 */
  defaultValue?: unknown
  /** 栅格列数 */
  colSpan?: number
  /** 选项数据（select/radio/checkbox类组件） */
  options?: Array<{ label: string; value: string | number | boolean; disabled?: boolean }>
}

/** 字段配置列表查询参数 */
export interface FieldConfigQuery {
  /** 视图编码（对应业务单据类型，如 purchase_order、sale_order） */
  viewCode: string
}

/** 字段配置列表响应 */
export interface FieldConfigListResponse {
  /** 视图编码 */
  viewCode: string
  /** 字段配置列表 */
  fields: FieldConfigItem[]
}
