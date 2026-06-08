<template>
  <div class="hrm-attendance-list-page">
    <!-- 快捷统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ pagination.total }}</div>
          <div class="stat-label">{{ $t('hrm.attendance.totalRecords') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--normal">
          <div class="stat-value">{{ stats.normalCount }}</div>
          <div class="stat-label">{{ $t('hrm.attendance.normal') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--absent">
          <div class="stat-value">{{ stats.absentCount }}</div>
          <div class="stat-label">{{ $t('hrm.attendance.absent') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--overtime">
          <div class="stat-value">{{ stats.overtimeCount }}</div>
          <div class="stat-label">{{ $t('hrm.attendance.overtime') }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item :label="$t('hrm.attendance.employeeName')">
          <el-input
            v-model="searchForm.employeeName"
            :placeholder="$t('hrm.attendance.employeeNamePlaceholder')"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item :label="$t('hrm.attendance.dateRange')">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="-"
            :start-placeholder="$t('common.startDate')"
            :end-placeholder="$t('common.endDate')"
            value-format="YYYY-MM-DD"
            style="width: 240px"
            @change="handleSearch"
          />
        </el-form-item>
        <el-form-item :label="$t('hrm.attendance.attendanceType')">
          <el-select
            v-model="searchForm.attendanceType"
            :placeholder="$t('common.pleaseSelect')"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option
              v-for="item in attendanceTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
          <span>{{ $t('hrm.attendance.recordCount', { total: pagination.total }) }}</span>
          <el-button type="primary" @click="handleCreate">
            {{ $t('hrm.attendance.add') }}
          </el-button>
        </div>
      </template>

      <vxe-table
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        stripe
        style="width: 100%"
      >
        <vxe-column
          field="employeeName"
          :title="$t('hrm.attendance.employeeName')"
          min-width="120"
        />
        <vxe-column
          field="attendanceDate"
          :title="$t('hrm.attendance.attendanceDate')"
          width="130"
          align="center"
          sortable
        />
        <vxe-column
          field="checkInTime"
          :title="$t('hrm.attendance.checkInTime')"
          width="170"
          align="center"
        />
        <vxe-column
          field="checkOutTime"
          :title="$t('hrm.attendance.checkOutTime')"
          width="170"
          align="center"
        />
        <vxe-column
          field="workHours"
          :title="$t('hrm.attendance.workHours')"
          width="100"
          align="center"
        />
        <vxe-column
          field="attendanceType"
          :title="$t('hrm.attendance.attendanceType')"
          width="110"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="
                row.attendanceType === 'normal'
                  ? 'success'
                  : row.attendanceType === 'late' || row.attendanceType === 'early'
                    ? 'warning'
                    : row.attendanceType === 'absent'
                      ? 'danger'
                      : 'primary'
              "
              size="small"
            >
              {{
                row.attendanceType === 'normal'
                  ? $t('hrm.attendance.typeNormal')
                  : row.attendanceType === 'late'
                    ? $t('hrm.attendance.typeLate')
                    : row.attendanceType === 'early'
                      ? $t('hrm.attendance.typeEarly')
                      : row.attendanceType === 'absent'
                        ? $t('hrm.attendance.typeAbsent')
                        : $t('hrm.attendance.typeOvertime')
              }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column
          field="overtimeHours"
          :title="$t('hrm.attendance.overtimeHours')"
          width="100"
          align="center"
        />
        <vxe-column :title="$t('common.operate')" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              {{ $t('common.edit') }}
            </el-button>
            <el-popconfirm
              :title="$t('hrm.attendance.deleteConfirm')"
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
      :title="isEdit ? $t('hrm.attendance.editTitle') : $t('hrm.attendance.addTitle')"
      width="600px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.attendance.employeeId')" prop="employeeId">
              <el-input-number
                v-model="formData.employeeId"
                :min="1"
                :placeholder="$t('hrm.attendance.employeeIdPlaceholder')"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.attendance.attendanceDate')" prop="attendanceDate">
              <el-date-picker
                v-model="formData.attendanceDate"
                type="date"
                style="width: 100%"
                value-format="YYYY-MM-DD"
                :placeholder="$t('common.pleaseSelect')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.attendance.checkInTime')">
              <el-date-picker
                v-model="formData.checkInTime"
                type="datetime"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
                :placeholder="$t('common.pleaseSelect')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.attendance.checkOutTime')">
              <el-date-picker
                v-model="formData.checkOutTime"
                type="datetime"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
                :placeholder="$t('common.pleaseSelect')"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.attendance.workHours')">
              <el-input-number
                v-model="formData.workHours"
                :min="0"
                :precision="2"
                :placeholder="$t('hrm.attendance.workHoursPlaceholder')"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('hrm.attendance.overtimeHours')">
              <el-input-number
                v-model="formData.overtimeHours"
                :min="0"
                :precision="2"
                :placeholder="$t('hrm.attendance.overtimeHoursPlaceholder')"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="$t('hrm.attendance.attendanceType')">
              <el-select
                v-model="formData.attendanceType"
                style="width: 100%"
                :placeholder="$t('common.pleaseSelect')"
              >
                <el-option
                  v-for="item in attendanceTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
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
  getAttendancePageApi,
  createAttendanceApi,
  updateAttendanceApi,
  deleteAttendanceApi,
  type AttendanceVO,
  type AttendanceQueryDTO,
  type AttendanceCreateDTO
} from '@/api/modules/hrm-attendance'

const formRef = ref<FormInstance>()
const tableLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const tableData = ref<AttendanceVO[]>([])

let debounceTimer: ReturnType<typeof setTimeout> | null = null

const searchForm = reactive<AttendanceQueryDTO & { employeeName?: string; dateRange?: string[] }>({
  pageNum: 1,
  pageSize: 20,
  employeeId: undefined,
  employeeName: '',
  dateRange: [],
  attendanceType: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const stats = computed(() => ({
  normalCount: tableData.value.filter((r) => r.attendanceType === 'normal').length,
  absentCount: tableData.value.filter((r) => r.attendanceType === 'absent').length,
  overtimeCount: tableData.value.filter((r) => r.attendanceType === 'overtime').length
}))

const attendanceTypeOptions = ref([
  { value: 'normal', label: '正常' },
  { value: 'late', label: '迟到' },
  { value: 'early', label: '早退' },
  { value: 'absent', label: '缺勤' },
  { value: 'overtime', label: '加班' }
])

const formData = reactive<AttendanceCreateDTO & { id?: number }>({
  employeeId: 0,
  attendanceDate: '',
  checkInTime: '',
  checkOutTime: '',
  workHours: undefined,
  attendanceType: 'normal',
  overtimeHours: undefined
})

const formRules: FormRules = {
  employeeId: [{ required: true, message: '员工ID不能为空', trigger: 'blur' }],
  attendanceDate: [{ required: true, message: '考勤日期不能为空', trigger: 'change' }]
}

async function loadTableData(): Promise<void> {
  tableLoading.value = true
  try {
    const query: AttendanceQueryDTO = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      employeeName: searchForm.employeeName || undefined,
      attendanceDateStart: searchForm.dateRange?.[0] || undefined,
      attendanceDateEnd: searchForm.dateRange?.[1] || undefined,
      attendanceType: searchForm.attendanceType || undefined
    }
    const res = await getAttendancePageApi(query)
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch {
    ElMessage.error('加载考勤列表失败')
  } finally {
    tableLoading.value = false
  }
}

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
  searchForm.employeeName = ''
  searchForm.dateRange = []
  searchForm.attendanceType = ''
  handleSearch()
}

function handleCreate(): void {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: AttendanceVO): void {
  isEdit.value = true
  editingId.value = row.id
  formData.employeeId = row.employeeId
  formData.attendanceDate = row.attendanceDate
  formData.checkInTime = row.checkInTime || ''
  formData.checkOutTime = row.checkOutTime || ''
  formData.workHours = row.workHours
  formData.attendanceType = row.attendanceType || 'normal'
  formData.overtimeHours = row.overtimeHours
  dialogVisible.value = true
}

async function handleDelete(row: AttendanceVO): Promise<void> {
  try {
    await deleteAttendanceApi(row.id)
    ElMessage.success('删除成功')
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
      await updateAttendanceApi({ id: editingId.value, ...formData })
      ElMessage.success('更新成功')
    } else {
      await createAttendanceApi(formData)
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
  formData.attendanceDate = ''
  formData.checkInTime = ''
  formData.checkOutTime = ''
  formData.workHours = undefined
  formData.attendanceType = 'normal'
  formData.overtimeHours = undefined
  formRef.value?.resetFields()
}

onMounted(() => {
  loadTableData()
})
</script>

<style scoped lang="scss">
.hrm-attendance-list-page {
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

      &--normal .stat-value {
        color: var(--el-color-success);
      }

      &--absent .stat-value {
        color: var(--el-color-danger);
      }

      &--overtime .stat-value {
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
