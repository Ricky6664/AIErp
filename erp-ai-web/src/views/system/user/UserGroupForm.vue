<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑用户组' : '新增用户组'"
    width="720px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="组编码" prop="groupCode">
        <el-input
          v-model="formData.groupCode"
          :disabled="isEdit"
          placeholder="请输入组编码"
          maxlength="30"
        />
      </el-form-item>
      <el-form-item label="组名称" prop="groupName">
        <el-input v-model="formData.groupName" placeholder="请输入组名称" maxlength="50" />
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

      <el-divider content-position="left">成员管理</el-divider>
      <el-form-item label="组成员" prop="memberUserIds">
        <div class="transfer-wrapper">
          <el-transfer
            v-model="formData.memberUserIds"
            :data="userTransferData"
            :titles="['可选用户', '已选成员']"
            :button-texts="['移除', '添加']"
            filterable
            :filter-method="filterUser"
            filter-placeholder="搜索用户名 / 昵称"
            style="width: 100%"
          />
        </div>
      </el-form-item>

      <el-divider content-position="left">角色分配</el-divider>
      <el-form-item label="继承角色" prop="roleIds">
        <el-checkbox-group v-model="formData.roleIds">
          <el-checkbox
            v-for="role in availableRoles"
            :key="role.id"
            :label="role.id"
            :value="role.id"
          >
            {{ role.roleName }}
          </el-checkbox>
        </el-checkbox-group>
        <p class="role-hint">用户组成员将自动继承所选角色的权限，与用户直接分配的角色权限叠加</p>
      </el-form-item>
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
import { getUserPageList } from '@/api/modules/user'
import { getRoleList, type RoleItem } from '@/api/modules/role'
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

interface TransferItem {
  key: number
  label: string
  disabled: boolean
}

const userTransferData = ref<TransferItem[]>([])
const availableRoles = ref<RoleItem[]>([])
const allUsers = ref<TransferItem[]>([])

const initialFormData = (): {
  groupCode: string
  groupName: string
  groupDesc: string
  isEnabled: boolean
  sortOrder: number
  memberUserIds: number[]
  roleIds: number[]
} => ({
  groupCode: '',
  groupName: '',
  groupDesc: '',
  isEnabled: true,
  sortOrder: 0,
  memberUserIds: [],
  roleIds: []
})

const formData = ref(initialFormData())

const validateGroupCode = (_rule: unknown, value: string, callback: (e?: Error) => void) => {
  if (!value) return callback(new Error('组编码不能为空'))
  if (!/^[a-zA-Z0-9_]{2,30}$/.test(value)) {
    return callback(new Error('组编码需2-30位，仅允许字母、数字、下划线'))
  }
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
  groupName: [
    { required: true, message: '组名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '组名称需2-50个字符', trigger: 'blur' }
  ]
}

async function loadReferenceData(): Promise<void> {
  try {
    const [users, roles] = await Promise.all([
      getUserPageList({ pageNum: 1, pageSize: 9999 }),
      getRoleList()
    ])
    const items: TransferItem[] = (users.list || []).map((u) => ({
      key: u.id,
      label: `${u.username} (${u.nickname || u.username})`,
      disabled: false
    }))
    allUsers.value = items
    userTransferData.value = [...items]
    availableRoles.value = roles
  } catch {
    // ignore reference data load failure
  }
}

function filterUser(query: string, item: TransferItem): boolean {
  return item.label.toLowerCase().includes(query.toLowerCase())
}

watch(
  () => props.visible,
  async (val) => {
    if (val) {
      await loadReferenceData()
      if (props.groupId !== undefined && props.groupId > 0) {
        await loadGroupDetail()
      } else {
        formData.value = initialFormData()
      }
    }
  }
)

async function loadGroupDetail(): Promise<void> {
  if (!props.groupId) return
  try {
    const detail = await getUserGroupDetail(props.groupId)
    formData.value = {
      groupCode: detail.groupCode,
      groupName: detail.groupName,
      groupDesc: detail.groupDesc || '',
      isEnabled: detail.isEnabled,
      sortOrder: detail.sortOrder,
      memberUserIds: detail.memberUserIds || [],
      roleIds: detail.roleIds || []
    }
  } catch {
    ElMessage.error('加载用户组详情失败')
    dialogVisible.value = false
  }
}

function handleClosed(): void {
  formRef.value?.resetFields()
  formData.value = initialFormData()
  userTransferData.value = []
  allUsers.value = []
  availableRoles.value = []
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
    const payload: UserGroupCreateDTO = {
      groupCode: formData.value.groupCode,
      groupName: formData.value.groupName,
      groupDesc: formData.value.groupDesc,
      isEnabled: formData.value.isEnabled,
      sortOrder: formData.value.sortOrder,
      memberUserIds: formData.value.memberUserIds,
      roleIds: formData.value.roleIds
    }
    if (isEdit.value && props.groupId) {
      await updateUserGroup(props.groupId, payload)
      ElMessage.success('修改成功')
    } else {
      await createUserGroup(payload)
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

<style scoped lang="scss">
.transfer-wrapper {
  width: 100%;
}

.role-hint {
  margin: 8px 0 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
}
</style>
