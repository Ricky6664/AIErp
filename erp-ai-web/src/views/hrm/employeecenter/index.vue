<template>
  <div class="hrm-employeecenter-page">
    <!-- 快捷统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">{{ $t('hrm.employee.total') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--active">
          <div class="stat-value">{{ stats.active }}</div>
          <div class="stat-label">{{ $t('hrm.employee.active') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--new">
          <div class="stat-value">{{ stats.newThisMonth }}</div>
          <div class="stat-label">{{ $t('hrm.employee.newThisMonth') }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--dept">
          <div class="stat-value">{{ stats.departments }}</div>
          <div class="stat-label">{{ $t('hrm.employee.departments') }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- P03 主从列表页基座 -->
    <PageP03MasterList
      view-id="hrm-employeecenter"
      page-type="P03"
      :config="pageConfig"
      :permissions="[]"
    >
      <!-- 查询区 -->
      <template #query-panel>
        <el-form :model="searchForm" :inline="true" class="query-form" @submit.prevent>
          <el-form-item :label="$t('hrm.employee.name')">
            <el-input
              v-model="searchForm.name"
              :placeholder="$t('hrm.employee.namePlaceholder')"
              clearable
              style="width: 180px"
              @input="handleSearchDebounced"
            />
          </el-form-item>
          <el-form-item :label="$t('hrm.employee.department')">
            <el-select
              v-model="searchForm.departmentId"
              :placeholder="$t('common.pleaseSelect')"
              clearable
              style="width: 180px"
              @change="handleSearch"
            >
              <el-option
                v-for="dept in deptOptions"
                :key="dept.value"
                :label="dept.label"
                :value="dept.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('hrm.employee.status')">
            <el-select
              v-model="searchForm.employeeStatus"
              :placeholder="$t('common.pleaseSelect')"
              clearable
              style="width: 160px"
              @change="handleSearch"
            >
              <el-option :label="$t('hrm.employee.statusActive')" value="在职" />
              <el-option :label="$t('hrm.employee.statusLeave')" value="离职" />
              <el-option :label="$t('hrm.employee.statusProbation')" value="试用期" />
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
      </template>

      <!-- 操作栏 -->
      <template #action-bar>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          {{ $t('hrm.employee.add') }}
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          {{ $t('common.export') }}
        </el-button>
      </template>

      <!-- 主列表 -->
      <template #list-content>
        <div class="list-wrap">
          <div class="list-header">
            <span class="list-title">{{
              $t('hrm.employee.recordCount', { total: pagination.total })
            }}</span>
          </div>
          <vxe-table
            :loading="tableLoading"
            :data="tableData"
            :scroll-y="{ enabled: true, gt: 100 }"
            max-height="420"
            stripe
            highlight-current-row
            style="width: 100%"
            @current-row-change="handleRowChange"
          >
            <vxe-column field="employeeNo" :title="$t('hrm.employee.employeeNo')" width="110" />
            <vxe-column field="name" :title="$t('hrm.employee.name')" min-width="90" />
            <vxe-column field="gender" :title="$t('hrm.employee.gender')" width="60" align="center">
              <template #default="{ row }">
                <span>{{ genderLabel(row.gender) }}</span>
              </template>
            </vxe-column>
            <vxe-column field="phone" :title="$t('hrm.employee.phone')" width="120" />
            <vxe-column field="entryDate" :title="$t('hrm.employee.entryDate')" width="110" />
            <vxe-column
              field="employeeStatus"
              :title="$t('hrm.employee.status')"
              width="90"
              align="center"
            >
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.employeeStatus)" size="small">
                  {{ row.employeeStatus || '-' }}
                </el-tag>
              </template>
            </vxe-column>
            <vxe-column :title="$t('common.operate')" width="130" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleEdit(row)">
                  {{ $t('common.edit') }}
                </el-button>
                <el-button type="danger" link size="small" @click="handleDelete(row)">
                  {{ $t('common.delete') }}
                </el-button>
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
              small
              @current-change="loadTableData"
              @size-change="loadTableData"
            />
          </div>
        </div>
      </template>

      <!-- 从表标签页 -->
      <template #form-content>
        <div v-if="!selectedEmployee" class="detail-empty">
          <el-empty :description="$t('hrm.employee.clickRowHint')" />
        </div>
        <div v-else class="detail-tabs-wrap">
          <div class="detail-header">
            <span class="detail-title"
              >{{ selectedEmployee.name }} {{ $t('hrm.employee.detailTitle') }}</span
            >
          </div>
          <el-tabs v-model="activeTab" class="detail-tabs" @tab-change="handleTabChange">
            <!-- 基本信息 -->
            <el-tab-pane :label="$t('hrm.employee.tabBasic')" name="basic">
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item :label="$t('hrm.employee.employeeNo')">
                  {{ selectedEmployee.employeeNo }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.employee.name')">
                  {{ selectedEmployee.name }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.employee.gender')">
                  {{ genderLabel(selectedEmployee.gender) }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.employee.phone')">
                  {{ selectedEmployee.phone || '-' }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.employee.email')">
                  {{ selectedEmployee.email || '-' }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.employee.idCard')">
                  {{ maskIdCard(selectedEmployee.idCard) }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.employee.entryDate')">
                  {{ selectedEmployee.entryDate || '-' }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('hrm.employee.status')">
                  <el-tag :type="statusTagType(selectedEmployee.employeeStatus)" size="small">
                    {{ selectedEmployee.employeeStatus || '-' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>

            <!-- 档案 -->
            <el-tab-pane :label="$t('hrm.employee.tabArchive')" name="archive" lazy>
              <div v-loading="archiveLoading" class="tab-data-wrap">
                <template v-if="archiveList.length">
                  <vxe-table
                    :data="archiveList"
                    border
                    size="small"
                    max-height="300"
                    style="width: 100%"
                  >
                    <vxe-column
                      field="education"
                      :title="$t('hrm.archive.education')"
                      min-width="80"
                    />
                    <vxe-column field="major" :title="$t('hrm.archive.major')" min-width="100" />
                    <vxe-column field="school" :title="$t('hrm.archive.school')" min-width="120" />
                    <vxe-column
                      field="emergencyContact"
                      :title="$t('hrm.archive.emergencyContact')"
                      min-width="100"
                    />
                    <vxe-column
                      field="emergencyPhone"
                      :title="$t('hrm.archive.emergencyPhone')"
                      min-width="120"
                    />
                    <vxe-column
                      field="bankCardNumber"
                      :title="$t('hrm.archive.bankCardNumber')"
                      min-width="130"
                    />
                    <vxe-column
                      field="archiveDate"
                      :title="$t('hrm.archive.archiveDate')"
                      width="110"
                    />
                  </vxe-table>
                </template>
                <el-empty v-else :description="$t('hrm.employee.archiveHint')" />
              </div>
            </el-tab-pane>

            <!-- 考勤 -->
            <el-tab-pane :label="$t('hrm.employee.tabAttendance')" name="attendance" lazy>
              <div v-loading="attendanceLoading" class="tab-data-wrap">
                <template v-if="attendanceList.length">
                  <vxe-table
                    :data="attendanceList"
                    border
                    size="small"
                    max-height="300"
                    style="width: 100%"
                  >
                    <vxe-column
                      field="attendanceDate"
                      :title="$t('hrm.attendance.attendanceDate')"
                      width="120"
                    />
                    <vxe-column
                      field="checkInTime"
                      :title="$t('hrm.attendance.checkInTime')"
                      width="100"
                    />
                    <vxe-column
                      field="checkOutTime"
                      :title="$t('hrm.attendance.checkOutTime')"
                      width="100"
                    />
                    <vxe-column
                      field="workHours"
                      :title="$t('hrm.attendance.workHours')"
                      width="90"
                      align="center"
                    />
                    <vxe-column
                      field="overtimeHours"
                      :title="$t('hrm.attendance.overtimeHours')"
                      width="100"
                      align="center"
                    />
                    <vxe-column
                      field="attendanceType"
                      :title="$t('hrm.attendance.attendanceType')"
                      width="90"
                      align="center"
                    />
                  </vxe-table>
                </template>
                <el-empty v-else :description="$t('hrm.employee.attendanceHint')" />
              </div>
            </el-tab-pane>

            <!-- 薪资 -->
            <el-tab-pane :label="$t('hrm.employee.tabSalary')" name="salary" lazy>
              <div v-loading="salaryLoading" class="tab-data-wrap">
                <template v-if="salaryList.length">
                  <vxe-table
                    :data="salaryList"
                    border
                    size="small"
                    max-height="300"
                    style="width: 100%"
                  >
                    <vxe-column
                      field="salaryMonth"
                      :title="$t('hrm.salary.salaryMonth')"
                      width="110"
                    />
                    <vxe-column
                      field="baseSalary"
                      :title="$t('hrm.salary.baseSalary')"
                      width="100"
                      align="right"
                    />
                    <vxe-column
                      field="overtimePay"
                      :title="$t('hrm.salary.overtimePay')"
                      width="100"
                      align="right"
                    />
                    <vxe-column
                      field="bonus"
                      :title="$t('hrm.salary.bonus')"
                      width="90"
                      align="right"
                    />
                    <vxe-column
                      field="allowance"
                      :title="$t('hrm.salary.allowance')"
                      width="90"
                      align="right"
                    />
                    <vxe-column
                      field="deduction"
                      :title="$t('hrm.salary.deduction')"
                      width="90"
                      align="right"
                    />
                    <vxe-column
                      field="netSalary"
                      :title="$t('hrm.salary.netSalary')"
                      width="100"
                      align="right"
                    />
                  </vxe-table>
                </template>
                <el-empty v-else :description="$t('hrm.employee.salaryHint')" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </template>
    </PageP03MasterList>

    <!-- 编辑弹窗（模态覆盖层，置于组件外部） -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? $t('hrm.employee.editTitle') : $t('hrm.employee.addTitle')"
      width="900px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <div class="p06-form-wrap">
        <el-divider content-position="left">{{ $t('hrm.employee.baseInfo') }}</el-divider>
        <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.employeeNo')" prop="employeeNo">
                <el-input
                  v-model="formData.employeeNo"
                  :placeholder="$t('hrm.employee.employeeNoPlaceholder')"
                  :disabled="isEdit"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.name')" prop="name">
                <el-input
                  v-model="formData.name"
                  :placeholder="$t('hrm.employee.namePlaceholder')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.gender')">
                <el-select v-model="formData.gender" style="width: 100%">
                  <el-option :label="$t('hrm.employee.genderMale')" value="男" />
                  <el-option :label="$t('hrm.employee.genderFemale')" value="女" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.idCard')" prop="idCard">
                <el-input
                  v-model="formData.idCard"
                  :placeholder="$t('hrm.employee.idCardPlaceholder')"
                  maxlength="18"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.phone')" prop="phone">
                <el-input
                  v-model="formData.phone"
                  :placeholder="$t('hrm.employee.phonePlaceholder')"
                  maxlength="11"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.email')" prop="email">
                <el-input
                  v-model="formData.email"
                  :placeholder="$t('hrm.employee.emailPlaceholder')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.department')">
                <el-select
                  v-model="formData.departmentId"
                  style="width: 100%"
                  :placeholder="$t('common.pleaseSelect')"
                  clearable
                >
                  <el-option
                    v-for="dept in deptOptions"
                    :key="dept.value"
                    :label="dept.label"
                    :value="dept.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.position')">
                <el-input
                  v-model="formData.positionId"
                  :placeholder="$t('hrm.employee.positionPlaceholder')"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.entryDate')">
                <el-date-picker
                  v-model="formData.entryDate"
                  type="date"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('hrm.employee.status')">
                <el-select v-model="formData.employeeStatus" style="width: 100%">
                  <el-option :label="$t('hrm.employee.statusActive')" value="在职" />
                  <el-option :label="$t('hrm.employee.statusLeave')" value="离职" />
                  <el-option :label="$t('hrm.employee.statusProbation')" value="试用期" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <el-divider content-position="left">{{ $t('hrm.employee.archiveInfo') }}</el-divider>
        <div class="archive-table-wrap">
          <div class="archive-table-header">
            <span class="archive-table-title">{{ $t('hrm.employee.archiveList') }}</span>
            <el-button type="primary" size="small" @click="addArchiveRow">
              + {{ $t('hrm.employee.addArchive') }}
            </el-button>
          </div>
          <vxe-table
            :data="formArchiveList"
            :edit-config="{ trigger: 'click', mode: 'cell' }"
            border
            size="small"
            max-height="300"
            style="width: 100%"
          >
            <vxe-column
              field="education"
              :title="$t('hrm.employee.archiveEducation')"
              min-width="100"
              :edit-render="{ name: 'input' }"
            />
            <vxe-column
              field="major"
              :title="$t('hrm.employee.archiveMajor')"
              min-width="120"
              :edit-render="{ name: 'input' }"
            />
            <vxe-column
              field="school"
              :title="$t('hrm.employee.archiveSchool')"
              min-width="140"
              :edit-render="{ name: 'input' }"
            />
            <vxe-column
              field="emergencyContact"
              :title="$t('hrm.employee.archiveEmergencyContact')"
              min-width="110"
              :edit-render="{ name: 'input' }"
            />
            <vxe-column
              field="bankCardNo"
              :title="$t('hrm.employee.archiveBankCardNo')"
              min-width="140"
              :edit-render="{ name: 'input' }"
            />
            <vxe-column :title="$t('common.operate')" width="80" align="center" fixed="right">
              <template #default="{ rowIndex }">
                <el-button type="danger" link size="small" @click="removeArchiveRow(rowIndex)">
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </vxe-column>
          </vxe-table>
        </div>
      </div>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import PageP03MasterList from '@/components/page-base/PageP03MasterList.vue'
import type { MasterListPageConfig } from '@/types/page-base.d.ts'
import {
  getEmployeePageApi,
  createEmployeeApi,
  updateEmployeeApi,
  deleteEmployeeApi,
  getEmployeeByIdApi,
  type EmployeeVO,
  type EmployeeQueryDTO,
  type EmployeeCreateDTO,
  type EmployeeArchiveDTO
} from '@/api/modules/hrm-employee'
import {
  getEmployeeArchivePageApi,
  type EmployeeArchiveVO,
  type EmployeeArchiveQueryDTO
} from '@/api/modules/hrm-archive'
import {
  getAttendancePageApi,
  type AttendanceVO,
  type AttendanceQueryDTO
} from '@/api/modules/hrm-attendance'
import { getSalaryPageApi, type SalaryVO, type SalaryQueryDTO } from '@/api/modules/hrm-salary'
import { getDeptTree } from '@/api/modules/system'
import type { DeptTreeNode } from '@/api/modules/user'

// ========== P03 页面配置 ==========
const pageConfig = ref<MasterListPageConfig>({
  title: '员工中心',
  showQueryPanel: true,
  showActionBar: true,
  listWidthPercent: 45
})

// ========== 表单 / 表格 ==========
const formRef = ref<FormInstance>()
const tableLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const selectedEmployee = ref<EmployeeVO | null>(null)
const activeTab = ref('basic')
const tableData = ref<EmployeeVO[]>([])

// ========== 从表标签页数据 ==========
const archiveLoading = ref(false)
const attendanceLoading = ref(false)
const salaryLoading = ref(false)
const archiveList = ref<EmployeeArchiveVO[]>([])
const attendanceList = ref<AttendanceVO[]>([])
const salaryList = ref<SalaryVO[]>([])

let debounceTimer: ReturnType<typeof setTimeout> | null = null

const searchForm = reactive<EmployeeQueryDTO>({
  pageNum: 1,
  pageSize: 20,
  name: '',
  employeeStatus: '',
  departmentId: undefined
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const stats = reactive({
  total: 0,
  active: 0,
  newThisMonth: 0,
  departments: 0
})

const deptOptions = ref<{ label: string; value: number }[]>([])

// 编辑弹窗中的档案子表数据
const formArchiveList = ref<EmployeeArchiveDTO[]>([])

const formData = reactive<EmployeeCreateDTO>({
  employeeNo: '',
  name: '',
  gender: '男',
  idCard: '',
  phone: '',
  email: '',
  departmentId: undefined,
  positionId: undefined,
  entryDate: '',
  employeeStatus: '在职',
  archives: []
})

const formRules: FormRules = {
  employeeNo: [
    { required: true, message: '工号不能为空', trigger: 'blur' },
    { max: 20, message: '工号最长20个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '姓名不能为空', trigger: 'blur' },
    { max: 50, message: '姓名最长50个字符', trigger: 'blur' }
  ],
  idCard: [
    {
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
      message: '身份证号格式不正确',
      trigger: 'blur'
    }
  ],
  phone: [{ pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

// ========== 数据加载 ==========
async function loadTableData(): Promise<void> {
  tableLoading.value = true
  try {
    const query: EmployeeQueryDTO = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      name: searchForm.name || undefined,
      employeeStatus: searchForm.employeeStatus || undefined,
      departmentId: searchForm.departmentId
    }
    const res = await getEmployeePageApi(query)
    tableData.value = res.records || []
    pagination.total = res.total || 0
    updateStats()
  } catch {
    ElMessage.error('加载员工列表失败')
  } finally {
    tableLoading.value = false
  }
}

function updateStats(): void {
  stats.total = pagination.total
  stats.active = tableData.value.filter((r) => r.employeeStatus === '在职').length
  stats.newThisMonth = tableData.value.filter((r) => {
    if (!r.entryDate) return false
    const now = new Date()
    const entry = new Date(r.entryDate)
    return entry.getMonth() === now.getMonth() && entry.getFullYear() === now.getFullYear()
  }).length
  const deptSet = new Set(tableData.value.map((r) => r.departmentId).filter(Boolean))
  stats.departments = deptSet.size
}

// ========== 从表标签页数据加载 ==========
async function loadArchiveData(): Promise<void> {
  if (!selectedEmployee.value) return
  archiveLoading.value = true
  try {
    const query: EmployeeArchiveQueryDTO = {
      employeeName: selectedEmployee.value.name,
      pageNum: 1,
      pageSize: 100
    }
    const res = await getEmployeeArchivePageApi(query)
    archiveList.value = res.records || []
  } catch {
    archiveList.value = []
  } finally {
    archiveLoading.value = false
  }
}

async function loadAttendanceData(): Promise<void> {
  if (!selectedEmployee.value) return
  attendanceLoading.value = true
  try {
    const query: AttendanceQueryDTO = {
      employeeId: selectedEmployee.value.id,
      pageNum: 1,
      pageSize: 100
    }
    const res = await getAttendancePageApi(query)
    attendanceList.value = res.records || []
  } catch {
    attendanceList.value = []
  } finally {
    attendanceLoading.value = false
  }
}

async function loadSalaryData(): Promise<void> {
  if (!selectedEmployee.value) return
  salaryLoading.value = true
  try {
    const query: SalaryQueryDTO = {
      employeeId: selectedEmployee.value.id,
      pageNum: 1,
      pageSize: 100
    }
    const res = await getSalaryPageApi(query)
    salaryList.value = res.records || []
  } catch {
    salaryList.value = []
  } finally {
    salaryLoading.value = false
  }
}

function handleTabChange(tabName: string | number): void {
  const name = String(tabName)
  switch (name) {
    case 'archive':
      if (archiveList.value.length === 0) loadArchiveData()
      break
    case 'attendance':
      if (attendanceList.value.length === 0) loadAttendanceData()
      break
    case 'salary':
      if (salaryList.value.length === 0) loadSalaryData()
      break
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
  searchForm.name = ''
  searchForm.employeeStatus = ''
  searchForm.departmentId = undefined
  handleSearch()
}

// ========== 导出 ==========
function handleExport(): void {
  ElMessage.info('导出功能开发中')
}

// ========== 行选择 ==========
function handleRowChange({ row }: { row: EmployeeVO | null }): void {
  selectedEmployee.value = row
  if (row) {
    activeTab.value = 'basic'
    // 重置从表缓存，让 tab-change 重新触发加载
    archiveList.value = []
    attendanceList.value = []
    salaryList.value = []
  }
}

// ========== CRUD操作 ==========
function handleCreate(): void {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: EmployeeVO): void {
  isEdit.value = true
  editingId.value = row.id
  loadEmployeeDetail(row.id)
}

async function loadEmployeeDetail(id: number): Promise<void> {
  try {
    const detail = await getEmployeeByIdApi(id)
    formData.employeeNo = detail.employeeNo
    formData.name = detail.name
    formData.gender = detail.gender || '男'
    formData.idCard = detail.idCard || ''
    formData.phone = detail.phone || ''
    formData.email = detail.email || ''
    formData.departmentId = detail.departmentId
    formData.positionId = detail.positionId
    formData.entryDate = detail.entryDate || ''
    formData.employeeStatus = detail.employeeStatus || '在职'
    formArchiveList.value = (detail as any).archives || []
    dialogVisible.value = true
  } catch {
    ElMessage.error('加载员工详情失败')
  }
}

async function handleDelete(row: EmployeeVO): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认删除员工「${row.name}」？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteEmployeeApi(row.id)
    ElMessage.success('删除成功')
    if (selectedEmployee.value?.id === row.id) {
      selectedEmployee.value = null
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
    const submitData = { ...formData, archives: formArchiveList.value }
    if (isEdit.value && editingId.value) {
      await updateEmployeeApi(editingId.value, { ...submitData, id: editingId.value })
      ElMessage.success('更新成功')
    } else {
      await createEmployeeApi(submitData)
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
  formData.employeeNo = ''
  formData.name = ''
  formData.gender = '男'
  formData.idCard = ''
  formData.phone = ''
  formData.email = ''
  formData.departmentId = undefined
  formData.positionId = undefined
  formData.entryDate = ''
  formData.employeeStatus = '在职'
  formArchiveList.value = []
  formRef.value?.resetFields()
}

// ========== 编辑弹窗中的档案子表 ==========
function addArchiveRow(): void {
  formArchiveList.value.push({
    education: '',
    major: '',
    school: '',
    emergencyContact: '',
    bankCardNo: ''
  })
}

function removeArchiveRow(index: number): void {
  formArchiveList.value.splice(index, 1)
}

// ========== 工具函数 ==========
function genderLabel(gender: string): string {
  if (gender === '男') return '男'
  if (gender === '女') return '女'
  return gender || '-'
}

function statusTagType(status: string): 'success' | 'danger' | 'warning' | 'info' {
  if (status === '在职') return 'success'
  if (status === '离职') return 'danger'
  if (status === '试用期') return 'warning'
  return 'info'
}

function maskIdCard(idCard: string | undefined): string {
  if (!idCard) return '-'
  if (idCard.length < 10) return idCard
  return idCard.substring(0, 6) + '****' + idCard.substring(idCard.length - 4)
}

// ========== 部门选项 ==========
async function loadDeptOptions(): Promise<void> {
  try {
    const tree = await getDeptTree()
    const flatList: { label: string; value: number }[] = []
    function flatten(nodes: DeptTreeNode[]): void {
      for (const n of nodes) {
        flatList.push({ label: n.name, value: n.id })
        if (n.children?.length) flatten(n.children)
      }
    }
    flatten(tree || [])
    deptOptions.value = flatList
  } catch {
    // 静默失败
  }
}

// ========== 生命周期 ==========
onMounted(() => {
  loadTableData()
  loadDeptOptions()
})
</script>

<style scoped lang="scss">
.hrm-employeecenter-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

// 统计卡片
.stats-row {
  flex-shrink: 0;

  .stat-card {
    text-align: center;

    .stat-value {
      font-size: 28px;
      font-weight: 700;
      color: var(--el-text-color-primary);
    }

    .stat-label {
      margin-top: 4px;
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }

    &--active .stat-value {
      color: var(--el-color-success);
    }

    &--new .stat-value {
      color: var(--el-color-primary);
    }

    &--dept .stat-value {
      color: var(--el-color-warning);
    }
  }
}

// 查询表单
.query-form {
  width: 100%;
}

// 列表区域
.list-wrap {
  display: flex;
  flex-direction: column;
  height: 100%;

  .list-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;

    .list-title {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }

  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
    flex-shrink: 0;
  }
}

// 详情区
.detail-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.detail-tabs-wrap {
  .detail-header {
    margin-bottom: 8px;

    .detail-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}

.tab-data-wrap {
  min-height: 200px;
}

// 编辑弹窗
.p06-form-wrap {
  .el-divider {
    margin: 8px 0 16px;
  }

  .archive-table-wrap {
    .archive-table-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 8px;

      .archive-table-title {
        font-size: 13px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}
</style>
