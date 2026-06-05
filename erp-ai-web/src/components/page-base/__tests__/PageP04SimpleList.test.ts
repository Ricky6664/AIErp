import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig, PageType } from '@/types/page-base.d.ts'

const mockConfig: SimpleListPageConfig = {
  title: '商品列表',
  showQueryPanel: true,
  showActionBar: true
}

const baseProps = {
  viewId: 'view-simple-001',
  pageType: 'P04' as PageType,
  config: mockConfig as SimpleListPageConfig & Record<string, unknown>,
  permissions: ['product:view', 'product:create', 'product:edit', 'product:delete']
}

function createWrapper(
  overrides: Record<string, unknown> = {}
): VueWrapper<typeof PageP04SimpleList> {
  return mount(PageP04SimpleList, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP04SimpleList component', () => {
  describe('rendering', () => {
    it('renders the simple-list container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p04-simple-list').exists()).toBe(true)
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

    it('renders main content area as full-width', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
    })

    it('renders main content placeholder when no main-content slot', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-content-area .area-placeholder').exists()).toBe(true)
    })

    it('does not have list/form split panels', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.list-panel').exists()).toBe(false)
      expect(wrapper.find('.form-panel').exists()).toBe(false)
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-simple-001')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P04')
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
      expect(wrapper.find('.page-p04-simple-list').exists()).toBe(true)
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
        viewId: 'view-simple-001',
        pageType: 'P04'
      })
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP04SimpleList, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP04SimpleList, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
      expect(wrapper.find('.custom-action').text()).toBe('自定义操作栏')
    })

    it('renders main-content slot content', () => {
      const wrapper = mount(PageP04SimpleList, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">列表内容</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('列表内容')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP04SimpleList, {
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
  })
})
