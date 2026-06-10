<template>
  <PageP04SimpleList
    view-id="company-list"
    page-type="P04"
    :config="pageConfig"
    :permissions="permissions"
  >
    <!-- 查询区 -->
    <template #query-panel>
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="公司名称">
          <el-input
            v-model="searchForm.companyName"
            placeholder="请输入公司名称"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="信用代码">
          <el-input
            v-model="searchForm.creditCode"
            placeholder="请输入信用代码"
            clearable
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
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </template>

    <!-- 操作栏 -->
    <template #action-bar>
      <div class="action-bar-left">
        <el-button type="primary" @click="handleCreate">新增</el-button>
      </div>
      <div class="action-bar-right">
        <span class="record-count">{{ pagination.total }} 条记录</span>
      </div>
    </template>

    <!-- 数据表格 -->
    <template #main-content>
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        stripe
        style="width: 100%"
        max-height="600"
      >
        <el-table-column prop="companyName" label="公司名称" min-width="180" />
        <el-table-column prop="companyShortName" label="公司简称" min-width="120" />
        <el-table-column prop="creditCode" label="统一社会信用代码" min-width="180" />
        <el-table-column prop="legalPerson" label="法定代表人" min-width="100" />
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" sortable />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm
              title="确认删除该公司？"
              confirm-button-text="确认"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-box">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          small
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </template>
  </PageP04SimpleList>

  <!-- 新增/编辑弹窗（P07 表单，留在 PageP04SimpleList 外部） -->
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? $t('org.company.editTitle') : $t('org.company.addTitle')"
    width="720px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('org.company.companyName')" prop="companyName">
            <el-input
              v-model="formData.companyName"
              :placeholder="$t('common.pleaseInput')"
              maxlength="200"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('org.company.companyShortName')" prop="companyShortName">
            <el-input
              v-model="formData.companyShortName"
              :placeholder="$t('common.pleaseInput')"
              maxlength="100"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('org.company.creditCode')" prop="creditCode">
            <el-input
              v-model="formData.creditCode"
              :placeholder="$t('org.company.creditCodePlaceholder')"
              maxlength="18"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('org.company.legalPerson')" prop="legalPerson">
            <el-input
              v-model="formData.legalPerson"
              :placeholder="$t('common.pleaseInput')"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('org.company.registeredCapital')" prop="registeredCapital">
            <el-input-number
              v-model="formData.registeredCapital"
              :min="0"
              :precision="0"
              controls-position="right"
              style="width: 100%"
            />
            <span class="input-unit">{{ $t('org.company.registeredCapitalUnit') }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('org.company.phone')" prop="phone">
            <el-input
              v-model="formData.phone"
              :placeholder="$t('org.company.phonePlaceholder')"
              maxlength="30"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item :label="$t('org.company.address')" prop="address">
        <el-input
          v-model="formData.address"
          type="textarea"
          :rows="3"
          :placeholder="$t('common.pleaseInput')"
          maxlength="300"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ $t('common.save') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import {
  getCompanyPage,
  getCompanyDetail,
  createCompany,
  updateCompany,
  deleteCompany
} from '@/api/modules/org'
import type { CompanyQueryDTO, CompanyCreateDTO, CompanyListVO } from '@/api/types/org'

// ==================== PageP04SimpleList 配置 ====================
const pageConfig: SimpleListPageConfig = {
  title: '公司管理',
  showQueryPanel: true,
  showActionBar: true
}
const permissions = [
  'org:company:view',
  'org:company:create',
  'org:company:edit',
  'org:company:delete'
]

// ==================== 列表数据 ====================
const tableLoading = ref(false)
const tableData = ref<CompanyListVO[]>([])

const searchForm = reactive<CompanyQueryDTO>({
  companyName: '',
  creditCode: '',
  status: undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

let debounceTimer: ReturnType<typeof setTimeout> | null = null

function handleSearchDebounced(): void {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

async function handleSearch(): Promise<void> {
  tableLoading.value = true
  try {
    const res = await getCompanyPage({
      companyName: searchForm.companyName || undefined,
      creditCode: searchForm.creditCode || undefined,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res) {
      tableData.value = res.records || []
      pagination.total = res.total || 0
    }
  } catch {
    ElMessage.error('获取公司列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    tableLoading.value = false
  }
}

function handleReset(): void {
  searchForm.companyName = ''
  searchForm.creditCode = ''
  searchForm.status = undefined
  pagination.pageNum = 1
  handleSearch()
}

async function handleDelete(row: CompanyListVO): Promise<void> {
  tableLoading.value = true
  try {
    await deleteCompany(row.id)
    ElMessage.success('删除成功')
    await handleSearch()
  } catch {
    ElMessage.error('删除失败')
  } finally {
    tableLoading.value = false
  }
}

// ==================== 弹窗/P07 表单逻辑（保留原全部验证与规则） ====================
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number>(0)

interface FormData {
  companyName: string
  companyShortName: string
  creditCode: string
  legalPerson: string
  registeredCapital: number | undefined
  address: string
  phone: string
}

const defaultForm = (): FormData => ({
  companyName: '',
  companyShortName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: undefined,
  address: '',
  phone: ''
})

const formData = ref<FormData>(defaultForm())
const submitting = ref(false)
const formRef = ref<FormInstance>()

const CREDIT_CODE_REGEX = /^[0-9A-HJ-NP-RTUW-Y]{2}\d{6}[0-9A-HJ-NP-RTUW-Y]{10}$/
const PHONE_REGEX = /^(0\d{2,3}-?\d{7,8})|(1[3-9]\d{9})$/

const formRules: FormRules = {
  companyName: [
    { required: true, message: '公司名称不能为空', trigger: 'blur' },
    { min: 2, max: 100, message: '公司名称长度须在2-100个字符之间', trigger: 'blur' }
  ],
  creditCode: [
    {
      validator: (_rule, value: string, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!CREDIT_CODE_REGEX.test(value)) {
          callback(new Error('请输入有效的统一社会信用代码'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    {
      validator: (_rule, value: string, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!PHONE_REGEX.test(value)) {
          callback(new Error('请输入正确的联系电话格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

async function initForm(): Promise<void> {
  formData.value = defaultForm()
  formRef.value?.clearValidate()

  if (editingId.value) {
    try {
      const detail = await getCompanyDetail(editingId.value)
      formData.value.companyName = detail.companyName ?? ''
      formData.value.companyShortName = detail.companyShortName ?? ''
      formData.value.creditCode = detail.creditCode ?? ''
      formData.value.legalPerson = detail.legalPerson ?? ''
      formData.value.registeredCapital =
        detail.registeredCapital != null ? Number(detail.registeredCapital) : undefined
      formData.value.address = detail.address ?? ''
      formData.value.phone = detail.phone ?? ''
    } catch {
      ElMessage.error('加载公司详情失败')
    }
  }
}

function handleCreate(): void {
  isEdit.value = false
  editingId.value = 0
  dialogVisible.value = true
  initForm()
}

async function handleEdit(row: CompanyListVO): Promise<void> {
  isEdit.value = true
  editingId.value = row.id
  dialogVisible.value = true
  await initForm()
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      companyName: formData.value.companyName,
      companyShortName: formData.value.companyShortName || undefined,
      creditCode: formData.value.creditCode || undefined,
      legalPerson: formData.value.legalPerson || undefined,
      registeredCapital: formData.value.registeredCapital,
      address: formData.value.address || undefined,
      phone: formData.value.phone || undefined
    }

    if (isEdit.value && editingId.value) {
      await updateCompany({ id: editingId.value, ...payload })
    } else {
      await createCompany(payload as CompanyCreateDTO)
    }

    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    await handleSearch()
  } catch {
    // error handled by request interceptor
  } finally {
    submitting.value = false
  }
}

function handleClosed(): void {
  formData.value = defaultForm()
  formRef.value?.resetFields()
}

// ==================== 生命周期 ====================
onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
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

.input-unit {
  margin-left: 8px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}
</style>
