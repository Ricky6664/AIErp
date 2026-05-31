import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/modules/user'

type PermissionValue = string | string[]

/**
 * 核心权限检查逻辑
 * 从userStore获取当前用户权限列表，判断是否包含目标权限码
 */
export function checkPermission(value: PermissionValue): boolean {
  if (!value || (Array.isArray(value) && value.length === 0)) return true
  const userStore = useUserStore()
  if (userStore.roles.includes('admin')) return true
  if (Array.isArray(value)) return value.some((p) => userStore.permissions.includes(p))
  return userStore.permissions.includes(value)
}

/**
 * 权限不足时的DOM处理
 * 从DOM中移除无权限元素
 */
export function removeElement(el: HTMLElement): void {
  el.parentNode?.removeChild(el)
}

export const permissionDirective: Directive<HTMLElement, PermissionValue> = {
  mounted(el: HTMLElement, binding: DirectiveBinding<PermissionValue>) {
    if (!checkPermission(binding.value)) {
      removeElement(el)
    }
  },

  updated(el: HTMLElement, binding: DirectiveBinding<PermissionValue>) {
    if (!checkPermission(binding.value)) {
      removeElement(el)
    }
  },

  unmounted(_el: HTMLElement) {
    // 清理工作：当前无持久化状态需清理，钩子保留供后续扩展
  }
}
