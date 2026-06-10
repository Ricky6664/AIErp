<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑用户' : '新增用户'"
    width="720px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="formData.username" :disabled="isEdit" placeholder="请输入用户名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="formData.nickname" placeholder="请输入昵称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="密码" :prop="'password'" :required="!isEdit">
            <el-input
              v-model="formData.password"
              type="password"
              show-password
              :placeholder="isEdit ? '不填则不修改密码' : '请输入密码'"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio value="normal">正常</el-radio>
              <el-radio value="disabled">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="formData.mobile" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="所属部门" prop="deptIds">
        <el-tree-select
          v-model="formData.deptIds"
          :data="deptTree"
          :props="{ label: 'menuName', children: 'children' }"
          node-key="id"
          placeholder="请选择部门"
          multiple
          check-strictly
          filterable
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item v-if="formData.deptIds.length > 0" label="主部门" prop="primaryDeptId">
        <el-radio-group v-model="formData.primaryDeptId">
          <el-radio v-for="deptId in formData.deptIds" :key="deptId" :value="deptId">
            {{ getDeptLabel(deptId) }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="角色分配" prop="roleIds">
        <el-select v-model="formData.roleIds" multiple placeholder="请选择角色" style="width: 100%">
          <el-option
            v-for="role in roleList"
            :key="role.id"
            :label="role.roleName"
            :value="role.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="头像">
        <el-upload
          class="avatar-uploader"
          action="/api/common/upload"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError"
        >
          <img v-if="formData.avatar" :src="formData.avatar" class="avatar-preview" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { MenuItem } from '@/api/types/menu'
import { getMenuTree } from '@/api/modules/menu'
import { getRoleList, type RoleItem } from '@/api/modules/role'
import { getUserDetail, createUser, updateUser } from '@/api/modules/user'

const props = defineProps<{
  visible: boolean
  userId?: number
}>()

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = ref(false)
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val
    if (val) initForm()
  }
)
watch(dialogVisible, (val) => emit('update:visible', val))

const isEdit = computed(() => !!props.userId)

const deptTree = ref<MenuItem[]>([])
const roleList = ref<RoleItem[]>([])

interface FormData {
  username: string
  nickname: string
  password: string
  email: string
  mobile: string
  avatar: string
  status: string
  deptIds: number[]
  primaryDeptId: number | null
  roleIds: number[]
}

const defaultForm = (): FormData => ({
  username: '',
  nickname: '',
  password: '',
  email: '',
  mobile: '',
  avatar: '',
  status: 'normal',
  deptIds: [] as number[],
  primaryDeptId: null,
  roleIds: [] as number[]
})

const formData = ref<FormData>(defaultForm())
const submitting = ref(false)
const formRef = ref<FormInstance>()

function isPasswordComplex(pwd: string): boolean {
  let count = 0
  if (/[a-z]/.test(pwd)) count++
  if (/[A-Z]/.test(pwd)) count++
  if (/\d/.test(pwd)) count++
  if (/[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(pwd)) count++
  return count >= 3
}

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '用户名仅支持字母和数字', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [
    {
      validator: (_rule, value: string, callback) => {
        if (!isEdit.value && !value) {
          callback(new Error('请输入密码'))
        } else if (value && value.length > 0) {
          if (value.length < 8 || value.length > 32) {
            callback(new Error('密码长度为 8-32 位'))
          } else if (!isPasswordComplex(value)) {
            callback(new Error('密码需包含大小写字母、数字和特殊字符中的至少三种'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  mobile: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  roleIds: [{ type: 'array', required: true, message: '请至少选择一个角色', trigger: 'change' }]
}

function getDeptLabel(deptId: number): string {
  const find = (items: MenuItem[]): MenuItem | undefined => {
    for (const item of items) {
      if (item.id === deptId) return item
      if (item.children?.length) {
        const found = find(item.children)
        if (found) return found
      }
    }
    return undefined
  }
  const item = find(deptTree.value)
  return ((item as Record<string, unknown> | undefined)?.menuName as string) ?? String(deptId)
}

function beforeAvatarUpload(file: File): boolean {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isImage) {
    ElMessage.error('头像仅支持 JPG/PNG 格式')
    return false
  }
  if (file.size / 1024 / 1024 > 2) {
    ElMessage.error('头像大小不能超过 2MB')
    return false
  }
  return true
}

function handleAvatarSuccess(res: { data?: string; url?: string }): void {
  formData.value.avatar = res.data ?? res.url ?? ''
}

function handleAvatarError(): void {
  ElMessage.error('头像上传失败')
}

async function initForm(): Promise<void> {
  formData.value = defaultForm()
  formRef.value?.clearValidate()
  await Promise.all([fetchDeptTree(), fetchRoleList()])
  if (props.userId) {
    await loadUserDetail(props.userId)
  }
}

async function fetchDeptTree(): Promise<void> {
  try {
    deptTree.value = await getMenuTree()
  } catch {
    deptTree.value = []
  }
}

async function fetchRoleList(): Promise<void> {
  try {
    roleList.value = await getRoleList(true)
  } catch {
    roleList.value = []
  }
}

async function loadUserDetail(id: number): Promise<void> {
  try {
    const user = (await getUserDetail(id)) as unknown as Record<string, unknown>
    formData.value.username = (user.username as string) ?? ''
    formData.value.nickname = (user.nickname as string) ?? ''
    formData.value.email = (user.email as string) ?? ''
    formData.value.mobile = (user.mobile as string) ?? ''
    formData.value.avatar = (user.avatar as string) ?? ''
    formData.value.status = (user.status as string) ?? 'normal'
  } catch {
    ElMessage.error('加载用户详情失败')
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload: Record<string, unknown> = {
      username: formData.value.username,
      nickname: formData.value.nickname,
      email: formData.value.email,
      mobile: formData.value.mobile,
      avatar: formData.value.avatar,
      status: formData.value.status
    }

    if (isEdit.value && props.userId) {
      if (formData.value.password) {
        payload.password = formData.value.password
      }
      await updateUser(props.userId, payload)
    } else {
      payload.password = formData.value.password
      payload.deptIds = formData.value.deptIds
      payload.roleIds = formData.value.roleIds
      if (formData.value.primaryDeptId) {
        payload.primaryDeptId = formData.value.primaryDeptId
      }
      await createUser(payload)
    }

    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    emit('success')
    dialogVisible.value = false
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
</script>

<style scoped lang="scss">
.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    width: 80px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      border-color: var(--el-color-primary);
    }
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: var(--el-text-color-secondary);
  }

  .avatar-preview {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 6px;
  }
}
</style>
