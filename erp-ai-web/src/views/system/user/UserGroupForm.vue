<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑用户组' : '新增用户组'"
    width="560px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="组编码" prop="groupCode">
        <el-input
          v-model="formData.groupCode"
          :disabled="isEdit"
          placeholder="请输入组编码"
          maxlength="64"
        />
      </el-form-item>
      <el-form-item label="组名称" prop="groupName">
        <el-input v-model="formData.groupName" placeholder="请输入组名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="描述" prop="groupDesc">
        <el-input
          v-model="formData.groupDesc"
          type="textarea"
          :rows="3"
          placeholder="请输入描述"
          maxlength="255"
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="启用状态" prop="isEnabled">
            <el-switch v-model="formData.isEnabled" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序" prop="sortOrder">
            <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  createUserGroup,
  updateUserGroup,
  getUserGroupDetail,
  checkGroupCode
} from '@/api/modules/userGroup'
import type { UserGroupCreateDTO } from '@/types/userGroup'

const props = defineProps<{
  visible: boolean
  groupId?: number
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const isEdit = computed(() => props.groupId !== undefined && props.groupId > 0)

const formRef = ref<FormInstance>()
const submitting = ref(false)

const initialFormData = (): {
  groupCode: string
  groupName: string
  groupDesc: string
  isEnabled: boolean
  sortOrder: number
} => ({
  groupCode: '',
  groupName: '',
  groupDesc: '',
  isEnabled: true,
  sortOrder: 0
})

const formData = ref(initialFormData())

const validateGroupCode = (_rule: unknown, value: string, callback: (e?: Error) => void) => {
  if (!value) return callback(new Error('组编码不能为空'))
  checkGroupCode(value, props.groupId)
    .then((unique) => {
      if (!unique) callback(new Error('组编码已存在'))
      else callback()
    })
    .catch(() => callback())
}

const formRules: FormRules = {
  groupCode: [
    { required: true, message: '组编码不能为空', trigger: 'blur' },
    { validator: validateGroupCode, trigger: 'blur' }
  ],
  groupName: [{ required: true, message: '组名称不能为空', trigger: 'blur' }]
}

watch(
  () => props.groupId,
  async (id) => {
    if (id !== undefined && id > 0) {
      try {
        const detail = await getUserGroupDetail(id)
        formData.value = {
          groupCode: detail.groupCode,
          groupName: detail.groupName,
          groupDesc: detail.groupDesc || '',
          isEnabled: detail.isEnabled,
          sortOrder: detail.sortOrder
        }
      } catch {
        ElMessage.error('加载用户组详情失败')
        dialogVisible.value = false
      }
    } else {
      formData.value = initialFormData()
    }
  },
  { immediate: true }
)

function handleClosed(): void {
  formRef.value?.resetFields()
  formData.value = initialFormData()
}

async function handleSubmit(): Promise<void> {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    if (isEdit.value && props.groupId) {
      await updateUserGroup(props.groupId, formData.value)
      ElMessage.success('修改成功')
    } else {
      await createUserGroup(formData.value as UserGroupCreateDTO)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('success')
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}
</script>
