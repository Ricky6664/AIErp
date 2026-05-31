import type { Directive, DirectiveBinding } from 'vue'
import { usePermissionStore } from '@/stores/modules/permission'

type PermissionValue = string | string[]

export function checkPermission(value: PermissionValue): boolean {
  if (!value || (Array.isArray(value) && value.length === 0)) return true
  const permissionStore = usePermissionStore()
  const perms = Array.isArray(value) ? value : [value]
  return perms.some((p) => permissionStore.permissions.includes(p))
}

export const permissionDirective: Directive<HTMLElement, PermissionValue> = {
  mounted(el: HTMLElement, binding: DirectiveBinding<PermissionValue>) {
    if (!checkPermission(binding.value)) {
      el.parentNode?.removeChild(el)
    }
  },

  updated(el: HTMLElement, binding: DirectiveBinding<PermissionValue>) {
    if (!checkPermission(binding.value)) {
      el.parentNode?.removeChild(el)
    }
  },

  unmounted(_el: HTMLElement) {
    // 清理工作：当前无持久化状态需清理，钩子保留供后续扩展
  }
}
