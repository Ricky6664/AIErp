import { ref } from 'vue'
import {
  getParamListApi,
  createParamApi,
  updateParamApi,
  deleteParamApi,
  refreshParamCacheApi
} from '@/api/modules/system'
import type { SysParamItem, SysParamCreateDTO } from '@/types/system'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useSystemParam() {
  const loading = ref(false)
  const paramList = ref<SysParamItem[]>([])
  const currentCategory = ref('')

  const dialogVisible = ref(false)
  const dialogTitle = ref('')
  const formData = ref<SysParamCreateDTO>({
    category: '',
    key: '',
    value: '',
    valueType: 1,
    description: ''
  })
  const isEdit = ref(false)

  async function fetchParams(category: string): Promise<void> {
    loading.value = true
    try {
      const data = await getParamListApi(category)
      paramList.value = data ?? []
      currentCategory.value = category
    } catch {
      paramList.value = []
    } finally {
      loading.value = false
    }
  }

  function openCreateDialog(category: string): void {
    isEdit.value = false
    dialogTitle.value = '新增参数'
    formData.value = { category, key: '', value: '', valueType: 1, description: '' }
    dialogVisible.value = true
  }

  function openEditDialog(item: SysParamItem): void {
    isEdit.value = true
    dialogTitle.value = '编辑参数'
    formData.value = {
      category: item.paramCategory,
      key: item.paramKey,
      value: item.paramValue,
      valueType: item.valueType,
      description: item.description
    }
    dialogVisible.value = true
  }

  async function handleSubmit(): Promise<void> {
    loading.value = true
    try {
      if (isEdit.value) {
        await updateParamApi(formData.value.category, formData.value.key, {
          value: formData.value.value,
          valueType: formData.value.valueType,
          description: formData.value.description
        })
        ElMessage.success('参数修改成功')
      } else {
        await createParamApi(formData.value)
        ElMessage.success('参数新增成功')
      }
      dialogVisible.value = false
      await fetchParams(currentCategory.value)
    } finally {
      loading.value = false
    }
  }

  async function handleDelete(category: string, key: string): Promise<void> {
    try {
      await ElMessageBox.confirm(`确定要删除参数「${key}」吗？`, '删除确认', {
        type: 'warning'
      })
      await deleteParamApi(category, key)
      ElMessage.success('参数删除成功')
      await fetchParams(category)
    } catch {
      // user cancelled or error handled by interceptor
    }
  }

  async function handleRefreshCache(): Promise<void> {
    loading.value = true
    try {
      const count = await refreshParamCacheApi()
      ElMessage.success(`缓存刷新成功，已清除 ${count ?? 0} 个缓存键`)
    } finally {
      loading.value = false
    }
  }

  return {
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
    handleSubmit,
    handleDelete,
    handleRefreshCache
  }
}
