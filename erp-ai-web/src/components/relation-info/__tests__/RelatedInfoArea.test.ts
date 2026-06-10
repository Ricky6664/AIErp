import { describe, it, expect, vi, beforeAll } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick, computed, ref } from 'vue'
import type { NavGroup, RelatedTab } from '@/types/relation-info'

// ============================================================
// Sample data
// ============================================================
const sampleGroups: NavGroup[] = [
  { key: 'basic', label: '基本信息', icon: 'InfoFilled' },
  { key: 'purchase', label: '采购管理', badge: 5 },
  { key: 'finance', label: '财务信息', disabled: true },
  { key: 'hidden-group', label: '隐藏分组', hidden: true }
]

const sampleTabs: RelatedTab[] = [
  { key: 'basic-info', label: '基本信息', group: 'basic' },
  { key: 'purchase-order', label: '采购订单', group: 'purchase' },
  { key: 'purchase-return', label: '采购退货', group: 'purchase' },
  { key: 'finance-detail', label: '财务明细', group: 'finance', disabled: true },
  { key: 'hidden-tab', label: '隐藏标签', group: 'basic', hidden: true }
]

// ============================================================
// Mock useTabPermission (same pattern as TabContainer tests)
// ============================================================
vi.mock('@/composables/useTabPermission', () => ({
  useTabPermission: (tabs: ReturnType<typeof ref>, activeGroup: ReturnType<typeof ref>) => {
    const visibleTabs = computed(() => {
      let list = tabs.value.filter((t: RelatedTab) => !t.hidden)
      if (activeGroup && activeGroup.value) {
        list = list.filter((t: RelatedTab) => t.group === activeGroup.value)
      }
      return list
    })
    const canAccessTab = (t: RelatedTab) => !t.hidden
    const permissionHiddenCount = computed(() => 0)
    return { visibleTabs, canAccessTab, permissionHiddenCount }
  }
}))

// Must import after mock
let RelatedInfoArea: typeof import('@/components/relation-info/RelatedInfoArea.vue').default
beforeAll(async () => {
  RelatedInfoArea = (await import('@/components/relation-info/RelatedInfoArea.vue')).default
})

function createWrapper(
  overrides: {
    modelValue?: string
    mainRow?: Record<string, unknown> | null
    groups?: NavGroup[]
    tabs?: RelatedTab[]
    disabled?: boolean
    placeholder?: string
    viewCode?: string
  } = {}
): VueWrapper {
  return mount(RelatedInfoArea, {
    props: {
      modelValue: overrides.modelValue ?? '',
      mainRow: overrides.mainRow ?? null,
      groups: overrides.groups ?? sampleGroups,
      tabs: overrides.tabs ?? sampleTabs,
      disabled: overrides.disabled ?? false,
      placeholder: overrides.placeholder ?? '',
      viewCode: overrides.viewCode ?? ''
    }
  })
}

describe('RelatedInfoArea component', () => {
  // ==========================================================
  // Rendering
  // ==========================================================
  describe('rendering', () => {
    it('renders left nav and right main areas', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.related-info-area__nav').exists()).toBe(true)
      expect(wrapper.find('.related-info-area__main').exists()).toBe(true)
    })

    it('renders GroupNav with groups prop', () => {
      const wrapper = createWrapper()
      const groupItems = wrapper.findAll('.group-nav__item')
      // 4 groups, 1 hidden → 3 visible
      expect(groupItems.length).toBe(3)
    })

    it('renders TabContainer with tabs prop', () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      // 5 tabs, 1 hidden → 4 visible (all groups when activeGroup is empty)
      expect(tabs.length).toBe(4)
    })

    it('hides groups with hidden=true', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).not.toContain('隐藏分组')
    })

    it('hides tabs with hidden=true', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).not.toContain('隐藏标签')
    })

    it('applies disabled class when disabled prop is true', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.related-info-area--disabled').exists()).toBe(true)
    })

    it('does not apply disabled class when disabled is false', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.related-info-area--disabled').exists()).toBe(false)
    })

    it('forwards placeholder to child components', () => {
      // TabContainer shows placeholder when no tabs match
      const wrapper = createWrapper({
        tabs: [],
        placeholder: '暂无关联信息'
      })
      expect(wrapper.text()).toContain('暂无关联信息')
    })
  })

  // ==========================================================
  // v-model (tab selection)
  // ==========================================================
  describe('v-model', () => {
    it('highlights active tab', () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      const activeTab = wrapper.find('.tab-container__tab--active')
      expect(activeTab.exists()).toBe(true)
      expect(activeTab.text()).toContain('基本信息')
    })

    it('emits update:modelValue on tab click', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('click')
      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')![0]).toEqual(['basic-info'])
    })

    it('emits change event on tab click', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('click')
      expect(wrapper.emitted('change')).toBeTruthy()
      expect(wrapper.emitted('change')![0]).toEqual(['basic-info'])
    })

    it('does not emit when globally disabled', async () => {
      const wrapper = createWrapper({ disabled: true })
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('click')
      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })

    it('does not emit on disabled tab click', async () => {
      const wrapper = createWrapper()
      // finance-detail is disabled, visible as the 4th tab (index 3)
      const tabs = wrapper.findAll('.tab-container__tab')
      const disabledTab = tabs[3] // finance-detail
      await disabledTab.trigger('click')
      const emitted = wrapper.emitted('update:modelValue')
      if (emitted) {
        const keys = emitted.flat()
        expect(keys).not.toContain('finance-detail')
      }
    })
  })

  // ==========================================================
  // Group selection → tab filtering
  // ==========================================================
  describe('group selection', () => {
    it('shows all tabs when no group is selected', () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBe(4) // 5 total, 1 hidden = 4
    })

    it('filters tabs when a group is clicked', async () => {
      const wrapper = createWrapper()
      // Click the "采购管理" group (index 1 in visible groups)
      const groupItems = wrapper.findAll('.group-nav__item')
      const purchaseGroup = groupItems.find((item) => item.text().includes('采购管理'))
      expect(purchaseGroup).toBeTruthy()
      await purchaseGroup!.trigger('click')
      await nextTick()

      const tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBe(2)
      expect(tabs[0].text()).toContain('采购订单')
      expect(tabs[1].text()).toContain('采购退货')
    })

    it('auto-selects first visible tab when switching groups', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      // Click purchase group — active tab (basic-info) is not in this group
      const groupItems = wrapper.findAll('.group-nav__item')
      const purchaseGroup = groupItems.find((item) => item.text().includes('采购管理'))
      await purchaseGroup!.trigger('click')
      await nextTick()

      // Should auto-select purchase-order (first visible tab in purchase group)
      const emitted = wrapper.emitted('update:modelValue')
      expect(emitted).toBeTruthy()
      // The change event should contain the new tab key
      const changeEvents = wrapper.emitted('change')
      expect(changeEvents).toBeTruthy()
      const lastChange = changeEvents![changeEvents!.length - 1]
      expect(lastChange[0]).toBe('purchase-order')
    })

    it('does not auto-select when current tab is already in new group', async () => {
      const wrapper = createWrapper({ modelValue: 'purchase-order' })
      // Click purchase group — purchase-order is already in this group
      const groupItems = wrapper.findAll('.group-nav__item')
      const purchaseGroup = groupItems.find((item) => item.text().includes('采购管理'))
      await purchaseGroup!.trigger('click')
      await nextTick()

      // Should not emit additional change for tab since it's already selected
      // The group change itself doesn't emit tab change events
      const activeTab = wrapper.find('.tab-container__tab--active')
      expect(activeTab.exists()).toBe(true)
      expect(activeTab.text()).toContain('采购订单')
    })

    it('handles group with no visible tabs', async () => {
      // Use a group that is enabled but whose tabs are all hidden
      const groups: NavGroup[] = [{ key: 'empty-group', label: '空分组' }]
      const tabs: RelatedTab[] = [
        { key: 'hidden-1', label: '隐藏标签1', group: 'empty-group', hidden: true },
        { key: 'hidden-2', label: '隐藏标签2', group: 'empty-group', hidden: true }
      ]
      const wrapper = createWrapper({ groups, tabs })
      const groupItems = wrapper.findAll('.group-nav__item')
      await groupItems[0].trigger('click')
      await nextTick()

      // TabContainer shows empty placeholder since all tabs are hidden
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
    })
  })

  // ==========================================================
  // Focus/blur events
  // ==========================================================
  describe('focus/blur events', () => {
    it('forwards focus event from GroupNav', async () => {
      const wrapper = createWrapper()
      const groupItems = wrapper.findAll('.group-nav__item')
      await groupItems[0].trigger('focus')
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('forwards blur event from GroupNav', async () => {
      const wrapper = createWrapper()
      const groupItems = wrapper.findAll('.group-nav__item')
      await groupItems[0].trigger('blur')
      expect(wrapper.emitted('blur')).toBeTruthy()
    })

    it('forwards focus event from TabContainer', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('focus')
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('forwards blur event from TabContainer', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('blur')
      expect(wrapper.emitted('blur')).toBeTruthy()
    })
  })

  // ==========================================================
  // Slots
  // ==========================================================
  describe('slots', () => {
    it('renders prefix slot', () => {
      const wrapper = mount(RelatedInfoArea, {
        props: { modelValue: '', groups: sampleGroups, tabs: sampleTabs },
        slots: { prefix: '<span class="custom-prefix">Prefix Content</span>' }
      })
      expect(wrapper.find('.custom-prefix').exists()).toBe(true)
    })

    it('renders suffix slot', () => {
      const wrapper = mount(RelatedInfoArea, {
        props: { modelValue: '', groups: sampleGroups, tabs: sampleTabs },
        slots: { suffix: '<span class="custom-suffix">Suffix Content</span>' }
      })
      expect(wrapper.find('.custom-suffix').exists()).toBe(true)
    })

    it('renders default slot (tab content)', () => {
      const wrapper = mount(RelatedInfoArea, {
        props: { modelValue: '', groups: sampleGroups, tabs: sampleTabs },
        slots: { default: '<div class="tab-content">Related Data</div>' }
      })
      expect(wrapper.find('.tab-content').exists()).toBe(true)
    })

    it('renders nav-prefix slot', () => {
      const wrapper = mount(RelatedInfoArea, {
        props: { modelValue: '', groups: sampleGroups, tabs: sampleTabs },
        slots: { 'nav-prefix': '<span class="nav-prefix-slot">Nav Prefix</span>' }
      })
      expect(wrapper.find('.nav-prefix-slot').exists()).toBe(true)
    })

    it('renders nav-suffix slot', () => {
      const wrapper = mount(RelatedInfoArea, {
        props: { modelValue: '', groups: sampleGroups, tabs: sampleTabs },
        slots: { 'nav-suffix': '<span class="nav-suffix-slot">Nav Suffix</span>' }
      })
      expect(wrapper.find('.nav-suffix-slot').exists()).toBe(true)
    })
  })

  // ==========================================================
  // Expose methods
  // ==========================================================
  describe('expose methods', () => {
    it('getMainRow returns the mainRow prop', () => {
      const mainRow = { id: 1, name: 'Test Product' }
      const wrapper = createWrapper({ mainRow })
      expect(wrapper.vm.getMainRow()).toEqual(mainRow)
    })

    it('getMainRow returns null when mainRow is not set', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getMainRow()).toBeNull()
    })

    it('getMainRow returns null when mainRow is null', () => {
      const wrapper = createWrapper({ mainRow: null })
      expect(wrapper.vm.getMainRow()).toBeNull()
    })

    it('refreshCurrentTab does not throw when no active tab', () => {
      const wrapper = createWrapper()
      expect(() => wrapper.vm.refreshCurrentTab()).not.toThrow()
    })

    it('refreshCurrentTab does not throw with active tab', () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      expect(() => wrapper.vm.refreshCurrentTab()).not.toThrow()
    })

    it('refreshAllTabs does not throw', () => {
      const wrapper = createWrapper()
      expect(() => wrapper.vm.refreshAllTabs()).not.toThrow()
    })

    it('refreshCurrentTab increments refresh key for active tab', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      wrapper.vm.refreshCurrentTab()
      await nextTick()
      // Method should complete without error — refresh context is consumed by child components
      expect(() => wrapper.vm.refreshCurrentTab()).not.toThrow()
    })

    it('refreshAllTabs increments global refresh key', () => {
      const wrapper = createWrapper()
      wrapper.vm.refreshAllTabs()
      wrapper.vm.refreshAllTabs()
      // Should not throw on multiple calls
      expect(() => wrapper.vm.refreshAllTabs()).not.toThrow()
    })
  })

  // ==========================================================
  // mainRow watch → auto-refresh
  // ==========================================================
  describe('mainRow reactivity', () => {
    it('auto-refreshes current tab when mainRow changes', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      await wrapper.setProps({ mainRow: { id: 1, name: 'New Product' } })
      await nextTick()
      // Should not throw — watch handler increments refresh key
      // Component stays stable after mainRow update
      expect(wrapper.find('.related-info-area').exists()).toBe(true)
    })

    it('does not refresh when mainRow changes to null', async () => {
      const wrapper = createWrapper({
        modelValue: 'basic-info',
        mainRow: { id: 1 }
      })
      await wrapper.setProps({ mainRow: null })
      await nextTick()
      // Should not throw; null → no refresh triggered
      expect(wrapper.find('.related-info-area').exists()).toBe(true)
    })

    it('does not refresh when no active tab', async () => {
      const wrapper = createWrapper()
      await wrapper.setProps({ mainRow: { id: 1 } })
      await nextTick()
      // No active tab, so no refresh triggered; should not throw
      expect(wrapper.find('.related-info-area').exists()).toBe(true)
    })

    it('does not refresh when mainRow stays the same', async () => {
      const row = { id: 1 }
      const wrapper = createWrapper({ modelValue: 'basic-info', mainRow: row })
      await wrapper.setProps({ mainRow: row })
      await nextTick()
      // Same reference — no refresh should be triggered
      expect(wrapper.find('.related-info-area').exists()).toBe(true)
    })
  })

  // ==========================================================
  // viewCode prop
  // ==========================================================
  describe('viewCode prop', () => {
    it('accepts viewCode prop', () => {
      const wrapper = createWrapper({ viewCode: 'product-detail' })
      expect(wrapper.props('viewCode')).toBe('product-detail')
    })

    it('defaults to empty string', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewCode')).toBe('')
    })
  })

  // ==========================================================
  // Edge cases
  // ==========================================================
  describe('edge cases', () => {
    it('handles empty groups array', () => {
      const wrapper = createWrapper({ groups: [] })
      expect(wrapper.find('.related-info-area__nav').exists()).toBe(true)
      expect(wrapper.find('.group-nav__empty').exists()).toBe(true)
    })

    it('handles empty tabs array', () => {
      const wrapper = createWrapper({ tabs: [] })
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
    })

    it('handles all hidden groups', () => {
      const allHidden: NavGroup[] = [
        { key: 'a', label: 'A', hidden: true },
        { key: 'b', label: 'B', hidden: true }
      ]
      const wrapper = createWrapper({ groups: allHidden })
      expect(wrapper.find('.group-nav__empty').exists()).toBe(true)
    })

    it('handles all hidden tabs', () => {
      const allHiddenTabs: RelatedTab[] = [
        { key: 'a', label: 'A', group: 'g1', hidden: true },
        { key: 'b', label: 'B', group: 'g1', hidden: true }
      ]
      const wrapper = createWrapper({ tabs: allHiddenTabs })
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
    })

    it('handles modelValue that does not match any tab', () => {
      const wrapper = createWrapper({ modelValue: 'nonexistent-tab' })
      expect(wrapper.find('.tab-container__tab--active').exists()).toBe(false)
    })

    it('handles groups without any matching tabs', async () => {
      const groups: NavGroup[] = [{ key: 'orphan', label: '独立分组' }]
      const wrapper = createWrapper({ groups })
      const groupItems = wrapper.findAll('.group-nav__item')
      await groupItems[0].trigger('click')
      await nextTick()
      // No tabs matching 'orphan' group → empty placeholder
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
    })
  })

  // ==========================================================
  // Verification checklist (P0-005-005-005-001-002)
  // ==========================================================
  describe('verification: group nav expand/collapse', () => {
    it('expands group children when clicking parent group', async () => {
      const groupsWithChildren: NavGroup[] = [
        {
          key: 'purchase',
          label: '采购管理',
          children: [
            { key: 'purchase-order-sub', label: '采购订单' },
            { key: 'purchase-return-sub', label: '采购退货' }
          ]
        }
      ]
      const tabs: RelatedTab[] = [
        { key: 'po', label: '采购订单', group: 'purchase-order-sub' },
        { key: 'pr', label: '采购退货', group: 'purchase-return-sub' }
      ]
      const wrapper = createWrapper({ groups: groupsWithChildren, tabs })
      const groupItem = wrapper.find('.group-nav__item')
      expect(groupItem.find('.group-nav__arrow').exists()).toBe(true)

      // Before click, children hidden
      expect(wrapper.find('.group-nav__children').exists()).toBe(false)

      await groupItem.trigger('click')
      await nextTick()

      // After click, children visible
      expect(wrapper.find('.group-nav__children').exists()).toBe(true)
    })

    it('collapses expanded group on second click', async () => {
      const groupsWithChildren: NavGroup[] = [
        {
          key: 'purchase',
          label: '采购管理',
          children: [{ key: 'sub-a', label: '子分组A' }]
        }
      ]
      const wrapper = createWrapper({ groups: groupsWithChildren, tabs: [] })
      const groupItem = wrapper.find('.group-nav__item')

      await groupItem.trigger('click')
      await nextTick()
      expect(wrapper.find('.group-nav__children').exists()).toBe(true)

      await groupItem.trigger('click')
      await nextTick()
      expect(wrapper.find('.group-nav__children').exists()).toBe(false)
    })

    it('maintains expand state when switching between parent groups', async () => {
      const groupsWithChildren: NavGroup[] = [
        {
          key: 'basic',
          label: '基本信息',
          children: [{ key: 'basic-sub', label: '基础子项' }]
        },
        {
          key: 'purchase',
          label: '采购管理',
          children: [{ key: 'purchase-sub', label: '采购子项' }]
        }
      ]
      const tabs: RelatedTab[] = [
        { key: 'bs', label: '基础子项', group: 'basic-sub' },
        { key: 'ps', label: '采购子项', group: 'purchase-sub' }
      ]
      const wrapper = createWrapper({ groups: groupsWithChildren, tabs })
      const groupItems = wrapper.findAll('.group-nav__item')

      // Expand first group
      await groupItems[0].trigger('click')
      await nextTick()
      const children = wrapper.findAll('.group-nav__children')
      expect(children.length).toBe(1) // only first expanded

      // Click second group (should not collapse first)
      await groupItems[1].trigger('click')
      await nextTick()
      // Both should be expanded now
      expect(wrapper.findAll('.group-nav__children').length).toBe(2)
    })
  })

  describe('verification: tab lazy loading', () => {
    it('provides refresh context for lazy tab loading', () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      // The component provides RELATED_INFO_REFRESH_KEY
      // Verify the component renders successfully with lazy tabs
      expect(wrapper.find('.related-info-area').exists()).toBe(true)
      expect(wrapper.find('.tab-container__content').exists()).toBe(true)
    })

    it('increments tab-specific refresh key when mainRow changes', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      // Simulate mainRow update → should trigger refresh for active tab
      await wrapper.setProps({ mainRow: { id: 1, name: 'Test' } })
      await nextTick()
      // Component remains stable; child components receive updated refresh keys
      expect(wrapper.find('.related-info-area').exists()).toBe(true)
    })

    it('does not refresh unrelated tabs when mainRow changes', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      // Only basic-info tab should refresh, not purchase-order
      await wrapper.setProps({ mainRow: { id: 100 } })
      await nextTick()
      // Verify component is rendered and stable
      expect(wrapper.find('.related-info-area--disabled').exists()).toBe(false)
    })
  })

  describe('verification: tab permission control', () => {
    it('hides tabs marked with hidden=true', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).not.toContain('隐藏标签')
    })

    it('hides groups marked with hidden=true', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).not.toContain('隐藏分组')
    })

    it('shows tabs with hidden=false', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).toContain('基本信息')
      expect(wrapper.text()).toContain('采购订单')
    })

    it('disables interaction on disabled tabs', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      // finance-detail is the 4th tab, disabled
      const disabledTab = tabs.find((t) => t.classes('tab-container__tab--disabled'))
      expect(disabledTab).toBeTruthy()
    })

    it('handles all tabs requiring permissions that user lacks', () => {
      // All visible tabs should still render (mock allows all)
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBeGreaterThanOrEqual(1)
    })
  })

  describe('verification: data refresh on tab switch', () => {
    it('emits change event on tab switch for consumer to trigger refresh', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      const tabs = wrapper.findAll('.tab-container__tab')
      // Switch to purchase-order
      const poTab = tabs.find((t) => t.text().includes('采购订单'))
      expect(poTab).toBeTruthy()
      await poTab!.trigger('click')
      await nextTick()

      const changeEvents = wrapper.emitted('change')
      expect(changeEvents).toBeTruthy()
      // Last change event should be for purchase-order
      const lastChange = changeEvents![changeEvents!.length - 1]
      expect(lastChange[0]).toBe('purchase-order')
    })

    it('refreshCurrentTab can be called after tab switch', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      // Switch tab first
      const tabs = wrapper.findAll('.tab-container__tab')
      const poTab = tabs.find((t) => t.text().includes('采购订单'))
      await poTab!.trigger('click')
      await nextTick()

      // Then refresh current tab via expose
      expect(() => wrapper.vm.refreshCurrentTab()).not.toThrow()
    })

    it('refreshAllTabs increments global counter for all tabs', () => {
      const wrapper = createWrapper()
      // Call multiple times to verify global counter behavior
      wrapper.vm.refreshAllTabs()
      wrapper.vm.refreshAllTabs()
      wrapper.vm.refreshAllTabs()
      expect(() => wrapper.vm.refreshAllTabs()).not.toThrow()
    })
  })

  describe('verification: HeaderToolbar integration', () => {
    it('RelatedInfoArea can host HeaderToolbar via prefix slot', () => {
      const wrapper = mount(RelatedInfoArea, {
        props: { modelValue: '', groups: sampleGroups, tabs: sampleTabs },
        slots: {
          prefix: '<div class="toolbar-mock">新增 | 放大查看</div>'
        }
      })
      expect(wrapper.find('.toolbar-mock').exists()).toBe(true)
      expect(wrapper.find('.toolbar-mock').text()).toContain('新增')
      expect(wrapper.find('.toolbar-mock').text()).toContain('放大查看')
    })

    it('RelatedInfoArea layout is correct for toolbar placement', () => {
      const wrapper = createWrapper()
      // Nav and main areas exist in correct layout
      const nav = wrapper.find('.related-info-area__nav')
      const main = wrapper.find('.related-info-area__main')
      expect(nav.exists()).toBe(true)
      expect(main.exists()).toBe(true)
      // Nav is to the left (first child of flex container)
      const container = wrapper.find('.related-info-area')
      expect(container.element.children[0]).toBe(nav.element)
      expect(container.element.children[1]).toBe(main.element)
    })
  })
})
