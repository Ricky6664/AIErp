import { ref, reactive, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ListTableColumn, SortEventParams, RowSize, SortConfig } from '@/types/list-table'
import type { DemoOrderItem, DemoOrderQuery } from '@/api/modules/demo'
import {
  getDemoOrderPage,
  saveDemoOrder,
  deleteDemoOrder,
  getDemoOrderDetail
} from '@/api/modules/demo'

export function useDemoListTable() {
  const loading = ref(false)
  const error = ref<string | null>(null)
  const data = ref<DemoOrderItem[]>([])
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(20)
  const currentRow = ref<Record<string, unknown> | null>(null)
  const tableSize = ref<RowSize>('medium')

  const searchKeyword = ref('')
  const searchStatus = ref('')

  const router = useRouter()
  const route = useRoute()

  const saving = ref(false)
  const deleting = ref(false)

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
    error.value = null
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
    } catch (err) {
      const message = err instanceof Error ? err.message : '数据加载失败'
      error.value = message
      ElMessage.error(message)
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
    const id = row.id as number
    if (id) {
      router.push({ path: `/demo/list-table`, query: { ...route.query, detailId: String(id) } })
    }
  }

  function handleSizeSelect(size: RowSize): void {
    tableSize.value = size
  }

  async function handleSave(formData: Partial<DemoOrderItem> & { id?: number }): Promise<boolean> {
    saving.value = true
    error.value = null
    try {
      await saveDemoOrder(formData)
      ElMessage.success(formData.id ? '订单修改成功' : '订单新增成功')
      await fetchData()
      return true
    } catch (err) {
      const message = err instanceof Error ? err.message : '保存失败'
      error.value = message
      ElMessage.error(message)
      return false
    } finally {
      saving.value = false
    }
  }

  async function handleDelete(id: number): Promise<boolean> {
    try {
      await ElMessageBox.confirm(`确认删除订单 #${id}？`, '删除确认', {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch {
      return false
    }
    deleting.value = true
    error.value = null
    try {
      await deleteDemoOrder(id)
      ElMessage.success('订单已删除')
      if (currentRow.value && (currentRow.value.id as number) === id) {
        currentRow.value = null
      }
      await fetchData()
      return true
    } catch (err) {
      const message = err instanceof Error ? err.message : '删除失败'
      error.value = message
      ElMessage.error(message)
      return false
    } finally {
      deleting.value = false
    }
  }

  async function handleViewDetail(id: number): Promise<void> {
    try {
      const detail = await getDemoOrderDetail(id)
      router.push({
        path: `/demo/list-table`,
        query: { ...route.query, detailId: String(id) }
      })
      currentRow.value = detail as unknown as Record<string, unknown>
    } catch (err) {
      ElMessage.error('获取订单详情失败')
    }
  }

  function syncQueryToState(): void {
    const q = route.query
    if (q.keyword && typeof q.keyword === 'string') searchKeyword.value = q.keyword
    if (q.status && typeof q.status === 'string') searchStatus.value = q.status
    if (q.page && typeof q.page === 'string') currentPage.value = Number(q.page) || 1
    if (q.size && typeof q.size === 'string') pageSize.value = Number(q.size) || 20
  }

  let debounceTimer: ReturnType<typeof setTimeout> | null = null
  watch([searchKeyword, searchStatus], () => {
    if (debounceTimer) clearTimeout(debounceTimer)
    debounceTimer = setTimeout(() => {
      handleSearch()
    }, 300)
  })

  watch([() => currentPage.value, () => pageSize.value], ([page, size]) => {
    router.replace({
      path: route.path,
      query: {
        ...route.query,
        keyword: searchKeyword.value || undefined,
        status: searchStatus.value || undefined,
        page: page > 1 ? String(page) : undefined,
        size: size !== 20 ? String(size) : undefined
      }
    })
  })

  return {
    loading,
    error,
    data,
    total,
    currentPage,
    pageSize,
    currentRow,
    tableSize,
    searchKeyword,
    searchStatus,
    saving,
    deleting,
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
    handleSizeSelect,
    handleSave,
    handleDelete,
    handleViewDetail,
    syncQueryToState
  }
}
