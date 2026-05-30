import type { Directive, DirectiveBinding } from 'vue'
import { usePermissionStore } from '@/stores/modules/permission'

export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string[]>) {
    const requiredPermissions = binding.value
    if (!requiredPermissions?.length) return

    const permissionStore = usePermissionStore()
    const hasAuth = requiredPermissions.some((p) => permissionStore.permissions.includes(p))

    if (!hasAuth) {
      el.parentNode?.removeChild(el)
    }
  }
}
