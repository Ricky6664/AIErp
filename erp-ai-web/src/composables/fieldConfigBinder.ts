import type { FieldConfig, FieldValidationRule } from '@/types/list-table'
import type { ValidatorRule } from '@/types/basic-input'

/**
 * 将 FieldValidationRule 转换为组件层 ValidatorRule
 * FieldConfig 使用 FieldValidationRule（表单层），录入组件使用 ValidatorRule（组件层）
 */
export function normalizeRule(rule: FieldValidationRule): ValidatorRule {
  const normalized: ValidatorRule = {
    message: rule.message
  }

  switch (rule.type) {
    case 'required':
      normalized.required = true
      break
    case 'min':
      normalized.min = rule.value as number | undefined
      break
    case 'max':
      normalized.max = rule.value as number | undefined
      break
    case 'pattern': {
      const pattern = rule.value
      normalized.pattern =
        typeof pattern === 'string' ? new RegExp(pattern) : (pattern as RegExp | undefined)
      break
    }
    case 'custom':
      if (rule.validator) {
        normalized.validator = async (value: unknown) => {
          const result = rule.validator!(value)
          return typeof result === 'string' ? false : result
        }
      }
      break
  }

  return normalized
}

/**
 * 从 FieldConfig 中提取组件层校验规则列表
 */
export function extractRules(fieldConfig?: FieldConfig): ValidatorRule[] {
  if (!fieldConfig?.rules || fieldConfig.rules.length === 0) return []
  return fieldConfig.rules.map(normalizeRule)
}

/**
 * 字段配置绑定器 — 桥接 FieldConfig 与表单/组件状态
 *
 * 职责：
 * - 提取并规范化校验规则（FieldValidationRule → ValidatorRule）
 * - 提供配置提取便捷方法
 */
export function useFieldConfigBinder(fieldConfig?: FieldConfig) {
  const rules = extractRules(fieldConfig)

  function getLabel(): string {
    return fieldConfig?.label || fieldConfig?.title || fieldConfig?.field || ''
  }

  function getPlaceholder(): string {
    return fieldConfig?.placeholder || `请输入${getLabel()}`
  }

  function isRequired(): boolean {
    return fieldConfig?.required === true || rules.some((r) => r.required === true)
  }

  function isReadonly(): boolean {
    return fieldConfig?.readonly === true
  }

  return {
    rules,
    getLabel,
    getPlaceholder,
    isRequired,
    isReadonly
  }
}
