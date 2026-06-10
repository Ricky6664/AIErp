import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP08Kanban from '@/components/page-base/PageP08Kanban.vue'
import type { KanbanPageConfig, PageType } from '@/types/page-base.d.ts'

const mockConfig: KanbanPageConfig = {
  title: '项目看板',
  showQueryPanel: true,
  showActionBar: true,
  columnWidth: 280,
  columns: [
    {
      id: 'col-todo',
      label: '待处理',
      color: 'warning',
      items: [
        {
          id: 'item-1',
          title: '用户登录优化',
          description: '优化登录页面性能和用户体验',
          tags: ['前端', '优化'],
          assignee: '张三',
          priority: 'high'
        },
        {
          id: 'item-2',
          title: '数据库索引优化',
          description: '分析慢查询并添加合适的索引',
          tags: ['后端', '数据库'],
          assignee: '李四',
          priority: 'medium'
        }
      ]
    },
    {
      id: 'col-in-progress',
      label: '进行中',
      color: 'primary',
      items: [
        {
          id: 'item-3',
          title: 'API接口重构',
          description: '重构订单模块API接口结构',
          tags: ['后端'],
          assignee: '王五',
          priority: 'high'
        }
      ]
    },
    {
      id: 'col-done',
      label: '已完成',
      color: 'success',
      items: []
    }
  ]
}

const baseProps = {
  viewId: 'view-kanban-003',
  pageType: 'P08' as PageType,
  config: mockConfig as KanbanPageConfig & Record<string, unknown>,
  permissions: ['project:view', 'project:create', 'project:edit', 'project:delete']
}

function createWrapper(overrides: Record<string, unknown> = {}): VueWrapper<typeof PageP08Kanban> {
  return mount(PageP08Kanban, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP08Kanban component', () => {
  describe('rendering', () => {
    it('renders the kanban container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p08-kanban').exists()).toBe(true)
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

    it('renders kanban board', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.kanban-board').exists()).toBe(true)
    })

    it('renders all columns from config', () => {
      const wrapper = createWrapper()
      const columns = wrapper.findAll('.kanban-column')
      expect(columns).toHaveLength(3)
    })

    it('renders column headers with labels', () => {
      const wrapper = createWrapper()
      const titles = wrapper.findAll('.column-title')
      expect(titles[0].text()).toBe('待处理')
      expect(titles[1].text()).toBe('进行中')
      expect(titles[2].text()).toBe('已完成')
    })

    it('renders item count per column', () => {
      const wrapper = createWrapper()
      const counts = wrapper.findAll('.column-header .el-tag')
      expect(counts[0].text()).toBe('2')
      expect(counts[1].text()).toBe('1')
      expect(counts[2].text()).toBe('0')
    })

    it('renders kanban cards with titles', () => {
      const wrapper = createWrapper()
      const cards = wrapper.findAll('.kanban-card')
      expect(cards).toHaveLength(3)
      expect(cards[0].find('.card-title').text()).toBe('用户登录优化')
    })

    it('renders card descriptions', () => {
      const wrapper = createWrapper()
      const descriptions = wrapper.findAll('.card-description')
      expect(descriptions).toHaveLength(3)
      expect(descriptions[0].text()).toBe('优化登录页面性能和用户体验')
    })

    it('renders card tags', () => {
      const wrapper = createWrapper()
      const firstCard = wrapper.findAll('.kanban-card')[0]
      const tags = firstCard.findAll('.card-tag')
      expect(tags).toHaveLength(2)
      expect(tags[0].text()).toBe('前端')
    })

    it('renders card assignee', () => {
      const wrapper = createWrapper()
      const firstCard = wrapper.findAll('.kanban-card')[0]
      const assignee = firstCard.find('.card-assignee')
      expect(assignee.exists()).toBe(true)
      expect(assignee.text()).toBe('张三')
    })

    it('renders empty column placeholder for columns with no items', () => {
      const wrapper = createWrapper()
      const emptyColumns = wrapper.findAll('.column-empty')
      expect(emptyColumns).toHaveLength(1)
      expect(emptyColumns[0].text()).toBe('暂无数据')
    })

    it('applies column color to header', () => {
      const wrapper = createWrapper()
      const headers = wrapper.findAll('.column-header')
      expect(headers[0].classes()).toContain('column-header--warning')
      expect(headers[1].classes()).toContain('column-header--primary')
      expect(headers[2].classes()).toContain('column-header--success')
    })

    it('renders placeholder when no columns configured', () => {
      const wrapper = createWrapper({
        config: { showQueryPanel: false, showActionBar: false, columns: [] }
      })
      expect(wrapper.find('.kanban-empty').exists()).toBe(true)
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-kanban-003')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P08')
    })

    it('accepts config prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('config')).toEqual(mockConfig)
    })

    it('accepts permissions prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('permissions')).toEqual([
        'project:view',
        'project:create',
        'project:edit',
        'project:delete'
      ])
    })

    it('handles empty config gracefully', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-p08-kanban').exists()).toBe(true)
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
      expect(wrapper.find('.kanban-board').exists()).toBe(true)
    })

    it('handles columns with no items gracefully', () => {
      const wrapper = createWrapper({
        config: {
          columns: [{ id: 'col-1', label: '空列' }]
        }
      })
      expect(wrapper.find('.kanban-column').exists()).toBe(true)
      expect(wrapper.find('.column-empty').exists()).toBe(true)
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
        viewId: 'view-kanban-003',
        pageType: 'P08'
      })
    })

    it('emits data-change when card is clicked', async () => {
      const wrapper = createWrapper()
      const firstCard = wrapper.findAll('.kanban-card')[0]
      await firstCard.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      expect(emitted).toBeTruthy()
      expect(emitted[0][0].source).toBe('kanban-card-click')
      expect(emitted[0][0].data).toEqual(expect.objectContaining({ id: 'item-1' }))
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP08Kanban, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP08Kanban, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
    })

    it('renders main-content slot content when no columns', () => {
      const wrapper = mount(PageP08Kanban, {
        props: {
          ...baseProps,
          config: {}
        },
        slots: {
          'main-content': '<div class="custom-main">自定义主内容区</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('自定义主内容区')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP08Kanban, {
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

  describe('custom column width', () => {
    it('applies default column width of 280px', () => {
      const wrapper = createWrapper()
      const column = wrapper.find('.kanban-column')
      const style = column.attributes('style')
      expect(style).toContain('280px')
    })

    it('applies custom columnWidth from config', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, columnWidth: 320 }
      })
      const column = wrapper.find('.kanban-column')
      const style = column.attributes('style')
      expect(style).toContain('320px')
    })

    it('accepts string columnWidth', () => {
      const wrapper = createWrapper({
        config: { ...mockConfig, columnWidth: '33%' }
      })
      const column = wrapper.find('.kanban-column')
      const style = column.attributes('style')
      expect(style).toContain('33%')
    })
  })

  describe('priority display', () => {
    it('renders priority tag when item has priority', () => {
      const wrapper = createWrapper()
      const cards = wrapper.findAll('.kanban-card')
      // First card has priority high
      const priorityTags = cards[0].findAll('.card-header .el-tag')
      expect(priorityTags.length).toBeGreaterThanOrEqual(1)
    })

    it('does not render priority tag when item has no priority', () => {
      const wrapper = createWrapper({
        config: {
          columns: [
            {
              id: 'col-1',
              label: '测试',
              items: [{ id: 'item-1', title: '无优先级任务' }]
            }
          ]
        }
      })
      const card = wrapper.find('.kanban-card')
      // Should not have a priority tag in card header
      expect(card.find('.card-header .el-tag').exists()).toBe(false)
    })
  })
})
