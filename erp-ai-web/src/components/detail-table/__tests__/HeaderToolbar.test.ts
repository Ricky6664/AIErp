import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import HeaderToolbar from '../HeaderToolbar.vue'
import type { DetailTableToolbarState, DetailTableToolbarItem } from '@/types/detail-table'

const baseState: DetailTableToolbarState = {
  maximized: false,
  rowHeight: 'default'
}

const customTools: DetailTableToolbarItem[] = [
  { key: 'add-row', tool: 'add-row', label: '添加行', tooltip: '添加明细行' },
  { key: 'maximize', tool: 'maximize', label: '铺满', tooltip: '铺满/还原' },
  { key: 'refresh', tool: 'refresh', label: '刷新', tooltip: '刷新明细数据' },
  { key: 'row-height', tool: 'row-height', label: '行高', tooltip: '行高调整' }
]

function createWrapper(
  overrides: {
    modelValue?: DetailTableToolbarState
    fieldConfig?: DetailTableToolbarItem[]
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

describe('HeaderToolbar component (detail-table)', () => {
  // ============================================================
  // 基础渲染
  // ============================================================

  describe('基础渲染', () => {
    it('应渲染工具栏按钮', () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      expect(buttons.length).toBeGreaterThanOrEqual(3)
    })

    it('fieldConfig 为空时应渲染默认工具按钮', () => {
      const wrapper = createWrapper({ fieldConfig: [] })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      expect(buttons.length).toBeGreaterThanOrEqual(3)
    })

    it('fieldConfig 为 undefined 时应渲染默认工具按钮', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState }
      })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      expect(buttons.length).toBeGreaterThanOrEqual(3)
    })

    it('应渲染添加行下拉按钮', () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      expect(dropdowns.length).toBeGreaterThanOrEqual(2)
    })

    it('应渲染行高下拉按钮', () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      expect(dropdowns.length).toBeGreaterThanOrEqual(2)
    })

    it('disabled 为 true 时应添加 disabled CSS 类', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.header-toolbar--disabled').exists()).toBe(true)
    })

    it('默认不应有 disabled CSS 类', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.header-toolbar--disabled').exists()).toBe(false)
    })

    it('应渲染 prefix 插槽内容', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { prefix: '<span class="custom-prefix">Prefix Content</span>' }
      })
      expect(wrapper.find('.custom-prefix').exists()).toBe(true)
      expect(wrapper.find('.custom-prefix').text()).toBe('Prefix Content')
    })

    it('应渲染 suffix 插槽内容', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { suffix: '<span class="custom-suffix">Suffix Content</span>' }
      })
      expect(wrapper.find('.custom-suffix').exists()).toBe(true)
      expect(wrapper.find('.custom-suffix').text()).toBe('Suffix Content')
    })

    it('应渲染 default 插槽内容', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: baseState, fieldConfig: customTools },
        slots: { default: '<span class="custom-default">Default Content</span>' }
      })
      expect(wrapper.find('.custom-default').exists()).toBe(true)
      expect(wrapper.find('.custom-default').text()).toBe('Default Content')
    })
  })

  // ============================================================
  // v-model 双向绑定
  // ============================================================

  describe('v-model 双向绑定', () => {
    it('应从 modelValue prop 初始化本地状态', () => {
      const wrapper = createWrapper({
        modelValue: { maximized: true, rowHeight: 'large' }
      })
      const vm = wrapper.vm as unknown as { localState: DetailTableToolbarState }
      expect(vm.localState.maximized).toBe(true)
      expect(vm.localState.rowHeight).toBe('large')
    })

    it('modelValue 字段缺失时应使用默认值', () => {
      const wrapper = mount(HeaderToolbar, {
        props: { modelValue: {} as DetailTableToolbarState }
      })
      const vm = wrapper.vm as unknown as { localState: DetailTableToolbarState }
      expect(vm.localState.maximized).toBe(false)
      expect(vm.localState.rowHeight).toBe('default')
    })

    it('点击铺满按钮时应 emit update:modelValue', async () => {
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

    it('应监听外部 modelValue 变更', async () => {
      const wrapper = createWrapper()
      await wrapper.setProps({
        modelValue: { maximized: true, rowHeight: 'compact' }
      })
      await nextTick()
      const vm = wrapper.vm as unknown as { localState: DetailTableToolbarState }
      expect(vm.localState.maximized).toBe(true)
      expect(vm.localState.rowHeight).toBe('compact')
    })
  })

  // ============================================================
  // change 事件
  // ============================================================

  describe('change 事件', () => {
    it('点击铺满按钮时应 emit change 事件 tool="maximize"', async () => {
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

    it('点击刷新按钮时应 emit change 事件 tool="refresh"', async () => {
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

    it('自定义工具按钮点击时应 emit change 事件 tool=key', async () => {
      const customConfig: DetailTableToolbarItem[] = [
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

    it('添加行下拉选择时应 emit change 事件 tool="add-row"', async () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      const addRowDropdown = dropdowns[0]
      if (addRowDropdown) {
        addRowDropdown.vm.$emit('command', 5)
        await nextTick()
        const emitted = wrapper.emitted('change') as unknown[][]
        expect(emitted).toBeTruthy()
        expect(emitted[0][0]).toBe('add-row')
      }
    })

    it('行高下拉选择时应 emit change 事件 tool="row-height"', async () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      const rowHeightDropdown = dropdowns[dropdowns.length - 1]
      if (rowHeightDropdown) {
        rowHeightDropdown.vm.$emit('command', 'compact')
        await nextTick()
        const emitted = wrapper.emitted('change') as unknown[][]
        expect(emitted).toBeTruthy()
        expect(emitted[0][0]).toBe('row-height')
      }
    })
  })

  // ============================================================
  // focus/blur 事件
  // ============================================================

  describe('focus/blur 事件', () => {
    it('按钮获得焦点时应 emit focus 事件', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const firstBtn = buttons[0]
      if (firstBtn) {
        await firstBtn.trigger('focus')
        const emitted = wrapper.emitted('focus')
        expect(emitted).toBeTruthy()
      }
    })

    it('按钮失去焦点时应 emit blur 事件', async () => {
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

  // ============================================================
  // 添加行下拉
  // ============================================================

  describe('添加行下拉', () => {
    it('应渲染 3 个添加行选项', () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      const addRowDropdown = dropdowns[0]
      if (addRowDropdown) {
        const items = addRowDropdown.findAllComponents({ name: 'ElDropdownItem' })
        expect(items.length).toBe(3)
      }
    })

    it('添加行选项应显示正确标签', () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      const addRowDropdown = dropdowns[0]
      if (addRowDropdown) {
        const items = addRowDropdown.findAllComponents({ name: 'ElDropdownItem' })
        const labels = items.map((item) => item.text())
        expect(labels).toContain('添加1行')
        expect(labels).toContain('添加5行')
        expect(labels).toContain('添加10行')
      }
    })
  })

  // ============================================================
  // 行高下拉
  // ============================================================

  describe('行高下拉', () => {
    it('应渲染 5 个行高预设选项', () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      const rowHeightDropdown = dropdowns[dropdowns.length - 1]
      if (rowHeightDropdown) {
        const items = rowHeightDropdown.findAllComponents({ name: 'ElDropdownItem' })
        expect(items.length).toBe(5)
      }
    })

    it('行高选项应显示正确标签', () => {
      const wrapper = createWrapper()
      const dropdowns = wrapper.findAllComponents({ name: 'ElDropdown' })
      const rowHeightDropdown = dropdowns[dropdowns.length - 1]
      if (rowHeightDropdown) {
        const items = rowHeightDropdown.findAllComponents({ name: 'ElDropdownItem' })
        const labels = items.map((item) => item.text())
        expect(labels).toContain('紧凑')
        expect(labels).toContain('较小')
        expect(labels).toContain('默认')
        expect(labels).toContain('较大')
        expect(labels).toContain('超大')
      }
    })
  })

  // ============================================================
  // 铺满切换
  // ============================================================

  describe('铺满切换', () => {
    it('连续点击铺满按钮应切换 maximized 状态', async () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      if (maximizeBtn) {
        await maximizeBtn.trigger('click')
        const vm = wrapper.vm as unknown as { localState: DetailTableToolbarState }
        expect(vm.localState.maximized).toBe(true)

        await maximizeBtn.trigger('click')
        expect(vm.localState.maximized).toBe(false)
      }
    })

    it('铺满状态时按钮应为 primary 类型', async () => {
      const wrapper = createWrapper({
        modelValue: { maximized: true, rowHeight: 'default' }
      })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      if (maximizeBtn) {
        expect(maximizeBtn.props('type')).toBe('primary')
      }
    })
  })

  // ============================================================
  // 禁用状态
  // ============================================================

  describe('禁用状态', () => {
    it('全局 disabled 时应阻止点击操作', async () => {
      const wrapper = createWrapper({ disabled: true })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const maximizeBtn = buttons.find((b) => b.text().includes('铺满'))
      if (maximizeBtn) {
        await maximizeBtn.trigger('click')
        expect(wrapper.emitted('change')).toBeFalsy()
      }
    })

    it('单个工具 disabled 时应阻止点击', async () => {
      const toolsWithDisabled: DetailTableToolbarItem[] = [
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

  // ============================================================
  // 隐藏工具过滤
  // ============================================================

  describe('隐藏工具过滤', () => {
    it('应过滤 hidden 为 true 的工具', () => {
      const toolsWithHidden: DetailTableToolbarItem[] = [
        { key: 'refresh', tool: 'refresh', label: '刷新' },
        { key: 'secret-tool', label: '隐藏工具', hidden: true }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithHidden })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const hiddenBtn = buttons.find((b) => b.text().includes('隐藏工具'))
      expect(hiddenBtn).toBeFalsy()
    })

    it('应显示 hidden 为 false 的工具', () => {
      const toolsWithHidden: DetailTableToolbarItem[] = [
        { key: 'refresh', tool: 'refresh', label: '刷新' },
        { key: 'visible-tool', label: '可见工具', hidden: false }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithHidden })
      const buttons = wrapper.findAllComponents({ name: 'ElButton' })
      const visibleBtn = buttons.find((b) => b.text().includes('可见工具'))
      expect(visibleBtn).toBeTruthy()
    })
  })

  // ============================================================
  // 分隔线
  // ============================================================

  describe('分隔线渲染', () => {
    it('showDivider 为 true 的工具后应渲染分隔线', () => {
      const toolsWithDivider: DetailTableToolbarItem[] = [
        { key: 'tool-a', label: '工具A', showDivider: true },
        { key: 'tool-b', label: '工具B' }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithDivider })
      const dividers = wrapper.findAllComponents({ name: 'ElDivider' })
      expect(dividers.length).toBe(1)
    })

    it('最后一个可见工具后不应渲染分隔线', () => {
      const toolsWithDivider: DetailTableToolbarItem[] = [
        { key: 'tool-a', label: '工具A' },
        { key: 'tool-b', label: '工具B', showDivider: true }
      ]
      const wrapper = createWrapper({ fieldConfig: toolsWithDivider })
      const dividers = wrapper.findAllComponents({ name: 'ElDivider' })
      expect(dividers.length).toBe(0)
    })
  })

  // ============================================================
  // expose 方法
  // ============================================================

  describe('expose 方法', () => {
    it('getState 应返回当前工具栏状态', () => {
      const wrapper = createWrapper()
      const vm = wrapper.vm as unknown as { getState: () => DetailTableToolbarState }
      const state = vm.getState()
      expect(state).toHaveProperty('maximized')
      expect(state).toHaveProperty('rowHeight')
    })

    it('toggleMaximize 应切换铺满状态', () => {
      const wrapper = createWrapper()
      const vm = wrapper.vm as unknown as {
        toggleMaximize: () => void
        localState: DetailTableToolbarState
      }
      expect(vm.localState.maximized).toBe(false)
      vm.toggleMaximize()
      expect(vm.localState.maximized).toBe(true)
    })

    it('toggleMaximize 应 emit change 事件', () => {
      const wrapper = createWrapper()
      const vm = wrapper.vm as unknown as { toggleMaximize: () => void }
      vm.toggleMaximize()
      const emitted = wrapper.emitted('change') as unknown[][]
      expect(emitted).toBeTruthy()
      expect(emitted[0][0]).toBe('maximize')
    })
  })
})
