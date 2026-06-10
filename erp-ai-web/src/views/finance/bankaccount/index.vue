<template>
  <PageP04SimpleList
    view-id="bankaccount-list"
    page-type="P04"
    :config="pageConfig"
    :permissions="permissions"
  >
    <!-- 统计卡片 -->
    <template #extra-area>
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
    </template>

    <!-- 查询区 -->
    <template #query-panel>
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="账户名称">
          <el-input
            v-model="searchForm.accountName"
            placeholder="请输入账户名称"
            clearable
            style="width: 200px"
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="开户银行">
          <el-input
            v-model="searchForm.bankName"
            placeholder="请输入开户银行"
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
    </template>

    <!-- 操作栏 -->
    <template #action-bar>
      <div class="action-bar-left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增银行账户</el-button>
      </div>
      <div class="action-bar-right">
        <span class="record-count">{{ stats.total }} 条记录</span>
      </div>
    </template>

    <!-- 数据表格 -->
    <template #main-content>
      <vxe-table
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        border
        style="width: 100%"
      >
        <vxe-column type="seq" title="序号" width="60" align="center" />
        <vxe-column field="accountName" title="账户名称" min-width="140" />
        <vxe-column field="bankAccountNo" title="银行账号" min-width="160" />
        <vxe-column field="bankName" title="开户银行" min-width="140" />
        <vxe-column field="bankBranch" title="开户支行" min-width="140" />
        <vxe-column field="accountType" title="账户类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="accountTypeTag(row.accountType)" size="small">
              {{ accountTypeLabel(row.accountType) }}
            </el-tag>
          </template>
        </vxe-column>
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
              title="确认删除该银行账户？"
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
      <div class="pagination-box">
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
    </template>
  </PageP04SimpleList>

  <!-- 新增/编辑弹窗（弹窗留在外部） -->
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑银行账户' : '新增银行账户'"
    width="600px"
    destroy-on-close
    @closed="handleDialogClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" @submit.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="账户名称" prop="accountName">
            <el-input v-model="formData.accountName" placeholder="请输入账户名称" maxlength="100" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="银行账号" prop="bankAccountNo">
            <el-input
              v-model="formData.bankAccountNo"
              placeholder="请输入银行账号"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开户银行" prop="bankName">
            <el-input v-model="formData.bankName" placeholder="请输入开户银行" maxlength="100" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开户支行" prop="bankBranch">
            <el-input v-model="formData.bankBranch" placeholder="请输入开户支行" maxlength="100" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="币种" prop="currencyId">
            <el-select
              v-model="formData.currencyId"
              placeholder="请选择币种"
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="item in currencyOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="账户类型" prop="accountType">
            <el-select
              v-model="formData.accountType"
              placeholder="请选择账户类型"
              style="width: 100%"
            >
              <el-option label="基本户" value="BASIC" />
              <el-option label="一般户" value="GENERAL" />
              <el-option label="专户" value="SPECIAL" />
              <el-option label="临时户" value="TEMP" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-switch
              v-model="formData.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="停用"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit"> 确认 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import {
  getBankAccountPageApi,
  getBankAccountByIdApi,
  createBankAccountApi,
  updateBankAccountApi,
  updateBankAccountStatusApi,
  deleteBankAccountApi,
  checkBankAccountNoApi,
  type BankAccountVO,
  type BankAccountSaveDTO
} from '@/api/modules/finance-bankaccount'

const pageConfig: SimpleListPageConfig = {
  title: '银行账户',
  showQueryPanel: true,
  showActionBar: true
}
const permissions = [
  'bankaccount:view',
  'bankaccount:create',
  'bankaccount:edit',
  'bankaccount:delete'
]

const tableLoading = ref(false)
const tableData = ref<BankAccountVO[]>([])

const searchForm = reactive({
  accountName: '',
  bankName: '',
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

const currencyOptions = [
  { label: 'CNY - 人民币', value: 1 },
  { label: 'USD - 美元', value: 2 },
  { label: 'EUR - 欧元', value: 3 },
  { label: 'JPY - 日元', value: 4 },
  { label: 'GBP - 英镑', value: 5 },
  { label: 'HKD - 港币', value: 6 }
]

const initFormData = (): BankAccountSaveDTO => ({
  accountName: '',
  bankAccountNo: '',
  bankName: '',
  bankBranch: '',
  currencyId: 1,
  accountType: 'BASIC',
  status: 1
})

const formData = reactive<BankAccountSaveDTO>(initFormData())

const formRules: FormRules = {
  accountName: [
    { required: true, message: '请输入账户名称', trigger: 'blur' },
    { max: 100, message: '账户名称最长100个字符', trigger: 'blur' }
  ],
  bankAccountNo: [
    { required: true, message: '请输入银行账号', trigger: 'blur' },
    { max: 50, message: '银行账号最长50个字符', trigger: 'blur' },
    { validator: validateBankAccountNo, trigger: 'blur' }
  ],
  bankName: [
    { required: true, message: '请输入开户银行', trigger: 'blur' },
    { max: 100, message: '开户银行最长100个字符', trigger: 'blur' }
  ],
  currencyId: [{ required: true, message: '请选择币种', trigger: 'change' }],
  accountType: [{ required: true, message: '请选择账户类型', trigger: 'change' }]
}

async function validateBankAccountNo(
  _rule: unknown,
  value: string,
  callback: (error?: Error) => void
): Promise<void> {
  if (!value) {
    callback()
    return
  }
  try {
    const exists = await checkBankAccountNoApi(value)
    if (exists) {
      callback(new Error('银行账号已存在'))
    } else {
      callback()
    }
  } catch {
    callback()
  }
}

function handleAdd(): void {
  isEdit.value = false
  editId.value = null
  Object.assign(formData, initFormData())
  dialogVisible.value = true
}

async function handleEdit(row: BankAccountVO): Promise<void> {
  isEdit.value = true
  editId.value = row.id
  try {
    const detail = await getBankAccountByIdApi(row.id)
    formData.accountName = detail.accountName
    formData.bankAccountNo = detail.bankAccountNo
    formData.bankName = detail.bankName
    formData.bankBranch = detail.bankBranch || ''
    formData.currencyId = detail.currencyId
    formData.accountType = detail.accountType
    formData.status = detail.status
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取银行账户详情失败')
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateBankAccountApi(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createBankAccountApi({ ...formData })
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

async function handleToggleStatus(row: BankAccountVO): Promise<void> {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '停用' : '启用'
  try {
    await updateBankAccountStatusApi(row.id, newStatus)
    ElMessage.success(`${actionText}成功`)
    await loadData()
  } catch {
    ElMessage.error(`${actionText}失败`)
  }
}

const accountTypeMap: Record<string, string> = {
  BASIC: '基本户',
  GENERAL: '一般户',
  SPECIAL: '专户',
  TEMP: '临时户'
}

function accountTypeLabel(type: string): string {
  return accountTypeMap[type] || type
}

function accountTypeTag(type: string): 'success' | 'warning' | 'info' {
  if (type === 'BASIC') return 'success'
  if (type === 'GENERAL') return 'info'
  if (type === 'SPECIAL') return 'warning'
  return 'info'
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
  searchForm.accountName = ''
  searchForm.bankName = ''
  searchForm.status = undefined
  pagination.pageNum = 1
  loadData()
}

async function loadData(): Promise<void> {
  tableLoading.value = true
  try {
    const res = await getBankAccountPageApi({
      accountName: searchForm.accountName || undefined,
      bankName: searchForm.bankName || undefined,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.list || []
    pagination.total = res.total || 0
    updateStats(res.list || [], res.total || 0)
  } catch {
    ElMessage.error('加载银行账户列表失败')
  } finally {
    tableLoading.value = false
  }
}

function updateStats(list: BankAccountVO[], total: number): void {
  stats.total = total
  stats.enabled = list.filter((item) => item.status === 1).length
  stats.disabled = list.filter((item) => item.status === 0).length
}

async function handleDelete(row: BankAccountVO): Promise<void> {
  try {
    await deleteBankAccountApi(row.id)
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

  &--enabled .stat-value {
    color: var(--el-color-success);
  }
  &--disabled .stat-value {
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

.pagination-box {
  display: flex;
  justify-content: flex-end;
  padding: 12px 0 0;
}
</style>
