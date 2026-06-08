<template>
  <div class="hrm-salary-page">
    <!-- 快捷统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">{{ $t('hrm.salary.totalRecords') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--base">
          <div class="stat-value">{{ stats.totalBaseSalary }}</div>
          <div class="stat-label">{{ $t('hrm.salary.totalBaseSalary') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--net">
          <div class="stat-value">{{ stats.totalNetSalary }}</div>
          <div class="stat-label">{{ $t('hrm.salary.totalNetSalary') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--count">
          <div class="stat-value">{{ stats.employees }}</div>
          <div class="stat-label">{{ $t('hrm.salary.employees') }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item :label="$t('hrm.salary.employeeId')">
          <el-input-number
            v-model="searchForm.employeeId"
            :min="1"
            :placeholder="$t('hrm.salary.employeeIdPlaceholder')"
            clearable
            style="width: 160px"
            @change="handleSearch"
          />
        </el-form-item>
        <el-form-item :label="$t('hrm.salary.salaryMonth')">
          <el-input
            v-model="searchForm.salaryMonth"
            :placeholder="$t('hrm.salary.salaryMonthPlaceholder')"
            clearable
            style="width: 160px"
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            {{ $t('common.search') }}
          </el-button>
          <el-button @click="handleReset">
            {{ $t('common.reset') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 主从区域 -->
    <el-row :gutter="16" class="master-detail-row">
      <!-- 主表 -->
      <el-col :xs="24" :md="14" class="master-col">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="table-header">
              <span>{{ $t('hrm.salary.recordCount', { total: pagination.total }) }}</span>
              <el-button type="primary" @click="handleCreate">
                {{ $t('hrm.salary.add') }}
              </el-button>
            </div>
          </template>

          <vxe-table
            ref="tableRef"
            :loading="tableLoading"
            :data="tableData"
            :scroll-y="{ enabled: true, gt: 100 }"
            max-height="500"
            stripe
            highlight-current-row
            style="width: 100%"
            @current-row-change="handleRowChange"
          >
            <vxe-column
              field="employeeId"
              :title="$t('hrm.salary.employeeId')"
              width="110"
              align="center"
            />
            <vxe-column
              field="salaryMonth"
              :title="$t('hrm.salary.salaryMonth')"
              width="120"
              align="center"
              sortable
            />
            <vxe-column
              field="baseSalary"
              :title="$t('hrm.salary.baseSalary')"
              width="130"
              align="right"
            >
              <template #default="{ row }">
                {{ formatCurrency(row.baseSalary) }}
              </template>
            </vxe-column>
            <vxe-column
              field="allowance"
              :title="$t('hrm.salary.allowance')"
              width="120"
              align="right"
            >
              <template #default="{ row }">
                {{ formatCurrency(row.allowance) }}
              </template>
            </vxe-column>
            <vxe-column
              field="deduction"
              :title="$t('hrm.salary.deduction')"
              width="120"
              align="right"
            >
              <template #default="{ row }">
                {{ formatCurrency(row.deduction) }}
              </template>
            </vxe-column>
            <vxe-column
              field="netSalary"
              :title="$t('hrm.salary.netSalary')"
              width="140"
              align="right"
            >
              <template #default="{ row }">
                <span class="net-salary-value">{{ formatCurrency(row.netSalary) }}</span>
              </template>
            </vxe-column>
            <vxe-column
              field="createTime"
              :title="$t('hrm.salary.createTime')"
              width="170"
              align="center"
            />
            <vxe-column :title="$t('common.operate')" width="150" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleEdit(row)">
                  {{ $t('common.edit') }}
                </el-button>
                <el-popconfirm
                  :title="$t('hrm.salary.deleteConfirm')"
                  :confirm-button-text="$t('common.confirm')"
                  :cancel-button-text="$t('common.cancel')"
                  @confirm="handleDelete(row)"
                >
                  <template #reference>
                    <el-button type="danger" link size="small">{{ $t('common.delete') }}</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </vxe-column>
          </vxe-table>

          <div class="pagination-wrap">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next"
              @current-change="loadTableData"
              @size-change="loadTableData"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 从表标签页 -->
      <el-col :xs="24" :md="10" class="detail-col">
        <el-card shadow="never" class="detail-card">
          <template #header>
            <span v-if="selectedSalary">
              {{ $t('hrm.salary.detailTitle', { id: selectedSalary.id }) }}
            </span>
            <span v-else>{{ $t('hrm.salary.selectHint') }}</span>
          </template>

          <div v-if="!selectedSalary" class="detail-empty">
            <el-empty :description="$t('hrm.salary.clickRowHint')" />
          </div>

          <el-tabs v-else v-model="activeTab" class="detail-tabs">
            <el-tab-pane :label="$t('hrm.salary.tabBreakdown')" name="breakdown">
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item :label="$t('hrm.salary.baseSalary')">
                  {{ formatCurrency(selectedSalary.baseSalary) }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.salary.allowance')">
                  {{ formatCurrency(selectedSalary.allowance) }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.salary.deduction')">
                  {{ formatCurrency(selectedSalary.deduction) }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.salary.grossSalary')">
                  <span class="summary-value">{{
                    formatCurrency(
                      (selectedSalary.baseSalary || 0) + (selectedSalary.allowance || 0)
                    )
                  }}</span>
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.salary.netSalary')">
                  <span class="net-value">{{ formatCurrency(selectedSalary.netSalary) }}</span>
                </el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>

            <el-tab-pane :label="$t('hrm.salary.tabSummary')" name="summary" lazy>
              <el-card shadow="never" size="small" class="summary-card">
                <el-statistic
                  :title="$t('hrm.salary.baseSalary')"
                  :value="formatCurrency(selectedSalary.baseSalary)"
                />
                <el-statistic
                  :title="$t('hrm.salary.allowance')"
                  :value="formatCurrency(selectedSalary.allowance)"
                />
                <el-statistic
                  :title="$t('hrm.salary.deduction')"
                  :value="'-' + formatCurrency(selectedSalary.deduction)"
                />
                <el-divider />
                <el-statistic
                  :title="$t('hrm.salary.netSalary')"
                  :value="formatCurrency(selectedSalary.netSalary)"
                />
              </el-card>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? $t('hrm.salary.editTitle') : $t('hrm.salary.addTitle')"
      width="600px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.salary.employeeId')" prop="employeeId">
              <el-input-number
                v-model="formData.employeeId"
                :min="1"
                :placeholder="$t('hrm.salary.employeeIdPlaceholder')"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.salary.salaryMonth')" prop="salaryMonth">
              <el-input
                v-model="formData.salaryMonth"
                :placeholder="$t('hrm.salary.salaryMonthFormat')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.salary.baseSalary')">
              <el-input-number
                v-model="formData.baseSalary"
                :min="0"
                :precision="2"
                :step="100"
                style="width: 100%"
                :placeholder="$t('hrm.salary.baseSalaryPlaceholder')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.salary.allowance')">
              <el-input-number
                v-model="formData.allowance"
                :min="0"
                :precision="2"
                :step="100"
                style="width: 100%"
                :placeholder="$t('hrm.salary.allowancePlaceholder')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.salary.deduction')">
              <el-input-number
                v-model="formData.deduction"
                :min="0"
                :precision="2"
                :step="100"
                style="width: 100%"
                :placeholder="$t('hrm.salary.deductionPlaceholder')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.salary.netSalaryPreview')">
              <el-input :value="formatCurrency(netSalaryPreview)" disabled style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getSalaryPageApi,
  createSalaryApi,
  updateSalaryApi,
  deleteSalaryApi,
  type SalaryVO,
  type SalaryQueryDTO,
  type SalaryCreateDTO
} from '@/api/modules/hrm-salary'

const tableRef = ref()
const formRef = ref<FormInstance>()
const tableLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const selectedSalary = ref<SalaryVO | null>(null)
const activeTab = ref('breakdown')
const tableData = ref<SalaryVO[]>([])

let debounceTimer: ReturnType<typeof setTimeout> | null = null

const searchForm = reactive<SalaryQueryDTO & { employeeId?: number; salaryMonth?: string }>({
  pageNum: 1,
  pageSize: 20,
  employeeId: undefined,
  salaryMonth: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const stats = computed(() => {
  const total = pagination.total
  const totalBaseSalary = tableData.value
    .reduce((sum, r) => sum + (r.baseSalary || 0), 0)
    .toFixed(2)
  const totalNetSalary = tableData.value.reduce((sum, r) => sum + (r.netSalary || 0), 0).toFixed(2)
  const employeeSet = new Set(tableData.value.map((r) => r.employeeId).filter(Boolean))
  return {
    total,
    totalBaseSalary: '￥' + totalBaseSalary,
    totalNetSalary: '￥' + totalNetSalary,
    employees: employeeSet.size
  }
})

const formData = reactive<SalaryCreateDTO & { id?: number }>({
  employeeId: 0,
  baseSalary: 0,
  allowance: 0,
  deduction: 0,
  salaryMonth: ''
})

const formRules: FormRules = {
  employeeId: [{ required: true, message: '员工ID不能为空', trigger: 'blur' }],
  salaryMonth: [
    { required: true, message: '薪资月份不能为空', trigger: 'blur' },
    { pattern: /^\d{4}-(0[1-9]|1[0-2])$/, message: '格式: YYYY-MM', trigger: 'blur' }
  ]
}

const netSalaryPreview = computed(() => {
  const base = formData.baseSalary || 0
  const allow = formData.allowance || 0
  const deduct = formData.deduction || 0
  return (base + allow - deduct).toFixed(2)
})

// ========== 数据加载 ==========
async function loadTableData(): Promise<void> {
  tableLoading.value = true
  try {
    const query: SalaryQueryDTO = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      employeeId: searchForm.employeeId || undefined,
      salaryMonth: searchForm.salaryMonth || undefined
    }
    const res = await getSalaryPageApi(query)
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch {
    ElMessage.error('加载薪资列表失败')
  } finally {
    tableLoading.value = false
  }
}

// ========== 搜索 ==========
function handleSearch(): void {
  pagination.current = 1
  loadTableData()
}

function handleSearchDebounced(): void {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

function handleReset(): void {
  searchForm.employeeId = undefined
  searchForm.salaryMonth = ''
  handleSearch()
}

// ========== 行选择 ==========
function handleRowChange({ row }: { row: SalaryVO | null }): void {
  selectedSalary.value = row
  if (row) {
    activeTab.value = 'breakdown'
  }
}

// ========== CRUD操作 ==========
function handleCreate(): void {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: SalaryVO): void {
  isEdit.value = true
  editingId.value = row.id
  formData.employeeId = row.employeeId
  formData.baseSalary = row.baseSalary || 0
  formData.allowance = row.allowance || 0
  formData.deduction = row.deduction || 0
  formData.salaryMonth = row.salaryMonth
  dialogVisible.value = true
}

async function handleDelete(row: SalaryVO): Promise<void> {
  try {
    await deleteSalaryApi(row.id)
    ElMessage.success('删除成功')
    if (selectedSalary.value?.id === row.id) {
      selectedSalary.value = null
    }
    loadTableData()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateSalaryApi({ id: editingId.value, ...formData })
      ElMessage.success('更新成功')
    } else {
      await createSalaryApi(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadTableData()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

function resetForm(): void {
  formData.employeeId = 0
  formData.baseSalary = 0
  formData.allowance = 0
  formData.deduction = 0
  formData.salaryMonth = ''
  formRef.value?.resetFields()
}

// ========== 工具函数 ==========
function formatCurrency(value: number | undefined | null): string {
  if (value === null || value === undefined || isNaN(value)) return '-'
  return (
    '￥' +
    Number(value).toLocaleString('zh-CN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    })
  )
}

// ========== 生命周期 ==========
onMounted(() => {
  loadTableData()
})
</script>

<style scoped lang="scss">
.hrm-salary-page {
  padding: 20px;

  .stats-row {
    margin-bottom: 16px;

    .stat-card {
      text-align: center;
      cursor: default;

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--el-text-color-primary);
        line-height: 1.4;
      }

      .stat-label {
        font-size: 13px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }

      &--base .stat-value {
        color: var(--el-color-primary);
      }

      &--net .stat-value {
        color: var(--el-color-success);
      }

      &--count .stat-value {
        color: var(--el-color-warning);
      }
    }
  }

  .search-card {
    margin-bottom: 16px;
  }

  .master-detail-row {
    .master-col,
    .detail-col {
      margin-bottom: 16px;
    }
  }

  .table-card {
    .table-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .pagination-wrap {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }

    .net-salary-value {
      font-weight: 600;
      color: var(--el-color-success);
    }
  }

  .detail-card {
    min-height: 400px;

    .detail-empty {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 300px;
    }

    .detail-tabs {
      .summary-card {
        .el-statistic {
          margin-bottom: 12px;
        }
      }

      .summary-value {
        font-weight: 600;
        color: var(--el-color-primary);
      }

      .net-value {
        font-weight: 600;
        color: var(--el-color-success);
      }
    }
  }
}
</style>
