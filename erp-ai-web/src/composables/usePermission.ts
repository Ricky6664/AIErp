import { useUserStore } from '@/stores/modules/user'

const SUPERADMIN_ROLE = 'superadmin'

export function usePermission() {
  const userStore = useUserStore()

  function isSuperAdmin(): boolean {
    return userStore.roles.includes(SUPERADMIN_ROLE)
  }

  function hasPermission(permissionCode: string): boolean {
    if (!permissionCode) return false
    if (isSuperAdmin()) return true
    return userStore.permissions.includes(permissionCode)
  }

  function hasAnyPermission(permissionCodes: string[]): boolean {
    if (!permissionCodes || permissionCodes.length === 0) return false
    if (isSuperAdmin()) return true
    return permissionCodes.some((code) => userStore.permissions.includes(code))
  }

  function hasRole(roleCode: string): boolean {
    if (!roleCode) return false
    if (isSuperAdmin()) return true
    return userStore.roles.includes(roleCode)
  }

  return {
    hasPermission,
    hasAnyPermission,
    hasRole
  }
}
