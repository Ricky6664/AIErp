import { ref, computed } from 'vue'
import type { IFieldCollector } from '@/types/basic-input'

export interface FormValidationResult {
  valid: boolean
  fieldErrors: Record<string, string[]>
}

/**
 * 表单校验器 — 编排多个字段收集器的校验
 *
 * 与 useFormValidation（针对 FormFieldConfig 数组校验）互补：
 * FormValidator 面向 IFieldCollector 实例数组，由字段收集器自行校验并汇总结果。
 */
export function useFormValidator() {
  const collectors = ref<IFieldCollector[]>([])
  const validating = ref(false)

  function register(collector: IFieldCollector): void {
    collectors.value.push(collector)
  }

  function unregister(fieldName: string): void {
    collectors.value = collectors.value.filter((c) => c.fieldName !== fieldName)
  }

  async function validateAll(): Promise<FormValidationResult> {
    validating.value = true
    const fieldErrors: Record<string, string[]> = {}
    let valid = true

    try {
      const results = await Promise.all(collectors.value.map((c) => c.validate()))
      for (let i = 0; i < collectors.value.length; i++) {
        const collector = collectors.value[i]
        if (!results[i]) {
          valid = false
          fieldErrors[collector.fieldName] = [...collector.errors.value]
        }
      }
    } finally {
      validating.value = false
    }

    return { valid, fieldErrors }
  }

  function collectAll(): Record<string, unknown> {
    const data: Record<string, unknown> = {}
    for (const collector of collectors.value) {
      data[collector.fieldName] = collector.collect()
    }
    return data
  }

  function resetAll(): void {
    for (const collector of collectors.value) {
      collector.reset()
    }
  }

  const allErrors = computed(() => {
    const merged: Record<string, string[]> = {}
    for (const c of collectors.value) {
      if (c.errors.value.length > 0) {
        merged[c.fieldName] = [...c.errors.value]
      }
    }
    return merged
  })

  const hasErrors = computed(() => Object.keys(allErrors.value).length > 0)

  return {
    collectors,
    validating,
    register,
    unregister,
    validateAll,
    collectAll,
    resetAll,
    allErrors,
    hasErrors
  }
}
