import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP09Query from '@/components/page-base/PageP09Query.vue'
import type { QueryPageConfig, PageType } from '@/types/page-base.d.ts'

const mockConfig: QueryPageConfig = {
  title: '订单查询',
  showQueryPanel: true,
  showActionBar: true,
  showResultCount: true,
  pageSize: 20,
  queryFields: [
    {
      id: 'qf-order-no',
      label: '订单编号',
      field: 'orderNo',
      type: 'input',
      placeholder: '请输入订单编号',
      span: 6
    },
    {
      id: 'qf-status',
      label: '订单状态',
      field: 'status',
      type: 'select',
      span: 6,
      options: [
        { label: '待付款', value: 'pending' },
        { label: '已付款', value: 'paid' },
        { label: '已取消', value: 'cancelled' }
      ]
    },
    {
      id: 'qf-date-range',
      label: '创建时间',
      field: 'createDate',
      type: 'date-range',
      span: 8
    },
    {
      id: 'qf-amount',
      label: '订单金额',
      field: 'amount',
      type: 'number',
      span: 4
    }
  ]
}

const baseProps = {
  viewId: 'view-query-002',
  pageType: 'P09' as PageType,
  config: mockConfig as QueryPageConfig & Record<string, unknown>,
  permissions: ['order:query', 'order:export']
}

function createWrapper(overrides: Record<string, unknown> = {}): VueWrapper<typeof PageP09Query> {
  return mount(PageP09Query, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP09Query component', () => {
  describe('rendering', () => {
    it('renders the query container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p09-query').exists()).toBe(true)
    })

    it('renders page title when provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-title').exists()).toBe(true)
      expect(wrapper.find('.page-title').text()).toBe('订单查询')
    })

    it('does not render page title when not provided', () => {
      const wrapper = createWrapper({ config: { queryFields: [] } })
      expect(wrapper.find('.page-title').exists()).toBe(false)
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

    it('renders result summary by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.result-summary').exists()).toBe(true)
    })

    it('hides result summary when showResultCount is false', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, showResultCount: false }
      })
      expect(wrapper.find('.result-summary').exists()).toBe(false)
    })

    it('renders pagination area when pageSize is set', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.pagination-area').exists()).toBe(true)
    })

    it('does not render pagination area when pageSize is 0', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, pageSize: 0 }
      })
      expect(wrapper.find('.pagination-area').exists()).toBe(false)
    })

    it('renders query form fields from config', () => {
      const wrapper = createWrapper()
      const formItems = wrapper.findAll('.el-form-item')
      expect(formItems).toHaveLength(4)
    })

    it('renders search and reset buttons when query fields present', () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAll('.query-form .el-button')
      expect(buttons).toHaveLength(2)
    })

    it('renders placeholder when no query fields configured', () => {
      const wrapper = createWrapper({ config: { showQueryPanel: true, queryFields: [] } })
      expect(wrapper.find('.query-area .area-placeholder').exists()).toBe(true)
    })

    it('renders placeholder in main content area by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-content-area .area-placeholder').exists()).toBe(true)
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-query-002')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P09')
    })

    it('accepts config prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('config')).toEqual(mockConfig)
    })

    it('accepts permissions prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('permissions')).toEqual(['order:query', 'order:export'])
    })

    it('handles empty config gracefully', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-p09-query').exists()).toBe(true)
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
    })

    it('handles queryFields with no items gracefully', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, queryFields: [] }
      })
      expect(wrapper.find('.query-area').exists()).toBe(true)
      expect(wrapper.find('.query-area .area-placeholder').exists()).toBe(true)
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
        viewId: 'view-query-002',
        pageType: 'P09'
      })
    })

    it('emits data-change when search button is clicked', async () => {
      const wrapper = createWrapper()
      const searchBtn = wrapper.findAll('.query-form .el-button')[0]
      await searchBtn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      expect(emitted).toBeTruthy()
      expect(emitted[0][0].source).toBe('query-search')
      expect(emitted[0][0].data).toEqual(expect.objectContaining({ orderNo: undefined }))
    })

    it('emits data-change when reset button is clicked', async () => {
      const wrapper = createWrapper()
      const resetBtn = wrapper.findAll('.query-form .el-button')[1]
      await resetBtn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      expect(emitted).toBeTruthy()
      expect(emitted[0][0].source).toBe('query-reset')
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP09Query, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP09Query, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
    })

    it('renders main-content slot content', () => {
      const wrapper = mount(PageP09Query, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">自定义主内容区</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('自定义主内容区')
    })

    it('renders pagination slot content', () => {
      const wrapper = mount(PageP09Query, {
        props: baseProps,
        slots: {
          pagination: '<div class="custom-pagination">自定义分页</div>'
        }
      })
      expect(wrapper.find('.custom-pagination').exists()).toBe(true)
      expect(wrapper.find('.custom-pagination').text()).toBe('自定义分页')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP09Query, {
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

  describe('query form initialization', () => {
    it('initializes query form from config with default values', () => {
      const configWithDefaults: QueryPageConfig = {
        queryFields: [
          {
            id: 'qf-1',
            label: '状态',
            field: 'status',
            type: 'select',
            options: [{ label: '全部', value: '' }],
            defaultValue: 'pending'
          }
        ]
      }
      const wrapper = mount(PageP09Query, {
        props: {
          ...baseProps,
          config: configWithDefaults as QueryPageConfig & Record<string, unknown>
        }
      })
      expect(wrapper.find('.page-p09-query').exists()).toBe(true)
    })

    it('handles config without queryFields gracefully', () => {
      const wrapper = createWrapper({ config: { showQueryPanel: true } })
      expect(wrapper.find('.query-area .area-placeholder').exists()).toBe(true)
    })
  })
})
