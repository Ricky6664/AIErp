import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import HeaderToolbar from '@/components/relation-info/HeaderToolbar.vue'
import type { RelatedInfoToolbarState, RelatedInfoToolbarItem } from '@/types/relation-info'

const baseState: RelatedInfoToolbarState = {
  maximized: false,
  rowHeight: 'default',
  formatSettingsVisible: false
}

const customTools: RelatedInfoToolbarItem[] = [
  { key: 'maximize', tool: 'maximize', label: '铺满', tooltip: '铺满/还原' },
  { key: 'refresh', tool: 'refresh', label: '刷新', tooltip: '刷新关联数据' },
  { key: 'format-settings', tool: 'format-settings', label: '格式', tooltip: '列格式设置' },
  { key: 'row-height', tool: 'row-height', label: '行高', tooltip: '行高调整' }
]

function createWrapper(
  overrides: {
    modelValue?: RelatedInfoToolbarState
    fieldConfig?: RelatedInfoToolbarItem[]
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

describe('HeaderToolbar component (relation-info)', () => {
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

    it('renders default tools when fieldConfig is undefined', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState }
      })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      expect(buttons.length).toBeGreaterThanOrEqual(3)
    })

    it('renders row-height dropdown', () => {
      const wrapper = createWrapper()
      const dropdown = wrapper.findComponent({ name: 'ElDropdown' })
      expect(dropdown.exists()).toBe(true)
    })

    it('applies disabled CSS class when disabled prop is true', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.header-toolbar--disabled').exists()).toBe(true)
    })

    it('does not apply disabled class by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.header-toolbar--disabled').exists()).toBe(false)
    })

    it('renders prefix slot content', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { prefix: '<span class="custom-prefix">Prefix Content</span>' }
      })
      expect(wrapper.find('.custom-prefix').exists()).toBe(true)
      expect(wrapper.find('.custom-prefix').text()).toBe('Prefix Content')
    })

    it('renders suffix slot content', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { suffix: '<span class="custom-suffix">Suffix Content</span>' }
      })
      expect(wrapper.find('.custom-suffix').exists()).toBe(true)
      expect(wrapper.find('.custom-suffix').text()).toBe('Suffix Content')
    })

    it('renders default slot content', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { default: '<span class="custom-default">Default Content</span>' }
      })
      expect(wrapper.find('.custom-default').exists()).toBe(true)
      expect(wrapper.find('.custom-default').text()).toBe('Default Content')
    })
  })

  describe('v-model two-way binding', () => {
    it('initializes local state from modelValue prop', () => {
      const wrapper = createWrapper({
        modelValue: { maximized: true, rowHeight: 'large', formatSettingsVisible: false }
      })
      const vm = wrapper.vm as unknown as { localState: RelatedInfoToolbarState }
      expect(vm.localState.maximized).toBe(true)
      expect(vm.localState.rowHeight).toBe('large')
    })

    it('initializes with defaults when modelValue fields are missing', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: {} as RelatedInfoToolbarState }
      })
      const vm = wrapper.vm as unknown as { localState: RelatedInfoToolbarState }
      expect(vm.localState.maximized).toBe(false)
      expect(vm.localState.rowHeight).toBe('default')
    })

    it('emits update:modelValue when maximize button is clicked', async () => {
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

    it('watches external modelValue changes from parent', async () => {
      const wrapper = createWrapper()
      await wrapper.setProps({
        modelValue: { maximized: true, rowHeight: 'compact', formatSettingsVisible: false }
      })
      await nextTick()
      const vm = wrapper.vm as unknown as { localState: RelatedInfoToolbarState }
      expect(vm.localState.maximized).toBe(true)
      expect(vm.localState.rowHeight).toBe('compact')
    })
  })

  describe('change event emission', () => {
    it('emits change with "maximize" tool key when maximize clicked', async () => {
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

    it('emits change with "refresh" tool key when refresh clicked', async () => {
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

    it('emits change with "format-settings" tool key when format settings clicked', async () => {
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

    it('emits change with custom tool key for non-built-in actions', async () => {
      const customConfig: RelatedInfoToolbarItem[] = [
        { key: 'export-excel', label: '导出Excel', tooltip: '导出为Excel文件' }
      ]
      const wrapper = createWrapper({ fieldConfig: customConfig })
      const exportBtn = wrapper.findAllComponents({ name: 'ElButton' })[0]
      if (exportBtn) {
        await exportBtn.trigger('click')
        const emitted = wrapper.emitted('change') as unknown[][]
        expect(emitted).toBeTruthy()
        expect(emitted[0][0]).toBe('export-excel')
      }
    })
  })

  describe('focus/blur event emission', () => {
    it('emits focus event when button receives focus', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const firstBtn = buttons[0]
      if (firstBtn) {
        await firstBtn.trigger('focus')
        const emitted = wrapper.emitted('focus')
        expect(emitted).toBeTruthy()
      }
    })

    it('emits blur event when button loses focus', async () => {
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
    it('renders all 5 row height presets in dropdown', async () => {
      const wrapper = createWrapper()
      const dropdown = wrapper.findComponent({ name: 'ElDropdown' })
      expect(dropdown.exists()).toBe(true)
      const dropdownItems = dropdown.findAllComponents({ name: 'ElDropdownItem' })
      expect(dropdownItems.length).toBe(5)
    })

    it('displays correct labels for row height options', () => {
      const wrapper = createWrapper()
      const dropdown = wrapper.findComponent({ name: 'ElDropdown' })
      const items = dropdown.findAllComponents({ name: 'ElDropdownItem' })
      const labels = items.map((item) => item.text())
      expect(labels).toContain('紧凑')
      expect(labels).toContain('较小')
      expect(labels).toContain('默认')
      expect(labels).toContain('较大')
      expect(labels).toContain('超大')
    })

    it('emits change with "row-height" when row height changed via dropdown command', async () => {
      const wrapper = createWrapper()
      const dropdown = wrapper.findComponent({ name: 'ElDropdown' })
      // 通过触发 el-dropdown 的 command 事件模拟下拉选择
      dropdown.vm.$emit('command', 'compact')
      await nextTick()
      const emitted = wrapper.emitted('change') as unknown[][]
      expect(emitted).toBeTruthy()
      expect(emitted[0][0]).toBe('row-height')
    })
  })

  describe('maximize toggle', () => {
    it('toggles maximized state on consecutive clicks', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      if (maximizeBtn) {
        await maximizeBtn.trigger('click')
        const vm = wrapper.vm as unknown as { localState: RelatedInfoToolbarState }
        expect(vm.localState.maximized).toBe(true)

        await maximizeBtn.trigger('click')
        expect(vm.localState.maximized).toBe(false)
      }
    })
  })

  describe('disabled state behavior', () => {
    it('prevents click actions when disabled', async () => {
      const wrapper = createWrapper({ disabled: true })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      if (maximizeBtn) {
        await maximizeBtn.trigger('click')
        expect(wrapper.emitted('change')).toBeFalsy()
      }
    })

    it('prevents click actions when individual tool is disabled', async () => {
      const toolsWithDisabled: RelatedInfoToolbarItem[] = [
        { key: 'refresh', tool: 'refresh', label: '刷新', disabled: true }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithDisabled })
      const refreshBtn = wrapper.findAllComponents({ name: 'ElButton' })[0]
      if (refreshBtn) {
        await refreshBtn.trigger('click')
        expect(wrapper.emitted('change')).toBeFalsy()
      }
    })
  })

  describe('hidden tools filtering', () => {
    it('filters out tools marked as hidden', () => {
      const toolsWithHidden: RelatedInfoToolbarItem[] = [
        { key: 'refresh', tool: 'refresh', label: '刷新' },
        { key: 'secret-tool', label: '隐藏工具', hidden: true }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithHidden })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const hiddenBtn = buttons.find((b) => b.text().includes('隐藏工具'))
      expect(hiddenBtn).toBeFalsy()
    })

    it('shows all visible tools', () => {
      const toolsWithHidden: RelatedInfoToolbarItem[] = [
        { key: 'refresh', tool: 'refresh', label: '刷新' },
        { key: 'visible-tool', label: '可见工具', hidden: false }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithHidden })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const visibleBtn = buttons.find((b) => b.text().includes('可见工具'))
      expect(visibleBtn).toBeTruthy()
    })
  })

  describe('divider rendering', () => {
    it('renders dividers for items with showDivider=true', () => {
      const toolsWithDivider: RelatedInfoToolbarItem[] = [
        { key: 'tool-a', label: '工具A', showDivider: true },
        { key: 'tool-b', label: '工具B' }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithDivider })
      const dividers = wrapper.findAllComponents({ name: 'ElDivider' })
      expect(dividers.length).toBe(1)
    })

    it('does not render divider after last visible item', () => {
      const toolsWithDivider: RelatedInfoToolbarItem[] = [
        { key: 'tool-a', label: '工具A' },
        { key: 'tool-b', label: '工具B', showDivider: true }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithDivider })
      const dividers = wrapper.findAllComponents({ name: 'ElDivider' })
      expect(dividers.length).toBe(0)
    })
  })
})
