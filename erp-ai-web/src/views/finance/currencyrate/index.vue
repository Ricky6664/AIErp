<template>
  <PageP04SimpleList
    view-id="currencyrate-list"
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
          <el-card shadow="hover" class="stat-card stat-card--today">
            <div class="stat-value">{{ stats.todayCount }}</div>
            <div class="stat-label">今日新增</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="stat-card stat-card--type">
            <div class="stat-value">{{ stats.typeCount }}</div>
            <div class="stat-label">汇率类型数</div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 查询区 -->
    <template #query-panel>
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="币种名称">
          <el-input
            v-model="searchForm.currencyName"
            placeholder="请输入币种名称"
            clearable
            style="width: 200px"
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="汇率类型">
          <el-select
            v-model="searchForm.rateType"
            placeholder="请选择汇率类型"
            clearable
            style="width: 160px"
            @change="handleSearch"
          >
            <el-option label="固定汇率" :value="1" />
            <el-option label="浮动汇率" :value="2" />
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增币种汇率</el-button>
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
        <vxe-column field="currencyCode" title="币种编码" min-width="120" />
        <vxe-column field="currencyName" title="币种名称" min-width="140" />
        <vxe-column field="currencySymbol" title="基准币种" width="100" align="center" />
        <vxe-column field="exchangeRate" title="汇率" width="140" align="right">
          <template #default="{ row }">
            <span>{{ formatRate(row.exchangeRate) }}</span>
          </template>
        </vxe-column>
        <vxe-column field="effectiveDate" title="汇率日期" width="130" align="center" />
        <vxe-column field="rateType" title="汇率类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="rateTypeTag(row.rateType)" size="small">
              {{ rateTypeLabel(row.rateType) }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column field="createTime" title="创建时间" width="180" sortable />
        <vxe-column title="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm
              title="确认删除该币种汇率？"
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
    :title="isEdit ? '编辑币种汇率' : '新增币种汇率'"
    width="600px"
    destroy-on-close
    @closed="handleDialogClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" @submit.prevent>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="币种编码" prop="currencyCode">
            <el-input
              v-model="formData.currencyCode"
              placeholder="请输入币种编码"
              :maxlength="20"
              :disabled="isEdit"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="币种名称" prop="currencyName">
            <el-input v-model="formData.currencyName" placeholder="请输入币种名称" maxlength="50" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="基准币种" prop="currencySymbol">
            <el-select
              v-model="formData.currencySymbol"
              placeholder="请选择基准币种"
              filterable
              allow-create
              style="width: 100%"
            >
              <el-option
                v-for="item in baseCurrencyOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="汇率" prop="exchangeRate">
            <el-input-number
              v-model="formData.exchangeRate"
              :precision="6"
              :min="0"
              placeholder="请输入汇率"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="汇率日期" prop="effectiveDate">
            <el-date-picker
              v-model="formData.effectiveDate"
              type="date"
              placeholder="请选择汇率日期"
              :disabled-date="disabledDate"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="汇率类型" prop="rateType">
            <el-select v-model="formData.rateType" placeholder="请选择汇率类型" style="width: 100%">
              <el-option label="固定汇率" :value="1" />
              <el-option label="浮动汇率" :value="2" />
            </el-select>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search, RefreshRight, Plus } from '@element-plus/icons-vue'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import {
  getCurrencyRatePageApi,
  getCurrencyRateByIdApi,
  createCurrencyRateApi,
  updateCurrencyRateApi,
  deleteCurrencyRateApi,
  checkCurrencyCodeApi,
  type CurrencyRateVO,
  type CurrencyRateSaveDTO
} from '@/api/modules/finance-currencyrate'

const pageConfig: SimpleListPageConfig = {
  title: '币种汇率',
  showQueryPanel: true,
  showActionBar: true
}
const permissions = [
  'currencyrate:view',
  'currencyrate:create',
  'currencyrate:edit',
  'currencyrate:delete'
]

const tableLoading = ref(false)
const tableData = ref<CurrencyRateVO[]>([])

const searchForm = reactive({
  currencyName: '',
  rateType: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const stats = reactive({
  total: 0,
  todayCount: 0,
  typeCount: 0
})

// ---------- 表单弹窗 ----------
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const baseCurrencyOptions = [
  { label: 'CNY - 人民币', value: 'CNY' },
  { label: 'USD - 美元', value: 'USD' },
  { label: 'EUR - 欧元', value: 'EUR' },
  { label: 'JPY - 日元', value: 'JPY' },
  { label: 'GBP - 英镑', value: 'GBP' },
  { label: 'HKD - 港币', value: 'HKD' },
  { label: 'KRW - 韩元', value: 'KRW' },
  { label: 'AUD - 澳元', value: 'AUD' }
]

const initFormData = (): CurrencyRateSaveDTO => ({
  currencyCode: '',
  currencyName: '',
  currencySymbol: '',
  exchangeRate: 1,
  rateType: 1,
  effectiveDate: ''
})

const formData = reactive<CurrencyRateSaveDTO>(initFormData())

const validateCurrencyCode = async (
  _rule: unknown,
  value: string,
  callback: (err?: Error) => void
) => {
  if (!value) {
    callback(new Error('请输入币种编码'))
    return
  }
  if (isEdit.value) {
    callback()
    return
  }
  try {
    const exists = await checkCurrencyCodeApi(value)
    if (exists) {
      callback(new Error('币种编码已存在'))
    } else {
      callback()
    }
  } catch {
    callback()
  }
}

const formRules: FormRules = {
  currencyCode: [
    { required: true, message: '请输入币种编码', trigger: 'blur' },
    { max: 20, message: '币种编码最长20个字符', trigger: 'blur' },
    { validator: validateCurrencyCode, trigger: 'blur' }
  ],
  currencyName: [
    { required: true, message: '请输入币种名称', trigger: 'blur' },
    { max: 50, message: '币种名称最长50个字符', trigger: 'blur' }
  ],
  exchangeRate: [{ required: true, message: '请输入汇率', trigger: 'blur' }],
  rateType: [{ required: true, message: '请选择汇率类型', trigger: 'change' }]
}

const maxDate = computed(() => {
  const d = new Date()
  d.setDate(d.getDate() + 30)
  return d
})

function disabledDate(date: Date): boolean {
  return date.getTime() > maxDate.value.getTime()
}

function handleAdd(): void {
  isEdit.value = false
  editId.value = null
  Object.assign(formData, initFormData())
  dialogVisible.value = true
}

async function handleEdit(row: CurrencyRateVO): Promise<void> {
  isEdit.value = true
  editId.value = row.id
  try {
    const detail = await getCurrencyRateByIdApi(row.id)
    formData.currencyCode = detail.currencyCode
    formData.currencyName = detail.currencyName
    formData.currencySymbol = detail.currencySymbol || ''
    formData.exchangeRate = detail.exchangeRate
    formData.rateType = detail.rateType
    formData.effectiveDate = detail.effectiveDate || ''
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取币种汇率详情失败')
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateCurrencyRateApi(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createCurrencyRateApi({ ...formData })
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

const rateTypeMap: Record<number, string> = {
  1: '固定汇率',
  2: '浮动汇率'
}

function rateTypeLabel(type: number): string {
  return rateTypeMap[type] || '未知'
}

function rateTypeTag(type: number): 'success' | 'warning' | 'info' {
  if (type === 1) return 'success'
  if (type === 2) return 'warning'
  return 'info'
}

function formatRate(rate: number): string {
  if (rate == null) return '-'
  return Number(rate).toFixed(6)
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
  searchForm.currencyName = ''
  searchForm.rateType = undefined
  pagination.pageNum = 1
  loadData()
}

async function loadData(): Promise<void> {
  tableLoading.value = true
  try {
    const res = await getCurrencyRatePageApi({
      currencyName: searchForm.currencyName || undefined,
      rateType: searchForm.rateType,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.list || []
    pagination.total = res.total || 0
    updateStats(res.list || [], res.total || 0)
  } catch {
    ElMessage.error('加载币种汇率列表失败')
  } finally {
    tableLoading.value = false
  }
}

function updateStats(list: CurrencyRateVO[], total: number): void {
  stats.total = total
  const today = new Date().toISOString().split('T')[0]
  stats.todayCount = list.filter(
    (item) => item.createTime && item.createTime.startsWith(today)
  ).length
  const types = new Set(list.map((item) => item.rateType))
  stats.typeCount = types.size
}

async function handleDelete(row: CurrencyRateVO): Promise<void> {
  try {
    await deleteCurrencyRateApi(row.id)
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

  &--today .stat-value {
    color: var(--el-color-success);
  }
  &--type .stat-value {
    color: var(--el-color-warning);
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
