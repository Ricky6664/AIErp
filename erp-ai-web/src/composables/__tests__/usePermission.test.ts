import { describe, it, expect, beforeEach, vi } from 'vitest'

// Mock Pinia store
const mockRoles: string[] = []
const mockPermissions: string[] = []

vi.mock('@/stores/modules/user', () => ({
  useUserStore: () => ({
    roles: mockRoles,
    permissions: mockPermissions
  })
}))

describe('usePermission composable', () => {
  beforeEach(() => {
    mockRoles.length = 0
    mockPermissions.length = 0
  })

  describe('hasPermission', () => {
    it('should return true when user has the permission', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      mockPermissions.push('system:user:add')
      const { hasPermission } = usePermission()
      expect(hasPermission('system:user:add')).toBe(true)
    })

    it('should return false when user does not have the permission', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      const { hasPermission } = usePermission()
      expect(hasPermission('system:user:delete')).toBe(false)
    })

    it('should return false for empty permission code', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      const { hasPermission } = usePermission()
      expect(hasPermission('')).toBe(false)
    })

    it('should return true for superadmin regardless of permissions', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      mockRoles.push('superadmin')
      const { hasPermission } = usePermission()
      expect(hasPermission('any:permission:action')).toBe(true)
    })
  })

  describe('hasAnyPermission', () => {
    it('should return true if any one permission matches', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      mockPermissions.push('system:user:edit')
      const { hasAnyPermission } = usePermission()
      expect(hasAnyPermission(['system:user:add', 'system:user:edit'])).toBe(true)
    })

    it('should return false if none match', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      mockPermissions.push('system:role:view')
      const { hasAnyPermission } = usePermission()
      expect(hasAnyPermission(['system:user:add', 'system:user:delete'])).toBe(false)
    })

    it('should return false for empty array', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      const { hasAnyPermission } = usePermission()
      expect(hasAnyPermission([])).toBe(false)
    })

    it('should return true for superadmin regardless of permission set', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      mockRoles.push('superadmin')
      const { hasAnyPermission } = usePermission()
      expect(hasAnyPermission(['some:permission:action'])).toBe(true)
    })
  })

  describe('hasRole', () => {
    it('should return true when user has the role', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      mockRoles.push('manager')
      const { hasRole } = usePermission()
      expect(hasRole('manager')).toBe(true)
    })

    it('should return false when user does not have the role', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      const { hasRole } = usePermission()
      expect(hasRole('admin')).toBe(false)
    })

    it('should return false for empty role code', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      const { hasRole } = usePermission()
      expect(hasRole('')).toBe(false)
    })

    it('should return true for superadmin even for roles they do not explicitly have', async () => {
      const { usePermission } = await import('@/composables/usePermission')
      mockRoles.push('superadmin')
      const { hasRole } = usePermission()
      expect(hasRole('any-role')).toBe(true)
    })
  })
})
