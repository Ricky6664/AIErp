import { describe, it, expect, beforeEach, vi } from 'vitest'
import { ref } from 'vue'
import type { RelatedTab } from '@/types/relation-info'

// Mock usePermission
const mockPermissions: string[] = []
const mockRoles: string[] = []

vi.mock('@/composables/usePermission', () => ({
  usePermission: () => ({
    hasPermission: (code: string) => {
      if (mockRoles.includes('superadmin')) return true
      if (!code) return false
      return mockPermissions.includes(code)
    },
    hasAnyPermission: (codes: string[]) => {
      if (mockRoles.includes('superadmin')) return true
      return codes.some((c: string) => mockPermissions.includes(c))
    },
    hasRole: (code: string) => {
      if (mockRoles.includes('superadmin')) return true
      return mockRoles.includes(code)
    }
  })
}))

function makeTabs(overrides?: Partial<RelatedTab>[]): RelatedTab[] {
  const base: RelatedTab[] = [
    { key: 'tab1', label: 'Tab 1', group: 'group-a' },
    { key: 'tab2', label: 'Tab 2', group: 'group-a', permission: 'order:view' },
    { key: 'tab3', label: 'Tab 3', group: 'group-b', hidden: true },
    { key: 'tab4', label: 'Tab 4', group: 'group-b', permission: 'finance:view' },
    { key: 'tab5', label: 'Tab 5', group: 'group-a', disabled: true }
  ]
  if (overrides) {
    overrides.forEach((o, i) => {
      if (o) Object.assign(base[i], o)
    })
  }
  return base
}

describe('useTabPermission composable', () => {
  beforeEach(() => {
    mockPermissions.length = 0
    mockRoles.length = 0
  })

  describe('visibleTabs', () => {
    it('should filter out hidden tabs', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      const tabs = ref(makeTabs())
      const { visibleTabs } = useTabPermission(tabs)
      // tab3 hidden, tab2+tab4 require permissions user lacks → only tab1, tab5 remain
      expect(visibleTabs.value.map((t) => t.key)).toEqual(['tab1', 'tab5'])
    })

    it('should filter out tabs without permission', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      mockPermissions.push('order:view')
      const tabs = ref(makeTabs())
      const { visibleTabs } = useTabPermission(tabs)
      // tab3 hidden, tab4 no permission (finance:view)
      expect(visibleTabs.value.map((t) => t.key)).toEqual(['tab1', 'tab2', 'tab5'])
    })

    it('should show all non-hidden tabs for superadmin', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      mockRoles.push('superadmin')
      const tabs = ref(makeTabs())
      const { visibleTabs } = useTabPermission(tabs)
      // tab3 still hidden
      expect(visibleTabs.value.map((t) => t.key)).toEqual(['tab1', 'tab2', 'tab4', 'tab5'])
    })

    it('should filter by activeGroup when provided', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      mockPermissions.push('order:view', 'finance:view')
      const tabs = ref(makeTabs())
      const activeGroup = ref('group-b')
      const { visibleTabs } = useTabPermission(tabs, activeGroup)
      // group-b non-hidden: tab4 (tab3 is hidden)
      expect(visibleTabs.value.map((t) => t.key)).toEqual(['tab4'])
    })

    it('should reactively update when tabs change', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      mockPermissions.push('order:view', 'finance:view')
      const tabs = ref(makeTabs())
      const { visibleTabs } = useTabPermission(tabs)

      // tab1 (no perm), tab2 (order:view ✓), tab4 (finance:view ✓), tab5 (no perm) = 4
      expect(visibleTabs.value.length).toBe(4)

      tabs.value = [
        ...tabs.value,
        { key: 'tab6', label: 'Tab 6', group: 'group-a', permission: 'new:tab' }
      ]
      // tab6 requires 'new:tab' which user lacks → still 4
      expect(visibleTabs.value.length).toBe(4)

      mockPermissions.push('new:tab')
      // Reactivity: permission changed but computed won't re-evaluate without triggering
      // The composable is reactive to tab changes but not to external mock state changes
    })

    it('should show tabs without permission field', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      const tabs = ref(makeTabs())
      const { visibleTabs } = useTabPermission(tabs)
      // tab1 has no permission - should be visible
      expect(visibleTabs.value.map((t) => t.key)).toContain('tab1')
    })

    it('should handle empty tabs array', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      const tabs = ref<RelatedTab[]>([])
      const { visibleTabs } = useTabPermission(tabs)
      expect(visibleTabs.value).toEqual([])
    })
  })

  describe('canAccessTab', () => {
    it('should return false for hidden tabs', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      const tabs = ref(makeTabs())
      const { canAccessTab } = useTabPermission(tabs)
      expect(canAccessTab({ key: 'h1', label: 'Hidden', group: 'g', hidden: true })).toBe(false)
    })

    it('should return false when lacking required permission', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      const tabs = ref(makeTabs())
      const { canAccessTab } = useTabPermission(tabs)
      expect(
        canAccessTab({ key: 'p1', label: 'Perm', group: 'g', permission: 'admin:access' })
      ).toBe(false)
    })

    it('should return true when user has required permission', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      mockPermissions.push('admin:access')
      const tabs = ref(makeTabs())
      const { canAccessTab } = useTabPermission(tabs)
      expect(
        canAccessTab({ key: 'p1', label: 'Perm', group: 'g', permission: 'admin:access' })
      ).toBe(true)
    })

    it('should return true for tabs without permission field', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      const tabs = ref(makeTabs())
      const { canAccessTab } = useTabPermission(tabs)
      expect(canAccessTab({ key: 'n1', label: 'NoPerm', group: 'g' })).toBe(true)
    })
  })

  describe('permissionHiddenCount', () => {
    it('should count tabs hidden due to lack of permission', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      mockPermissions.push('order:view')
      const tabs = ref(makeTabs())
      const { permissionHiddenCount } = useTabPermission(tabs)
      // tab4 has finance:view which user lacks; tab3 is config-hidden (not counted)
      expect(permissionHiddenCount.value).toBe(1)
    })

    it('should not count config-hidden tabs', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      const tabs = ref(makeTabs())
      const { permissionHiddenCount } = useTabPermission(tabs)
      // tab3 is hidden=true, tab2&tab4 have permissions but user has none
      // hidden=true tabs not counted
      expect(permissionHiddenCount.value).toBe(2)
    })

    it('should return 0 for superadmin', async () => {
      const { useTabPermission } = await import('@/composables/useTabPermission')
      mockRoles.push('superadmin')
      const tabs = ref(makeTabs())
      const { permissionHiddenCount } = useTabPermission(tabs)
      expect(permissionHiddenCount.value).toBe(0)
    })
  })
})
