import { reactive } from 'vue'
import type { FieldLinkageRule } from '@/types/list-table'
import type { FormFieldConfig } from '@/types/master-form'

export interface FieldLinkageState {
  /** field visibility overrides (false = hidden by linkage) */
  visibility: Record<string, boolean>
  /** field disabled state overrides (true = disabled by linkage) */
  disabled: Record<string, boolean>
}

export interface LinkageProcessResult {
  setValues: Array<{ field: string; value: unknown }>
  setOptions: Array<{ field: string; options: unknown[] }>
  visibilityChanges: Array<{ field: string; visible: boolean }>
  disabledChanges: Array<{ field: string; disabled: boolean }>
}

export function useFormLinkage() {
  const state = reactive<FieldLinkageState>({
    visibility: {},
    disabled: {}
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
      disabledChanges: []
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
   * Reset all linkage state.
   */
  function reset(): void {
    Object.keys(state.visibility).forEach((k) => delete state.visibility[k])
    Object.keys(state.disabled).forEach((k) => delete state.disabled[k])
  }

  return {
    state,
    evaluateCondition,
    processLinkages,
    applyLinkageResult,
    applyOptionsResult,
    executeLinkages,
    isFieldVisible,
    isFieldDisabled,
    reset
  }
}
