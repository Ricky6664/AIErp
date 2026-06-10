import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP15Designer from '@/components/page-base/PageP15Designer.vue'
import type { DesignerPageConfig, PageType } from '@/types/page-base.d.ts'

const mockAvailableComponents = [
  { type: 'list-table' as const, label: '数据表格', icon: 'List', category: 'data' as const },
  { type: 'master-form' as const, label: '主从表单', icon: 'Document', category: 'form' as const },
  { type: 'simple-form' as const, label: '简单表单', icon: 'Edit', category: 'form' as const },
  { type: 'query-panel' as const, label: '查询面板', icon: 'Search', category: 'layout' as const },
  { type: 'action-bar' as const, label: '操作栏', icon: 'Operation', category: 'layout' as const },
  { type: 'kanban' as const, label: '看板', icon: 'DataBoard', category: 'data' as const },
  { type: 'chart' as const, label: '图表', icon: 'DataAnalysis', category: 'chart' as const },
  { type: 'custom' as const, label: '自定义', icon: 'More', category: 'other' as const }
]

const mockPlacedComponents = [
  {
    id: 'comp-1',
    type: 'list-table' as const,
    label: '订单列表',
    icon: 'List',
    region: 'main' as const,
    span: 24
  },
  {
    id: 'comp-2',
    type: 'query-panel' as const,
    label: '筛选查询',
    icon: 'Search',
    region: 'query' as const,
    span: 24
  }
]

const baseConfig: DesignerPageConfig = {
  title: '页面设计器',
  showQueryPanel: false,
  showActionBar: false,
  showPalette: true,
  showProperties: true,
  availableComponents: mockAvailableComponents
}

const baseProps = {
  viewId: 'view-p15-001',
  pageType: 'P15' as PageType,
  config: baseConfig as DesignerPageConfig & Record<string, unknown>,
  permissions: ['designer:use', 'designer:save']
}

function createWrapper(
  overrides: Record<string, unknown> = {}
): VueWrapper<typeof PageP15Designer> {
  return mount(PageP15Designer, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP15Designer component', () => {
  describe('rendering', () => {
    it('renders the designer container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p15-designer').exists()).toBe(true)
    })

    it('renders the three-panel layout', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.designer-main').exists()).toBe(true)
      expect(wrapper.find('.designer-palette').exists()).toBe(true)
      expect(wrapper.find('.designer-canvas').exists()).toBe(true)
      expect(wrapper.find('.designer-properties').exists()).toBe(true)
    })

    it('renders palette header with title', () => {
      const wrapper = createWrapper()
      const header = wrapper.find('.palette-header')
      expect(header.exists()).toBe(true)
      expect(header.text()).toContain('组件面板')
    })

    it('renders properties panel header', () => {
      const wrapper = createWrapper()
      const header = wrapper.find('.properties-header')
      expect(header.exists()).toBe(true)
      expect(header.text()).toContain('属性配置')
    })

    it('renders canvas placeholder when no components placed', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.canvas-placeholder').exists()).toBe(true)
      expect(wrapper.find('.canvas-placeholder-title').text()).toContain('拖拽组件到此处构建页面')
    })

    it('renders palette items from availableComponents', () => {
      const wrapper = createWrapper()
      const items = wrapper.findAll('.palette-item')
      expect(items).toHaveLength(8)
      expect(items[0].text()).toContain('数据表格')
    })
  })

  describe('query and action areas', () => {
    it('hides query area when showQueryPanel is false', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area').exists()).toBe(false)
    })

    it('shows query area when showQueryPanel is true', () => {
      const cfg = { ...baseConfig, showQueryPanel: true }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.query-area').exists()).toBe(true)
    })

    it('hides action area when showActionBar is false', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })
  })

  describe('placed components rendering', () => {
    it('renders placed components in canvas', () => {
      const cfg = { ...baseConfig, components: mockPlacedComponents }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      const comps = wrapper.findAll('.canvas-component')
      expect(comps).toHaveLength(2)
    })

    it('displays component labels in canvas', () => {
      const cfg = { ...baseConfig, components: mockPlacedComponents }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      const labels = wrapper.findAll('.canvas-comp-header')
      expect(labels[0].text()).toContain('订单列表')
      expect(labels[1].text()).toContain('筛选查询')
    })

    it('does not show canvas placeholder when components exist', () => {
      const cfg = { ...baseConfig, components: mockPlacedComponents }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.canvas-placeholder').exists()).toBe(false)
    })
  })

  describe('component selection', () => {
    it('selects component on click', async () => {
      const cfg = { ...baseConfig, components: mockPlacedComponents }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      const comp = wrapper.findAll('.canvas-component')[0]
      await comp.trigger('click')
      const selected = wrapper.find('.canvas-component.is-selected')
      expect(selected.exists()).toBe(true)
    })

    it('shows properties for selected component', async () => {
      const cfg = { ...baseConfig, components: mockPlacedComponents }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      const comp = wrapper.findAll('.canvas-component')[0]
      await comp.trigger('click')
      expect(wrapper.find('.prop-group').exists()).toBe(true)
    })
  })

  describe('component removal', () => {
    it('removes component from canvas', async () => {
      const cfg = { ...baseConfig, components: [mockPlacedComponents[0]] }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      expect(wrapper.findAll('.canvas-component')).toHaveLength(1)

      // third button in header is the close button (after Top, Bottom)
      const buttons = wrapper.find('.comp-header-actions').findAllComponents({ name: 'ElButton' })
      expect(buttons).toHaveLength(3)
      await buttons[2].trigger('click')
      expect(wrapper.findAll('.canvas-component')).toHaveLength(0)
    })

    it('emits data-change on component remove', async () => {
      const cfg = { ...baseConfig, components: [mockPlacedComponents[0]] }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      const buttons = wrapper.find('.comp-header-actions').findAllComponents({ name: 'ElButton' })
      await buttons[2].trigger('click')
      const emitted = wrapper.emitted('data-change')
      expect(emitted).toBeTruthy()
      const removeEvents = (emitted as unknown[]).filter(
        (e: unknown) => (e as { source: string }[])[0]?.source === 'designer:component-remove'
      )
      expect(removeEvents.length).toBeGreaterThan(0)
    })
  })

  describe('drag and drop', () => {
    it('shows palette items as draggable', () => {
      const wrapper = createWrapper()
      const item = wrapper.find('.palette-item')
      expect(item.attributes('draggable')).toBe('true')
    })

    it('shows canvas drop zone', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.canvas-body').exists()).toBe(true)
    })
  })

  describe('palette filtering', () => {
    it('renders category radio buttons', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.palette-categories').exists()).toBe(true)
      expect(wrapper.find('.el-radio-group').exists()).toBe(true)
    })
  })

  describe('hide palette and properties', () => {
    it('hides palette when showPalette config is false', () => {
      const cfg = { ...baseConfig, showPalette: false }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.designer-palette').exists()).toBe(false)
    })

    it('hides properties when showProperties config is false', () => {
      const cfg = { ...baseConfig, showProperties: false }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.designer-properties').exists()).toBe(false)
    })
  })

  describe('page-ready event', () => {
    it('emits page-ready on mount', () => {
      const wrapper = createWrapper()
      const emitted = wrapper.emitted('page-ready')
      expect(emitted).toBeTruthy()
      expect(emitted![0]).toEqual([{ viewId: 'view-p15-001', pageType: 'P15' }])
    })
  })

  describe('clear canvas', () => {
    it('shows clear button when components exist', () => {
      const cfg = { ...baseConfig, components: mockPlacedComponents }
      const wrapper = createWrapper({
        config: cfg as DesignerPageConfig & Record<string, unknown>
      })
      expect(wrapper.find('.canvas-toolbar').text()).toContain('清空画布')
    })

    it('does not show clear button when no components', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.canvas-toolbar').text()).not.toContain('清空画布')
    })
  })
})
