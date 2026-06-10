import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import GroupNav from '@/components/relation-info/GroupNav.vue'
import type { NavGroup } from '@/types/relation-info'

const sampleGroups: NavGroup[] = [
  { key: 'basic', label: '基本信息', icon: 'InfoFilled' },
  {
    key: 'purchase',
    label: '采购管理',
    icon: 'ShoppingCart',
    badge: 5,
    children: [
      { key: 'purchase-order', label: '采购订单' },
      { key: 'purchase-return', label: '采购退货' }
    ]
  },
  {
    key: 'inventory',
    label: '库存管理',
    icon: 'Box',
    children: [
      { key: 'stock', label: '库存查询', badge: 99 },
      { key: 'stocktaking', label: '盘点记录' }
    ]
  },
  { key: 'finance', label: '财务信息', icon: 'Money', disabled: true },
  { key: 'hidden-group', label: '隐藏分组', hidden: true }
]

function createWrapper(
  overrides: {
    modelValue?: string
    fieldConfig?: NavGroup[]
    disabled?: boolean
    placeholder?: string
  } = {}
): VueWrapper {
  return mount(GroupNav, {
    props: {
      modelValue: overrides.modelValue ?? '',
      fieldConfig: overrides.fieldConfig ?? sampleGroups,
      disabled: overrides.disabled ?? false,
      placeholder: overrides.placeholder ?? ''
    }
  })
}

describe('GroupNav component', () => {
  describe('rendering', () => {
    it('renders visible groups from fieldConfig', () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      // 5 groups total, 1 hidden → 4 visible top-level items
      expect(items.length).toBe(4)
    })

    it('hides groups with hidden=true', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).not.toContain('隐藏分组')
    })

    it('renders group icons', () => {
      const wrapper = createWrapper()
      const icons = wrapper.findAll('.group-nav__icon')
      expect(icons.length).toBeGreaterThanOrEqual(1)
    })

    it('renders badges', () => {
      const wrapper = createWrapper()
      const badges = wrapper.findAllComponents({ name: 'ElBadge' })
      expect(badges.length).toBeGreaterThanOrEqual(1)
    })

    it('renders expand arrow for groups with children', () => {
      const wrapper = createWrapper()
      const arrows = wrapper.findAll('.group-nav__arrow')
      // purchase and inventory have children
      expect(arrows.length).toBe(2)
    })

    it('does not render arrow for groups without children', () => {
      const wrapper = createWrapper()
      const basicItem = wrapper.find('.group-nav__item')
      expect(basicItem.find('.group-nav__arrow').exists()).toBe(false)
    })

    it('renders empty placeholder when no groups', () => {
      const wrapper = createWrapper({ fieldConfig: [] })
      expect(wrapper.find('.group-nav__empty').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无分组')
    })

    it('renders custom placeholder text', () => {
      const wrapper = createWrapper({
        fieldConfig: [],
        placeholder: '请配置导航分组'
      })
      expect(wrapper.text()).toContain('请配置导航分组')
    })

    it('applies disabled class', () => {
      const wrapper = createWrapper({ disabled: true })
      expect(wrapper.find('.group-nav--disabled').exists()).toBe(true)
    })

    it('applies disabled style to disabled groups', () => {
      const wrapper = createWrapper()
      const disabledItems = wrapper.findAll('.group-nav__item--disabled')
      // finance group has disabled: true
      expect(disabledItems.length).toBe(1)
    })
  })

  describe('slots', () => {
    it('renders prefix slot', () => {
      const wrapper = mount(GroupNav, {
        props: { modelValue: '', fieldConfig: sampleGroups },
        slots: { prefix: '<span class="custom-prefix">Prefix Content</span>' }
      })
      expect(wrapper.find('.custom-prefix').exists()).toBe(true)
    })

    it('renders suffix slot', () => {
      const wrapper = mount(GroupNav, {
        props: { modelValue: '', fieldConfig: sampleGroups },
        slots: { suffix: '<span class="custom-suffix">Suffix Content</span>' }
      })
      expect(wrapper.find('.custom-suffix').exists()).toBe(true)
    })

    it('renders default slot', () => {
      const wrapper = mount(GroupNav, {
        props: { modelValue: '', fieldConfig: sampleGroups },
        slots: { default: '<span class="custom-default">Default Content</span>' }
      })
      expect(wrapper.find('.custom-default').exists()).toBe(true)
    })
  })

  describe('v-model', () => {
    it('highlights active group', async () => {
      const wrapper = createWrapper({ modelValue: 'basic' })
      const activeItem = wrapper.find('.group-nav__item--active')
      expect(activeItem.exists()).toBe(true)
      expect(activeItem.text()).toContain('基本信息')
    })

    it('emits update:modelValue on click', async () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      await items[0].trigger('click')
      expect(wrapper.emitted('update:modelValue')).toBeTruthy()
      expect(wrapper.emitted('update:modelValue')![0]).toEqual(['basic'])
    })

    it('emits change event on click', async () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      await items[0].trigger('click')
      expect(wrapper.emitted('change')).toBeTruthy()
      expect(wrapper.emitted('change')![0][0]).toBe('basic')
    })

    it('does not emit on disabled group click', async () => {
      const wrapper = createWrapper()
      // finance is the 4th visible item (index 3)
      const items = wrapper.findAll('.group-nav__item')
      const financeItem = items[3]
      await financeItem.trigger('click')
      // finance is disabled, so no update:modelValue should be emitted for it
      const emitted = wrapper.emitted('update:modelValue')
      // Either not emitted at all, or if emitted it shouldn't be 'finance'
      if (emitted) {
        expect(emitted[0][0]).not.toBe('finance')
      }
    })

    it('does not emit when globally disabled', async () => {
      const wrapper = createWrapper({ disabled: true })
      const items = wrapper.findAll('.group-nav__item')
      await items[0].trigger('click')
      expect(wrapper.emitted('update:modelValue')).toBeFalsy()
    })
  })

  describe('expand/collapse', () => {
    it('expands children on click', async () => {
      const wrapper = createWrapper()
      // purchase is the 2nd item (index 1), has children
      const items = wrapper.findAll('.group-nav__item')
      await items[1].trigger('click')

      // Children should now be visible
      const childItems = wrapper.findAll('.group-nav__child-item')
      expect(childItems.length).toBe(2)
      expect(childItems[0].text()).toContain('采购订单')
    })

    it('collapses children on second click', async () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      // Expand
      await items[1].trigger('click')
      let childItems = wrapper.findAll('.group-nav__child-item')
      expect(childItems.length).toBe(2)

      // Collapse
      await items[1].trigger('click')
      childItems = wrapper.findAll('.group-nav__child-item')
      expect(childItems.length).toBe(0)
    })

    it('rotates arrow when expanded', async () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      const arrow = items[1].find('.group-nav__arrow')

      expect(arrow.classes()).not.toContain('group-nav__arrow--expanded')

      await items[1].trigger('click')
      expect(arrow.classes()).toContain('group-nav__arrow--expanded')
    })

    it('selects child item on click', async () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      // Expand purchase group first
      await items[1].trigger('click')

      const childItems = wrapper.findAll('.group-nav__child-item')
      await childItems[0].trigger('click')

      expect(wrapper.emitted('update:modelValue')!.slice(-1)[0]).toEqual(['purchase-order'])
      expect(wrapper.emitted('change')!.slice(-1)[0][0]).toBe('purchase-order')
    })
  })

  describe('focus/blur events', () => {
    it('emits focus on group focus', async () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      await items[0].trigger('focus')
      expect(wrapper.emitted('focus')).toBeTruthy()
    })

    it('emits blur on group blur', async () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.group-nav__item')
      await items[0].trigger('blur')
      expect(wrapper.emitted('blur')).toBeTruthy()
    })
  })

  describe('expose methods', () => {
    it('getActiveKey returns current modelValue', () => {
      const wrapper = createWrapper({ modelValue: 'basic' })
      expect(wrapper.vm.getActiveKey()).toBe('basic')
    })

    it('getActiveKey returns undefined when empty', () => {
      const wrapper = createWrapper()
      expect(wrapper.vm.getActiveKey()).toBeUndefined()
    })

    it('setActiveKey updates selection', async () => {
      const wrapper = createWrapper()
      wrapper.vm.setActiveKey('inventory')
      await nextTick()
      expect(wrapper.emitted('update:modelValue')![0]).toEqual(['inventory'])
    })

    it('expand opens group children', async () => {
      const wrapper = createWrapper()
      wrapper.vm.expand('purchase')
      await nextTick()
      expect(wrapper.findAll('.group-nav__child-item').length).toBe(2)
    })

    it('collapse closes group children', async () => {
      const wrapper = createWrapper()
      wrapper.vm.expand('purchase')
      await nextTick()
      expect(wrapper.findAll('.group-nav__child-item').length).toBe(2)

      wrapper.vm.collapse('purchase')
      await nextTick()
      expect(wrapper.findAll('.group-nav__child-item').length).toBe(0)
    })

    it('getExpandedKeys returns expanded key list', () => {
      const wrapper = createWrapper()
      wrapper.vm.expand('purchase')
      wrapper.vm.expand('inventory')
      const keys = wrapper.vm.getExpandedKeys()
      expect(keys).toContain('purchase')
      expect(keys).toContain('inventory')
    })
  })

  describe('edge cases', () => {
    it('handles empty fieldConfig gracefully', () => {
      const wrapper = createWrapper({ fieldConfig: [] })
      expect(wrapper.find('.group-nav__empty').exists()).toBe(true)
      expect(wrapper.find('.group-nav__list').exists()).toBe(false)
    })

    it('handles groups with empty children array', () => {
      const groups: NavGroup[] = [{ key: 'test', label: 'Test', children: [] }]
      const wrapper = createWrapper({ fieldConfig: groups })
      const arrow = wrapper.find('.group-nav__arrow')
      expect(arrow.exists()).toBe(false)
    })

    it('filters hidden children', () => {
      const groups: NavGroup[] = [
        {
          key: 'parent',
          label: 'Parent',
          children: [
            { key: 'visible-child', label: 'Visible' },
            { key: 'hidden-child', label: 'Hidden', hidden: true }
          ]
        }
      ]
      const wrapper = createWrapper({ fieldConfig: groups })
      // Should still show the arrow since there's at least one visible child
      expect(wrapper.find('.group-nav__arrow').exists()).toBe(true)
    })

    it('selecting same group twice does not re-emit', async () => {
      const wrapper = createWrapper({ modelValue: 'basic' })
      const items = wrapper.findAll('.group-nav__item')
      await items[0].trigger('click')
      // Should only have the initial emission, not a second one
      const emitted = wrapper.emitted('update:modelValue')
      // basic is already selected, clicking again should not re-emit
      if (emitted) {
        expect(emitted.length).toBe(0)
      }
    })
  })
})
