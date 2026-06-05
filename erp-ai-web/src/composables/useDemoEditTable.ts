import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { EditTableColumn, EditChangeParams, DragSortEventParams } from '@/types/edit-table'
import type { SummaryConfig } from '@/types/list-table'

interface EditTableRow {
  id: number
  lineNo: number
  productName: string
  spec: string
  quantity: number
  unitPrice: number
  totalAmount: number
  status: string
}

function generateMockRows(): EditTableRow[] {
  const products = [
    'ERP管理系统V3',
    'CRM客户管理软件',
    'WMS仓储管理平台',
    'MES生产执行系统',
    'QMS质量管理系统'
  ]
  const statuses = ['draft', 'confirmed', 'cancelled']
  return Array.from({ length: 8 }, (_, i) => {
    const quantity = Math.floor(Math.random() * 50) + 1
    const unitPrice = Math.round((Math.random() * 2000 + 100) * 100) / 100
    return {
      id: i + 1,
      lineNo: i + 1,
      productName: products[i % products.length],
      spec: `规格-${String.fromCharCode(65 + i)}`,
      quantity,
      unitPrice,
      totalAmount: Math.round(quantity * unitPrice * 100) / 100,
      status: statuses[i % statuses.length]
    }
  })
}

export function useDemoEditTable() {
  const loading = ref(false)
  const error = ref<string | null>(null)
  const tableData = ref<EditTableRow[]>([])
  const summaryEnabled = ref(true)
  const tableSize = ref<'mini' | 'small' | 'medium' | 'large'>('medium')
  const selectedMethod = ref<string>('sum')

  const columns: EditTableColumn[] = [
    { field: 'lineNo', title: '行号', width: 70, align: 'center', editable: false },
    {
      field: 'productName',
      title: '商品名称',
      minWidth: 160,
      editable: true,
      editRender: { name: 'input', props: { placeholder: '请输入商品名称' } }
    },
    {
      field: 'spec',
      title: '规格型号',
      width: 120,
      align: 'center',
      editable: true,
      editRender: { name: 'input', props: { placeholder: '请输入规格' } }
    },
    {
      field: 'quantity',
      title: '数量',
      width: 110,
      align: 'right',
      editable: true,
      editRender: { name: 'input', props: { type: 'number', min: 1, max: 9999 } },
      editRules: [{ required: true, message: '数量必填' }],
      summary: { field: 'quantity', method: 'sum', suffix: ' 件' }
    },
    {
      field: 'unitPrice',
      title: '单价（元）',
      width: 130,
      align: 'right',
      editable: true,
      editRender: { name: 'input', props: { type: 'number', min: 0, step: 0.01 } },
      formatter: (_v, row) => `¥${(row.unitPrice as number).toFixed(2)}`,
      summary: {
        field: 'unitPrice',
        method: 'avg',
        prefix: '均价¥',
        formatter: (v) => Number(v).toFixed(2)
      }
    },
    {
      field: 'totalAmount',
      title: '总金额（元）',
      width: 150,
      align: 'right',
      editable: false,
      formatter: (_v, row) => `¥${(row.totalAmount as number).toFixed(2)}`,
      summary: {
        field: 'totalAmount',
        method: 'sum',
        prefix: '¥',
        formatter: (v) => Number(v).toFixed(2)
      }
    },
    {
      field: 'status',
      title: '状态',
      width: 110,
      align: 'center',
      editable: true,
      editRender: {
        name: 'select',
        props: {
          options: [
            { label: '草稿', value: 'draft' },
            { label: '已确认', value: 'confirmed' },
            { label: '已取消', value: 'cancelled' }
          ]
        }
      }
    }
  ]

  const summaryConfig = computed<SummaryConfig>(() => ({
    enabled: summaryEnabled.value
  }))

  const statusLabelMap: Record<string, string> = {
    draft: '草稿',
    confirmed: '已确认',
    cancelled: '已取消'
  }

  const statusTagTypeMap: Record<string, 'info' | 'success' | 'warning'> = {
    draft: 'info',
    confirmed: 'success',
    cancelled: 'warning'
  }

  function fetchData(): void {
    loading.value = true
    error.value = null
    try {
      tableData.value = generateMockRows()
    } catch (err) {
      error.value = err instanceof Error ? err.message : '数据加载失败'
      ElMessage.error(error.value!)
    } finally {
      loading.value = false
    }
  }

  function handleCellChange(params: EditChangeParams): void {
    const { row, field, rowIndex } = params
    const newData = [...tableData.value]
    newData[rowIndex] = { ...newData[rowIndex], [field]: row[field] }

    if (field === 'quantity' || field === 'unitPrice') {
      const qty = Number(newData[rowIndex].quantity) || 0
      const price = Number(newData[rowIndex].unitPrice) || 0
      newData[rowIndex].totalAmount = Math.round(qty * price * 100) / 100
    }

    tableData.value = newData
  }

  function handleDragSort(params: DragSortEventParams): void {
    tableData.value = params.newData.map((row, idx) => ({
      ...(row as unknown as EditTableRow),
      lineNo: idx + 1
    }))
    ElMessage.success('行排序已更新')
  }

  function addRow(): void {
    const maxId = tableData.value.reduce((max, r) => Math.max(max, r.id), 0)
    const newRow: EditTableRow = {
      id: maxId + 1,
      lineNo: tableData.value.length + 1,
      productName: '',
      spec: '',
      quantity: 1,
      unitPrice: 0,
      totalAmount: 0,
      status: 'draft'
    }
    tableData.value = [...tableData.value, newRow]
  }

  function deleteRow(rowIndex: number): void {
    const newData = [...tableData.value]
    newData.splice(rowIndex, 1)
    newData.forEach((row, idx) => {
      row.lineNo = idx + 1
    })
    tableData.value = newData
    ElMessage.success('行已删除')
  }

  function handleSave(): void {
    ElMessage.success('数据保存成功（演示）')
  }

  function handleReset(): void {
    fetchData()
    ElMessage.info('数据已重置')
  }

  function toggleSummary(): void {
    summaryEnabled.value = !summaryEnabled.value
    ElMessage.info(summaryEnabled.value ? '合计行已启用' : '合计行已禁用')
  }

  const methodOptions = [
    { label: '求和 (sum)', value: 'sum' },
    { label: '平均值 (avg)', value: 'avg' },
    { label: '计数 (count)', value: 'count' },
    { label: '最小值 (min)', value: 'min' },
    { label: '最大值 (max)', value: 'max' }
  ]

  const rowSizeOptions: { key: string; label: string }[] = [
    { key: 'mini', label: '迷你' },
    { key: 'small', label: '小' },
    { key: 'medium', label: '中' },
    { key: 'large', label: '大' }
  ]

  return {
    loading,
    error,
    tableData,
    summaryEnabled,
    tableSize,
    selectedMethod,
    columns,
    summaryConfig,
    statusLabelMap,
    statusTagTypeMap,
    methodOptions,
    rowSizeOptions,
    fetchData,
    handleCellChange,
    handleDragSort,
    addRow,
    deleteRow,
    handleSave,
    handleReset,
    toggleSummary
  }
}
