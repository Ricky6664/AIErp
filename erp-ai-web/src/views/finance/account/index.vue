<template>
  <div class="account-list-page">
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">科目总数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--leaf">
          <div class="stat-value">{{ stats.leafCount }}</div>
          <div class="stat-label">末级科目</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--enabled">
          <div class="stat-value">{{ stats.enabled }}</div>
          <div class="stat-label">已启用</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--disabled">
          <div class="stat-value">{{ stats.disabled }}</div>
          <div class="stat-label">已停用</div>
        </el-card>
      </el-col>
    </el-row>

    <div class="content-layout">
      <div class="tree-panel">
        <el-card shadow="never" class="tree-card">
          <template #header>
            <div class="tree-header">
              <span>科目树</span>
              <el-button type="primary" size="small" :icon="Plus" @click="handleAddRoot">
                新增根科目
              </el-button>
            </div>
          </template>
          <el-input
            v-model="treeFilterText"
            placeholder="搜索科目..."
            clearable
            size="small"
            class="tree-filter"
          />
          <el-tree
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            :filter-node-method="filterTreeNode"
            :expand-on-click-node="true"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleTreeNodeClick"
          >
            <template #default="{ data }">
              <span class="tree-node">
                <span class="tree-node-label">
                  <span class="tree-node-code">{{ data.accountCode }}</span>
                  {{ data.accountName }}
                  <el-tag v-if="data.isLeaf" size="small" type="success" class="leaf-tag">
                    末级
                  </el-tag>
                </span>
                <span class="tree-node-actions">
                  <el-button
                    v-if="!data.isLeaf"
                    link
                    type="primary"
                    size="small"
                    :icon="Plus"
                    @click.stop="handleAddChild(data)"
                  />
                  <el-button
                    link
                    type="primary"
                    size="small"
                    :icon="Edit"
                    @click.stop="handleEditFromTree(data)"
                  />
                  <el-popconfirm
                    title="确认删除该科目？"
                    confirm-button-text="确认"
                    cancel-button-text="取消"
                    @confirm="handleDeleteFromTree(data)"
                  >
                    <template #reference>
                      <el-button link type="danger" size="small" :icon="Delete" @click.stop />
                    </template>
                  </el-popconfirm>
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </div>

      <div class="table-panel">
        <el-card shadow="never" class="search-card">
          <el-form :model="searchForm" :inline="true" @submit.prevent>
            <el-form-item label="科目名称">
              <el-input
                v-model="searchForm.accountName"
                placeholder="请输入科目名称"
                clearable
                style="width: 180px"
                @input="handleSearchDebounced"
              />
            </el-form-item>
            <el-form-item label="科目类别">
              <el-select
                v-model="searchForm.accountType"
                placeholder="请选择科目类别"
                clearable
                style="width: 140px"
                @change="handleSearch"
              >
                <el-option
                  v-for="item in accountTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select
                v-model="searchForm.status"
                placeholder="请选择状态"
                clearable
                style="width: 100px"
                @change="handleSearch"
              >
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleSearch"> 查询 </el-button>
              <el-button :icon="RefreshRight" @click="handleReset"> 重置 </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="table-header">
              <span>{{ selectedNodeName || '请选择左侧科目' }}</span>
              <span v-if="tableData.length">共 {{ pagination.total }} 条</span>
            </div>
          </template>

          <vxe-table
            :loading="tableLoading"
            :data="tableData"
            :scroll-y="{ enabled: true, gt: 100 }"
            max-height="600"
            border
            style="width: 100%"
          >
            <vxe-column type="seq" title="序号" width="60" align="center" />
            <vxe-column field="accountCode" title="科目编码" min-width="140" />
            <vxe-column field="accountName" title="科目名称" min-width="160" />
            <vxe-column field="category" title="科目类别" width="120" align="center">
              <template #default="{ row }">
                {{ categoryLabel(row.category) }}
              </template>
            </vxe-column>
            <vxe-column field="balanceDirection" title="余额方向" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.balanceDirection === 1 ? 'danger' : 'success'" size="small">
                  {{ row.balanceDirection === 1 ? '借方' : '贷方' }}
                </el-tag>
              </template>
            </vxe-column>
            <vxe-column field="isLeaf" title="是否末级" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.isLeaf ? 'success' : 'info'" size="small">
                  {{ row.isLeaf ? '是' : '否' }}
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
            <vxe-column title="操作" width="200" align="center" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button
                  link
                  :type="row.status === 1 ? 'warning' : 'success'"
                  size="small"
                  @click="handleToggleStatus(row)"
                >
                  {{ row.status === 1 ? '停用' : '启用' }}
                </el-button>
                <el-popconfirm
                  title="确认删除该科目？"
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

          <div class="pagination-wrapper">
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
        </el-card>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
      @closed="handleDialogClosed"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科目编码" prop="accountCode">
              <el-input
                v-model="formData.accountCode"
                placeholder="请输入科目编码"
                maxlength="20"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科目名称" prop="accountName">
              <el-input
                v-model="formData.accountName"
                placeholder="请输入科目名称"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科目类别" prop="category">
              <el-select
                v-model="formData.category"
                placeholder="请选择科目类别"
                style="width: 100%"
              >
                <el-option
                  v-for="item in accountTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="余额方向" prop="balanceDirection">
              <el-select
                v-model="formData.balanceDirection"
                placeholder="请选择余额方向"
                style="width: 100%"
              >
                <el-option label="借方" :value="1" />
                <el-option label="贷方" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否现金">
              <el-switch v-model="formData.isCash" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否银行">
              <el-switch v-model="formData.isBank" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否外币">
              <el-switch v-model="formData.isForeignCurrency" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="辅助核算" prop="isAuxiliary">
              <el-input
                v-model="formData.isAuxiliary"
                placeholder="请输入辅助核算"
                maxlength="100"
              />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search, RefreshRight, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getAccountTreeApi,
  getAccountPageApi,
  getAccountByIdApi,
  createAccountApi,
  updateAccountApi,
  updateAccountStatusApi,
  deleteAccountApi,
  type AccountVO,
  type AccountTreeVO,
  type AccountSaveDTO
} from '@/api/modules/finance-account'

const tableLoading = ref(false)
const tableData = ref<AccountVO[]>([])
const treeData = ref<AccountTreeVO[]>([])
const treeFilterText = ref('')
const selectedNodeId = ref<number | null>(null)
const selectedNodeName = ref('')

const searchForm = reactive({
  accountName: '',
  accountType: undefined as number | undefined,
  status: undefined as number | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const stats = reactive({
  total: 0,
  leafCount: 0,
  enabled: 0,
  disabled: 0
})

const treeProps = {
  children: 'children',
  label: 'accountName'
}

const accountTypeOptions = [
  { label: '资产类', value: 1 },
  { label: '负债类', value: 2 },
  { label: '共同类', value: 3 },
  { label: '所有者权益类', value: 4 },
  { label: '成本类', value: 5 },
  { label: '损益类', value: 6 }
]

const categoryMap: Record<string, string> = {
  资产类: '资产类',
  负债类: '负债类',
  共同类: '共同类',
  所有者权益类: '所有者权益类',
  成本类: '成本类',
  损益类: '损益类'
}

function categoryLabel(category: string): string {
  return categoryMap[category] || category
}

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const parentIdForAdd = ref(0)

const dialogTitle = computed(() => {
  if (isEdit.value) return '编辑会计科目'
  if (parentIdForAdd.value === 0) return '新增根科目'
  return '新增子科目'
})

const initFormData = (): AccountSaveDTO => ({
  parentId: 0,
  accountCode: '',
  accountName: '',
  accountType: 1,
  category: '资产类',
  balanceDirection: 1,
  isCash: false,
  isBank: false,
  isForeignCurrency: '',
  isAuxiliary: '',
  status: 1
})

const formData = reactive<AccountSaveDTO>(initFormData())

const formRules: FormRules = {
  accountCode: [
    { required: true, message: '请输入科目编码', trigger: 'blur' },
    { max: 20, message: '科目编码最长20个字符', trigger: 'blur' }
  ],
  accountName: [
    { required: true, message: '请输入科目名称', trigger: 'blur' },
    { max: 100, message: '科目名称最长100个字符', trigger: 'blur' }
  ],
  category: [{ required: true, message: '请选择科目类别', trigger: 'change' }],
  balanceDirection: [{ required: true, message: '请选择余额方向', trigger: 'change' }]
}

function handleAddRoot(): void {
  isEdit.value = false
  editId.value = null
  parentIdForAdd.value = 0
  Object.assign(formData, initFormData())
  dialogVisible.value = true
}

function handleAddChild(data: AccountTreeVO): void {
  isEdit.value = false
  editId.value = null
  parentIdForAdd.value = data.id
  Object.assign(formData, initFormData())
  formData.parentId = data.id
  dialogVisible.value = true
}

async function handleEditFromTree(data: AccountTreeVO): Promise<void> {
  await loadEditData(data.id)
}

async function handleEdit(row: AccountVO): Promise<void> {
  await loadEditData(row.id)
}

async function loadEditData(id: number): Promise<void> {
  isEdit.value = true
  editId.value = id
  try {
    const detail = await getAccountByIdApi(id)
    formData.parentId = detail.parentId
    formData.accountCode = detail.accountCode
    formData.accountName = detail.accountName
    formData.accountType = detail.accountType
    formData.category = detail.category
    formData.balanceDirection = detail.balanceDirection
    formData.isCash = detail.isCash
    formData.isBank = detail.isBank
    formData.isForeignCurrency = detail.isForeignCurrency || ''
    formData.isAuxiliary = detail.isAuxiliary || ''
    formData.status = detail.status
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取科目详情失败')
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateAccountApi(editId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await createAccountApi({ ...formData })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await loadTree()
    await loadTableData()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
  } finally {
    submitLoading.value = false
  }
}

function handleDialogClosed(): void {
  formRef.value?.resetFields()
}

async function handleToggleStatus(row: AccountVO): Promise<void> {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '停用' : '启用'
  try {
    await updateAccountStatusApi(row.id, newStatus)
    ElMessage.success(`${actionText}成功`)
    await loadTableData()
  } catch {
    ElMessage.error(`${actionText}失败`)
  }
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
  await loadTableData()
}

function handleReset(): void {
  searchForm.accountName = ''
  searchForm.accountType = undefined
  searchForm.status = undefined
  pagination.pageNum = 1
  loadTableData()
}

function handleTreeNodeClick(data: AccountTreeVO): void {
  selectedNodeId.value = data.id
  selectedNodeName.value = data.accountName
  pagination.pageNum = 1
  loadTableData()
}

function filterTreeNode(value: string, data: AccountTreeVO): boolean {
  if (!value) return true
  return data.accountName.includes(value) || data.accountCode.includes(value)
}

async function loadTree(): Promise<void> {
  try {
    const res = await getAccountTreeApi()
    treeData.value = res || []
  } catch {
    ElMessage.error('加载科目树失败')
  }
}

async function loadTableData(): Promise<void> {
  tableLoading.value = true
  try {
    const res = await getAccountPageApi({
      accountName: searchForm.accountName || undefined,
      accountType: searchForm.accountType,
      status: searchForm.status,
      parentId: selectedNodeId.value ?? undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.list || []
    pagination.total = res.total || 0
    updateStats(res.list || [], res.total || 0)
  } catch {
    ElMessage.error('加载科目列表失败')
  } finally {
    tableLoading.value = false
  }
}

function updateStats(list: AccountVO[], total: number): void {
  stats.total = total
  stats.leafCount = list.filter((item) => item.isLeaf).length
  stats.enabled = list.filter((item) => item.status === 1).length
  stats.disabled = list.filter((item) => item.status === 0).length
}

async function handleDeleteFromTree(data: AccountTreeVO): Promise<void> {
  try {
    await deleteAccountApi(data.id)
    ElMessage.success('删除成功')
    if (selectedNodeId.value === data.id) {
      selectedNodeId.value = null
      selectedNodeName.value = ''
    }
    await loadTree()
    await loadTableData()
  } catch {
    ElMessage.error('删除失败')
  }
}

async function handleDelete(row: AccountVO): Promise<void> {
  try {
    await deleteAccountApi(row.id)
    ElMessage.success('删除成功')
    await loadTree()
    await loadTableData()
  } catch {
    ElMessage.error('删除失败')
  }
}

function handleSizeChange(): void {
  pagination.pageNum = 1
  loadTableData()
}

function handlePageChange(): void {
  loadTableData()
}

onMounted(() => {
  loadTree()
  loadTableData()
})
</script>

<style scoped lang="scss">
.account-list-page {
  padding: 20px;

  .stats-row {
    margin-bottom: 16px;

    .stat-card {
      text-align: center;

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--el-color-primary);
        line-height: 1.2;
      }

      .stat-label {
        margin-top: 8px;
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }

      &--leaf {
        .stat-value {
          color: var(--el-color-success);
        }
      }

      &--enabled {
        .stat-value {
          color: var(--el-color-success);
        }
      }

      &--disabled {
        .stat-value {
          color: var(--el-color-danger);
        }
      }
    }
  }

  .content-layout {
    display: flex;
    gap: 16px;

    .tree-panel {
      width: 320px;
      flex-shrink: 0;

      .tree-card {
        height: 100%;

        .tree-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
        }

        .tree-filter {
          margin-bottom: 12px;
        }

        :deep(.el-tree-node__content) {
          height: 36px;
        }

        .tree-node {
          display: flex;
          align-items: center;
          justify-content: space-between;
          flex: 1;
          min-width: 0;

          .tree-node-label {
            display: flex;
            align-items: center;
            gap: 4px;
            min-width: 0;

            .tree-node-code {
              font-size: 12px;
              color: var(--el-text-color-secondary);
            }

            .leaf-tag {
              margin-left: 4px;
            }
          }

          .tree-node-actions {
            display: flex;
            align-items: center;
            gap: 2px;
            visibility: hidden;
          }

          &:hover .tree-node-actions {
            visibility: visible;
          }
        }
      }
    }

    .table-panel {
      flex: 1;
      min-width: 0;

      .search-card {
        margin-bottom: 16px;
      }

      .table-card {
        .table-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
        }

        .pagination-wrapper {
          display: flex;
          justify-content: flex-end;
          margin-top: 16px;
        }
      }
    }
  }
}
</style>
