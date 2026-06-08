<template>
  <div class="hrm-archive-list-page">
    <!-- 快捷统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">{{ $t('hrm.archive.totalArchives') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--enabled">
          <div class="stat-value">{{ stats.enabled }}</div>
          <div class="stat-label">{{ $t('hrm.archive.enabled') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--disabled">
          <div class="stat-value">{{ stats.disabled }}</div>
          <div class="stat-label">{{ $t('hrm.archive.disabled') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--month">
          <div class="stat-value">{{ stats.newThisMonth }}</div>
          <div class="stat-label">{{ $t('hrm.archive.newThisMonth') }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item :label="$t('hrm.archive.employeeName')">
          <el-input
            v-model="searchForm.employeeName"
            :placeholder="$t('hrm.archive.employeeNamePlaceholder')"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item :label="$t('hrm.archive.education')">
          <el-select
            v-model="searchForm.education"
            :placeholder="$t('common.pleaseSelect')"
            clearable
            style="width: 160px"
            @change="handleSearch"
          >
            <el-option v-for="item in educationOptions" :key="item" :label="item" :value="item" />
          </el-select>
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

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-header">
          <span>{{ $t('hrm.archive.recordCount', { total: pagination.total }) }}</span>
          <el-button type="primary" @click="handleCreate">
            {{ $t('hrm.archive.add') }}
          </el-button>
        </div>
      </template>

      <vxe-table
        ref="tableRef"
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        stripe
        style="width: 100%"
      >
        <vxe-column field="employeeName" :title="$t('hrm.archive.employeeName')" min-width="100" />
        <vxe-column
          field="education"
          :title="$t('hrm.archive.education')"
          min-width="100"
          align="center"
        />
        <vxe-column field="major" :title="$t('hrm.archive.major')" min-width="120" align="center" />
        <vxe-column field="school" :title="$t('hrm.archive.school')" min-width="160" />
        <vxe-column
          field="emergencyContact"
          :title="$t('hrm.archive.emergencyContact')"
          min-width="120"
          align="center"
        />
        <vxe-column
          field="archiveDate"
          :title="$t('hrm.archive.archiveDate')"
          width="120"
          sortable
        />
        <vxe-column field="status" :title="$t('hrm.archive.status')" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{
                row.status === 1
                  ? $t('hrm.archive.statusEnabled')
                  : $t('hrm.archive.statusDisabled')
              }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column :title="$t('common.operate')" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              {{ $t('common.edit') }}
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? $t('hrm.archive.disable') : $t('hrm.archive.enable') }}
            </el-button>
            <el-popconfirm
              :title="$t('hrm.archive.deleteConfirm')"
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

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? $t('hrm.archive.editTitle') : $t('hrm.archive.addTitle')"
      width="600px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-form-item :label="$t('hrm.archive.employeeName')" prop="employeeId">
          <el-select
            v-model="formData.employeeId"
            :placeholder="$t('hrm.archive.employeeNamePlaceholder')"
            filterable
            remote
            :remote-method="searchEmployees"
            :loading="employeeLoading"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="emp in employeeOptions"
              :key="emp.id"
              :label="emp.name"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.education')">
              <el-select
                v-model="formData.education"
                style="width: 100%"
                :placeholder="$t('common.pleaseSelect')"
                clearable
              >
                <el-option
                  v-for="item in educationOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.major')">
              <el-input
                v-model="formData.major"
                :placeholder="$t('hrm.archive.majorPlaceholder')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.school')">
              <el-input
                v-model="formData.school"
                :placeholder="$t('hrm.archive.schoolPlaceholder')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.emergencyContact')">
              <el-input
                v-model="formData.emergencyContact"
                :placeholder="$t('hrm.archive.emergencyContactPlaceholder')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.emergencyPhone')" prop="emergencyPhone">
              <el-input
                v-model="formData.emergencyPhone"
                :placeholder="$t('hrm.archive.emergencyPhonePlaceholder')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.address')">
              <el-input
                v-model="formData.address"
                :placeholder="$t('hrm.archive.addressPlaceholder')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.bankCardNumber')">
              <el-input
                v-model="formData.bankCardNumber"
                :placeholder="$t('hrm.archive.bankCardNumberPlaceholder')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.bankName')">
              <el-input
                v-model="formData.bankName"
                :placeholder="$t('hrm.archive.bankNamePlaceholder')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.socialSecurityAccount')">
              <el-input
                v-model="formData.socialSecurityAccount"
                :placeholder="$t('hrm.archive.socialSecurityAccountPlaceholder')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.archive.archiveDate')">
              <el-date-picker
                v-model="formData.archiveDate"
                type="date"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('hrm.archive.status')">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">{{ $t('hrm.archive.statusEnabled') }}</el-radio>
            <el-radio :value="0">{{ $t('hrm.archive.statusDisabled') }}</el-radio>
          </el-radio-group>
        </el-form-item>
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
  getEmployeeArchivePageApi,
  createEmployeeArchiveApi,
  updateEmployeeArchiveApi,
  deleteEmployeeArchiveApi,
  updateEmployeeArchiveStatusApi,
  getEmployeeArchiveByIdApi,
  type EmployeeArchiveVO,
  type EmployeeArchiveQueryDTO,
  type EmployeeArchiveCreateDTO
} from '@/api/modules/hrm-archive'
import { getEmployeePageApi, type EmployeeVO } from '@/api/modules/hrm-employee'
import { useDebounceFn } from '@vueuse/core'

const tableRef = ref()
const formRef = ref<FormInstance>()
const tableLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const tableData = ref<EmployeeArchiveVO[]>([])
const employeeLoading = ref(false)
const employeeOptions = ref<EmployeeVO[]>([])

const searchForm = reactive<EmployeeArchiveQueryDTO>({
  pageNum: 1,
  pageSize: 20,
  employeeName: '',
  education: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const stats = computed(() => {
  const total = pagination.total
  const enabled = tableData.value.filter((r) => r.status === 1).length
  const disabled = tableData.value.filter((r) => r.status === 0).length
  const now = new Date()
  const newThisMonth = tableData.value.filter((r) => {
    if (!r.archiveDate) return false
    const d = new Date(r.archiveDate)
    return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear()
  }).length
  return { total, enabled, disabled, newThisMonth }
})

const educationOptions = ref(['高中', '大专', '本科', '硕士', '博士', '其他'])

const formData = reactive<EmployeeArchiveCreateDTO & { id?: number }>({
  employeeId: undefined,
  employeeName: '',
  education: '',
  major: '',
  school: '',
  emergencyContact: '',
  emergencyPhone: '',
  address: '',
  bankCardNumber: '',
  bankName: '',
  socialSecurityAccount: '',
  archiveDate: '',
  status: 1
})

const formRules: FormRules = {
  employeeId: [{ required: true, message: '请选择员工', trigger: 'change' }],
  emergencyPhone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }]
}

// ========== 数据加载 ==========
async function loadTableData(): Promise<void> {
  tableLoading.value = true
  try {
    const query: EmployeeArchiveQueryDTO = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      employeeName: searchForm.employeeName || undefined,
      education: searchForm.education || undefined
    }
    const res = await getEmployeeArchivePageApi(query)
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch {
    ElMessage.error('加载档案列表失败')
  } finally {
    tableLoading.value = false
  }
}

// ========== 搜索 ==========
const handleSearchDebounced = useDebounceFn(() => {
  handleSearch()
}, 300)

function handleSearch(): void {
  pagination.current = 1
  loadTableData()
}

function handleReset(): void {
  searchForm.employeeName = ''
  searchForm.education = ''
  handleSearch()
}

// ========== 员工搜索 ==========
async function searchEmployees(query: string): Promise<void> {
  if (!query) {
    employeeOptions.value = []
    return
  }
  employeeLoading.value = true
  try {
    const res = await getEmployeePageApi({ name: query, pageNum: 1, pageSize: 20 })
    employeeOptions.value = res.records || []
  } catch {
    // 静默失败
  } finally {
    employeeLoading.value = false
  }
}

// ========== CRUD操作 ==========
function handleCreate(): void {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

async function handleEdit(row: EmployeeArchiveVO): Promise<void> {
  isEdit.value = true
  editingId.value = row.id
  dialogVisible.value = true
  try {
    const detail = await getEmployeeArchiveByIdApi(row.id)
    formData.employeeId = detail.employeeId
    formData.employeeName = detail.employeeName
    formData.education = detail.education || ''
    formData.major = detail.major || ''
    formData.school = detail.school || ''
    formData.emergencyContact = detail.emergencyContact || ''
    formData.emergencyPhone = detail.emergencyPhone || ''
    formData.address = detail.address || ''
    formData.bankCardNumber = detail.bankCardNumber || ''
    formData.bankName = detail.bankName || ''
    formData.socialSecurityAccount = detail.socialSecurityAccount || ''
    formData.archiveDate = detail.archiveDate || ''
    formData.status = detail.status
    // 回填员工下拉选项
    if (detail.employeeId && detail.employeeName) {
      employeeOptions.value = [{ id: detail.employeeId, name: detail.employeeName } as EmployeeVO]
    }
  } catch {
    ElMessage.error('获取档案详情失败')
    dialogVisible.value = false
  }
}

async function handleDelete(row: EmployeeArchiveVO): Promise<void> {
  try {
    await deleteEmployeeArchiveApi(row.id)
    ElMessage.success('删除成功')
    loadTableData()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function handleToggleStatus(row: EmployeeArchiveVO): Promise<void> {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '停用' : '启用'
  try {
    await updateEmployeeArchiveStatusApi(row.id, newStatus)
    ElMessage.success(`${actionText}成功`)
    loadTableData()
  } catch {
    ElMessage.error(`${actionText}失败`)
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    // 从选中的员工获取姓名
    const selectedEmp = employeeOptions.value.find((e) => e.id === formData.employeeId)
    const submitData = { ...formData, employeeName: selectedEmp?.name || formData.employeeName }
    if (isEdit.value && editingId.value) {
      await updateEmployeeArchiveApi({ id: editingId.value, ...submitData })
      ElMessage.success('更新成功')
    } else {
      await createEmployeeArchiveApi(submitData)
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
  formData.employeeId = undefined
  formData.employeeName = ''
  formData.education = ''
  formData.major = ''
  formData.school = ''
  formData.emergencyContact = ''
  formData.emergencyPhone = ''
  formData.address = ''
  formData.bankCardNumber = ''
  formData.bankName = ''
  formData.socialSecurityAccount = ''
  formData.archiveDate = ''
  formData.status = 1
  employeeOptions.value = []
  formRef.value?.resetFields()
}

// ========== 生命周期 ==========
onMounted(() => {
  loadTableData()
})
</script>

<style scoped lang="scss">
.hrm-archive-list-page {
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

      &--enabled .stat-value {
        color: var(--el-color-success);
      }

      &--disabled .stat-value {
        color: var(--el-color-danger);
      }

      &--month .stat-value {
        color: var(--el-color-primary);
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

    .pagination-wrap {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }
}
</style>
