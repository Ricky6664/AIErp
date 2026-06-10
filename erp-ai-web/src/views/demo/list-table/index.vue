<template>
  <div class="demo-list-table-page">
    <div class="page-header">
      <h2>列表数据表格基础标配功能演示页</h2>
      <p class="page-desc">
        演示 ListTable
        组件的基础标配功能：列定义、排序、筛选、分页、行高调节、合计行、虚拟滚动、当前行高亮、
        一键初始化/清空搜索排序、CRUD数据变更、路由参数同步等
      </p>
    </div>

    <el-alert
      v-if="error"
      :title="error"
      type="error"
      show-icon
      closable
      class="error-alert"
      @close="error = null"
    />

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchKeyword"
          placeholder="订单编号 / 客户 / 商品"
          clearable
          style="width: 260px"
          @keyup.enter="handleSearch"
        >
          <template #prepend>关键字</template>
        </el-input>
        <el-select
          v-model="searchStatus"
          placeholder="订单状态"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="处理中" value="processing" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="success" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
        <el-button type="warning" :disabled="!currentRow" @click="openEditDialog">
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
        <el-button
          type="danger"
          :disabled="!currentRow"
          :loading="deleting"
          @click="handleDeleteRow"
        >
          <el-icon><Delete /></el-icon>
          删除
        </el-button>
        <el-divider direction="vertical" />
        <el-dropdown trigger="click" @command="handleSizeSelect">
          <el-button>
            行高：{{ sizeLabel }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="opt in rowSizeOptions"
                :key="opt.key"
                :command="opt.key"
                :class="{ 'is-active': tableSize === opt.key }"
              >
                {{ opt.label }}
                <el-icon v-if="tableSize === opt.key" class="check-icon"><Check /></el-icon>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="danger" plain @click="handleResetAll">
          <el-icon><Delete /></el-icon>
          一键初始化
        </el-button>
        <el-button @click="handleClearSearchSort">
          <el-icon><Operation /></el-icon>
          清空搜索排序
        </el-button>
      </div>
    </div>

    <div class="table-wrapper">
      <ListTable
        ref="tableRef"
        :columns="columns"
        :data="data"
        :loading="loading"
        :total="total"
        :current-page="currentPage"
        :page-size="pageSize"
        :size="tableSize"
        :sort-config="sortConfig"
        :summary-data="totalAmountSummary"
        view-code="demo-list-table"
        page-mode="server"
        border="full"
        @update:current-page="handlePageChange"
        @update:page-size="handleSizeChange"
        @sort-change="handleSortChange"
        @current-change="handleCurrentChange"
        @row-click="handleRowClick"
        @row-dblclick="handleRowDblclick"
      >
        <template #status="{ row }">
          <el-tag :type="(statusTagTypeMap[row.status as string] as any) || 'info'" size="small">
            {{ statusLabelMap[row.status as string] || row.status }}
          </el-tag>
        </template>
      </ListTable>
    </div>

    <div v-if="currentRow" class="current-row-info">
      <span>当前选中行：</span>
      <el-tag type="primary" size="small" closable @close="currentRow = null">
        #{{ currentRow.id }} — {{ currentRow.orderNo }} — {{ currentRow.customerName }}
      </el-tag>
      <el-button text type="primary" size="small" @click="handleViewDetail">查看详情</el-button>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑订单' : '新增订单'"
      width="520px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" label-width="90px">
        <el-form-item label="客户名称" required>
          <el-input v-model="form.customerName" placeholder="请输入客户名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="商品名称" required>
          <el-input v-model="form.productName" placeholder="请输入商品名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="数量" required>
          <el-input-number v-model="form.quantity" :min="1" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单价" required>
          <el-input-number
            v-model="form.unitPrice"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单日期">
          <el-date-picker
            v-model="form.orderDate"
            type="date"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="交付日期">
          <el-date-picker
            v-model="form.deliveryDate"
            type="date"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import {
  Search,
  Refresh,
  ArrowDown,
  Delete,
  Operation,
  Check,
  Plus,
  Edit
} from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import type { RowSize } from '@/types/list-table'
import type { DemoOrderItem } from '@/api/modules/demo'
import { useDemoListTable } from '@/composables/useDemoListTable'
import ListTable from '@/components/list-table/index.vue'

const {
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
  handleViewDetail: viewDetail,
  syncQueryToState
} = useDemoListTable()

const tableRef = ref<InstanceType<typeof ListTable> | null>(null)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance | null>(null)

const defaultForm = (): Partial<DemoOrderItem> => ({
  customerName: '',
  productName: '',
  quantity: 1,
  unitPrice: 0,
  status: 'pending' as const,
  orderDate: new Date().toISOString().slice(0, 10),
  deliveryDate: ''
})

const form = reactive<Partial<DemoOrderItem>>(defaultForm())

function openAddDialog(): void {
  editingId.value = null
  Object.assign(form, defaultForm())
  dialogVisible.value = true
}

function openEditDialog(): void {
  if (!currentRow.value) return
  editingId.value = currentRow.value.id as number
  form.customerName = (currentRow.value.customerName as string) || ''
  form.productName = (currentRow.value.productName as string) || ''
  form.quantity = (currentRow.value.quantity as number) || 1
  form.unitPrice = (currentRow.value.unitPrice as number) || 0
  form.status = (currentRow.value.status as DemoOrderItem['status']) || 'pending'
  form.orderDate = (currentRow.value.orderDate as string) || ''
  form.deliveryDate = (currentRow.value.deliveryDate as string) || ''
  dialogVisible.value = true
}

async function submitForm(): Promise<void> {
  const success = await handleSave({ ...form, id: editingId.value ?? undefined })
  if (success) {
    dialogVisible.value = false
  }
}

function resetForm(): void {
  editingId.value = null
  Object.assign(form, defaultForm())
  formRef.value?.resetFields()
}

async function handleDeleteRow(): Promise<void> {
  if (!currentRow.value) return
  await handleDelete(currentRow.value.id as number)
}

function handleViewDetail(): void {
  if (!currentRow.value) return
  viewDetail(currentRow.value.id as number)
}

const rowSizeOptions: { key: RowSize; label: string }[] = [
  { key: 'mini', label: '迷你 (mini)' },
  { key: 'small', label: '小 (small)' },
  { key: 'medium', label: '中 (medium)' },
  { key: 'large', label: '大 (large)' },
  { key: 'loose', label: '宽松 (loose)' },
  { key: 'x-large', label: '超大 (x-large)' },
  { key: 'xx-large', label: '特大 (xx-large)' }
]

const sizeLabelMap: Record<string, string> = {
  mini: '迷你',
  small: '小',
  medium: '中',
  large: '大',
  loose: '宽松',
  'x-large': '超大',
  'xx-large': '特大'
}

const sizeLabel = computed(() => sizeLabelMap[tableSize.value] || tableSize.value)

function handleRefresh(): void {
  fetchData()
}

function handleResetAll(): void {
  tableRef.value?.resetAll()
  searchKeyword.value = ''
  searchStatus.value = ''
  fetchData()
}

function handleClearSearchSort(): void {
  tableRef.value?.clearSearchAndSort()
}

onMounted(() => {
  syncQueryToState()
  fetchData()
})
</script>

<style scoped lang="scss">
.demo-list-table-page {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;

  .page-header {
    margin-bottom: 16px;
    flex-shrink: 0;

    h2 {
      margin: 0 0 6px;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-desc {
      margin: 0;
      font-size: 13px;
      color: var(--el-text-color-secondary);
      line-height: 1.6;
    }
  }

  .error-alert {
    margin-bottom: 12px;
    flex-shrink: 0;
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    flex-shrink: 0;
    flex-wrap: wrap;
    gap: 12px;

    .toolbar-left,
    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
    }
  }

  .table-wrapper {
    flex: 1;
    min-height: 0;
    border: 1px solid var(--el-border-color-light);
    border-radius: 4px;
    overflow: hidden;
  }

  .current-row-info {
    margin-top: 12px;
    flex-shrink: 0;
    font-size: 13px;
    color: var(--el-text-color-secondary);
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .check-icon {
    margin-left: auto;
    color: var(--el-color-primary);
  }
}

.is-active {
  color: var(--el-color-primary);
  font-weight: 500;
}
</style>
