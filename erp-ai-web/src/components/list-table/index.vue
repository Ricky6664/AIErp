<template>
  <div class="list-table">
    <!-- 头部工具栏区域 -->
    <div v-if="$slots.toolbar || showToolbar" class="list-table__toolbar">
      <slot name="toolbar" :grid-ref="gridRef" />
    </div>

    <vxe-grid
      ref="gridRef"
      v-bind="gridOptions"
      :data="props.data"
      :loading="props.loading"
      :columns="displayColumns"
      :pager-config="pagerConfig"
      :sort-config="sortConfigValue"
      :row-config="rowConfigValue"
      :column-config="columnConfigValue"
      :size="gridSize"
      :stripe="props.stripe"
      :border="props.border"
      :height="props.height"
      :max-height="props.maxHeight"
      :show-header="props.showHeader"
      :empty-text="props.emptyText || '暂无数据'"
      :virtual-scroll="{ enabled: props.virtualScroll !== false }"
      @sort-change="handleSortChange"
      @filter-change="handleFilterChange"
      @current-change="handleCurrentChange"
      @cell-click="handleCellClick"
      @row-click="handleRowClick"
      @row-dblclick="handleRowDblclick"
      @page-change="handlePageChange"
      @column-resize="handleColumnResize"
    >
      <!-- 自定义列插槽 -->
      <template v-for="col in slottedColumns" :key="col.field" #[col.slot]="{ row }">
        <slot :name="col.slot" :row="row" :column="col" />
      </template>
    </vxe-grid>

    <!-- 底部插槽 -->
    <div v-if="$slots.footer" class="list-table__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { VxeGridInstance, VxeGridProps } from 'vxe-table'
import type {
  ListTableColumn,
  ListTableProps,
  SortConfig,
  SortEventParams,
  FilterEventParams,
  ColumnPersistData
} from '@/types/list-table'

const props = withDefaults(
  defineProps<{
    columns: ListTableColumn[]
    data: unknown[]
    loading?: boolean
    total?: number
    currentPage?: number
    pageSize?: number
    pageMode?: 'client' | 'server'
    rowConfig?: ListTableProps['rowConfig']
    height?: number | string
    maxHeight?: number | string
    size?: 'mini' | 'small' | 'medium' | 'large' | 'loose' | 'x-large' | 'xx-large'
    stripe?: boolean
    border?: 'inner' | 'full' | 'none'
    showHeader?: boolean
    summaryData?: Record<string, unknown>
    emptyText?: string
    viewCode?: string
    virtualScroll?: boolean
    currentRow?: Record<string, unknown> | null
    sortConfig?: SortConfig | null
    showToolbar?: boolean
  }>(),
  {
    loading: false,
    total: 0,
    currentPage: 1,
    pageSize: 20,
    pageMode: 'server',
    size: 'medium',
    stripe: true,
    border: 'inner',
    showHeader: true,
    virtualScroll: true,
    showToolbar: false,
    currentRow: null,
    sortConfig: null,
    rowConfig: undefined,
    height: undefined,
    maxHeight: undefined,
    summaryData: undefined,
    emptyText: '',
    viewCode: ''
  }
)

const emit = defineEmits<{
  'update:currentPage': [page: number]
  'update:pageSize': [size: number]
  'sort-change': [params: SortEventParams]
  'filter-change': [params: FilterEventParams]
  'current-change': [row: Record<string, unknown> | null]
  'cell-click': [row: Record<string, unknown>, column: ListTableColumn]
  'row-click': [row: Record<string, unknown>]
  'row-dblclick': [row: Record<string, unknown>]
}>()

const gridRef = ref<VxeGridInstance>()

// 列持久化存储key
const storageKey = computed(() => (props.viewCode ? `list-table-columns-${props.viewCode}` : ''))

// 从localStorage加载列配置
function loadColumnPersist(): ColumnPersistData[] | null {
  if (!storageKey.value) return null
  try {
    const raw = localStorage.getItem(storageKey.value)
    if (raw) {
      const parsed = JSON.parse(raw) as ColumnPersistData[]
      if (Array.isArray(parsed) && parsed.length > 0) {
        return parsed
      }
    }
  } catch {
    // localStorage数据损坏，忽略
  }
  return null
}

// 保存列配置到localStorage
function saveColumnPersist(data: ColumnPersistData[]): void {
  if (!storageKey.value) return
  try {
    localStorage.setItem(storageKey.value, JSON.stringify(data))
  } catch {
    // localStorage写入失败，静默忽略
  }
}

// 已持久化的列配置
const persistedColumns = ref<ColumnPersistData[] | null>(loadColumnPersist())

// 带插槽的列
const slottedColumns = computed(() => props.columns.filter((col) => col.slot))

// 应用列持久化配置后的显示列
const displayColumns = computed(() => {
  const persist = persistedColumns.value
  if (!persist || persist.length === 0) {
    return props.columns.map((col, idx) => ({
      ...col,
      visible: col.visible !== false,
      _order: idx
    }))
  }

  const persistMap = new Map<string, ColumnPersistData>(persist.map((p) => [p.field, p]))

  const ordered = props.columns
    .map((col, idx) => {
      const p = persistMap.get(col.field)
      return {
        ...col,
        visible: p ? p.visible : col.visible !== false,
        width: p?.width ?? col.width,
        fixed: p?.fixed ?? col.fixed,
        _order: p?.order ?? idx
      }
    })
    .sort((a, b) => (a._order as number) - (b._order as number))

  return ordered
})

// 列配置值
const columnConfigValue = computed<VxeGridProps['columnConfig']>(() => ({
  resizable: true,
  isHover: true
}))

// 排序配置值
const sortConfigValue = computed<VxeGridProps['sortConfig']>(() => {
  const defaultConfig: VxeGridProps['sortConfig'] = {
    trigger: 'cell',
    defaultSort: undefined,
    multiple: false
  }

  if (props.sortConfig) {
    defaultConfig.defaultSort = {
      field: props.sortConfig.field,
      order: props.sortConfig.order
    }
  }

  return defaultConfig
})

// 行配置值
const rowConfigValue = computed<VxeGridProps['rowConfig']>(() => ({
  isCurrent: props.rowConfig?.isCurrent ?? true,
  isHover: props.rowConfig?.isHover ?? true,
  keyField: props.rowConfig?.keyField ?? 'id',
  currentRow: props.currentRow ?? undefined
}))

// 分页配置
const pagerConfig = computed<VxeGridProps['pagerConfig']>(() => ({
  enabled: true,
  mode: 'pager',
  pageSize: props.pageSize,
  currentPage: props.currentPage,
  total: props.total,
  layouts: ['Total', 'PrevPage', 'Number', 'NextPage', 'Sizes', 'FullJump', 'PageCount'],
  pageSizes: [10, 20, 50, 100, 200]
}))

// vxe-table 尺寸映射
const sizeMap: Record<string, 'mini' | 'small' | 'medium' | 'large'> = {
  mini: 'mini',
  small: 'small',
  medium: 'medium',
  large: 'large',
  loose: 'medium',
  'x-large': 'large',
  'xx-large': 'large'
}

const gridSize = computed(() => sizeMap[props.size] || 'medium')

// 暴露grid实例
const gridOptions = computed(() => ({
  // 静态配置
}))

// 排序变更处理
function handleSortChange({ field, order }: { field: string; order: 'asc' | 'desc' | null }): void {
  emit('sort-change', { field, order })
}

// 筛选变更处理
function handleFilterChange({ field, values }: { field: string; values: unknown[] }): void {
  emit('filter-change', { field, values })
}

// 当前行变更处理
function handleCurrentChange({ row }: { row: Record<string, unknown> | null }): void {
  emit('current-change', row)
}

// 单元格点击处理
function handleCellClick({
  row,
  column
}: {
  row: Record<string, unknown>
  column: ListTableColumn
}): void {
  emit('cell-click', row, column)
}

// 行点击处理
function handleRowClick({ row }: { row: Record<string, unknown> }): void {
  emit('row-click', row)
}

// 行双击处理
function handleRowDblclick({ row }: { row: Record<string, unknown> }): void {
  emit('row-dblclick', row)
}

// 分页变更处理
function handlePageChange({
  currentPage,
  pageSize
}: {
  currentPage: number
  pageSize: number
}): void {
  if (pageSize !== props.pageSize) {
    emit('update:pageSize', pageSize)
  }
  if (currentPage !== props.currentPage) {
    emit('update:currentPage', currentPage)
  }
}

// 列宽调整处理
function handleColumnResize({
  column,
  resizeWidth
}: {
  column: ListTableColumn
  resizeWidth: number
}): void {
  if (!storageKey.value) return
  try {
    const allCols = displayColumns.value.map((col, idx) => ({
      field: col.field,
      visible: col.visible !== false,
      width: col.field === column.field ? resizeWidth : (col.width as number | undefined),
      fixed: col.fixed,
      order: ((col as Record<string, unknown>)._order as number) ?? idx
    }))
    saveColumnPersist(allCols)
  } catch {
    // 静默忽略
  }
}

// 重置列配置（一键初始化/清空搜索排序）
function resetColumns(): void {
  persistedColumns.value = null
  if (storageKey.value) {
    try {
      localStorage.removeItem(storageKey.value)
    } catch {
      // 静默忽略
    }
  }
}

// 刷新表格
function refresh(): void {
  gridRef.value?.reloadData(props.data)
}

// 清除排序
function clearSort(): void {
  gridRef.value?.clearSort()
}

// 清除筛选
function clearFilter(): void {
  gridRef.value?.clearFilter()
}

// 清除选中
function clearCurrent(): void {
  gridRef.value?.clearCurrentRow()
}

// 获取选中行
function getCurrentRow(): Record<string, unknown> | null {
  return (gridRef.value?.getCurrentRecord() as Record<string, unknown>) ?? null
}

// 设置当前行
function setCurrentRow(row: Record<string, unknown>): void {
  gridRef.value?.setCurrentRow(row)
}

defineExpose({
  gridRef,
  resetColumns,
  refresh,
  clearSort,
  clearFilter,
  clearCurrent,
  getCurrentRow,
  setCurrentRow
})
</script>

<style scoped lang="scss">
.list-table {
  display: flex;
  flex-direction: column;
  height: 100%;

  &__toolbar {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 0;
    flex-shrink: 0;
  }

  &__footer {
    padding: 8px 0;
    flex-shrink: 0;
  }
}
</style>
