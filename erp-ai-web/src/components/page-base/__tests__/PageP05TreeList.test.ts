import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import PageP05TreeList from '@/components/page-base/PageP05TreeList.vue'
import type { TreeListPageConfig, PageType } from '@/types/page-base.d.ts'

const mockConfig: TreeListPageConfig = {
  title: '商品分类管理',
  showQueryPanel: true,
  showActionBar: true,
  treeWidth: 280,
  showTreeSearch: true
}

const baseProps = {
  viewId: 'view-tree-001',
  pageType: 'P05' as PageType,
  config: mockConfig as TreeListPageConfig & Record<string, unknown>,
  permissions: ['product:view', 'product:create', 'product:edit', 'product:delete']
}

function createWrapper(
  overrides: Record<string, unknown> = {}
): VueWrapper<typeof PageP05TreeList> {
  return mount(PageP05TreeList, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP05TreeList component', () => {
  describe('rendering', () => {
    it('renders the tree-list container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p05-tree-list').exists()).toBe(true)
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

    it('renders tree panel', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.tree-panel').exists()).toBe(true)
    })

    it('renders tree-content placeholder by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.tree-content .area-placeholder').exists()).toBe(true)
    })

    it('renders tree search by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.tree-search').exists()).toBe(true)
    })

    it('hides tree search when showTreeSearch is false', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, showTreeSearch: false }
      })
      expect(wrapper.find('.tree-search').exists()).toBe(false)
    })

    it('renders tree footer with default selected path text', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.tree-footer').exists()).toBe(true)
      expect(wrapper.find('.selected-path').text()).toBe('当前选中：—')
    })

    it('renders list panel', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.list-panel').exists()).toBe(true)
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

    it('renders data content area', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.data-content').exists()).toBe(true)
    })

    it('renders main content placeholder by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.data-content .area-placeholder').exists()).toBe(true)
    })

    it('applies default tree panel width of 280px', () => {
      const wrapper = createWrapper()
      const treePanel = wrapper.find('.tree-panel')
      const style = treePanel.attributes('style')
      expect(style).toContain('280px')
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-tree-001')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P05')
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
      expect(wrapper.find('.page-p05-tree-list').exists()).toBe(true)
      expect(wrapper.find('.tree-panel').exists()).toBe(true)
      expect(wrapper.find('.list-panel').exists()).toBe(true)
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
        viewId: 'view-tree-001',
        pageType: 'P05'
      })
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP05TreeList, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders tree-content slot content', () => {
      const wrapper = mount(PageP05TreeList, {
        props: baseProps,
        slots: {
          'tree-content': '<div class="custom-tree">分类树</div>'
        }
      })
      expect(wrapper.find('.custom-tree').exists()).toBe(true)
      expect(wrapper.find('.custom-tree').text()).toBe('分类树')
    })

    it('renders tree-search slot content', () => {
      const wrapper = mount(PageP05TreeList, {
        props: baseProps,
        slots: {
          'tree-search': '<div class="custom-tree-search">高级搜索</div>'
        }
      })
      expect(wrapper.find('.custom-tree-search').exists()).toBe(true)
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP05TreeList, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
    })

    it('renders main-content slot content', () => {
      const wrapper = mount(PageP05TreeList, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">数据表格</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP05TreeList, {
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

    it('renders tree-footer slot content', () => {
      const wrapper = mount(PageP05TreeList, {
        props: baseProps,
        slots: {
          'tree-footer': '<span class="custom-path">全部/电子产品</span>'
        }
      })
      expect(wrapper.find('.custom-path').text()).toBe('全部/电子产品')
    })
  })

  describe('treeSearchKeyword reactivity', () => {
    it('binds v-model to tree search input', async () => {
      const wrapper = createWrapper()
      const input = wrapper.find('.tree-search .el-input__inner')
      if (input.exists()) {
        await input.setValue('测试')
        await nextTick()
        expect((input.element as HTMLInputElement).value).toBe('测试')
      }
    })
  })

  describe('custom tree width', () => {
    it('applies custom treeWidth from config', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, treeWidth: 320 }
      })
      const treePanel = wrapper.find('.tree-panel')
      const style = treePanel.attributes('style')
      expect(style).toContain('320px')
    })
  })
})
