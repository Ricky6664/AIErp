<template>
  <PageP04SimpleList
    view-id="location-list"
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
            <div class="stat-label">库位总数</div>
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
        <el-form-item label="库位名称">
          <el-input
            v-model="searchForm.locationName"
            placeholder="请输入库位名称"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="所属仓库">
          <el-select
            v-model="searchForm.warehouseId"
            placeholder="请选择仓库"
            clearable
            style="width: 180px"
            @change="handleSearch"
          >
            <el-option
              v-for="wh in warehouseList"
              :key="wh.id"
              :label="wh.warehouseName"
              :value="wh.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库位类型">
          <el-select
            v-model="searchForm.locationType"
            placeholder="请选择库位类型"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option
              v-for="opt in locationTypeOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
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
    </template>

    <!-- 操作栏 -->
    <template #action-bar>
      <div class="action-bar-left">
        <el-button type="primary" @click="handleCreate">新建库位</el-button>
      </div>
      <div class="action-bar-right">
        <span class="record-count">{{ pagination.total }} 条记录</span>
      </div>
    </template>

    <!-- 数据表格 -->
    <template #main-content>
      <vxe-table
        :loading="tableLoading"
        :data="tableData"
        :scroll-y="{ enabled: true, gt: 100 }"
        max-height="600"
        style="width: 100%"
      >
        <vxe-column field="locationCode" title="库位编码" min-width="140" />
        <vxe-column field="locationName" title="库位名称" min-width="160" />
        <vxe-column field="warehouseId" title="所属仓库" width="160" align="center">
          <template #default="{ row }">
            <span>{{ warehouseNameMap[row.warehouseId] || row.warehouseId }}</span>
          </template>
        </vxe-column>
        <vxe-column field="locationType" title="库位类型" width="120" align="center">
          <template #default="{ row }">
            <span>{{ locationTypeLabel(row.locationType) }}</span>
          </template>
        </vxe-column>
        <vxe-column field="sortOrder" title="排序号" width="100" align="center" sortable />
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
              title="确认删除该库位？"
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

      <div class="pagination-box">
        <vxe-pager
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :layouts="['Total', 'Sizes', 'PrevPage', 'Number', 'NextPage', 'FullJump', 'PageCount']"
          background
          @page-change="handleSearch"
        />
      </div>
    </template>
  </PageP04SimpleList>

  <!-- 库位表单弹窗（弹窗留在外部） -->
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑库位' : '新增库位'"
    width="640px"
    destroy-on-close
    @closed="handleDialogClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="库位编码" prop="locationCode">
            <el-input
              v-model="formData.locationCode"
              placeholder="请输入库位编码"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="库位名称" prop="locationName">
            <el-input
              v-model="formData.locationName"
              placeholder="请输入库位名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="所属仓库" prop="warehouseId">
            <el-select v-model="formData.warehouseId" placeholder="请选择仓库" style="width: 100%">
              <el-option
                v-for="wh in warehouseList"
                :key="wh.id"
                :label="wh.warehouseName"
                :value="wh.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="库位类型" prop="locationType">
            <el-select
              v-model="formData.locationType"
              placeholder="请选择库位类型"
              style="width: 100%"
            >
              <el-option
                v-for="opt in locationTypeOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="排序号" prop="sortOrder">
            <el-input-number
              v-model="formData.sortOrder"
              :min="0"
              :max="9999"
              style="width: 100%"
            />
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
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import {
  getLocationPage,
  getLocationDetail,
  createLocation,
  updateLocation,
  deleteLocation
} from '@/api/modules/location'
import { getWarehousePage } from '@/api/modules/warehouse'
import type { LocationListVO, LocationCreateDTO } from '@/api/types/location'
import type { WarehouseListVO } from '@/api/types/warehouse'
import type { FormInstance, FormRules } from 'element-plus'

const pageConfig: SimpleListPageConfig = {
  title: '库位管理',
  showQueryPanel: true,
  showActionBar: true
}
const permissions = ['warehouse:view', 'warehouse:create', 'warehouse:edit', 'warehouse:delete']

const formRef = ref<FormInstance>()
const tableLoading = ref(false)
const tableData = ref<LocationListVO[]>([])
const warehouseList = ref<WarehouseListVO[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(0)
const submitLoading = ref(false)

const formData = reactive<LocationCreateDTO & { id?: number }>({
  warehouseId: 0,
  locationCode: '',
  locationName: '',
  locationType: '',
  sortOrder: 0,
  status: 1
})

const formRules: FormRules = {
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  locationCode: [
    { required: true, message: '请输入库位编码', trigger: 'blur' },
    { max: 50, message: '库位编码不超过50个字符', trigger: 'blur' }
  ],
  locationName: [
    { required: true, message: '请输入库位名称', trigger: 'blur' },
    { max: 100, message: '库位名称不超过100个字符', trigger: 'blur' }
  ],
  locationType: [{ required: true, message: '请选择库位类型', trigger: 'change' }]
}

const searchForm = reactive({
  locationName: '',
  warehouseId: undefined as number | undefined,
  locationType: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const locationTypeOptions = [
  { label: '存储', value: 'STORAGE' },
  { label: '拣货', value: 'PICKING' },
  { label: '暂存', value: 'STAGING' },
  { label: '不良品', value: 'DEFECTIVE' }
]

const warehouseNameMap = computed<Record<number, string>>(() => {
  const map: Record<number, string> = {}
  warehouseList.value.forEach((wh) => {
    map[wh.id] = wh.warehouseName
  })
  return map
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
    const res = await getLocationPage({
      locationName: searchForm.locationName || undefined,
      warehouseId: searchForm.warehouseId,
      locationType: searchForm.locationType || undefined,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res) {
      tableData.value = res.records || []
      pagination.total = res.total || 0
    }
  } catch {
    ElMessage.error('获取库位列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    tableLoading.value = false
  }
}

function handleReset() {
  searchForm.locationName = ''
  searchForm.warehouseId = undefined
  searchForm.locationType = ''
  searchForm.status = undefined
  pagination.pageNum = 1
  handleSearch()
}

function locationTypeLabel(type: string): string {
  const found = locationTypeOptions.find((opt) => opt.value === type)
  return found ? found.label : type
}

async function fetchWarehouseList() {
  try {
    const res = await getWarehousePage({ pageNum: 1, pageSize: 200 })
    if (res) {
      warehouseList.value = res.records || []
    }
  } catch {
    // 仓库列表加载失败不影响页面主流程
  }
}

function handleCreate() {
  isEdit.value = false
  editingId.value = 0
  formData.warehouseId = 0
  formData.locationCode = ''
  formData.locationName = ''
  formData.locationType = ''
  formData.sortOrder = 0
  formData.status = 1
  dialogVisible.value = true
}

async function handleEdit(row: LocationListVO) {
  isEdit.value = true
  editingId.value = row.id
  try {
    const detail = await getLocationDetail(row.id)
    if (detail) {
      formData.warehouseId = detail.warehouseId
      formData.locationCode = detail.locationCode
      formData.locationName = detail.locationName
      formData.locationType = detail.locationType
      formData.sortOrder = detail.sortOrder
      formData.status = detail.status
    }
  } catch {
    ElMessage.error('获取库位详情失败')
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
      await updateLocation({ id: editingId.value, ...formData })
      ElMessage.success('更新成功')
    } else {
      await createLocation(formData)
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

async function handleToggleStatus(row: LocationListVO) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '停用' : '启用'
  try {
    await updateLocation({ id: row.id, status: newStatus })
    ElMessage.success(`${actionText}成功`)
    await handleSearch()
  } catch {
    ElMessage.error(`${actionText}失败`)
  }
}

async function handleDelete(row: LocationListVO) {
  tableLoading.value = true
  try {
    await deleteLocation(row.id)
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
  fetchWarehouseList()
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
