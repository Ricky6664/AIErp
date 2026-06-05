import { reactive, watch } from 'vue'
import type {
  FieldLinkageRule,
  LinkageConditionConfig,
  LinkageRuleConfig
} from '@/types/list-table'
import type { FormFieldConfig } from '@/types/master-form'

/** Options for watchFieldLinkages */
export interface WatchLinkagesOptions {
  /** If true, immediately trigger linkages when watcher is set up */
  immediate?: boolean
}

export interface FieldLinkageState {
  /** field visibility overrides (false = hidden by linkage) */
  visibility: Record<string, boolean>
  /** field disabled state overrides (true = disabled by linkage) */
  disabled: Record<string, boolean>
  /** field required overrides (true = required by linkage, false = optional by linkage) */
  required: Record<string, boolean>
}

export interface LinkageProcessResult {
  setValues: Array<{ field: string; value: unknown }>
  setOptions: Array<{ field: string; options: unknown[] }>
  visibilityChanges: Array<{ field: string; visible: boolean }>
  disabledChanges: Array<{ field: string; disabled: boolean }>
  requiredChanges: Array<{ field: string; required: boolean }>
}

export function useFormLinkage() {
  const state = reactive<FieldLinkageState>({
    visibility: {},
    disabled: {},
    required: {}
  })

  function collectLinkages(fieldConfigs: FormFieldConfig[]): FieldLinkageRule[] {
    return fieldConfigs.flatMap((f) => f.linkages ?? [])
  }

  /**
   * Evaluate a linkage condition against the current value.
   * If no condition is provided, the linkage always triggers.
   */
  function evaluateCondition(linkage: FieldLinkageRule, value: unknown): boolean {
    if (!linkage.condition) return true
    try {
      return linkage.condition(value)
    } catch {
      return false
    }
  }

  /**
   * Process all linkages triggered by a field value change.
   * Returns the set of changes to apply (side-effect free result).
   */
  function processLinkages(
    changedField: string,
    value: unknown,
    fieldConfigs: FormFieldConfig[]
  ): LinkageProcessResult {
    const result: LinkageProcessResult = {
      setValues: [],
      setOptions: [],
      visibilityChanges: [],
      disabledChanges: [],
      requiredChanges: []
    }

    const allLinkages = collectLinkages(fieldConfigs)

    for (const linkage of allLinkages) {
      if (linkage.triggerField !== changedField) continue
      if (!evaluateCondition(linkage, value)) continue

      switch (linkage.action) {
        case 'show':
          result.visibilityChanges.push({ field: linkage.targetField, visible: true })
          break
        case 'hide':
          result.visibilityChanges.push({ field: linkage.targetField, visible: false })
          break
        case 'enable':
          result.disabledChanges.push({ field: linkage.targetField, disabled: false })
          break
        case 'disable':
          result.disabledChanges.push({ field: linkage.targetField, disabled: true })
          break
        case 'setValue': {
          const paramValue = linkage.params?.value
          result.setValues.push({ field: linkage.targetField, value: paramValue })
          break
        }
        case 'setOptions': {
          const newOptions = linkage.params?.options
          if (Array.isArray(newOptions)) {
            result.setOptions.push({ field: linkage.targetField, options: newOptions })
          }
          break
        }
        case 'setRequired': {
          const required = linkage.params?.required !== false
          result.requiredChanges.push({ field: linkage.targetField, required })
          break
        }
      }
    }

    return result
  }

  /**
   * Apply linkage processing results to form state.
   * Returns the list of fields whose values changed (for cascade processing).
   */
  function applyLinkageResult(
    result: LinkageProcessResult,
    localData: Record<string, unknown>
  ): string[] {
    const changedFields: string[] = []

    for (const { field, visible } of result.visibilityChanges) {
      state.visibility[field] = visible
    }
    for (const { field, disabled } of result.disabledChanges) {
      state.disabled[field] = disabled
    }
    for (const { field, required } of result.requiredChanges) {
      state.required[field] = required
    }
    for (const { field, value } of result.setValues) {
      localData[field] = value
      changedFields.push(field)
    }

    return changedFields
  }

  /**
   * Apply setOptions results to mutable field configs.
   */
  function applyOptionsResult(result: LinkageProcessResult, fieldConfigs: FormFieldConfig[]): void {
    for (const { field, options } of result.setOptions) {
      const idx = fieldConfigs.findIndex((f) => f.field === field)
      if (idx !== -1) {
        fieldConfigs[idx] = {
          ...fieldConfigs[idx],
          options: options as FormFieldConfig['options']
        }
      }
    }
  }

  /**
   * Full linkage pipeline: process → apply → cascade.
   * Handles cascading linkages where setting a value triggers further linkages.
   */
  function executeLinkages(
    changedField: string,
    value: unknown,
    fieldConfigs: FormFieldConfig[],
    localData: Record<string, unknown>
  ): void {
    runLinkageChain(changedField, value, fieldConfigs, localData, new Set())
  }

  function runLinkageChain(
    changedField: string,
    value: unknown,
    fieldConfigs: FormFieldConfig[],
    localData: Record<string, unknown>,
    visited: Set<string>
  ): void {
    const chainKey = `${changedField}:${JSON.stringify(value)}`
    if (visited.has(chainKey)) return
    visited.add(chainKey)

    const result = processLinkages(changedField, value, fieldConfigs)
    applyOptionsResult(result, fieldConfigs)
    const changedFields = applyLinkageResult(result, localData)

    for (const field of changedFields) {
      runLinkageChain(field, localData[field], fieldConfigs, localData, visited)
    }
  }

  /**
   * Watch trigger fields on form data and auto-execute linkages when values change.
   * Uses a batching guard to prevent re-entry from setValue-triggered watch loops.
   * Returns a cleanup function that stops all watchers.
   */
  function watchFieldLinkages(
    formData: Record<string, unknown>,
    fieldConfigs: FormFieldConfig[],
    options?: WatchLinkagesOptions
  ): () => void {
    // Collect unique trigger fields across all linkage rules
    const triggerFields = new Set<string>()
    for (const config of fieldConfigs) {
      const linkages = config.linkages ?? []
      for (const linkage of linkages) {
        if (linkage.triggerField) {
          triggerFields.add(linkage.triggerField)
        }
      }
    }

    let processing = false
    const stopHandlers: (() => void)[] = []

    for (const field of triggerFields) {
      const stop = watch(
        () => formData[field],
        (newVal, oldVal) => {
          if (processing) return
          if (newVal === oldVal) return
          processing = true
          try {
            executeLinkages(field, newVal, fieldConfigs, formData)
          } finally {
            processing = false
          }
        },
        { immediate: options?.immediate ?? false }
      )
      stopHandlers.push(stop)
    }

    return () => {
      stopHandlers.forEach((s) => s())
    }
  }

  /**
   * Check if a field is visible, taking linkage overrides into account.
   */
  function isFieldVisible(fieldConfig: FormFieldConfig): boolean {
    if (fieldConfig.visible === false) return false
    if (state.visibility[fieldConfig.field] === false) return false
    return true
  }

  /**
   * Check if a field is disabled, taking linkage overrides into account.
   */
  function isFieldDisabled(fieldConfig: FormFieldConfig): boolean {
    if (state.disabled[fieldConfig.field] === true) return true
    return false
  }

  /**
   * Check if a field is required, taking linkage overrides into account.
   * Linkage override takes precedence over fieldConfig.required.
   */
  function isFieldRequired(fieldConfig: FormFieldConfig): boolean {
    if (fieldConfig.field in state.required) {
      return state.required[fieldConfig.field]
    }
    return fieldConfig.required === true
  }

  /**
   * Reset all linkage state.
   */
  function reset(): void {
    Object.keys(state.visibility).forEach((k) => delete state.visibility[k])
    Object.keys(state.disabled).forEach((k) => delete state.disabled[k])
    Object.keys(state.required).forEach((k) => delete state.required[k])
  }

  return {
    state,
    evaluateCondition,
    processLinkages,
    applyLinkageResult,
    applyOptionsResult,
    executeLinkages,
    watchFieldLinkages,
    isFieldVisible,
    isFieldDisabled,
    isFieldRequired,
    reset
  }
}

/**
 * 将 JSON 可序列化的条件配置转换为可执行的条件函数
 */
export function buildConditionFn(config: LinkageConditionConfig): (value: unknown) => boolean {
  const { operator, value } = config
  switch (operator) {
    case 'eq':
      return (v: unknown) => v === value
    case 'neq':
      return (v: unknown) => v !== value
    case 'gt':
      return (v: unknown) => Number(v) > Number(value)
    case 'gte':
      return (v: unknown) => Number(v) >= Number(value)
    case 'lt':
      return (v: unknown) => Number(v) < Number(value)
    case 'lte':
      return (v: unknown) => Number(v) <= Number(value)
    case 'in':
      return (v: unknown) => {
        if (!Array.isArray(value)) return false
        return (value as unknown[]).includes(v)
      }
    case 'notIn':
      return (v: unknown) => {
        if (!Array.isArray(value)) return true
        return !(value as unknown[]).includes(v)
      }
    case 'isEmpty':
      return (v: unknown) => v === null || v === undefined || v === ''
    case 'isNotEmpty':
      return (v: unknown) => v !== null && v !== undefined && v !== ''
    case 'startsWith':
      return (v: unknown) => String(v).startsWith(String(value))
    case 'endsWith':
      return (v: unknown) => String(v).endsWith(String(value))
    case 'contains':
      return (v: unknown) => String(v).includes(String(value))
    default:
      return () => false
  }
}

/**
 * 解析 JSON 可序列化的联动规则配置，转换为运行时 FieldLinkageRule[]
 */
export function parseLinkageJson(rules: LinkageRuleConfig[]): FieldLinkageRule[] {
  if (!Array.isArray(rules)) {
    throw new Error('联动规则配置必须是数组')
  }
  return rules.map((rule) => {
    if (!rule.triggerField || !rule.targetField || !rule.action) {
      throw new Error(
        `联动规则缺少必填字段(triggerField/targetField/action): ${JSON.stringify(rule)}`
      )
    }
    return {
      triggerField: rule.triggerField,
      targetField: rule.targetField,
      action: rule.action,
      condition: rule.condition ? buildConditionFn(rule.condition) : undefined,
      params: rule.params
    }
  })
}
