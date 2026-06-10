import { reactive, computed } from 'vue'
import type { FieldConfig } from '@/types/list-table'
import { saveExtensionFields, updateExtensionFields } from '@/api/modules/fieldConfig'
import type { ExtFieldSaveParams, ExtFieldSaveResult } from '@/api/modules/fieldConfig'

/**
 * 扩展字段保存操作状态
 */
export interface ExtFieldSaveState {
  loading: boolean
  error: Error | null
  data: ExtFieldSaveResult | null
}

/**
 * useExtFieldSave 返回值
 */
export interface IExtFieldSave {
  state: ExtFieldSaveState
  isSuccess: import('vue').ComputedRef<boolean>
  /**
   * 从表单数据中分离扩展字段并保存
   * @param formData 完整表单数据
   * @param fieldConfigs 字段配置列表（含 isExtension 标记）
   * @param viewCode 视图编码
   * @param recordId 主记录 ID（编辑时传入，新增时 undefined）
   * @param extRecordId 扩展字段记录 ID（编辑时传入，新增时 undefined）
   */
  saveExtFields: (
    formData: Record<string, unknown>,
    fieldConfigs: FieldConfig[],
    viewCode: string,
    recordId: string | number,
    extRecordId?: string | number
  ) => Promise<ExtFieldSaveResult | null>
  reset: () => void
}

/**
 * 分离扩展字段值
 */
export function extractExtensionFields(
  formData: Record<string, unknown>,
  fieldConfigs: FieldConfig[]
): Record<string, unknown> {
  const extFields: Record<string, unknown> = {}
  for (const config of fieldConfigs) {
    if (config.isExtension && config.field in formData) {
      extFields[config.field] = formData[config.field]
    }
  }
  return extFields
}

/**
 * 扩展字段保存组合式函数
 *
 * 从完整表单数据中自动分离扩展字段（isExtension=true），
 * 调用扩展字段专用 API 进行保存。与 useMutation（主表字段保存）配合使用。
 *
 * @example
 * ```ts
 * const { state, saveExtFields } = useExtFieldSave({
 *   onSuccess: (data) => emit('ext-saved', data),
 * })
 *
 * async function handleSave(formData: Record<string, unknown>) {
 *   // 1. 先保存主表字段
 *   await mainMutate(formData)
 *   // 2. 再保存扩展字段
 *   await saveExtFields(formData, fieldConfigs, viewCode, recordId, extRecordId)
 * }
 * ```
 */
export function useExtFieldSave(
  options: {
    onSuccess?: (data: ExtFieldSaveResult) => void
    onError?: (error: Error) => void
  } = {}
): IExtFieldSave {
  const { onSuccess, onError } = options

  const state = reactive<ExtFieldSaveState>({
    loading: false,
    error: null,
    data: null
  })

  const isSuccess = computed(() => state.error === null && state.data != null)

  async function saveExtFields(
    formData: Record<string, unknown>,
    fieldConfigs: FieldConfig[],
    viewCode: string,
    recordId: string | number,
    extRecordId?: string | number
  ): Promise<ExtFieldSaveResult | null> {
    state.loading = true
    state.error = null
    state.data = null

    try {
      const extFields = extractExtensionFields(formData, fieldConfigs)

      if (Object.keys(extFields).length === 0) {
        state.loading = false
        return null
      }

      const params: ExtFieldSaveParams = {
        viewCode,
        recordId,
        extFields
      }

      let result: ExtFieldSaveResult
      if (extRecordId != null) {
        result = await updateExtensionFields(extRecordId, params)
      } else {
        result = await saveExtensionFields(params)
      }

      state.data = result
      onSuccess?.(result)
      return result
    } catch (err) {
      const error = err instanceof Error ? err : new Error(String(err))
      state.error = error
      onError?.(error)
      return null
    } finally {
      state.loading = false
    }
  }

  function reset(): void {
    state.loading = false
    state.error = null
    state.data = null
  }

  return {
    state,
    isSuccess,
    saveExtFields,
    reset
  }
}
