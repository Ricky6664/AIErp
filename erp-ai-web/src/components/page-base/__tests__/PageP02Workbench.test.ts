import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import PageP02Workbench from '@/components/page-base/PageP02Workbench.vue'
import type { WorkbenchPageConfig, PageType } from '@/types/page-base.d.ts'

const mockConfig: WorkbenchPageConfig = {
  title: '销售工作台',
  statCards: [
    { id: 's1', label: '待审核', value: 12, icon: 'DocumentChecked', color: 'orange' },
    { id: 's2', label: '今日新增', value: 38, icon: 'Plus', color: 'blue' },
    { id: 's3', label: '本月完成', value: 256, icon: 'CircleCheck', color: 'green' },
    { id: 's4', label: '逾期未处理', value: 3, icon: 'WarningFilled', color: 'red' }
  ],
  showQueryPanel: true,
  showActionBar: true
}

const baseProps = {
  viewId: 'view-workbench-001',
  pageType: 'P02' as PageType,
  config: mockConfig as WorkbenchPageConfig & Record<string, unknown>,
  permissions: ['order:view', 'order:create', 'order:edit', 'order:delete']
}

function createWrapper(overrides: Record<string, unknown> = {}): VueWrapper {
  return mount(PageP02Workbench, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP02Workbench component', () => {
  describe('rendering', () => {
    it('renders the workbench container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p02-workbench').exists()).toBe(true)
    })

    it('renders the header with title', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.workbench-header').exists()).toBe(true)
      expect(wrapper.find('.page-title').text()).toBe('销售工作台')
    })

    it('defaults title when not provided', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-title').text()).toBe('工作台')
    })

    it('renders stat cards when config has statCards', () => {
      const wrapper = createWrapper()
      const cards = wrapper.findAll('.stat-card-item')
      expect(cards.length).toBe(4)
    })

    it('does not render stat cards row when empty', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.stat-cards-row').exists()).toBe(false)
    })

    it('renders stat card values and labels', () => {
      const wrapper = createWrapper()
      const firstCard = wrapper.find('.stat-card-item')
      expect(firstCard.find('.stat-card-value').text()).toBe('12')
      expect(firstCard.find('.stat-card-label').text()).toBe('待审核')
    })

    it('renders query area by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area').exists()).toBe(true)
    })

    it('renders query placeholder when no query-panel slot', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-placeholder').exists()).toBe(true)
    })

    it('hides query area when showQueryPanel is false', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, showQueryPanel: false }
      })
      expect(wrapper.find('.query-area').exists()).toBe(false)
    })

    it('renders action area by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.action-area').exists()).toBe(true)
    })

    it('renders action placeholder when no action-bar slot', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.action-placeholder').exists()).toBe(true)
    })

    it('hides action area when showActionBar is false', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, showActionBar: false }
      })
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })

    it('renders main content area', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-area').exists()).toBe(true)
    })

    it('renders main placeholder when no main-content slot', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-placeholder').exists()).toBe(true)
    })

    it('applies correct color class to stat cards', () => {
      const wrapper = createWrapper()
      const cards = wrapper.findAll('.stat-card-item')
      expect(cards[0].classes()).toContain('stat-card-orange')
      expect(cards[1].classes()).toContain('stat-card-blue')
      expect(cards[2].classes()).toContain('stat-card-green')
      expect(cards[3].classes()).toContain('stat-card-red')
    })

    it('renders header-extra slot area', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.header-right').exists()).toBe(true)
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-workbench-001')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P02')
    })

    it('accepts config prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('config')).toEqual(mockConfig)
    })

    it('accepts permissions prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('permissions')).toEqual([
        'order:view',
        'order:create',
        'order:edit',
        'order:delete'
      ])
    })

    it('handles empty config gracefully', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-p02-workbench').exists()).toBe(true)
      expect(wrapper.find('.workbench-header').exists()).toBe(true)
    })
  })

  describe('events', () => {
    it('emits page-ready on mount with viewId and pageType', () => {
      const wrapper = createWrapper()
      const emitted = wrapper.emitted('page-ready') as Array<
        Array<{ viewId: string; pageType: string }>
      >
      expect(emitted).toBeTruthy()
      expect(emitted[0][0]).toEqual({
        viewId: 'view-workbench-001',
        pageType: 'P02'
      })
    })

    it('emits navigate when stat card with "to" is clicked', async () => {
      const configWithTo: WorkbenchPageConfig = {
        ...mockConfig,
        statCards: [
          {
            id: 's1',
            label: '待审核',
            value: 12,
            icon: 'DocumentChecked',
            color: 'orange',
            to: '/workflow/approval'
          }
        ]
      }
      const wrapper = createWrapper({ config: configWithTo })
      const card = wrapper.find('.stat-card-item')
      await card.trigger('click')
      const emitted = wrapper.emitted('navigate') as Array<Array<{ to: string }>>
      expect(emitted).toBeTruthy()
      expect(emitted[0][0]).toEqual({ to: '/workflow/approval' })
    })

    it('does not emit navigate when stat card has no "to"', async () => {
      const wrapper = createWrapper()
      const card = wrapper.find('.stat-card-item')
      await card.trigger('click')
      const emitted = wrapper.emitted('navigate')
      expect(emitted).toBeFalsy()
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP02Workbench, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP02Workbench, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
      expect(wrapper.find('.custom-action').text()).toBe('自定义操作栏')
    })

    it('renders main-content slot content', () => {
      const wrapper = mount(PageP02Workbench, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">表格/表单内容</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('表格/表单内容')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP02Workbench, {
        props: baseProps,
        slots: {
          'extra-area': '<div class="custom-extra">额外区域内容</div>'
        }
      })
      expect(wrapper.find('.extra-area').exists()).toBe(true)
      expect(wrapper.find('.custom-extra').text()).toBe('额外区域内容')
    })

    it('does not render extra-area when slot not provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.extra-area').exists()).toBe(false)
    })

    it('renders header-extra slot content', () => {
      const wrapper = mount(PageP02Workbench, {
        props: baseProps,
        slots: {
          'header-extra': '<div class="header-custom">头部额外内容</div>'
        }
      })
      expect(wrapper.find('.header-custom').exists()).toBe(true)
    })
  })

  describe('reactive config', () => {
    it('updates title when config changes', async () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-title').text()).toBe('销售工作台')
      await wrapper.setProps({
        config: { ...mockConfig, title: '采购工作台' }
      })
      await nextTick()
      expect(wrapper.find('.page-title').text()).toBe('采购工作台')
    })

    it('updates stat cards when config changes', async () => {
      const wrapper = createWrapper()
      expect(wrapper.findAll('.stat-card-item').length).toBe(4)
      await wrapper.setProps({
        config: { ...mockConfig, statCards: [mockConfig.statCards![0]] }
      })
      await nextTick()
      expect(wrapper.findAll('.stat-card-item').length).toBe(1)
    })

    it('shows query area when showQueryPanel toggles', async () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area').exists()).toBe(true)
      await wrapper.setProps({
        config: { ...mockConfig, showQueryPanel: false }
      })
      await nextTick()
      expect(wrapper.find('.query-area').exists()).toBe(false)
    })

    it('shows action area when showActionBar toggles', async () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.action-area').exists()).toBe(true)
      await wrapper.setProps({
        config: { ...mockConfig, showActionBar: false }
      })
      await nextTick()
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })
  })

  describe('stat card color classes', () => {
    it('defaults to blue when no color specified', () => {
      const configNoColor: WorkbenchPageConfig = {
        statCards: [{ id: 's1', label: '默认', value: 10, icon: 'Star' }]
      }
      const wrapper = createWrapper({ config: configNoColor })
      const card = wrapper.find('.stat-card-item')
      expect(card.classes()).toContain('stat-card-blue')
    })
  })
})
