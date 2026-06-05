import { ref, computed } from 'vue'
import type { ActionItem, UseActionBarOptions } from '@/types/action-bar'

/**
 * 功能操作区组合式函数
 * 管理操作按钮的配置、状态和生命周期
 */
export function useActionBar(options?: UseActionBarOptions) {
  const gap = options?.gap ?? 8
  const showDivider = options?.showDivider ?? true

  /** 操作按钮配置列表 */
  const actions = ref<ActionItem[]>([])

  /** 操作栏加载状态 */
  const loading = ref(false)

  /** 过滤隐藏项后的可见操作按钮 */
  const visibleActions = computed(() => actions.value.filter((item) => !item.hidden))

  /** 左侧操作按钮 */
  const leftActions = computed<ActionItem[]>(() => {
    const visible = visibleActions.value
    const primaryActions = visible.filter((item) => item.type && item.type !== 'default')
    const defaultActions = visible.filter((item) => !item.type || item.type === 'default')
    const midPoint = Math.ceil(defaultActions.length / 2)
    return [...primaryActions, ...defaultActions.slice(0, midPoint)]
  })

  /** 右侧操作按钮 */
  const rightActions = computed<ActionItem[]>(() => {
    const visible = visibleActions.value
    const defaultActions = visible.filter((item) => !item.type || item.type === 'default')
    const midPoint = Math.ceil(defaultActions.length / 2)
    return defaultActions.slice(midPoint)
  })

  /** 设置操作按钮配置 */
  function setActions(items: ActionItem[]): void {
    actions.value = items
  }

  /** 更新指定按钮的加载状态 */
  function setLoading(key: string, isLoading: boolean): void {
    const target = actions.value.find((item) => item.key === key)
    if (target) {
      target.loading = isLoading
    }
  }

  /** 设置指定按钮的禁用状态 */
  function setDisabled(key: string, disabled: boolean): void {
    const target = actions.value.find((item) => item.key === key)
    if (target) {
      target.disabled = disabled
    }
  }

  /** 设置指定按钮的隐藏状态 */
  function setHidden(key: string, hidden: boolean): void {
    const target = actions.value.find((item) => item.key === key)
    if (target) {
      target.hidden = hidden
    }
  }

  /** 获取默认的列表页操作按钮配置 */
  function getDefaultListActions(): ActionItem[] {
    return [
      { key: 'add', label: '新增', type: 'primary', action: 'add' },
      { key: 'edit', label: '编辑', type: 'default', action: 'edit' },
      { key: 'delete', label: '删除', type: 'danger', action: 'delete' },
      { key: 'export', label: '导出', type: 'default', action: 'export' },
      { key: 'import', label: '导入', type: 'default', action: 'import' },
      { key: 'refresh', label: '刷新', type: 'default', action: 'refresh' }
    ]
  }

  return {
    actions,
    loading,
    visibleActions,
    leftActions,
    rightActions,
    gap,
    showDivider,
    setActions,
    setLoading,
    setDisabled,
    setHidden,
    getDefaultListActions
  }
}
