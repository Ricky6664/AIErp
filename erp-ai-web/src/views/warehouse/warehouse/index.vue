<template>
  <div class="warehouse-list-page">
    <!-- 快捷统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">仓库总数</div>
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
        <el-form-item label="仓库名称">
          <el-input
            v-model="searchForm.warehouseName"
            placeholder="请输入仓库名称"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="仓库类型">
          <el-select
            v-model="searchForm.warehouseType"
            placeholder="请选择仓库类型"
            clearable
            style="width: 160px"
            @change="handleSearch"
          >
            <el-option label="普通仓" value="NORMAL" />
            <el-option label="保税仓" value="BONDED" />
            <el-option label="虚拟仓" value="VIRTUAL" />
          </el-select>
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
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-header">
          <span>{{ stats.total }} 条记录</span>
          <el-button type="primary" @click="handleCreate">新建仓库</el-button>
        </div>
      </template>

      <vxe-table
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        style="width: 100%"
      >
        <vxe-column field="warehouseCode" title="仓库编码" min-width="140" />
        <vxe-column field="warehouseName" title="仓库名称" min-width="160" />
        <vxe-column field="warehouseType" title="仓库类型" width="120" align="center">
          <template #default="{ row }">
            <span>{{ warehouseTypeLabel(row.warehouseType) }}</span>
          </template>
        </vxe-column>
        <vxe-column field="address" title="地址" min-width="200" />
        <vxe-column field="phone" title="联系电话" width="140" />
        <vxe-column field="status" title="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column field="createTime" title="创建时间" width="180" sortable />
        <vxe-column title="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-popconfirm
              title="确认删除该仓库？"
              confirm-button-text="确认"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </vxe-column>
      </vxe-table>

      <div class="pagination-wrapper">
        <vxe-pager
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :layouts="['Total', 'Sizes', 'PrevPage', 'Number', 'NextPage', 'FullJump', 'PageCount']"
          background
          @page-change="handleSearch"
        />
      </div>
    </el-card>

    <!-- 仓库表单弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑仓库' : '新增仓库'"
      width="640px"
      destroy-on-close
      @closed="handleDialogClosed"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库编码" prop="warehouseCode">
              <el-input v-model="formData.warehouseCode" disabled placeholder="系统自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="warehouseName">
              <el-input
                v-model="formData.warehouseName"
                placeholder="请输入仓库名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库类型" prop="warehouseType">
              <el-select
                v-model="formData.warehouseType"
                placeholder="请选择仓库类型"
                style="width: 100%"
              >
                <el-option label="普通仓" value="NORMAL" />
                <el-option label="保税仓" value="BONDED" />
                <el-option label="虚拟仓" value="VIRTUAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="managerId">
              <el-input v-model="formData.managerId" placeholder="请输入负责人ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            placeholder="请输入仓库地址"
            maxlength="500"
            show-word-limit
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getWarehousePage,
  getWarehouseDetail,
  createWarehouse,
  updateWarehouse,
  deleteWarehouse
} from '@/api/modules/warehouse'
import type { WarehouseListVO, WarehouseCreateDTO } from '@/api/types/warehouse'
import type { FormInstance, FormRules } from 'element-plus'

const formRef = ref<FormInstance>()
const tableLoading = ref(false)
const tableData = ref<WarehouseListVO[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(0)
const submitLoading = ref(false)

const formData = reactive<WarehouseCreateDTO & { id?: number }>({
  warehouseCode: '',
  warehouseName: '',
  warehouseType: '',
  address: '',
  managerId: undefined,
  phone: '',
  status: 1
})

const formRules: FormRules = {
  warehouseName: [
    { required: true, message: '请输入仓库名称', trigger: 'blur' },
    { max: 100, message: '仓库名称不超过100个字符', trigger: 'blur' }
  ],
  warehouseType: [{ required: true, message: '请选择仓库类型', trigger: 'change' }],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ],
  address: [{ max: 500, message: '地址不超过500个字符', trigger: 'blur' }]
}

const searchForm = reactive({
  warehouseName: '',
  warehouseType: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const stats = computed(() => {
  const total = pagination.total
  const enabled = tableData.value.filter((item) => item.status === 1).length
  return { total, enabled, disabled: total - enabled }
})

let debounceTimer: ReturnType<typeof setTimeout> | null = null

function handleSearchDebounced() {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

async function handleSearch() {
  tableLoading.value = true
  try {
    const res = await getWarehousePage({
      warehouseName: searchForm.warehouseName || undefined,
      warehouseType: searchForm.warehouseType || undefined,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res) {
      tableData.value = res.records || []
      pagination.total = res.total || 0
    }
  } catch {
    ElMessage.error('获取仓库列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    tableLoading.value = false
  }
}

function handleReset() {
  searchForm.warehouseName = ''
  searchForm.warehouseType = ''
  searchForm.status = undefined
  pagination.pageNum = 1
  handleSearch()
}

function warehouseTypeLabel(type: string): string {
  const map: Record<string, string> = {
    NORMAL: '普通仓',
    BONDED: '保税仓',
    VIRTUAL: '虚拟仓'
  }
  return map[type] || type
}

function handleCreate() {
  isEdit.value = false
  editingId.value = 0
  formData.warehouseCode = ''
  formData.warehouseName = ''
  formData.warehouseType = ''
  formData.address = ''
  formData.managerId = undefined
  formData.phone = ''
  formData.status = 1
  dialogVisible.value = true
}

async function handleEdit(row: WarehouseListVO) {
  isEdit.value = true
  editingId.value = row.id
  try {
    const detail = await getWarehouseDetail(row.id)
    if (detail) {
      formData.warehouseCode = detail.warehouseCode
      formData.warehouseName = detail.warehouseName
      formData.warehouseType = detail.warehouseType
      formData.address = detail.address || ''
      formData.managerId = detail.managerId
      formData.phone = detail.phone || ''
      formData.status = detail.status
    }
  } catch {
    ElMessage.error('获取仓库详情失败')
    return
  }
  dialogVisible.value = true
}

function handleDialogClosed() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateWarehouse({ id: editingId.value, ...formData })
      ElMessage.success('更新成功')
    } else {
      await createWarehouse(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await handleSearch()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleToggleStatus(row: WarehouseListVO) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '停用' : '启用'
  try {
    await updateWarehouse({ id: row.id, status: newStatus })
    ElMessage.success(`${actionText}成功`)
    await handleSearch()
  } catch {
    ElMessage.error(`${actionText}失败`)
  }
}

async function handleDelete(row: WarehouseListVO) {
  tableLoading.value = true
  try {
    await deleteWarehouse(row.id)
    ElMessage.success('删除成功')
    await handleSearch()
  } catch {
    ElMessage.error('删除失败')
  } finally {
    tableLoading.value = false
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.warehouse-list-page {
  padding: 20px;

  .stats-row {
    margin-bottom: 16px;
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

  .search-card {
    margin-bottom: 16px;
  }

  .table-card {
    .table-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
