import { ref, computed } from 'vue'
import type { FieldValidationRule } from '@/types/list-table'
import type { FormFieldConfig } from '@/types/master-form'

export interface ValidationResult {
  valid: boolean
  errors: Record<string, string[]>
}

export function useFormValidation() {
  const errors = ref<Record<string, string[]>>({})

  function validateRule(value: unknown, rule: FieldValidationRule): string | null {
    switch (rule.type) {
      case 'required': {
        const isEmpty = value === undefined || value === null || value === ''
        if (isEmpty) return rule.message || '此字段为必填项'
        break
      }
      case 'min': {
        const numVal = Number(value)
        if (isNaN(numVal) || numVal < (rule.value as number)) {
          return rule.message || `最小值为${rule.value}`
        }
        break
      }
      case 'max': {
        const numVal = Number(value)
        if (isNaN(numVal) || numVal > (rule.value as number)) {
          return rule.message || `最大值为${rule.value}`
        }
        break
      }
      case 'pattern': {
        const pattern = rule.value as RegExp | string
        const regex = typeof pattern === 'string' ? new RegExp(pattern) : pattern
        if (value !== undefined && value !== null && value !== '' && !regex.test(String(value))) {
          return rule.message || '格式不正确'
        }
        break
      }
      case 'custom': {
        if (rule.validator) {
          const result = rule.validator(value)
          if (typeof result === 'string') return result
          if (result === false) return rule.message || '校验不通过'
        }
        break
      }
    }
    return null
  }

  function validateField(value: unknown, rules?: FieldValidationRule[]): string[] {
    if (!rules || rules.length === 0) return []
    const messages: string[] = []
    for (const rule of rules) {
      const msg = validateRule(value, rule)
      if (msg) messages.push(msg)
    }
    return messages
  }

  function validateForm(
    data: Record<string, unknown>,
    fieldConfigs: FormFieldConfig[]
  ): ValidationResult {
    const newErrors: Record<string, string[]> = {}
    let valid = true

    for (const config of fieldConfigs) {
      if (config.visible === false) continue
      const value = data[config.field]
      const rules = config.rules
      if (!rules || rules.length === 0) continue

      const fieldErrors = validateField(value, rules)
      if (fieldErrors.length > 0) {
        newErrors[config.field] = fieldErrors
        valid = false
      }
    }

    errors.value = newErrors
    return { valid, errors: newErrors }
  }

  function clearValidate(field?: string) {
    if (field) {
      const newErrors = { ...errors.value }
      delete newErrors[field]
      errors.value = newErrors
    } else {
      errors.value = {}
    }
  }

  const hasErrors = computed(() => Object.keys(errors.value).length > 0)

  return { errors, hasErrors, validateField, validateForm, clearValidate }
}
