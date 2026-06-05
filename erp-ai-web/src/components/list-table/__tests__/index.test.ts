import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ListTable from '../index.vue'
import type { ListTableColumn, SortConfig } from '@/types/list-table'

const mockLocalStorage = {
  store: {} as Record<string, string>,
  getItem(key: string): string | null {
    return this.store[key] ?? null
  },
  setItem(key: string, value: string): void {
    this.store[key] = value
  },
  removeItem(key: string): void {
    delete this.store[key]
  },
  clear(): void {
    this.store = {}
  }
}

Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage,
  writable: true
})

const baseColumns: ListTableColumn[] = [
  { field: 'id', title: 'ID', width: 80, sortable: true },
  { field: 'name', title: '名称', minWidth: 120, sortable: true },
  { field: 'status', title: '状态', width: 100 },
  { field: 'createTime', title: '创建时间', width: 180 }
]

const baseData = [
  { id: 1, name: '测试数据1', status: '启用', createTime: '2026-01-01' },
  { id: 2, name: '测试数据2', status: '禁用', createTime: '2026-01-02' },
  { id: 3, name: '测试数据3', status: '启用', createTime: '2026-01-03' }
]

function createWrapper(props = {}) {
  return mount(ListTable, {
    props: {
      columns: baseColumns,
      data: baseData,
      ...props
    },
    global: {
      stubs: {
        VxeGrid: {
          template: '<div class="vxe-grid-mock"><slot /></div>',
          props: [
            'data',
            'loading',
            'columns',
            'pagerConfig',
            'sortConfig',
            'rowConfig',
            'columnConfig',
            'size',
            'stripe',
            'border',
            'height',
            'maxHeight',
            'showHeader',
            'emptyText',
            'virtualScroll'
          ],
          emits: [
            'sort-change',
            'filter-change',
            'current-change',
            'cell-click',
            'row-click',
            'row-dblclick',
            'page-change',
            'column-resize'
          ]
        }
      }
    }
  })
}

describe('ListTable', () => {
  beforeEach(() => {
    mockLocalStorage.clear()
  })

  it('should render component', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('.list-table').exists()).toBe(true)
  })

  it('should accept columns prop', () => {
    const wrapper = createWrapper()
    expect(wrapper.props('columns')).toEqual(baseColumns)
  })

  it('should accept data prop', () => {
    const wrapper = createWrapper()
    expect(wrapper.props('data')).toEqual(baseData)
  })

  it('should accept loading prop', () => {
    const wrapper = createWrapper({ loading: true })
    expect(wrapper.props('loading')).toBe(true)
  })

  it('should use default prop values', () => {
    const wrapper = createWrapper()
    expect(wrapper.props('pageSize')).toBe(20)
    expect(wrapper.props('currentPage')).toBe(1)
    expect(wrapper.props('pageMode')).toBe('server')
    expect(wrapper.props('size')).toBe('medium')
    expect(wrapper.props('stripe')).toBe(true)
    expect(wrapper.props('border')).toBe('inner')
    expect(wrapper.props('showHeader')).toBe(true)
    expect(wrapper.props('virtualScroll')).toBe(true)
  })

  it('should accept custom pagination props', () => {
    const wrapper = createWrapper({
      currentPage: 3,
      pageSize: 50,
      total: 200,
      pageMode: 'server'
    })
    expect(wrapper.props('currentPage')).toBe(3)
    expect(wrapper.props('pageSize')).toBe(50)
    expect(wrapper.props('total')).toBe(200)
    expect(wrapper.props('pageMode')).toBe('server')
  })

  it('should expose resetColumns method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.resetColumns).toBe('function')
  })

  it('should expose refresh method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.refresh).toBe('function')
  })

  it('should expose clearSort method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.clearSort).toBe('function')
  })

  it('should expose clearFilter method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.clearFilter).toBe('function')
  })

  it('should expose clearCurrent method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.clearCurrent).toBe('function')
  })

  it('should expose getCurrentRow method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.getCurrentRow).toBe('function')
  })

  it('should expose setCurrentRow method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.setCurrentRow).toBe('function')
  })

  it('should compute display columns from columns prop', () => {
    const wrapper = createWrapper()
    const displayCols = wrapper.vm.displayColumns
    expect(displayCols).toHaveLength(baseColumns.length)
    for (const col of displayCols) {
      expect(col).toHaveProperty('field')
      expect(col).toHaveProperty('title')
    }
  })

  it('should filter invisible columns', () => {
    const colsWithHidden = [
      ...baseColumns,
      { field: 'hidden_field', title: '隐藏列', visible: false }
    ]
    const wrapper = createWrapper({ columns: colsWithHidden })
    const displayCols = wrapper.vm.displayColumns
    const hiddenCol = displayCols.find((c: ListTableColumn) => c.field === 'hidden_field')
    expect(hiddenCol?.visible).toBe(false)
  })

  it('should persist column width to localStorage when viewCode provided', () => {
    const wrapper = createWrapper({ viewCode: 'test-view' })
    wrapper.vm.resetColumns()
    const key = 'list-table-columns-test-view'
    expect(mockLocalStorage.getItem(key)).toBeNull()
  })

  it('should clear localStorage on resetColumns', () => {
    const key = 'list-table-columns-reset-view'
    mockLocalStorage.setItem(key, JSON.stringify([{ field: 'id', visible: false }]))
    const wrapper = createWrapper({ viewCode: 'reset-view' })
    wrapper.vm.resetColumns()
    expect(mockLocalStorage.getItem(key)).toBeNull()
  })

  it('should handle slot columns', () => {
    const colsWithSlot: ListTableColumn[] = [
      ...baseColumns,
      { field: 'action', title: '操作', width: 120, slot: 'action' }
    ]
    const wrapper = createWrapper({ columns: colsWithSlot })
    const slotted = wrapper.vm.slottedColumns
    expect(slotted).toHaveLength(1)
    expect((slotted[0] as ListTableColumn).slot).toBe('action')
  })

  it('should map size prop to grid size', () => {
    const wrapper = createWrapper({ size: 'mini' })
    // gridSize is computed
    expect(wrapper.vm.gridSize).toBe('mini')
  })

  it('should default size to medium', () => {
    const wrapper = createWrapper()
    expect(wrapper.vm.gridSize).toBe('medium')
  })

  it('should handle sortConfig prop', () => {
    const sortCfg = { field: 'name', order: 'asc' as const }
    const wrapper = createWrapper({ sortConfig: sortCfg })
    expect(wrapper.props('sortConfig')).toEqual(sortCfg)
  })

  describe('column sorting', () => {
    it('should accept single-column sortConfig', () => {
      const sortCfg: SortConfig = { field: 'name', order: 'asc' }
      const wrapper = createWrapper({ sortConfig: sortCfg })
      expect(wrapper.props('sortConfig')).toEqual(sortCfg)
    })

    it('should accept multi-column sortConfig with fields array', () => {
      const sortCfg: SortConfig = {
        fields: [
          { field: 'name', order: 'asc' },
          { field: 'createTime', order: 'desc' }
        ],
        multiple: true
      }
      const wrapper = createWrapper({ sortConfig: sortCfg })
      const cfg = wrapper.props('sortConfig') as SortConfig
      expect(cfg.fields).toHaveLength(2)
      expect(cfg.multiple).toBe(true)
    })

    it('should compute sortConfigValue for single sort', () => {
      const sortCfg: SortConfig = { field: 'id', order: 'desc', trigger: 'cell' }
      const wrapper = createWrapper({ sortConfig: sortCfg })
      const sv = wrapper.vm.sortConfigValue
      expect(sv.trigger).toBe('cell')
      expect(sv.multiple).toBe(false)
    })

    it('should compute sortConfigValue for multi sort', () => {
      const sortCfg: SortConfig = {
        fields: [{ field: 'name', order: 'asc' }],
        multiple: true,
        remote: true
      }
      const wrapper = createWrapper({ sortConfig: sortCfg })
      const sv = wrapper.vm.sortConfigValue
      expect(sv.multiple).toBe(true)
      expect(sv.remote).toBe(true)
    })

    it('should expose setSort method', () => {
      const wrapper = createWrapper()
      expect(typeof wrapper.vm.setSort).toBe('function')
    })

    it('should expose getSortColumns method', () => {
      const wrapper = createWrapper()
      expect(typeof wrapper.vm.getSortColumns).toBe('function')
    })

    it('should expose clearSort method', () => {
      const wrapper = createWrapper()
      expect(typeof wrapper.vm.clearSort).toBe('function')
    })

    it('should return empty array from getSortColumns when no sort active', () => {
      const wrapper = createWrapper()
      const cols = wrapper.vm.getSortColumns()
      expect(cols).toEqual([])
    })

    it('should handle sortConfig with remote flag', () => {
      const sortCfg: SortConfig = { field: 'status', order: 'desc', remote: true }
      const wrapper = createWrapper({ sortConfig: sortCfg })
      const sv = wrapper.vm.sortConfigValue
      expect(sv.remote).toBe(true)
    })

    it('should default showIcon to true', () => {
      const sortCfg: SortConfig = { field: 'name', order: 'asc' }
      const wrapper = createWrapper({ sortConfig: sortCfg })
      const sv = wrapper.vm.sortConfigValue
      expect(sv.showIcon).toBe(true)
    })

    it('should respect showIcon false', () => {
      const sortCfg: SortConfig = { field: 'name', order: 'asc', showIcon: false }
      const wrapper = createWrapper({ sortConfig: sortCfg })
      const sv = wrapper.vm.sortConfigValue
      expect(sv.showIcon).toBe(false)
    })
  })
})
