import { reactive, computed } from 'vue'

/**
 * 保存操作状态
 */
export interface MutationState<TData = unknown> {
  loading: boolean
  error: Error | null
  data: TData | null
}

/**
 * useMutation 构造选项
 */
export interface MutationOptions<TData = unknown, TInput = Record<string, unknown>> {
  /** 创建函数（POST），接收表单数据，返回保存结果 */
  create?: (data: TInput) => Promise<TData>
  /** 更新函数（PUT），接收表单数据（含 id/version），返回保存结果 */
  update?: (data: TInput) => Promise<TData>
  /** 保存成功回调 */
  onSuccess?: (data: TData) => void
  /** 保存失败回调 */
  onError?: (error: Error) => void
  /** 是否自动判断新增/编辑模式（数据含 id 字段则走 update，否则走 create） */
  autoDetectMode?: boolean
}

/**
 * useMutation 返回值
 */
export interface IMutation<TData = unknown, TInput = Record<string, unknown>> {
  /** 响应式状态 */
  state: MutationState<TData>
  /** 保存是否成功 */
  isSuccess: import('vue').ComputedRef<boolean>
  /** 执行保存 */
  mutate: (data: TInput) => Promise<TData | null>
  /** 重置状态 */
  reset: () => void
  /** 执行保存并携带乐观锁版本号 */
  mutateWithVersion: (data: TInput & { version?: number }) => Promise<TData | null>
}

/**
 * 保存操作组合式函数 — 管理表单数据的创建/更新操作状态
 *
 * 支持两种模式：
 * 1. 显式模式 — 调用方自行判断新增/编辑，分别传入 create 和 update
 * 2. 自动模式 — autoDetectMode: true，根据数据中是否有 id 字段自动选择
 *
 * 集成乐观锁：update 时自动携带 version 字段，后端校验版本冲突
 *
 * @example
 * ```ts
 * const { state, mutate, reset } = useMutation({
 *   create: (data) => fieldConfigApi.create(data),
 *   update: (data) => fieldConfigApi.update(data.id, data),
 *   onSuccess: (data) => emit('saved', data),
 *   autoDetectMode: true
 * })
 *
 * // 在保存流程中使用
 * async function handleSave(formData: Record<string, unknown>) {
 *   const result = await mutate(formData)
 *   if (result) { ElMessage.success('保存成功') }
 * }
 * ```
 */
export function useMutation<TData = unknown, TInput = Record<string, unknown>>(
  options: MutationOptions<TData, TInput> = {}
): IMutation<TData, TInput> {
  const { create, update, onSuccess, onError, autoDetectMode = true } = options

  const state = reactive<MutationState<TData>>({
    loading: false,
    error: null,
    data: null
  })

  const isSuccess = computed(() => state.error === null && state.data != null)

  async function mutate(data: TInput): Promise<TData | null> {
    state.loading = true
    state.error = null
    state.data = null

    try {
      let result: TData

      if (autoDetectMode && update && (data as Record<string, unknown>).id != null) {
        result = await update(data)
      } else if (create) {
        result = await create(data)
      } else if (update) {
        result = await update(data)
      } else {
        throw new Error('至少需要提供 create 或 update 函数')
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

  async function mutateWithVersion(data: TInput & { version?: number }): Promise<TData | null> {
    return mutate(data)
  }

  function reset(): void {
    state.loading = false
    state.error = null
    state.data = null
  }

  return {
    state,
    isSuccess,
    mutate,
    reset,
    mutateWithVersion
  }
}
