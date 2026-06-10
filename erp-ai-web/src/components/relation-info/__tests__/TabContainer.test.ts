import { describe, it, expect, vi, beforeAll } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick, computed, ref } from 'vue'
import type { RelatedTab } from '@/types/relation-info'

const sampleTabs: RelatedTab[] = [
  { key: 'basic-info', label: '基本信息', group: 'basic' },
  { key: 'purchase-order', label: '采购订单', group: 'purchase' },
  { key: 'purchase-return', label: '采购退货', group: 'purchase' },
  { key: 'stock-query', label: '库存查询', group: 'inventory' },
  { key: 'stocktaking', label: '盘点记录', group: 'inventory' },
  { key: 'finance-detail', label: '财务明细', group: 'finance', disabled: true },
  { key: 'hidden-tab', label: '隐藏标签', group: 'basic', hidden: true }
]

// Provide a permission-free stub: return visible tabs filtered only by hidden & activeGroup
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
let TabContainer: typeof import('@/components/relation-info/TabContainer.vue').default
beforeAll(async () => {
  TabContainer = (await import('@/components/relation-info/TabContainer.vue')).default
})

function createWrapper(
  overrides: {
    modelValue?: string
    tabs?: RelatedTab[]
    activeGroup?: string
    disabled?: boolean
    placeholder?: string
  } = {}
): VueWrapper {
  return mount(TabContainer, {
    props: {
      modelValue: overrides.modelValue ?? '',
      tabs: overrides.tabs ?? sampleTabs,
      activeGroup: overrides.activeGroup ?? '',
      disabled: overrides.disabled ?? false,
      placeholder: overrides.placeholder ?? ''
    }
  })
}

describe('TabContainer component', () => {
  describe('rendering', () => {
    it('renders all visible tabs when no activeGroup set', () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      // 7 tabs total, 1 hidden → 6 visible
      expect(tabs.length).toBe(6)
    })

    it('filters tabs by activeGroup', () => {
      const wrapper = createWrapper({ activeGroup: 'purchase' })
      const tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBe(2)
      expect(tabs[0].text()).toContain('采购订单')
      expect(tabs[1].text()).toContain('采购退货')
    })

    it('hides tabs with hidden=true', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).not.toContain('隐藏标签')
    })

    it('renders empty placeholder when no tabs', () => {
      const wrapper = createWrapper({ tabs: [] })
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无标签页')
    })

    it('renders empty placeholder when activeGroup has no tabs', () => {
      const wrapper = createWrapper({ activeGroup: 'nonexistent' })
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
    })

    it('renders custom placeholder text', () => {
      const wrapper = createWrapper({
        tabs: [],
        placeholder: '请选择左侧分组'
      })
      expect(wrapper.text()).toContain('请选择左侧分组')
    })

    it('applies disabled class', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.tab-container--disabled').exists()).toBe(true)
    })

    it('applies disabled style to disabled tabs', () => {
      const wrapper = createWrapper()
      const disabledTabs = wrapper.findAll('.tab-container__tab--disabled')
      // finance-detail has disabled: true
      expect(disabledTabs.length).toBe(1)
    })
  })

  describe('slots', () => {
    it('renders prefix slot', () => {
      const wrapper = mount(TabContainer, {
        props: { modelValue: '', tabs: sampleTabs },
        slots: { prefix: '<span class="custom-prefix">Prefix Content</span>' }
      })
      expect(wrapper.find('.custom-prefix').exists()).toBe(true)
    })

    it('renders suffix slot', () => {
      const wrapper = mount(TabContainer, {
        props: { modelValue: '', tabs: sampleTabs },
        slots: { suffix: '<span class="custom-suffix">Suffix Content</span>' }
      })
      expect(wrapper.find('.custom-suffix').exists()).toBe(true)
    })

    it('renders default slot', () => {
      const wrapper = mount(TabContainer, {
        props: { modelValue: '', tabs: sampleTabs },
        slots: { default: '<span class="custom-default">Tab Content</span>' }
      })
      expect(wrapper.find('.custom-default').exists()).toBe(true)
    })
  })

  describe('v-model', () => {
    it('highlights active tab', async () => {
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
      expect(wrapper.emitted('change')![0][0]).toBe('basic-info')
      expect(wrapper.emitted('change')![0][1]).toEqual(sampleTabs[0])
    })

    it('does not emit on disabled tab click', async () => {
      const wrapper = createWrapper()
      // finance-detail is disabled, it's the 6th visible tab (index 5)
      const tabs = wrapper.findAll('.tab-container__tab')
      const disabledTab = tabs[5]
      await disabledTab.trigger('click')
      const emitted = wrapper.emitted('update:modelValue')
      if (emitted) {
        const keys = emitted.flat()
        expect(keys).not.toContain('finance-detail')
      }
    })

    it('does not emit when globally disabled', async () => {
      const wrapper = createWrapper({ disabled: true })
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('click')
      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })

    it('selecting same tab twice does not re-emit', async () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('click')
      const emitted = wrapper.emitted('update:modelValue')
      if (emitted) {
        expect(emitted.length).toBe(0)
      }
    })
  })

  describe('focus/blur events', () => {
    it('emits focus on tab focus', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('focus')
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('emits blur on tab blur', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.tab-container__tab')
      await tabs[0].trigger('blur')
      expect(wrapper.emitted('blur')).toBeTruthy()
    })
  })

  describe('activeGroup filtering', () => {
    it('shows all visible tabs when activeGroup is empty', () => {
      const wrapper = createWrapper({ activeGroup: '' })
      const tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBe(6)
    })

    it('shows only tabs matching activeGroup', () => {
      const wrapper = createWrapper({ activeGroup: 'inventory' })
      const tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBe(2)
      expect(tabs[0].text()).toContain('库存查询')
      expect(tabs[1].text()).toContain('盘点记录')
    })

    it('switches visible tabs when activeGroup changes', async () => {
      const wrapper = createWrapper({ activeGroup: 'basic' })
      let tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBe(1)
      expect(tabs[0].text()).toContain('基本信息')

      await wrapper.setProps({ activeGroup: 'purchase' })
      tabs = wrapper.findAll('.tab-container__tab')
      expect(tabs.length).toBe(2)
      expect(tabs[0].text()).toContain('采购订单')
    })
  })

  describe('expose methods', () => {
    it('getActiveKey returns current modelValue', () => {
      const wrapper = createWrapper({ modelValue: 'basic-info' })
      expect(wrapper.vm.getActiveKey()).toBe('basic-info')
    })

    it('getActiveKey returns undefined when empty', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getActiveKey()).toBeUndefined()
    })

    it('setActiveKey updates selection', async () => {
      const wrapper = createWrapper()
      wrapper.vm.setActiveKey('stock-query')
      await nextTick()
      expect(wrapper.emitted('update:modelValue')![0]).toEqual(['stock-query'])
    })

    it('setActiveKey does nothing for hidden tabs', () => {
      const wrapper = createWrapper()
      wrapper.vm.setActiveKey('hidden-tab')
      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })

    it('getVisibleTabs returns tabs filtered by activeGroup', () => {
      const wrapper = createWrapper({ activeGroup: 'inventory' })
      const tabs = wrapper.vm.getVisibleTabs()
      expect(tabs.length).toBe(2)
      expect(tabs[0].key).toBe('stock-query')
      expect(tabs[1].key).toBe('stocktaking')
    })

    it('getVisibleTabs excludes hidden tabs', () => {
      const wrapper = createWrapper({ activeGroup: 'basic' })
      const tabs = wrapper.vm.getVisibleTabs()
      expect(tabs.length).toBe(1)
      expect(tabs[0].key).toBe('basic-info')
    })
  })

  describe('edge cases', () => {
    it('handles empty tabs array gracefully', () => {
      const wrapper = createWrapper({ tabs: [] })
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
      expect(wrapper.find('.tab-container__header').exists()).toBe(false)
    })

    it('handles all hidden tabs', () => {
      const hiddenTabs: RelatedTab[] = [
        { key: 'a', label: 'A', group: 'g1', hidden: true },
        { key: 'b', label: 'B', group: 'g1', hidden: true }
      ]
      const wrapper = createWrapper({ tabs: hiddenTabs, activeGroup: 'g1' })
      expect(wrapper.find('.tab-container__tab').exists()).toBe(false)
      expect(wrapper.find('.tab-container__empty').exists()).toBe(true)
    })

    it('handles tabs with different groups', () => {
      const tabs: RelatedTab[] = [
        { key: 'a', label: 'A', group: 'g1' },
        { key: 'b', label: 'B', group: 'g2' },
        { key: 'c', label: 'C', group: 'g3' }
      ]
      const wrapper = createWrapper({ tabs, activeGroup: 'g2' })
      const rendered = wrapper.findAll('.tab-container__tab')
      expect(rendered.length).toBe(1)
      expect(rendered[0].text()).toContain('B')
    })
  })
})
