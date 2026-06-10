import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import HeaderToolbar from '@/components/action-bar/HeaderToolbar.vue'
import type { HeaderToolbarState, HeaderToolbarItem } from '@/types/action-bar'

const baseState: HeaderToolbarState = {
  maximized: false,
  rowHeight: 'default',
  formatSettingsVisible: false
}

const customTools: HeaderToolbarItem[] = [
  { key: 'maximize', tool: 'maximize', label: '铺满', tooltip: '铺满/还原' },
  { key: 'refresh', tool: 'refresh', label: '刷新', tooltip: '刷新数据' },
  { key: 'format-settings', tool: 'format-settings', label: '格式', tooltip: '列格式设置' },
  { key: 'row-height', tool: 'row-height', label: '行高', tooltip: '行高调整' }
]

function createWrapper(
  overrides: {
    modelValue?: HeaderToolbarState
    fieldConfig?: HeaderToolbarItem[]
    disabled?: boolean
  } = {}
): VueWrapper {
  return mount(HeaderToolbar, {
    props: {
      modelValue: overrides.modelValue ?? baseState,
      fieldConfig: overrides.fieldConfig ?? customTools,
      disabled: overrides.disabled ?? false
    }
  })
}

describe('HeaderToolbar component', () => {
  describe('rendering', () => {
    it('renders toolbar buttons from fieldConfig', () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      expect(buttons.length).toBeGreaterThanOrEqual(3)
    })

    it('renders default tools when fieldConfig is empty', () => {
      const wrapper = createWrapper({ fieldConfig: [] })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      expect(buttons.length).toBeGreaterThanOrEqual(3)
    })

    it('renders row-height dropdown', () => {
      const wrapper = createWrapper()
      const dropdown = wrapper.findComponent({ name: 'ElDropdown' })
      expect(dropdown.exists()).toBe(true)
    })

    it('applies disabled state', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.header-toolbar--disabled').exists()).toBe(true)
    })

    it('renders prefix slot', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { prefix: '<span class="custom-prefix">Prefix</span>' }
      })
      expect(wrapper.find('.custom-prefix').exists()).toBe(true)
    })

    it('renders suffix slot', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { suffix: '<span class="custom-suffix">Suffix</span>' }
      })
      expect(wrapper.find('.custom-suffix').exists()).toBe(true)
    })

    it('renders default slot', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { default: '<span class="custom-default">Default</span>' }
      })
      expect(wrapper.find('.custom-default').exists()).toBe(true)
    })
  })

  describe('v-model', () => {
    it('initializes local state from modelValue prop', () => {
      const wrapper = createWrapper({
        modelValue: { maximized: true, rowHeight: 'large', formatSettingsVisible: false }
      })
      const vm = wrapper.vm as unknown as { localState: HeaderToolbarState }
      expect(vm.localState.maximized).toBe(true)
      expect(vm.localState.rowHeight).toBe('large')
    })

    it('emits update:modelValue when maximize is toggled', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      expect(maximizeBtn).toBeTruthy()
      if (maximizeBtn) {
        await maximizeBtn.trigger('click')
        const emitted = wrapper.emitted('update:modelValue') as unknown[][]
        expect(emitted).toBeTruthy()
        expect(emitted[0][0]).toHaveProperty('maximized', true)
      }
    })

    it('watches external modelValue changes', async () => {
      const wrapper = createWrapper()
      await wrapper.setProps({
        modelValue: { maximized: true, rowHeight: 'compact', formatSettingsVisible: false }
      })
      await nextTick()
      const vm = wrapper.vm as unknown as { localState: HeaderToolbarState }
      expect(vm.localState.maximized).toBe(true)
      expect(vm.localState.rowHeight).toBe('compact')
    })
  })

  describe('change event', () => {
    it('emits change when maximize is clicked', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      if (maximizeBtn) {
        await maximizeBtn.trigger('click')
        const emitted = wrapper.emitted('change') as unknown[][]
        expect(emitted).toBeTruthy()
        expect(emitted[0][0]).toBe('maximize')
      }
    })

    it('emits change when refresh is clicked', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const refreshBtn = buttons.find((b) => b.text().includes('刷新'))
      if (refreshBtn) {
        await refreshBtn.trigger('click')
        const emitted = wrapper.emitted('change') as unknown[][]
        expect(emitted).toBeTruthy()
        expect(emitted[0][0]).toBe('refresh')
      }
    })

    it('emits change when format-settings is clicked', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const formatBtn = buttons.find((b) => b.text().includes('格式'))
      if (formatBtn) {
        await formatBtn.trigger('click')
        const emitted = wrapper.emitted('change') as unknown[][]
        expect(emitted).toBeTruthy()
        expect(emitted[0][0]).toBe('format-settings')
      }
    })
  })

  describe('focus/blur events', () => {
    it('emits focus when button gets focus', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const firstBtn = buttons[0]
      if (firstBtn) {
        await firstBtn.trigger('focus')
        const emitted = wrapper.emitted('focus')
        expect(emitted).toBeTruthy()
      }
    })

    it('emits blur when button loses focus', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const firstBtn = buttons[0]
      if (firstBtn) {
        await firstBtn.trigger('blur')
        const emitted = wrapper.emitted('blur')
        expect(emitted).toBeTruthy()
      }
    })
  })

  describe('row height dropdown', () => {
    it('renders row height options in dropdown', async () => {
      const wrapper = createWrapper()
      const dropdown = wrapper.findComponent({ name: 'ElDropdown' })
      expect(dropdown.exists()).toBe(true)
      const dropdownItems = dropdown.findAllComponents({ name: 'ElDropdownItem' })
      expect(dropdownItems.length).toBe(5)
    })

    it('has correct labels for row height options', () => {
      const wrapper = createWrapper()
      const dropdown = wrapper.findComponent({ name: 'ElDropdown' })
      const items = dropdown.findAllComponents({ name: 'ElDropdownItem' })
      const labels = items.map((item) => item.text())
      expect(labels).toContain('紧凑')
      expect(labels).toContain('默认')
      expect(labels).toContain('超大')
    })
  })

  describe('disabled state', () => {
    it('prevents click when disabled', async () => {
      const wrapper = createWrapper({ disabled: true })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      if (maximizeBtn) {
        await maximizeBtn.trigger('click')
        expect(wrapper.emitted('change')).toBeFalsy()
      }
    })
  })

  describe('hidden tools', () => {
    it('hides tools with hidden=true', () => {
      const toolsWithHidden: HeaderToolbarItem[] = [
        { key: 'refresh', tool: 'refresh', label: '刷新' },
        { key: 'export', tool: undefined, label: '导出', hidden: true }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithHidden })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const exportBtn = buttons.find((b) => b.text().includes('导出'))
      expect(exportBtn).toBeFalsy()
    })
  })
})
