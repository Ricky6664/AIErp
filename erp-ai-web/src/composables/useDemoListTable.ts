import { ref, reactive, computed } from 'vue'
import type { ListTableColumn, SortEventParams, RowSize, SortConfig } from '@/types/list-table'
import type { DemoOrderItem, DemoOrderQuery } from '@/api/modules/demo'
import { getDemoOrderPage } from '@/api/modules/demo'

export function useDemoListTable() {
  const loading = ref(false)
  const data = ref<DemoOrderItem[]>([])
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(20)
  const currentRow = ref<Record<string, unknown> | null>(null)
  const tableSize = ref<RowSize>('medium')

  const searchKeyword = ref('')
  const searchStatus = ref('')

  const sortParams = reactive<{ field: string; order: 'asc' | 'desc' | null }>({
    field: '',
    order: null
  })

  const statusLabelMap: Record<string, string> = {
    pending: '待处理',
    processing: '处理中',
    completed: '已完成',
    cancelled: '已取消'
  }

  const statusTagTypeMap: Record<string, 'warning' | 'primary' | 'success' | 'info'> = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    cancelled: 'info'
  }

  const columns: ListTableColumn[] = [
    { field: 'id', title: 'ID', width: 80, align: 'center', sortable: true },
    { field: 'orderNo', title: '订单编号', width: 160, sortable: true },
    { field: 'customerName', title: '客户名称', minWidth: 140, sortable: true },
    { field: 'productName', title: '商品名称', minWidth: 160, sortable: true },
    {
      field: 'quantity',
      title: '数量',
      width: 100,
      align: 'right',
      sortable: true,
      summary: { field: 'quantity', method: 'sum' }
    },
    {
      field: 'unitPrice',
      title: '单价',
      width: 120,
      align: 'right',
      sortable: true,
      formatter: (_v, row) => `¥${(row.unitPrice as number).toFixed(2)}`
    },
    {
      field: 'totalAmount',
      title: '总金额',
      width: 140,
      align: 'right',
      sortable: true,
      formatter: (_v, row) => `¥${(row.totalAmount as number).toFixed(2)}`,
      summary: { field: 'totalAmount', method: 'sum', formatter: (v) => `¥${Number(v).toFixed(2)}` }
    },
    {
      field: 'status',
      title: '状态',
      width: 110,
      align: 'center',
      slot: 'status',
      filters: [
        {
          type: 'select',
          options: [
            { label: '待处理', value: 'pending' },
            { label: '处理中', value: 'processing' },
            { label: '已完成', value: 'completed' },
            { label: '已取消', value: 'cancelled' }
          ]
        }
      ]
    },
    {
      field: 'orderDate',
      title: '下单日期',
      width: 130,
      align: 'center',
      sortable: true,
      type: 'date'
    },
    {
      field: 'deliveryDate',
      title: '交付日期',
      width: 130,
      align: 'center',
      sortable: true,
      type: 'date'
    }
  ]

  const sortConfig = computed<SortConfig>(() => ({
    remote: true,
    multiple: false,
    trigger: 'cell',
    showIcon: true
  }))

  const totalAmountSummary = computed(() => {
    if (data.value.length === 0) return undefined
    const sum = data.value.reduce((acc, item) => acc + item.totalAmount, 0)
    return {
      totalAmount: Math.round(sum * 100) / 100,
      quantity: data.value.reduce((acc, item) => acc + item.quantity, 0)
    }
  })

  async function fetchData(): Promise<void> {
    loading.value = true
    try {
      const params: DemoOrderQuery = {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
      if (searchKeyword.value) params.keyword = searchKeyword.value
      if (searchStatus.value) params.status = searchStatus.value
      if (sortParams.field && sortParams.order) {
        params.sortField = sortParams.field
        params.sortOrder = sortParams.order
      }
      const result = await getDemoOrderPage(params)
      data.value = result.records
      total.value = result.total
    } finally {
      loading.value = false
    }
  }

  function handleSearch(): void {
    currentPage.value = 1
    fetchData()
  }

  function handleReset(): void {
    searchKeyword.value = ''
    searchStatus.value = ''
    sortParams.field = ''
    sortParams.order = null
    currentPage.value = 1
    fetchData()
  }

  function handlePageChange(page: number): void {
    currentPage.value = page
    fetchData()
  }

  function handleSizeChange(size: number): void {
    pageSize.value = size
    currentPage.value = 1
    fetchData()
  }

  function handleSortChange(params: SortEventParams): void {
    sortParams.field = params.field
    sortParams.order = params.order
    currentPage.value = 1
    fetchData()
  }

  function handleCurrentChange(row: Record<string, unknown> | null): void {
    currentRow.value = row
  }

  function handleRowClick(row: Record<string, unknown>): void {
    currentRow.value = row
  }

  function handleRowDblclick(row: Record<string, unknown>): void {
    console.log('双击行:', row)
  }

  function handleSizeSelect(size: RowSize): void {
    tableSize.value = size
  }

  return {
    loading,
    data,
    total,
    currentPage,
    pageSize,
    currentRow,
    tableSize,
    searchKeyword,
    searchStatus,
    columns,
    sortConfig,
    totalAmountSummary,
    statusLabelMap,
    statusTagTypeMap,
    fetchData,
    handleSearch,
    handleReset,
    handlePageChange,
    handleSizeChange,
    handleSortChange,
    handleCurrentChange,
    handleRowClick,
    handleRowDblclick,
    handleSizeSelect
  }
}
