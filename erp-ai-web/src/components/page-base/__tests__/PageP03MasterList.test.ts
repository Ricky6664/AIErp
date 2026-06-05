import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import PageP03MasterList from '@/components/page-base/PageP03MasterList.vue'
import type { MasterListPageConfig, PageType } from '@/types/page-base.d.ts'

const mockConfig: MasterListPageConfig = {
  title: '商品主列表',
  showQueryPanel: true,
  showActionBar: true,
  listWidthPercent: 40
}

const baseProps = {
  viewId: 'view-master-001',
  pageType: 'P03' as PageType,
  config: mockConfig as MasterListPageConfig & Record<string, unknown>,
  permissions: ['product:view', 'product:create', 'product:edit', 'product:delete']
}

function createWrapper(
  overrides: Record<string, unknown> = {}
): VueWrapper<typeof PageP03MasterList> {
  return mount(PageP03MasterList, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP03MasterList component', () => {
  describe('rendering', () => {
    it('renders the master-list container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p03-master-list').exists()).toBe(true)
    })

    it('renders query area by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area').exists()).toBe(true)
    })

    it('renders query placeholder when no query-panel slot', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area .area-placeholder').exists()).toBe(true)
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
      expect(wrapper.find('.action-area .area-placeholder').exists()).toBe(true)
    })

    it('hides action area when showActionBar is false', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, showActionBar: false }
      })
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })

    it('renders main content area with list and form panels', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
      expect(wrapper.find('.list-panel').exists()).toBe(true)
      expect(wrapper.find('.form-panel').exists()).toBe(true)
    })

    it('renders list panel placeholder when no list-content slot', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.list-panel .area-placeholder').exists()).toBe(true)
    })

    it('renders form panel placeholder when no form-content slot', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.form-panel .area-placeholder').exists()).toBe(true)
    })

    it('applies default list width when not specified', () => {
      const wrapper = createWrapper({ config: {} })
      const listPanel = wrapper.find('.list-panel')
      expect(listPanel.attributes('style')).toContain('40%')
    })

    it('applies custom list width from config', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, listWidthPercent: 60 }
      })
      const listPanel = wrapper.find('.list-panel')
      expect(listPanel.attributes('style')).toContain('60%')
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-master-001')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P03')
    })

    it('accepts config prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('config')).toEqual(mockConfig)
    })

    it('accepts permissions prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('permissions')).toEqual([
        'product:view',
        'product:create',
        'product:edit',
        'product:delete'
      ])
    })

    it('handles empty config gracefully', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-p03-master-list').exists()).toBe(true)
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
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
        viewId: 'view-master-001',
        pageType: 'P03'
      })
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP03MasterList, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP03MasterList, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
      expect(wrapper.find('.custom-action').text()).toBe('自定义操作栏')
    })

    it('renders list-content slot content', () => {
      const wrapper = mount(PageP03MasterList, {
        props: baseProps,
        slots: {
          'list-content': '<div class="custom-list">列表内容</div>'
        }
      })
      expect(wrapper.find('.custom-list').exists()).toBe(true)
      expect(wrapper.find('.custom-list').text()).toBe('列表内容')
    })

    it('renders form-content slot content', () => {
      const wrapper = mount(PageP03MasterList, {
        props: baseProps,
        slots: {
          'form-content': '<div class="custom-form">表单内容</div>'
        }
      })
      expect(wrapper.find('.custom-form').exists()).toBe(true)
      expect(wrapper.find('.custom-form').text()).toBe('表单内容')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP03MasterList, {
        props: baseProps,
        slots: {
          'extra-area': '<div class="custom-extra">额外区域</div>'
        }
      })
      expect(wrapper.find('.extra-area').exists()).toBe(true)
      expect(wrapper.find('.custom-extra').text()).toBe('额外区域')
    })

    it('does not render extra-area when slot not provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.extra-area').exists()).toBe(false)
    })
  })

  describe('reactive config', () => {
    it('hides query area when showQueryPanel changes to false', async () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area').exists()).toBe(true)
      await wrapper.setProps({
        config: { ...mockConfig, showQueryPanel: false }
      })
      await nextTick()
      expect(wrapper.find('.query-area').exists()).toBe(false)
    })

    it('hides action area when showActionBar changes to false', async () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.action-area').exists()).toBe(true)
      await wrapper.setProps({
        config: { ...mockConfig, showActionBar: false }
      })
      await nextTick()
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })

    it('updates list panel width when listWidthPercent changes', async () => {
      const wrapper = createWrapper()
      const listPanel = wrapper.find('.list-panel')
      expect(listPanel.attributes('style')).toContain('40%')
      await wrapper.setProps({
        config: { ...mockConfig, listWidthPercent: 50 }
      })
      await nextTick()
      const updatedPanel = wrapper.find('.list-panel')
      expect(updatedPanel.attributes('style')).toContain('50%')
    })
  })
})
