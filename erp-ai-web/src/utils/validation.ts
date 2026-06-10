import { i18n } from '@/i18n'

export interface ValidationRule {
  type: 'required' | 'format' | 'length' | 'range' | 'custom'
  subType?: string
  min?: number
  max?: number
}

function replacePlaceholders(template: string, params: Record<string, unknown>): string {
  return template.replace(/\$\{(\w+)\}/g, (_, key: string) => {
    const value = params[key]
    return value !== undefined && value !== null ? String(value) : ''
  })
}

export function renderValidationMessage(rule: ValidationRule, label: string): string {
  const key = rule.subType
    ? `validation.${rule.type}.${rule.subType}`
    : rule.type === 'required'
      ? 'validation.required'
      : `validation.${rule.type}.range`
  const template = i18n.global.t(key)
  return replacePlaceholders(template, { label, min: rule.min, max: rule.max })
}

export function getValidationMessages(rules: ValidationRule[], label: string): string {
  return rules.map((rule) => renderValidationMessage(rule, label)).join('\n')
}
