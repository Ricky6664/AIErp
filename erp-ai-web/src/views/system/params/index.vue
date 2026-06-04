<template>
  <div class="system-params-page">
    <div class="page-header">
      <h2>系统参数前端加载</h2>
      <p class="page-desc">管理系统运行参数，支持按分类加载、增删改查及缓存刷新</p>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchCategory"
          placeholder="请输入参数分类（如 system、email）"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #prepend>分类</template>
        </el-input>
        <el-button type="primary" :loading="loading" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button v-if="currentCategory" type="success" @click="openCreateDialog(currentCategory)">
          <el-icon><Plus /></el-icon>
          新增参数
        </el-button>
        <el-button :loading="loading" @click="handleRefreshCache">
          <el-icon><RefreshRight /></el-icon>
          刷新缓存
        </el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="paramList"
      border
      stripe
      empty-text="请输入分类后点击查询"
      style="width: 100%"
    >
      <el-table-column prop="paramCategory" label="分类" width="140" />
      <el-table-column prop="paramKey" label="参数键" width="200" />
      <el-table-column prop="paramValue" label="参数值" min-width="200">
        <template #default="{ row }">
          <span v-if="row.paramValue && row.paramValue.length > 80" :title="row.paramValue">
            {{ row.paramValue.substring(0, 80) }}...
          </span>
          <span v-else>{{ row.paramValue }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="valueType" label="值类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="valueTypeTag(row.valueType)" size="small">
            {{ valueTypeLabel(row.valueType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="说明" min-width="150" show-overflow-tooltip />
      <el-table-column prop="isSystem" label="系统参数" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isSystem === 1 ? 'warning' : 'info'" size="small">
            {{ row.isSystem === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="openEditDialog(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-popconfirm
            title="确定删除该参数？"
            @confirm="handleDelete(row.paramCategory, row.paramKey)"
          >
            <template #reference>
              <el-button type="danger" link size="small" :disabled="row.isSystem === 1">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="分类" prop="category">
          <el-input v-model="formData.category" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="参数键" prop="key">
          <el-input v-model="formData.key" :disabled="isEdit" placeholder="如 site_name" />
        </el-form-item>
        <el-form-item label="参数值" prop="value">
          <el-input
            v-if="formData.valueType !== 4"
            v-model="formData.value"
            :type="formData.valueType === 2 ? 'number' : 'text'"
            placeholder="请输入参数值"
          />
          <el-input
            v-else
            v-model="formData.value"
            type="textarea"
            :rows="4"
            placeholder="请输入 JSON 格式的参数值"
          />
        </el-form-item>
        <el-form-item label="值类型" prop="valueType">
          <el-select v-model="formData.valueType" style="width: 100%">
            <el-option :value="1" label="字符串" />
            <el-option :value="2" label="数字" />
            <el-option :value="3" label="布尔" />
            <el-option :value="4" label="JSON" />
            <el-option :value="5" label="日期" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明" prop="description">
          <el-input v-model="formData.description" placeholder="参数用途说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, Plus, RefreshRight, Edit, Delete } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useSystemParam } from '@/composables/useSystemParam'

const {
  loading,
  paramList,
  currentCategory,
  dialogVisible,
  dialogTitle,
  formData,
  isEdit,
  fetchParams,
  openCreateDialog,
  openEditDialog,
  handleSubmit: composableSubmit,
  handleDelete: composableDelete,
  handleRefreshCache
} = useSystemParam()

const searchCategory = ref('')
const formRef = ref<FormInstance>()

const formRules: FormRules = {
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }],
  key: [{ required: true, message: '请输入参数键', trigger: 'blur' }],
  value: [{ required: true, message: '请输入参数值', trigger: 'blur' }]
}

function valueTypeLabel(type: number): string {
  const map: Record<number, string> = { 1: '字符串', 2: '数字', 3: '布尔', 4: 'JSON', 5: '日期' }
  return map[type] ?? '未知'
}

function valueTypeTag(type: number): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<number, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    1: '',
    2: 'warning',
    3: 'info',
    4: 'danger',
    5: 'success'
  }
  return map[type] ?? ''
}

function handleSearch(): void {
  if (!searchCategory.value.trim()) return
  fetchParams(searchCategory.value.trim())
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  await composableSubmit()
}

function handleDelete(category: string, key: string): void {
  composableDelete(category, key)
}

function resetForm(): void {
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.system-params-page {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0 0 8px;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-desc {
      margin: 0;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    flex-wrap: wrap;
    gap: 12px;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }
}
</style>
