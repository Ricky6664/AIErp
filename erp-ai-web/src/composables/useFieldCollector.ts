import { ref, watch } from 'vue'
import type { IFieldCollector, FieldCollectorOptions } from '@/types/basic-input'
import type { ValidatorRule } from '@/types/basic-input'
import { extractRules } from './fieldConfigBinder'

/**
 * 字段值收集器组合式函数 — 为多行文本输入框（ErpTextarea）及其他录入组件
 * 提供统一的字段值收集能力。
 *
 * 支持两种收集方式：
 * 1. v-model 双向绑定 — 值变更实时通过 onUpdate 回调向上同步
 * 2. collect() 手动收集 — 表单提交时调用 collect() 获取当前值
 *
 * @example
 * ```ts
 * const collector = useFieldCollector({
 *   fieldConfig: { field: 'remark', fieldType: 'textarea', rules: [...] },
 *   modelValue: props.modelValue,
 *   onUpdate: (v) => emit('update:modelValue', v)
 * })
 * // 在模板中使用
 * // <ErpTextarea v-model="collector.value.value" :rules="collector.rules" />
 * // 提交时
 * const formData = collector.collect()
 * const ok = await collector.validate()
 * ```
 */
export function useFieldCollector(options: FieldCollectorOptions): IFieldCollector {
  const { fieldConfig } = options

  const innerValue = ref(options.modelValue)
  const errors = ref<string[]>([])
  const rules = extractRules(fieldConfig)

  // 同步外部 modelValue 变化（父组件 v-model 更新时）
  watch(
    () => options.modelValue,
    (newVal) => {
      innerValue.value = newVal
    }
  )

  // 内部值变化时向上同步（子组件值变更时）
  watch(innerValue, (newVal, oldVal) => {
    if (newVal !== oldVal) {
      options.onUpdate?.(newVal)
    }
  })

  function collect(): any {
    return innerValue.value
  }

  function setValue(val: any): void {
    innerValue.value = val
    errors.value = []
  }

  function reset(): void {
    innerValue.value = ''
    errors.value = []
    options.onUpdate?.('')
  }

  function collectErrors(value: unknown, ruleList: ValidatorRule[]): string[] {
    if (!ruleList || ruleList.length === 0) return []
    const msgs: string[] = []
    for (const rule of ruleList) {
      if (rule.required && (value === undefined || value === null || value === '')) {
        msgs.push(rule.message || '此项为必填')
        continue
      }
      if (rule.min !== undefined && typeof value === 'string' && value.length < rule.min) {
        msgs.push(rule.message || `最少 ${rule.min} 个字符`)
        continue
      }
      if (rule.max !== undefined && typeof value === 'string' && value.length > rule.max) {
        msgs.push(rule.message || `最多 ${rule.max} 个字符`)
        continue
      }
      if (rule.pattern && typeof value === 'string' && !rule.pattern.test(value)) {
        msgs.push(rule.message || '格式不正确')
        continue
      }
    }
    return msgs
  }

  async function runCustomValidators(value: unknown, ruleList: ValidatorRule[]): Promise<string[]> {
    if (!ruleList || ruleList.length === 0) return []
    const msgs: string[] = []
    for (const rule of ruleList) {
      if (rule.validator) {
        try {
          const result = await rule.validator(value)
          if (!result) {
            msgs.push(rule.message || '校验不通过')
          }
        } catch {
          msgs.push(rule.message || '校验异常')
        }
      }
    }
    return msgs
  }

  async function validate(): Promise<boolean> {
    let allErrors: string[] = collectErrors(innerValue.value, rules)
    const customErrors = await runCustomValidators(innerValue.value, rules)
    allErrors = [...allErrors, ...customErrors]
    errors.value = allErrors
    return allErrors.length === 0
  }

  return {
    fieldName: fieldConfig.field,
    value: innerValue,
    errors,
    collect,
    setValue,
    reset,
    validate
  }
}
