import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import EditTable from '../index.vue'
import type { EditTableColumn, FieldConfig } from '@/types/edit-table'

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

const baseColumns: EditTableColumn[] = [
  { field: 'id', title: 'ID', width: 80, editable: false },
  {
    field: 'name',
    title: '名称',
    minWidth: 120,
    editable: true,
    editRender: { name: 'input' }
  },
  {
    field: 'price',
    title: '价格',
    width: 120,
    editable: true,
    editRender: { name: 'number' }
  },
  { field: 'createTime', title: '创建时间', width: 180, editable: false }
]

const baseData = [
  { id: 1, name: '商品A', price: 100, createTime: '2026-01-01' },
  { id: 2, name: '商品B', price: 200, createTime: '2026-01-02' },
  { id: 3, name: '商品C', price: 300, createTime: '2026-01-03' }
]

function createWrapper(props = {}) {
  return mount(EditTable, {
    props: {
      columns: baseColumns,
      modelValue: baseData,
      ...props
    },
    global: {
      stubs: {
        VxeGrid: {
          template: '<div class="vxe-grid-mock"><slot /></div>',
          props: [
            'data',
            'columns',
            'editConfig',
            'rowConfig',
            'columnConfig',
            'size',
            'stripe',
            'border',
            'height',
            'maxHeight',
            'showHeader',
            'emptyText'
          ],
          emits: ['edit-closed', 'focus', 'blur'],
          methods: {
            reloadData: () => {},
            validate: () => Promise.resolve(true),
            clearValidate: () => {}
          }
        }
      }
    }
  })
}

describe('EditTable', () => {
  beforeEach(() => {
    mockLocalStorage.clear()
  })

  it('should render component', () => {
    const wrapper = createWrapper()
    expect(wrapper.find('.edit-table').exists()).toBe(true)
  })

  it('should accept columns prop', () => {
    const wrapper = createWrapper()
    expect(wrapper.props('columns')).toEqual(baseColumns)
  })

  it('should accept modelValue prop', () => {
    const wrapper = createWrapper()
    expect(wrapper.props('modelValue')).toEqual(baseData)
  })

  it('should use default prop values', () => {
    const wrapper = createWrapper()
    expect(wrapper.props('disabled')).toBe(false)
    expect(wrapper.props('placeholder')).toBe('')
    expect(wrapper.props('size')).toBe('medium')
    expect(wrapper.props('stripe')).toBe(true)
    expect(wrapper.props('border')).toBe('inner')
    expect(wrapper.props('showHeader')).toBe(true)
    expect(wrapper.props('editTrigger')).toBe('click')
  })

  it('should accept disabled prop', () => {
    const wrapper = createWrapper({ disabled: true })
    expect(wrapper.props('disabled')).toBe(true)
  })

  it('should accept placeholder prop', () => {
    const wrapper = createWrapper({ placeholder: '请填写数据' })
    expect(wrapper.props('placeholder')).toBe('请填写数据')
  })

  it('should accept fieldConfig prop', () => {
    const fieldConfig: Record<string, FieldConfig> = {
      name: { field: 'name', fieldType: 'text', required: true }
    }
    const wrapper = createWrapper({ fieldConfig })
    expect(wrapper.props('fieldConfig')).toEqual(fieldConfig)
  })

  it('should accept size prop', () => {
    const wrapper = createWrapper({ size: 'small' })
    expect(wrapper.props('size')).toBe('small')
  })

  it('should accept stripe prop', () => {
    const wrapper = createWrapper({ stripe: false })
    expect(wrapper.props('stripe')).toBe(false)
  })

  it('should accept border prop', () => {
    const wrapper = createWrapper({ border: 'full' })
    expect(wrapper.props('border')).toBe('full')
  })

  it('should accept viewCode prop', () => {
    const wrapper = createWrapper({ viewCode: 'test-view' })
    expect(wrapper.props('viewCode')).toBe('test-view')
  })

  it('should accept editTrigger prop', () => {
    const wrapper = createWrapper({ editTrigger: 'dblclick' })
    expect(wrapper.props('editTrigger')).toBe('dblclick')
  })

  it('should accept summaryConfig prop', () => {
    const summaryCfg = {
      enabled: true,
      columns: [{ field: 'price', method: 'sum' as const }]
    }
    const wrapper = createWrapper({ summaryConfig: summaryCfg })
    expect(wrapper.props('summaryConfig')).toEqual(summaryCfg)
  })

  it('should accept height prop', () => {
    const wrapper = createWrapper({ height: 400 })
    expect(wrapper.props('height')).toBe(400)
  })

  it('should accept maxHeight prop', () => {
    const wrapper = createWrapper({ maxHeight: 600 })
    expect(wrapper.props('maxHeight')).toBe(600)
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
    const colsWithHidden: EditTableColumn[] = [
      ...baseColumns,
      {
        field: 'hidden_field',
        title: '隐藏列',
        editable: true,
        editRender: { name: 'input' },
        visible: false
      }
    ]
    const wrapper = createWrapper({ columns: colsWithHidden })
    const displayCols = wrapper.vm.displayColumns
    const hiddenCol = displayCols.find((c: EditTableColumn) => c.field === 'hidden_field')
    expect(hiddenCol?.visible).toBe(false)
  })

  it('should handle slot columns', () => {
    const colsWithSlot: EditTableColumn[] = [
      ...baseColumns,
      { field: 'action', title: '操作', width: 120, slot: 'action', editable: false }
    ]
    const wrapper = createWrapper({ columns: colsWithSlot })
    const slotted = wrapper.vm.slottedColumns
    expect(slotted).toHaveLength(1)
    expect((slotted[0] as EditTableColumn).slot).toBe('action')
  })

  it('should map size prop to grid size', () => {
    const wrapper = createWrapper({ size: 'mini' })
    expect(wrapper.vm.gridSize).toBe('mini')
  })

  it('should default size to medium', () => {
    const wrapper = createWrapper()
    expect(wrapper.vm.gridSize).toBe('medium')
  })

  it('should sync innerData with modelValue', () => {
    const wrapper = createWrapper()
    expect(wrapper.vm.innerData).toEqual(baseData)
  })

  it('should update innerData when modelValue changes', async () => {
    const wrapper = createWrapper()
    const newData = [{ id: 4, name: '商品D', price: 400, createTime: '2026-01-04' }]
    await wrapper.setProps({ modelValue: newData })
    expect(wrapper.vm.innerData).toEqual(newData)
  })

  it('should expose refresh method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.refresh).toBe('function')
  })

  it('should expose resetColumns method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.resetColumns).toBe('function')
  })

  it('should expose getData method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.getData).toBe('function')
  })

  it('should expose setData method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.setData).toBe('function')
  })

  it('should expose validate method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.validate).toBe('function')
  })

  it('should expose clearValidate method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.clearValidate).toBe('function')
  })

  it('should expose resetAll method', () => {
    const wrapper = createWrapper()
    expect(typeof wrapper.vm.resetAll).toBe('function')
  })

  it('should expose gridRef', () => {
    const wrapper = createWrapper()
    expect(wrapper.vm.gridRef).toBeDefined()
  })

  it('should getData return copy of inner data', () => {
    const wrapper = createWrapper()
    const data = wrapper.vm.getData()
    expect(data).toEqual(baseData)
    data.push({ id: 99, name: 'test' } as Record<string, unknown>)
    expect(wrapper.vm.innerData).toHaveLength(baseData.length)
  })

  it('should setData replace inner data', () => {
    const wrapper = createWrapper()
    const newData = [{ id: 99, name: 'new' }]
    wrapper.vm.setData(newData)
    expect(wrapper.vm.innerData).toEqual(newData)
  })

  it('should resetColumns clear localStorage', () => {
    const key = 'edit-table-columns-reset-view'
    mockLocalStorage.setItem(key, JSON.stringify([{ field: 'id', visible: false }]))
    const wrapper = createWrapper({ viewCode: 'reset-view' })
    wrapper.vm.resetColumns()
    expect(mockLocalStorage.getItem(key)).toBeNull()
  })

  it('should compute editConfig with disabled affecting beforeEditMethod', () => {
    const wrapper = createWrapper({ disabled: true })
    const editCfg = wrapper.vm.editConfigValue
    expect(editCfg.trigger).toBe('click')
    expect(editCfg.mode).toBe('cell')
    expect(editCfg.beforeEditMethod).toBeDefined()
    // disabled should make beforeEditMethod return false for all columns
    const result = editCfg.beforeEditMethod!({ column: baseColumns[1] })
    expect(result).toBe(false)
  })

  it('should compute editConfig with beforeEditMethod respecting column editable', () => {
    const wrapper = createWrapper()
    const editCfg = wrapper.vm.editConfigValue
    // non-editable column
    const result1 = editCfg.beforeEditMethod!({ column: baseColumns[0] })
    expect(result1).toBe(false)
    // editable column
    const result2 = editCfg.beforeEditMethod!({ column: baseColumns[1] })
    expect(result2).toBe(true)
  })

  it('should compute summary data when summaryConfig enabled', () => {
    const wrapper = createWrapper({
      summaryConfig: {
        enabled: true,
        columns: [{ field: 'price', method: 'sum' as const }]
      }
    })
    const summary = wrapper.vm.summaryData
    expect(summary).toHaveLength(1)
    expect(summary[0].displayValue).toBe('600')
  })

  it('should compute average summary correctly', () => {
    const wrapper = createWrapper({
      summaryConfig: {
        enabled: true,
        columns: [{ field: 'price', method: 'avg' as const }]
      }
    })
    const summary = wrapper.vm.summaryData
    expect(summary[0].displayValue).toBe('200')
  })

  it('should return empty summary when no data', () => {
    const wrapper = createWrapper({
      modelValue: [],
      summaryConfig: {
        enabled: true,
        columns: [{ field: 'price', method: 'sum' as const }]
      }
    })
    expect(wrapper.vm.summaryData).toEqual([])
  })

  it('should handle editTrigger prop', () => {
    const wrapper = createWrapper({ editTrigger: 'manual' })
    const editCfg = wrapper.vm.editConfigValue
    expect(editCfg.trigger).toBe('manual')
  })

  describe('rowConfig integration', () => {
    it('should accept rowConfig prop', () => {
      const rc = { isCurrent: true, isHover: true, keyField: 'uuid' }
      const wrapper = createWrapper({ rowConfig: rc })
      expect(wrapper.props('rowConfig')).toEqual(rc)
    })

    it('should default rowConfig keyField to id', () => {
      const wrapper = createWrapper()
      const rcv = wrapper.vm.rowConfigValue
      expect(rcv.keyField).toBe('id')
    })
  })
})
