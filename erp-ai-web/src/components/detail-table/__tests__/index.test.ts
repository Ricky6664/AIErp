import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import DetailTable from '../index.vue'
import type { DetailTableTab } from '@/types/detail-table'

const baseTabs: DetailTableTab[] = [
  { key: 'order-items', label: '订单明细' },
  { key: 'payments', label: '收款记录' },
  { key: 'logistics', label: '物流信息', lazy: true },
  { key: 'hidden-tab', label: '隐藏标签页', hidden: true }
]

function createWrapper(props = {}) {
  return mount(DetailTable, {
    props: {
      fieldConfig: baseTabs,
      modelValue: 'order-items',
      ...props
    }
  })
}

describe('DetailTable', () => {
  beforeEach(() => {
    document.body.innerHTML = ''
  })

  // ============================================================
  // 基础渲染
  // ============================================================

  describe('基础渲染', () => {
    it('应渲染标签页头部', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.detail-table__header').exists()).toBe(true)
    })

    it('应渲染可见的标签页（过滤隐藏项）', () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.detail-table__tab')
      expect(tabs).toHaveLength(3) // 4 - 1 hidden
    })

    it('应高亮激活的标签页', () => {
      const wrapper = createWrapper()
      const activeTab = wrapper.find('.detail-table__tab--active')
      expect(activeTab.exists()).toBe(true)
      expect(activeTab.text()).toContain('订单明细')
    })

    it('应在禁用时添加 disabled 类', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.detail-table--disabled').exists()).toBe(true)
    })

    it('应在无可见标签页时显示空状态', () => {
      const wrapper = createWrapper({ fieldConfig: [] })
      expect(wrapper.find('.detail-table__empty').exists()).toBe(true)
    })

    it('应使用自定义 placeholder', () => {
      const wrapper = createWrapper({ fieldConfig: [], placeholder: '暂无数据' })
      expect(wrapper.find('.detail-table__empty').text()).toBe('暂无数据')
    })

    it('应过滤 hidden 为 true 的标签页', () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.detail-table__tab')
      const hiddenTab = tabs.find((t) => t.text().includes('隐藏标签页'))
      expect(hiddenTab).toBeUndefined()
    })
  })

  // ============================================================
  // 标签页切换
  // ============================================================

  describe('标签页切换', () => {
    it('点击标签页应触发 update:modelValue 和 change 事件', async () => {
      const wrapper = createWrapper()
      const tabs = wrapper.findAll('.detail-table__tab')
      const paymentTab = tabs.find((t) => t.text().includes('收款记录'))
      expect(paymentTab).toBeTruthy()
      await paymentTab!.trigger('click')

      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')![0]).toEqual(['payments'])
      expect(wrapper.emitted('change')).toBeTruthy()
    })

    it('重复点击已激活标签页不应触发事件', async () => {
      const wrapper = createWrapper()
      const activeTab = wrapper.find('.detail-table__tab--active')
      await activeTab.trigger('click')

      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })

    it('点击禁用的标签页不应触发事件', async () => {
      const disabledTabs: DetailTableTab[] = [
        { key: 'a', label: 'A', disabled: true },
        { key: 'b', label: 'B' }
      ]
      const wrapper = createWrapper({ fieldConfig: disabledTabs, modelValue: 'b' })
      const tabs = wrapper.findAll('.detail-table__tab')
      const disabledTab = tabs.find((t) => t.text().includes('A'))
      await disabledTab!.trigger('click')

      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })

    it('全局 disabled 时所有标签页不可点击', async () => {
      const wrapper = createWrapper({ disabled: true })
      const tabs = wrapper.findAll('.detail-table__tab')
      const paymentTab = tabs.find((t) => t.text().includes('收款记录'))
      await paymentTab!.trigger('click')

      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })
  })

  // ============================================================
  // expose 方法
  // ============================================================

  describe('expose 方法', () => {
    it('getActiveKey 应返回当前激活标签页 key', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getActiveKey()).toBe('order-items')
    })

    it('无激活标签页时 getActiveKey 应返回 undefined', () => {
      const wrapper = createWrapper({ modelValue: '' })
      expect(wrapper.vm.getActiveKey()).toBeUndefined()
    })

    it('setActiveKey 应切换激活标签页', async () => {
      const wrapper = createWrapper()
      wrapper.vm.setActiveKey('payments')

      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')![0]).toEqual(['payments'])
    })

    it('setActiveKey 对禁用的标签页不应生效', () => {
      const disabledTabs: DetailTableTab[] = [
        { key: 'a', label: 'A', disabled: true },
        { key: 'b', label: 'B' }
      ]
      const wrapper = createWrapper({ fieldConfig: disabledTabs, modelValue: 'b' })
      wrapper.vm.setActiveKey('a')

      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })

    it('getVisibleTabs 应返回可见标签页列表', () => {
      const wrapper = createWrapper()
      const visible = wrapper.vm.getVisibleTabs()
      expect(visible).toHaveLength(3)
      expect(visible.every((t: DetailTableTab) => !t.hidden)).toBe(true)
    })
  })

  // ============================================================
  // 插槽
  // ============================================================

  describe('插槽', () => {
    it('应渲染 prefix 插槽内容', () => {
      const wrapper = mount(DetailTable, {
        props: { fieldConfig: baseTabs, modelValue: 'order-items' },
        slots: { prefix: '<div class="prefix-mock">Prefix Content</div>' }
      })
      expect(wrapper.find('.prefix-mock').exists()).toBe(true)
    })

    it('应渲染 suffix 插槽内容', () => {
      const wrapper = mount(DetailTable, {
        props: { fieldConfig: baseTabs, modelValue: 'order-items' },
        slots: { suffix: '<div class="suffix-mock">Suffix Content</div>' }
      })
      expect(wrapper.find('.suffix-mock').exists()).toBe(true)
    })

    it('应渲染 default 插槽内容', () => {
      const wrapper = mount(DetailTable, {
        props: { fieldConfig: baseTabs, modelValue: 'order-items' },
        slots: { default: '<div class="default-mock">Default Content</div>' }
      })
      expect(wrapper.find('.default-mock').exists()).toBe(true)
    })
  })

  // ============================================================
  // 懒加载
  // ============================================================

  describe('懒加载', () => {
    it('默认懒加载：未激活标签页的具名插槽内容不应渲染', () => {
      const wrapper = mount(DetailTable, {
        props: {
          fieldConfig: [
            { key: 'tab-a', label: '标签A' },
            { key: 'tab-b', label: '标签B', lazy: true },
            { key: 'tab-c', label: '标签C' }
          ],
          modelValue: 'tab-a'
        },
        slots: {
          'tab-a': '<div class="slot-a">Content A</div>',
          'tab-b': '<div class="slot-b">Content B</div>',
          'tab-c': '<div class="slot-c">Content C</div>'
        }
      })
      // 只有激活标签页的内容应渲染
      expect(wrapper.find('.slot-a').exists()).toBe(true)
      expect(wrapper.find('.slot-b').exists()).toBe(false)
      expect(wrapper.find('.slot-c').exists()).toBe(false)
    })

    it('lazy=false 时标签页内容应始终渲染（非懒加载）', () => {
      const wrapper = mount(DetailTable, {
        props: {
          fieldConfig: [
            { key: 'tab-a', label: '标签A' },
            { key: 'tab-b', label: '标签B', lazy: false }
          ],
          modelValue: 'tab-a'
        },
        slots: {
          'tab-a': '<div class="slot-a">Content A</div>',
          'tab-b': '<div class="slot-b">Content B</div>'
        }
      })
      // lazy=false 的标签页内容也应渲染
      expect(wrapper.find('.slot-a').exists()).toBe(true)
      expect(wrapper.find('.slot-b').exists()).toBe(true)
    })

    it('切换标签页后新激活标签页内容应变为可见', async () => {
      const wrapper = mount(DetailTable, {
        props: {
          fieldConfig: [
            { key: 'tab-a', label: '标签A' },
            { key: 'tab-b', label: '标签B' }
          ],
          modelValue: 'tab-a'
        },
        slots: {
          'tab-a': '<div class="slot-a">Content A</div>',
          'tab-b': '<div class="slot-b">Content B</div>'
        }
      })
      expect(wrapper.find('.slot-b').exists()).toBe(false)

      await wrapper.setProps({ modelValue: 'tab-b' })
      // 切换后 tab-b 内容应渲染
      expect(wrapper.find('.slot-b').exists()).toBe(true)
    })

    it('默认插槽应始终渲染', () => {
      const wrapper = mount(DetailTable, {
        props: {
          fieldConfig: [
            { key: 'tab-a', label: '标签A' },
            { key: 'tab-b', label: '标签B' }
          ],
          modelValue: 'tab-a'
        },
        slots: {
          default: '<div class="default-slot">Default Content</div>'
        }
      })
      expect(wrapper.find('.default-slot').exists()).toBe(true)
    })
  })

  // ============================================================
  // 焦点事件
  // ============================================================

  describe('焦点事件', () => {
    it('标签页 focus 应触发 focus 事件', async () => {
      const wrapper = createWrapper()
      const tab = wrapper.find('.detail-table__tab')
      await tab.trigger('focus')
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('标签页 blur 应触发 blur 事件', async () => {
      const wrapper = createWrapper()
      const tab = wrapper.find('.detail-table__tab')
      await tab.trigger('blur')
      expect(wrapper.emitted('blur')).toBeTruthy()
    })
  })

  // ============================================================
  // 区域铺满切换
  // ============================================================

  describe('区域铺满切换', () => {
    it('maximized 为 false 时不应有 maximized CSS 类', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.detail-table--maximized').exists()).toBe(false)
    })

    it('maximized 为 true 时应添加 maximized CSS 类', () => {
      const wrapper = createWrapper({ maximized: true })
      expect(wrapper.find('.detail-table--maximized').exists()).toBe(true)
    })

    it('maximized prop 默认值应为 false', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('maximized')).toBe(false)
    })

    it('toggleMaximize 从 false 切换应 emit maximize 事件', () => {
      const wrapper = createWrapper()
      wrapper.vm.toggleMaximize()
      expect(wrapper.emitted('maximize')).toBeTruthy()
    })

    it('toggleMaximize 从 true 切换应 emit unmaximize 事件', () => {
      const wrapper = createWrapper({ maximized: true })
      wrapper.vm.toggleMaximize()
      expect(wrapper.emitted('unmaximize')).toBeTruthy()
    })

    it('disabled 时 maximize 样式不应冲突', () => {
      const wrapper = createWrapper({ disabled: true, maximized: true })
      expect(wrapper.find('.detail-table--disabled').exists()).toBe(true)
      expect(wrapper.find('.detail-table--maximized').exists()).toBe(true)
    })
  })
})
