import { describe, it, expect } from 'vitest'
import { mount, type VueWrapper } from '@vue/test-utils'
import PageP10Report from '@/components/page-base/PageP10Report.vue'
import type { ReportPageConfig, PageType } from '@/types/page-base.d.ts'

const mockFilterFields = [
  {
    id: 'ff-period',
    label: '会计期间',
    field: 'period',
    type: 'select' as const,
    span: 6,
    options: [
      { label: '2026-01', value: '2026-01' },
      { label: '2026-02', value: '2026-02' }
    ]
  },
  {
    id: 'ff-org',
    label: '核算组织',
    field: 'orgId',
    type: 'select' as const,
    span: 6,
    options: [
      { label: '总公司', value: '1' },
      { label: '分公司A', value: '2' }
    ]
  },
  {
    id: 'ff-account',
    label: '科目编码',
    field: 'accountCode',
    type: 'input' as const,
    span: 6,
    placeholder: '请输入科目编码'
  },
  {
    id: 'ff-date-range',
    label: '日期范围',
    field: 'dateRange',
    type: 'date-range' as const,
    span: 8
  }
]

const mockColumns = [
  { id: 'col-1', label: '科目编码', field: 'accountCode', width: 120, align: 'left' as const },
  { id: 'col-2', label: '科目名称', field: 'accountName', width: 200, align: 'left' as const },
  { id: 'col-3', label: '期初余额', field: 'beginBalance', width: 150, align: 'right' as const },
  { id: 'col-4', label: '本期借方', field: 'debit', width: 150, align: 'right' as const },
  { id: 'col-5', label: '本期贷方', field: 'credit', width: 150, align: 'right' as const },
  { id: 'col-6', label: '期末余额', field: 'endBalance', width: 150, align: 'right' as const }
]

const mockRows = [
  {
    accountCode: '1001',
    accountName: '库存现金',
    beginBalance: 50000,
    debit: 10000,
    credit: 5000,
    endBalance: 55000
  },
  {
    accountCode: '1002',
    accountName: '银行存款',
    beginBalance: 2000000,
    debit: 500000,
    credit: 300000,
    endBalance: 2200000
  }
]

const mockTableConfig: ReportPageConfig = {
  title: '科目余额表',
  showFilterPanel: true,
  showActionBar: true,
  reportType: 'table',
  filterFields: mockFilterFields,
  columns: mockColumns,
  rows: mockRows,
  showPrint: true,
  showExportExcel: true,
  showExportPdf: true
}

const baseProps = {
  viewId: 'view-report-001',
  pageType: 'P10' as PageType,
  config: mockTableConfig as ReportPageConfig & Record<string, unknown>,
  permissions: ['report:view', 'report:export']
}

function createWrapper(overrides: Record<string, unknown> = {}): VueWrapper<typeof PageP10Report> {
  return mount(PageP10Report, {
    props: { ...baseProps, ...overrides }
  })
}

describe('PageP10Report component', () => {
  describe('rendering', () => {
    it('renders the report container', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p10-report').exists()).toBe(true)
    })

    it('renders page title when provided', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-title').exists()).toBe(true)
      expect(wrapper.find('.page-title').text()).toBe('科目余额表')
    })

    it('does not render page title when not provided', () => {
      const wrapper = createWrapper({ config: { reportType: 'table' } })
      expect(wrapper.find('.page-title').exists()).toBe(false)
    })

    it('renders filter panel by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.query-area').exists()).toBe(true)
    })

    it('hides filter panel when showFilterPanel is false', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, showFilterPanel: false }
      })
      expect(wrapper.find('.query-area').exists()).toBe(false)
    })

    it('renders action area by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.action-area').exists()).toBe(true)
    })

    it('hides action area when showActionBar is false', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, showActionBar: false }
      })
      expect(wrapper.find('.action-area').exists()).toBe(false)
    })

    it('renders main content area', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
    })

    it('renders filter form fields from config', () => {
      const wrapper = createWrapper()
      const formItems = wrapper.findAll('.el-form-item')
      expect(formItems).toHaveLength(4)
    })

    it('renders query and reset buttons when filter fields present', () => {
      const wrapper = createWrapper()
      const buttons = wrapper.findAll('.filter-form .el-button')
      expect(buttons).toHaveLength(2)
    })

    it('renders placeholder when no filter fields configured', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, filterFields: [] }
      })
      expect(wrapper.find('.query-area .area-placeholder').exists()).toBe(true)
    })
  })

  describe('report type: table', () => {
    it('renders table layout by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.report-table-layout').exists()).toBe(true)
    })

    it('renders el-table with columns from config', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.el-table').exists()).toBe(true)
    })

    it('renders row count label', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.report-count').exists()).toBe(true)
      expect(wrapper.find('.report-count').text()).toContain('2')
    })

    it('shows empty state when no rows', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, rows: [] }
      })
      expect(wrapper.find('.table-empty').exists()).toBe(true)
    })

    it('shows placeholder when no columns configured', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, columns: [], rows: [] }
      })
      expect(wrapper.find('.main-content-area .area-placeholder').exists()).toBe(true)
    })
  })

  describe('report type: tree', () => {
    it('renders tree layout when reportType is tree', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, reportType: 'tree', columns: undefined, rows: undefined }
      })
      expect(wrapper.find('.report-tree-layout').exists()).toBe(true)
    })

    it('renders tree panel and data panel', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, reportType: 'tree', columns: undefined, rows: undefined }
      })
      expect(wrapper.find('.report-tree-panel').exists()).toBe(true)
      expect(wrapper.find('.report-data-panel').exists()).toBe(true)
    })

    it('renders placeholder in tree body', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, reportType: 'tree', columns: undefined, rows: undefined }
      })
      expect(wrapper.find('.report-tree-body .area-placeholder').exists()).toBe(true)
    })
  })

  describe('report type: ledger', () => {
    it('renders ledger layout when reportType is ledger', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, reportType: 'ledger', columns: undefined, rows: undefined }
      })
      expect(wrapper.find('.report-ledger-layout').exists()).toBe(true)
    })

    it('renders ledger header with six columns', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, reportType: 'ledger', columns: undefined, rows: undefined }
      })
      expect(wrapper.find('.report-ledger-header').exists()).toBe(true)
      expect(wrapper.find('.ledger-col--date').exists()).toBe(true)
      expect(wrapper.find('.ledger-col--voucher').exists()).toBe(true)
      expect(wrapper.find('.ledger-col--summary').exists()).toBe(true)
      // debit + credit + balance = 3 amount columns + date + voucher + summary = 6 ledger cols
      expect(wrapper.findAll('.ledger-col')).toHaveLength(6)
    })

    it('renders placeholder in ledger body', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, reportType: 'ledger', columns: undefined, rows: undefined }
      })
      expect(wrapper.find('.report-ledger-body .area-placeholder').exists()).toBe(true)
    })
  })

  describe('action bar buttons', () => {
    it('shows print button by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).toContain('打印')
    })

    it('hides print button when showPrint is false', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, showPrint: false }
      })
      expect(wrapper.text()).not.toContain('打印')
    })

    it('shows export Excel button by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).toContain('导出Excel')
    })

    it('hides export Excel button when showExportExcel is false', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, showExportExcel: false }
      })
      expect(wrapper.text()).not.toContain('导出Excel')
    })

    it('shows export PDF button by default', () => {
      const wrapper = createWrapper()
      expect(wrapper.text()).toContain('导出PDF')
    })

    it('hides export PDF button when showExportPdf is false', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, showExportPdf: false }
      })
      expect(wrapper.text()).not.toContain('导出PDF')
    })
  })

  describe('props', () => {
    it('accepts viewId prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('viewId')).toBe('view-report-001')
    })

    it('accepts pageType prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('pageType')).toBe('P10')
    })

    it('accepts config prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('config')).toEqual(mockTableConfig)
    })

    it('accepts permissions prop', () => {
      const wrapper = createWrapper()
      expect(wrapper.props('permissions')).toEqual(['report:view', 'report:export'])
    })

    it('handles empty config gracefully', () => {
      const wrapper = createWrapper({ config: {} })
      expect(wrapper.find('.page-p10-report').exists()).toBe(true)
      expect(wrapper.find('.main-content-area').exists()).toBe(true)
    })

    it('handles filterFields with no items gracefully', () => {
      const wrapper = createWrapper({
        config: { ...mockTableConfig, filterFields: [] }
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
        viewId: 'view-report-001',
        pageType: 'P10'
      })
    })

    it('emits data-change with report-query source when query button clicked', async () => {
      const wrapper = createWrapper()
      const queryBtn = wrapper.findAll('.filter-form .el-button')[0]
      await queryBtn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      expect(emitted).toBeTruthy()
      expect(emitted[0][0].source).toBe('report-query')
    })

    it('emits data-change with report-reset source when reset button clicked', async () => {
      const wrapper = createWrapper()
      const resetBtn = wrapper.findAll('.filter-form .el-button')[1]
      await resetBtn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      expect(emitted).toBeTruthy()
      expect(emitted[0][0].source).toBe('report-reset')
    })

    it('emits data-change with report-print source when print button clicked', async () => {
      const wrapper = createWrapper()
      const actionBtns = wrapper.findAll('.action-bar-right .el-button')
      const printBtn = actionBtns[0]
      await printBtn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      const printEvents = emitted.filter((e) => e[0].source === 'report-print')
      expect(printEvents).toHaveLength(1)
    })

    it('emits data-change with report-export-excel source', async () => {
      const wrapper = createWrapper()
      const actionBtns = wrapper.findAll('.action-bar-right .el-button')
      const excelBtn = actionBtns[1]
      await excelBtn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      const excelEvents = emitted.filter((e) => e[0].source === 'report-export-excel')
      expect(excelEvents).toHaveLength(1)
    })

    it('emits data-change with report-export-pdf source', async () => {
      const wrapper = createWrapper()
      const actionBtns = wrapper.findAll('.action-bar-right .el-button')
      const pdfBtn = actionBtns[2]
      await pdfBtn.trigger('click')
      const emitted = wrapper.emitted('data-change') as Array<
        Array<{ source: string; data: unknown }>
      >
      const pdfEvents = emitted.filter((e) => e[0].source === 'report-export-pdf')
      expect(pdfEvents).toHaveLength(1)
    })
  })

  describe('slots', () => {
    it('renders query-panel slot content', () => {
      const wrapper = mount(PageP10Report, {
        props: baseProps,
        slots: {
          'query-panel': '<div class="custom-query">自定义查询区</div>'
        }
      })
      expect(wrapper.find('.custom-query').exists()).toBe(true)
      expect(wrapper.find('.custom-query').text()).toBe('自定义查询区')
    })

    it('renders action-bar slot content', () => {
      const wrapper = mount(PageP10Report, {
        props: baseProps,
        slots: {
          'action-bar': '<div class="custom-action">自定义操作栏</div>'
        }
      })
      expect(wrapper.find('.custom-action').exists()).toBe(true)
    })

    it('renders main-content slot content', () => {
      const wrapper = mount(PageP10Report, {
        props: baseProps,
        slots: {
          'main-content': '<div class="custom-main">自定义主内容区</div>'
        }
      })
      expect(wrapper.find('.custom-main').exists()).toBe(true)
      expect(wrapper.find('.custom-main').text()).toBe('自定义主内容区')
    })

    it('renders extra-area slot content', () => {
      const wrapper = mount(PageP10Report, {
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

  describe('filter form initialization', () => {
    it('initializes filter form from config', () => {
      const wrapper = createWrapper()
      expect(wrapper.find('.page-p10-report').exists()).toBe(true)
    })

    it('handles config without filterFields gracefully', () => {
      const wrapper = createWrapper({ config: { showFilterPanel: true, reportType: 'table' } })
      expect(wrapper.find('.query-area .area-placeholder').exists()).toBe(true)
    })
  })
})
