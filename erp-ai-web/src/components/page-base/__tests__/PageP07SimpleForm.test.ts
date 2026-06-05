import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP07SimpleForm from '@/components/page-base/PageP07SimpleForm.vue'
import type { SimpleFormPageConfig, PageType } from '@/types/page-base.d.ts'

const mockConfig: SimpleFormPageConfig = {
  title: '商品信息录入',
  showQueryPanel: true,
  showActionBar: true,
  formMaxWidth: 960
}

const baseProps = {
  viewId: 'view-form-002',
  pageType: 'P07' as PageType,
  config: mockConfig as SimpleFormPageConfig & Record<string, unknown>,
  permissions: ['product:view', 'product:create', 'product:edit', 'product:delete']
}

function createWrapper(
  overrides: Record<string, unknown> = {}
): VueWrapper<typeof PageP07SimpleForm> {
  return mount(PageP07SimpleForm, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP07SimpleForm component', () => {
  describe('rendering', () => {
    it('renders the simple-form container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p07-simple-form').exists()).toBe(true)
    })

    it('renders query area by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area').exists()).toBe(true)
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

    it('hides action area when showActionBar is false', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, showActionBar: false }
      })
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })

    it('renders main content area', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
    })

    it('renders form wrapper', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.form-wrapper').exists()).toBe(true)
    })

    it('renders main content placeholder by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.form-wrapper .area-placeholder').exists()).toBe(true)
    })

    it('applies default form max width of 960px', () => {
      const wrapper = createWrapper()
      const formWrapper = wrapper.find('.form-wrapper')
      const style = formWrapper.attributes('style')
      expect(style).toContain('960px')
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-form-002')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P07')
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
      expect(wrapper.find('.page-p07-simple-form').exists()).toBe(true)
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
      expect(wrapper.find('.form-wrapper').exists()).toBe(true)
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
        viewId: 'view-form-002',
        pageType: 'P07'
      })
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP07SimpleForm, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP07SimpleForm, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
    })

    it('renders main-content slot content', () => {
      const wrapper = mount(PageP07SimpleForm, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">表单内容区</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('表单内容区')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP07SimpleForm, {
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

  describe('custom form max width', () => {
    it('applies custom formMaxWidth from config', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, formMaxWidth: 800 }
      })
      const formWrapper = wrapper.find('.form-wrapper')
      const style = formWrapper.attributes('style')
      expect(style).toContain('800px')
    })

    it('accepts string formMaxWidth', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, formMaxWidth: '100%' }
      })
      const formWrapper = wrapper.find('.form-wrapper')
      const style = formWrapper.attributes('style')
      expect(style).toContain('100%')
    })
  })
})
