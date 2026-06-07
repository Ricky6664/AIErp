<template>
  <div class="voucherword-list-page">
    <!-- 快捷统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总记录数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--enabled">
          <div class="stat-value">{{ stats.enabled }}</div>
          <div class="stat-label">已启用</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card stat-card--disabled">
          <div class="stat-value">{{ stats.disabled }}</div>
          <div class="stat-label">已停用</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="凭证字名称">
          <el-input
            v-model="searchForm.wordName"
            placeholder="请输入凭证字名称"
            clearable
            style="width: 200px"
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑凭证字' : '新增凭证字'"
      width="500px"
      destroy-on-close
      @closed="handleDialogClosed"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        @submit.prevent
      >
        <el-form-item label="凭证字编码" prop="wordCode">
          <el-input v-model="formData.wordCode" placeholder="请输入凭证字编码" maxlength="50" />
        </el-form-item>
        <el-form-item label="凭证字名称" prop="wordName">
          <el-input v-model="formData.wordName" placeholder="请输入凭证字名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="9999"
            placeholder="请输入排序号"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="停用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit"> 确认 </el-button>
      </template>
    </el-dialog>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-header">
          <span>{{ stats.total }} 条记录</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增凭证字</el-button>
        </div>
      </template>

      <vxe-table
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        border
        style="width: 100%"
      >
        <vxe-column type="seq" title="序号" width="60" align="center" />
        <vxe-column field="wordCode" title="凭证字编码" min-width="120" />
        <vxe-column field="wordName" title="凭证字名称" min-width="140" />
        <vxe-column field="sortOrder" title="排序号" width="100" align="center" sortable />
        <vxe-column field="status" title="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column field="createTime" title="创建时间" width="180" sortable />
        <vxe-column title="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-popconfirm
              title="确认删除该凭证字？"
              confirm-button-text="确认"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </vxe-column>
      </vxe-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'
import {
  getVoucherWordPageApi,
  getVoucherWordByIdApi,
  createVoucherWordApi,
  updateVoucherWordApi,
  updateVoucherWordStatusApi,
  deleteVoucherWordApi,
  type VoucherWordVO,
  type VoucherWordSaveDTO
} from '@/api/modules/finance-voucherword'

const tableLoading = ref(false)
const tableData = ref<VoucherWordVO[]>([])

const searchForm = reactive({
  wordName: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const stats = reactive({
  total: 0,
  enabled: 0,
  disabled: 0
})

// ---------- 表单弹窗 ----------
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const initFormData = (): VoucherWordSaveDTO => ({
  wordName: '',
  wordCode: '',
  sortOrder: 0,
  status: 1
})

const formData = reactive<VoucherWordSaveDTO>(initFormData())

const formRules: FormRules = {
  wordName: [
    { required: true, message: '请输入凭证字名称', trigger: 'blur' },
    { max: 100, message: '凭证字名称最长100个字符', trigger: 'blur' }
  ],
  wordCode: [
    { required: true, message: '请输入凭证字编码', trigger: 'blur' },
    { max: 50, message: '凭证字编码最长50个字符', trigger: 'blur' }
  ],
  sortOrder: [{ required: true, message: '请输入排序号', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function handleAdd(): void {
  isEdit.value = false
  editId.value = null
  Object.assign(formData, initFormData())
  dialogVisible.value = true
}

async function handleEdit(row: VoucherWordVO): Promise<void> {
  isEdit.value = true
  editId.value = row.id
  try {
    const detail = await getVoucherWordByIdApi(row.id)
    formData.wordName = detail.wordName
    formData.wordCode = detail.wordCode
    formData.sortOrder = detail.sortOrder
    formData.status = detail.status
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取凭证字详情失败')
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateVoucherWordApi(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createVoucherWordApi({ ...formData })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadData()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

function handleDialogClosed(): void {
  formRef.value?.resetFields()
}
// ---------- 表单弹窗结束 ----------

async function handleToggleStatus(row: VoucherWordVO): Promise<void> {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '停用' : '启用'
  try {
    await updateVoucherWordStatusApi(row.id, newStatus)
    ElMessage.success(`${actionText}成功`)
    await loadData()
  } catch {
    ElMessage.error(`${actionText}失败`)
  }
}

let debounceTimer: ReturnType<typeof setTimeout> | null = null

function handleSearchDebounced(): void {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

async function handleSearch(): Promise<void> {
  pagination.pageNum = 1
  await loadData()
}

function handleReset(): void {
  searchForm.wordName = ''
  searchForm.status = undefined
  pagination.pageNum = 1
  loadData()
}

async function loadData(): Promise<void> {
  tableLoading.value = true
  try {
    const res = await getVoucherWordPageApi({
      wordName: searchForm.wordName || undefined,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.list || []
    pagination.total = res.total || 0
    updateStats(res.list || [], res.total || 0)
  } catch {
    ElMessage.error('加载凭证字列表失败')
  } finally {
    tableLoading.value = false
  }
}

function updateStats(list: VoucherWordVO[], total: number): void {
  stats.total = total
  stats.enabled = list.filter((item) => item.status === 1).length
  stats.disabled = list.filter((item) => item.status === 0).length
}

async function handleDelete(row: VoucherWordVO): Promise<void> {
  try {
    await deleteVoucherWordApi(row.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

function handleSizeChange(): void {
  pagination.pageNum = 1
  loadData()
}

function handlePageChange(): void {
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.voucherword-list-page {
  padding: 20px;

  .stats-row {
    margin-bottom: 16px;

    .stat-card {
      text-align: center;

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--el-color-primary);
        line-height: 1.2;
      }

      .stat-label {
        margin-top: 8px;
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }

      &--enabled {
        .stat-value {
          color: var(--el-color-success);
        }
      }

      &--disabled {
        .stat-value {
          color: var(--el-color-danger);
        }
      }
    }
  }

  .search-card {
    margin-bottom: 16px;
  }

  .table-card {
    .table-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
