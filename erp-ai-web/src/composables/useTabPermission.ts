import { computed, type Ref, type ComputedRef } from 'vue'
import { usePermission } from '@/composables/usePermission'
import type { RelatedTab } from '@/types/relation-info'

export interface UseTabPermissionReturn {
  /** 过滤后的可见标签页列表（排除 hidden 和无权限项） */
  visibleTabs: ComputedRef<RelatedTab[]>
  /** 原始标签页中因无权限被隐藏的数量 */
  permissionHiddenCount: ComputedRef<number>
  /** 检查单个标签页是否有权限访问 */
  canAccessTab: (tab: RelatedTab) => boolean
}

/**
 * 标签页权限过滤组合式函数
 * 根据用户权限和 hidden 属性过滤标签页列表
 *
 * @param tabs - 标签页配置列表（响应式）
 * @param activeGroup - 当前选中的分组 key（可选，用于分组筛选）
 */
export function useTabPermission(
  tabs: Ref<RelatedTab[]> | ComputedRef<RelatedTab[]>,
  activeGroup?: Ref<string> | ComputedRef<string>
): UseTabPermissionReturn {
  const { hasPermission } = usePermission()

  /** 检查单个标签页是否可访问（未隐藏且有权限） */
  function canAccessTab(tab: RelatedTab): boolean {
    if (tab.hidden) return false
    if (tab.permission && !hasPermission(tab.permission)) return false
    return true
  }

  /** 过滤后的可见标签页 */
  const visibleTabs = computed<RelatedTab[]>(() => {
    let list = tabs.value.filter((t) => canAccessTab(t))
    if (activeGroup && activeGroup.value) {
      list = list.filter((t) => t.group === activeGroup.value)
    }
    return list
  })

  /** 因权限被隐藏的标签页数量 */
  const permissionHiddenCount = computed<number>(() => {
    return tabs.value.filter((t) => !t.hidden && t.permission && !hasPermission(t.permission))
      .length
  })

  return {
    visibleTabs,
    permissionHiddenCount,
    canAccessTab
  }
}
