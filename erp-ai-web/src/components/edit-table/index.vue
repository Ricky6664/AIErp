<template>
  <div class="edit-table">
    <!-- 前缀插槽 -->
    <div v-if="$slots.prefix" class="edit-table__prefix">
      <slot name="prefix" />
    </div>

    <vxe-grid
      ref="gridRef"
      v-bind="gridOptions"
      :data="innerData"
      :columns="displayColumns"
      :edit-config="editConfigValue"
      :row-config="rowConfigValue"
      :column-config="columnConfigValue"
      :drag-config="dragConfigValue"
      :size="gridSize"
      :stripe="props.stripe"
      :border="props.border"
      :height="props.height"
      :max-height="props.maxHeight"
      :show-header="props.showHeader"
      :empty-text="props.emptyText || props.placeholder || '暂无数据'"
      @edit-closed="handleEditClosed"
      @column-resize="handleColumnResize"
      @drag-sort="handleDragSort"
      @focus="handleFocus"
      @blur="handleBlur"
    >
      <!-- 自定义列插槽 -->
      <template v-for="col in slottedColumns" :key="col.field" #[col.slot]="{ row }">
        <slot :name="col.slot" :row="row" :column="col" />
      </template>
    </vxe-grid>

    <!-- 汇总行 -->
    <div v-if="summaryConfigValue.enabled && summaryData.length > 0" class="edit-table__summary">
      <div
        v-for="col in summaryColumns"
        :key="col.field"
        class="edit-table__summary-cell"
        :style="{ width: col.width + 'px', textAlign: col.align || 'left' }"
      >
        {{ col.displayValue }}
      </div>
    </div>

    <!-- 后缀插槽 -->
    <div v-if="$slots.suffix" class="edit-table__suffix">
      <slot name="suffix" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { VxeGridInstance, VxeGridProps } from 'vxe-table'
import type {
  EditTableColumn,
  EditChangeParams,
  ColumnPersistData,
  SummaryColumnConfig,
  FieldConfig,
  DragConfig,
  DragSortEventParams
} from '@/types/edit-table'
import type { SummaryConfig, RowConfig } from '@/types/list-table'

const props = withDefaults(
  defineProps<{
    columns: EditTableColumn[]
    modelValue?: Record<string, unknown>[]
    fieldConfig?: Record<string, FieldConfig>
    disabled?: boolean
    placeholder?: string
    height?: number | string
    maxHeight?: number | string
    size?: 'mini' | 'small' | 'medium' | 'large'
    stripe?: boolean
    border?: 'inner' | 'full' | 'none'
    showHeader?: boolean
    emptyText?: string
    viewCode?: string
    editTrigger?: 'click' | 'dblclick' | 'manual'
    dragConfig?: DragConfig
    summaryConfig?: SummaryConfig
    rowConfig?: RowConfig
  }>(),
  {
    modelValue: () => [],
    disabled: false,
    placeholder: '',
    size: 'medium',
    stripe: true,
    border: 'inner',
    showHeader: true,
    emptyText: '',
    viewCode: '',
    editTrigger: 'click',
    dragConfig: undefined,
    rowConfig: undefined,
    height: undefined,
    maxHeight: undefined,
    summaryConfig: undefined,
    fieldConfig: undefined
  }
)

const emit = defineEmits<{
  'update:modelValue': [rows: Record<string, unknown>[]]
  change: [params: EditChangeParams]
  focus: []
  blur: []
  'drag-sort': [params: DragSortEventParams]
}>()

const gridRef = ref<VxeGridInstance>()

// 内部数据副本（响应式编辑用）
const innerData = ref<Record<string, unknown>[]>([...props.modelValue])

// 外部数据变化时同步内部数据
watch(
  () => props.modelValue,
  (newVal) => {
    innerData.value = [...(newVal || [])]
  }
)

// 列持久化存储key
const storageKey = computed(() => (props.viewCode ? `edit-table-columns-${props.viewCode}` : ''))

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

// 编辑配置
const editConfigValue = computed<VxeGridProps['editConfig']>(() => ({
  trigger: props.editTrigger,
  mode: 'cell',
  showStatus: true,
  autoFocus: true,
  beforeEditMethod: ({ column }: { column: EditTableColumn }) => {
    if (props.disabled) return false
    if (column.editable === false) return false
    return true
  }
}))

// 行配置
const rowConfigValue = computed<VxeGridProps['rowConfig']>(() => ({
  isCurrent: props.rowConfig?.isCurrent ?? true,
  isHover: props.rowConfig?.isHover ?? true,
  keyField: props.rowConfig?.keyField ?? 'id'
}))

// 列配置
const columnConfigValue = computed<VxeGridProps['columnConfig']>(() => ({
  resizable: true,
  isHover: true
}))

// vxe-table 尺寸映射
const sizeMap: Record<string, 'mini' | 'small' | 'medium' | 'large'> = {
  mini: 'mini',
  small: 'small',
  medium: 'medium',
  large: 'large'
}

const gridSize = computed(() => sizeMap[props.size] || 'medium')

// 静态grid配置
const gridOptions = computed(() => ({}))

// 拖拽排序配置
const dragConfigValue = computed(() => {
  const dc = props.dragConfig
  if (!dc || !dc.enabled) {
    return { enabled: false }
  }
  return {
    enabled: dc.enabled,
    trigger: dc.trigger || 'icon',
    type: dc.type || 'row',
    showTip: dc.showTip !== false,
    handle: dc.handle || '.vxe-table-icon-drag-handle'
  }
})

// 行拖拽排序事件处理
function handleDragSort({
  row,
  oldIndex,
  newIndex
}: {
  row: Record<string, unknown>
  oldIndex: number
  newIndex: number
}): void {
  try {
    const newData = [...innerData.value]
    const moved = newData.splice(oldIndex, 1)[0]
    newData.splice(newIndex, 0, moved)

    innerData.value = newData
    emit('update:modelValue', newData)

    emit('drag-sort', {
      row,
      oldIndex,
      newIndex,
      newData
    })
  } catch (err) {
    console.error('[EditTable] 行拖拽排序失败:', err)
  }
}

// 合计行配置
const summaryConfigValue = computed<SummaryConfig>(() => {
  if (props.summaryConfig) return props.summaryConfig
  return { enabled: false }
})

// 合计数据
const summaryData = computed(() => {
  if (!summaryConfigValue.value.enabled || !summaryConfigValue.value.columns) return []

  const rows = innerData.value
  if (rows.length === 0) return []

  return summaryConfigValue.value.columns.map((col: SummaryColumnConfig) => {
    const { field, method, customMethod, formatter, prefix, suffix } = col
    let result: number | string = ''

    if (method === 'none') {
      return { field, displayValue: '' }
    }

    if (method === 'custom' && customMethod) {
      result = customMethod(rows)
    } else {
      const values = rows.map((row) => Number(row[field])).filter((v) => !isNaN(v))

      switch (method) {
        case 'sum':
          result = values.reduce((a, b) => a + b, 0)
          break
        case 'avg':
          result = values.length > 0 ? values.reduce((a, b) => a + b, 0) / values.length : 0
          break
        case 'count':
          result = rows.length
          break
        case 'min':
          result = values.length > 0 ? Math.min(...values) : 0
          break
        case 'max':
          result = values.length > 0 ? Math.max(...values) : 0
          break
        default:
          result = 0
      }
    }

    let displayValue = typeof result === 'number' ? String(result) : result
    if (formatter) {
      displayValue = formatter(result)
    }
    if (prefix) displayValue = prefix + displayValue
    if (suffix) displayValue = displayValue + suffix

    const colDef = props.columns.find((c) => c.field === field)
    return {
      field,
      displayValue,
      width: colDef?.width,
      align: colDef?.align
    }
  })
})

// 合计列配置
const summaryColumns = computed(() => {
  return (
    summaryConfigValue.value.columns?.map((col: SummaryColumnConfig) => {
      const colDef = props.columns.find((c) => c.field === col.field)
      return {
        field: col.field,
        width: colDef?.width,
        align: colDef?.align
      }
    }) || []
  )
})

// 编辑关闭事件处理（单元格值确认）
function handleEditClosed({
  row,
  column,
  rowIndex
}: {
  row: Record<string, unknown>
  column: EditTableColumn
  rowIndex: number
}): void {
  // 更新内部数据中对应的行
  const newData = [...innerData.value]
  newData[rowIndex] = { ...row }
  innerData.value = newData

  // 发送v-model更新
  emit('update:modelValue', newData)

  // 发送change事件
  const params: EditChangeParams = {
    row,
    field: column.field,
    value: row[column.field],
    rowIndex
  }
  emit('change', params)
}

// 列宽调整处理
function handleColumnResize({
  column,
  resizeWidth
}: {
  column: EditTableColumn
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

// 焦点事件处理
function handleFocus(): void {
  emit('focus')
}

// 失焦事件处理
function handleBlur(): void {
  emit('blur')
}

// === 暴露方法 ===

// 刷新表格
function refresh(): void {
  gridRef.value?.reloadData(innerData.value)
}

// 重置列配置
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

// 获取当前数据
function getData(): Record<string, unknown>[] {
  return [...innerData.value]
}

// 设置数据
function setData(data: Record<string, unknown>[]): void {
  innerData.value = [...data]
}

// 校验所有可编辑单元格
async function validate(): Promise<boolean> {
  try {
    const result = await gridRef.value?.validate()
    return result !== false
  } catch {
    return false
  }
}

// 清除校验状态
function clearValidate(): void {
  gridRef.value?.clearValidate()
}

// 一键重置
function resetAll(): void {
  resetColumns()
  clearValidate()
}

// 程序化行排序
function reorder(fromIndex: number, toIndex: number): void {
  try {
    if (fromIndex < 0 || fromIndex >= innerData.value.length) return
    if (toIndex < 0 || toIndex >= innerData.value.length) return
    if (fromIndex === toIndex) return

    const newData = [...innerData.value]
    const moved = newData.splice(fromIndex, 1)[0]
    newData.splice(toIndex, 0, moved)

    innerData.value = newData
    emit('update:modelValue', newData)

    emit('drag-sort', {
      row: moved,
      oldIndex: fromIndex,
      newIndex: toIndex,
      newData
    })
  } catch (err) {
    console.error('[EditTable] 程序化行排序失败:', err)
  }
}

defineExpose({
  gridRef,
  refresh,
  resetColumns,
  getData,
  setData,
  validate,
  clearValidate,
  resetAll,
  reorder,
  // 内部状态暴露（供测试使用）
  innerData,
  displayColumns,
  slottedColumns,
  gridSize,
  editConfigValue,
  dragConfigValue,
  rowConfigValue,
  summaryData
})
</script>

<style scoped lang="scss">
.edit-table {
  display: flex;
  flex-direction: column;
  height: 100%;

  &__prefix {
    flex-shrink: 0;
  }

  &__suffix {
    flex-shrink: 0;
  }

  &__summary {
    display: flex;
    align-items: center;
    padding: 8px 0;
    border-top: 2px solid #409eff;
    background-color: #f5f7fa;
    font-weight: 600;
    flex-shrink: 0;
    overflow-x: auto;
  }

  &__summary-cell {
    padding: 0 8px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
