<template>
  <PageP04SimpleList
    view-id="hrm-recruitment-list"
    page-type="P04"
    :config="pageConfig"
    :permissions="permissions"
  >
    <!-- 统计卡片 -->
    <template #extra-area>
      <el-row :gutter="16" class="stats-row">
        <el-col :xs="24" :sm="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">{{ $t('hrm.recruitment.totalRecruitments') }}</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="6">
          <el-card shadow="hover" class="stat-card stat-card--recruiting">
            <div class="stat-value">{{ stats.recruiting }}</div>
            <div class="stat-label">{{ $t('hrm.recruitment.recruiting') }}</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="6">
          <el-card shadow="hover" class="stat-card stat-card--completed">
            <div class="stat-value">{{ stats.completed }}</div>
            <div class="stat-label">{{ $t('hrm.recruitment.completed') }}</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="6">
          <el-card shadow="hover" class="stat-card stat-card--cancelled">
            <div class="stat-value">{{ stats.cancelled }}</div>
            <div class="stat-label">{{ $t('hrm.recruitment.cancelled') }}</div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 查询区 -->
    <template #query-panel>
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item :label="$t('hrm.recruitment.positionName')">
          <el-input
            v-model="searchForm.positionName"
            :placeholder="$t('hrm.recruitment.positionNamePlaceholder')"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item :label="$t('hrm.recruitment.departmentName')">
          <el-input
            v-model="searchForm.departmentName"
            :placeholder="$t('hrm.recruitment.departmentNamePlaceholder')"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item :label="$t('hrm.recruitment.recruitStatus')">
          <el-select
            v-model="searchForm.recruitStatus"
            :placeholder="$t('common.pleaseSelect')"
            clearable
            style="width: 160px"
            @change="handleSearch"
          >
            <el-option
              v-for="item in statusOptions"
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
    </template>

    <!-- 操作栏 -->
    <template #action-bar>
      <div class="action-bar-left">
        <el-button type="primary" @click="handleCreate">
          {{ $t('hrm.recruitment.add') }}
        </el-button>
      </div>
      <div class="action-bar-right">
        <span class="record-count">{{
          $t('hrm.recruitment.recordCount', { total: pagination.total })
        }}</span>
      </div>
    </template>

    <!-- 数据表格 -->
    <template #main-content>
      <vxe-table
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        stripe
        style="width: 100%"
      >
        <vxe-column
          field="positionName"
          :title="$t('hrm.recruitment.positionName')"
          min-width="140"
        />
        <vxe-column
          field="departmentName"
          :title="$t('hrm.recruitment.departmentName')"
          min-width="120"
          align="center"
        />
        <vxe-column
          field="recruitNum"
          :title="$t('hrm.recruitment.recruitNum')"
          width="100"
          align="center"
        />
        <vxe-column
          field="salaryRange"
          :title="$t('hrm.recruitment.salaryRange')"
          min-width="140"
          align="center"
        />
        <vxe-column
          field="requirements"
          :title="$t('hrm.recruitment.requirements')"
          min-width="180"
        >
          <template #default="{ row }">
            {{
              row.requirements?.length > 40
                ? row.requirements.substring(0, 40) + '...'
                : row.requirements || '-'
            }}
          </template>
        </vxe-column>
        <vxe-column
          field="recruitStatus"
          :title="$t('hrm.recruitment.recruitStatus')"
          width="110"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="
                row.recruitStatus === 'recruiting'
                  ? 'success'
                  : row.recruitStatus === 'completed'
                    ? 'primary'
                    : 'danger'
              "
              size="small"
            >
              {{
                row.recruitStatus === 'recruiting'
                  ? $t('hrm.recruitment.statusRecruiting')
                  : row.recruitStatus === 'completed'
                    ? $t('hrm.recruitment.statusCompleted')
                    : $t('hrm.recruitment.statusCancelled')
              }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column field="deadline" :title="$t('hrm.recruitment.deadline')" width="120" sortable />
        <vxe-column :title="$t('common.operate')" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              {{ $t('common.edit') }}
            </el-button>
            <el-button
              :type="row.recruitStatus === 'recruiting' ? 'warning' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{
                row.recruitStatus === 'recruiting'
                  ? $t('hrm.recruitment.disable')
                  : $t('hrm.recruitment.enable')
              }}
            </el-button>
            <el-popconfirm
              :title="$t('hrm.recruitment.deleteConfirm')"
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
    </template>
  </PageP04SimpleList>

  <!-- 编辑弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? $t('hrm.recruitment.editTitle') : $t('hrm.recruitment.addTitle')"
    width="600px"
    :close-on-click-modal="false"
    @closed="resetForm"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item :label="$t('hrm.recruitment.positionName')" prop="positionName">
            <el-input
              v-model="formData.positionName"
              :placeholder="$t('hrm.recruitment.positionNamePlaceholder')"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('hrm.recruitment.departmentName')" prop="departmentName">
            <el-select
              v-model="formData.departmentName"
              :placeholder="$t('common.pleaseSelect')"
              clearable
              filterable
              style="width: 100%"
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
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item :label="$t('hrm.recruitment.recruitNum')">
            <el-input-number
              v-model="formData.recruitNum"
              :min="0"
              :placeholder="$t('hrm.recruitment.recruitNumPlaceholder')"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('hrm.recruitment.salaryRange')">
            <el-input
              v-model="formData.salaryRange"
              :placeholder="$t('hrm.recruitment.salaryRangePlaceholder')"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item :label="$t('hrm.recruitment.requirements')">
            <el-input
              v-model="formData.requirements"
              type="textarea"
              :rows="3"
              :placeholder="$t('hrm.recruitment.requirementsPlaceholder')"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item :label="$t('hrm.recruitment.recruitStatus')">
            <el-select
              v-model="formData.recruitStatus"
              style="width: 100%"
              :placeholder="$t('common.pleaseSelect')"
            >
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('hrm.recruitment.deadline')" prop="deadline">
            <el-date-picker
              v-model="formData.deadline"
              type="date"
              style="width: 100%"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
            />
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
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import {
  getRecruitmentPageApi,
  getRecruitmentByIdApi,
  createRecruitmentApi,
  updateRecruitmentApi,
  deleteRecruitmentApi,
  updateRecruitmentStatusApi,
  type RecruitmentVO,
  type RecruitmentQueryDTO,
  type RecruitmentCreateDTO
} from '@/api/modules/hrm-recruitment'
import { getDeptTree } from '@/api/modules/system'
import type { DeptTreeNode } from '@/api/modules/user'

const pageConfig: SimpleListPageConfig = {
  title: '招聘管理',
  showQueryPanel: true,
  showActionBar: true
}
const permissions = [
  'hrm:recruitment:view',
  'hrm:recruitment:create',
  'hrm:recruitment:edit',
  'hrm:recruitment:delete'
]

const formRef = ref<FormInstance>()
const tableLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const tableData = ref<RecruitmentVO[]>([])

let debounceTimer: ReturnType<typeof setTimeout> | null = null

const searchForm = reactive<RecruitmentQueryDTO>({
  pageNum: 1,
  pageSize: 20,
  positionName: '',
  departmentName: '',
  recruitStatus: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const stats = computed(() => {
  const total = pagination.total
  const recruiting = tableData.value.filter((r) => r.recruitStatus === 'recruiting').length
  const completed = tableData.value.filter((r) => r.recruitStatus === 'completed').length
  const cancelled = tableData.value.filter((r) => r.recruitStatus === 'cancelled').length
  return { total, recruiting, completed, cancelled }
})

const statusOptions = ref([
  { value: 'recruiting', label: '招聘中' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' }
])

const formData = reactive<RecruitmentCreateDTO & { id?: number }>({
  positionName: '',
  departmentName: '',
  recruitNum: 0,
  salaryRange: '',
  requirements: '',
  recruitStatus: 'recruiting',
  deadline: ''
})

const deptOptions = ref<{ label: string; value: string }[]>([])

const formRules: FormRules = {
  positionName: [
    { required: true, message: '招聘岗位不能为空', trigger: 'blur' },
    { max: 100, message: '岗位名称最长100个字符', trigger: 'blur' }
  ],
  departmentName: [{ required: true, message: '所属部门不能为空', trigger: 'change' }],
  recruitNum: [
    { required: true, message: '招聘人数不能为空', trigger: 'blur' },
    { type: 'number', min: 1, message: '招聘人数必须为正整数', trigger: 'blur' }
  ],
  deadline: [
    {
      validator: (_rule, value, callback) => {
        if (value) {
          const today = new Date()
          today.setHours(0, 0, 0, 0)
          const deadline = new Date(value)
          if (deadline < today) {
            callback(new Error('截止日期不得早于当前日期'))
            return
          }
        }
        callback()
      },
      trigger: 'change'
    }
  ]
}

async function loadTableData(): Promise<void> {
  tableLoading.value = true
  try {
    const query: RecruitmentQueryDTO = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      positionName: searchForm.positionName || undefined,
      departmentName: searchForm.departmentName || undefined,
      recruitStatus: searchForm.recruitStatus || undefined
    }
    const res = await getRecruitmentPageApi(query)
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch {
    ElMessage.error('加载招聘列表失败')
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
  searchForm.positionName = ''
  searchForm.departmentName = ''
  searchForm.recruitStatus = ''
  handleSearch()
}

function handleCreate(): void {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

async function handleEdit(row: RecruitmentVO): Promise<void> {
  isEdit.value = true
  editingId.value = row.id
  try {
    const detail = await getRecruitmentByIdApi(row.id)
    formData.positionName = detail.positionName
    formData.departmentName = detail.departmentName || ''
    formData.recruitNum = detail.recruitNum || 0
    formData.salaryRange = detail.salaryRange || ''
    formData.requirements = detail.requirements || ''
    formData.recruitStatus = detail.recruitStatus || 'recruiting'
    formData.deadline = detail.deadline || ''
  } catch {
    // Fallback to row data
    formData.positionName = row.positionName
    formData.departmentName = row.departmentName || ''
    formData.recruitNum = row.recruitNum || 0
    formData.salaryRange = row.salaryRange || ''
    formData.requirements = row.requirements || ''
    formData.recruitStatus = row.recruitStatus || 'recruiting'
    formData.deadline = row.deadline || ''
  }
  dialogVisible.value = true
}

async function handleDelete(row: RecruitmentVO): Promise<void> {
  try {
    await deleteRecruitmentApi(row.id)
    ElMessage.success('删除成功')
    loadTableData()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function handleToggleStatus(row: RecruitmentVO): Promise<void> {
  const newStatus = row.recruitStatus === 'recruiting' ? 'cancelled' : 'recruiting'
  const actionText = newStatus === 'cancelled' ? '停用' : '启用'
  try {
    await updateRecruitmentStatusApi(row.id, newStatus)
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
    if (isEdit.value && editingId.value) {
      await updateRecruitmentApi({ id: editingId.value, ...formData })
      ElMessage.success('更新成功')
    } else {
      await createRecruitmentApi(formData)
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
  formData.positionName = ''
  formData.departmentName = ''
  formData.recruitNum = 0
  formData.salaryRange = ''
  formData.requirements = ''
  formData.recruitStatus = 'recruiting'
  formData.deadline = ''
  formRef.value?.resetFields()
}

function disabledDate(time: Date): boolean {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time < today
}

async function loadDeptOptions(): Promise<void> {
  try {
    const tree = await getDeptTree()
    const flatList: { label: string; value: string }[] = []
    function flatten(nodes: DeptTreeNode[]): void {
      for (const n of nodes) {
        flatList.push({ label: n.name, value: n.name })
        if (n.children?.length) flatten(n.children)
      }
    }
    flatten(tree || [])
    deptOptions.value = flatList
  } catch {
    // 静默失败，部门数据不可用时不影响主流程
  }
}

onMounted(() => {
  loadTableData()
  loadDeptOptions()
})
</script>

<style scoped lang="scss">
.stats-row {
  margin-bottom: 0;
}

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

  &--recruiting .stat-value {
    color: var(--el-color-success);
  }

  &--completed .stat-value {
    color: var(--el-color-primary);
  }

  &--cancelled .stat-value {
    color: var(--el-color-danger);
  }
}

.action-bar-left {
  display: flex;
  gap: 8px;
}

.action-bar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.record-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
