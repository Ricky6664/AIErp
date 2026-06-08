<template>
  <div class="approval-definition-page">
    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="定义名称">
          <el-input
            v-model="searchForm.definitionName"
            placeholder="请输入审批定义名称"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-input
            v-model="searchForm.businessType"
            placeholder="请输入业务类型"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.enableFlag"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="启用" :value="true" />
            <el-option label="停用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card shadow="never" class="toolbar-card">
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        新增定义
      </el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="definitionName" label="定义名称" min-width="160" />
        <el-table-column prop="definitionCode" label="定义编码" min-width="140" />
        <el-table-column prop="businessType" label="业务类型" width="140" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.enableFlag"
              active-text="启用"
              inactive-text="停用"
              @change="(val: boolean) => handleToggleEnable(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="定义名称" prop="definitionName">
          <el-input v-model="formData.definitionName" placeholder="请输入审批定义名称" />
        </el-form-item>
        <el-form-item label="定义编码" prop="definitionCode">
          <el-input
            v-model="formData.definitionCode"
            placeholder="请输入审批定义编码"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType">
          <el-input v-model="formData.businessType" placeholder="请输入业务类型" />
        </el-form-item>
        <el-form-item label="流程配置">
          <el-input
            v-model="formData.flowConfig"
            type="textarea"
            :rows="4"
            placeholder="请输入JSON格式的流程配置"
          />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="formData.enableFlag" active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getDefinitionPage,
  createDefinition,
  updateDefinition,
  deleteDefinition
} from '@/api/modules/approval'
import type {
  DefinitionListVO,
  DefinitionCreateDTO,
  DefinitionUpdateDTO
} from '@/api/types/approval'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<DefinitionListVO[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增审批定义')
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const searchForm = reactive({
  definitionName: '',
  businessType: '',
  enableFlag: undefined as boolean | undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  definitionName: '',
  definitionCode: '',
  businessType: '',
  flowConfig: '',
  enableFlag: true
})

const formRules: FormRules = {
  definitionName: [{ required: true, message: '请输入审批定义名称', trigger: 'blur' }],
  definitionCode: [{ required: true, message: '请输入审批定义编码', trigger: 'blur' }],
  businessType: [{ required: true, message: '请输入业务类型', trigger: 'blur' }]
}

let searchTimer: ReturnType<typeof setTimeout> | null = null

function handleSearchDebounced() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => handleSearch(), 300)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getDefinitionPage({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchData()
}

function handleReset() {
  searchForm.definitionName = ''
  searchForm.businessType = ''
  searchForm.enableFlag = undefined
  handleSearch()
}

function handleCreate() {
  dialogTitle.value = '新增审批定义'
  isEdit.value = false
  editId.value = null
  formData.definitionName = ''
  formData.definitionCode = ''
  formData.businessType = ''
  formData.flowConfig = ''
  formData.enableFlag = true
  dialogVisible.value = true
}

function handleEdit(row: DefinitionListVO) {
  dialogTitle.value = '编辑审批定义'
  isEdit.value = true
  editId.value = row.id
  formData.definitionName = row.definitionName
  formData.definitionCode = row.definitionCode
  formData.businessType = row.businessType
  formData.flowConfig = ''
  formData.enableFlag = row.enableFlag
  dialogVisible.value = true
}

function handleDialogClose() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value && editId.value) {
      const updateData: DefinitionUpdateDTO = {
        id: editId.value,
        definitionName: formData.definitionName,
        businessType: formData.businessType,
        flowConfig: formData.flowConfig || undefined,
        enableFlag: formData.enableFlag
      }
      await updateDefinition(updateData)
      ElMessage.success('修改成功')
    } else {
      const createData: DefinitionCreateDTO = {
        definitionName: formData.definitionName,
        definitionCode: formData.definitionCode,
        businessType: formData.businessType,
        flowConfig: formData.flowConfig || undefined,
        enableFlag: formData.enableFlag
      }
      await createDefinition(createData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

async function handleToggleEnable(row: DefinitionListVO, val: boolean) {
  try {
    await updateDefinition({ id: row.id, enableFlag: val })
    row.enableFlag = val
    ElMessage.success(val ? '已启用' : '已停用')
  } catch {
    // revert on failure handled by interceptor
  }
}

async function handleDelete(row: DefinitionListVO) {
  await ElMessageBox.confirm(`确定要删除审批定义「${row.definitionName}」吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await deleteDefinition(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-definition-page {
  padding: 16px;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
