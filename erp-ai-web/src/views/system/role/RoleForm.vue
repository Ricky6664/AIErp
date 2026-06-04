<template>
  <div class="role-form-page">
    <div class="page-header">
      <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
      <h2>{{ isEdit ? '编辑角色' : '新增角色' }}</h2>
      <p v-if="isEdit" class="page-subtitle">角色编码：{{ formData.roleCode }}</p>
    </div>

    <div class="form-card">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input
                v-model="formData.roleName"
                placeholder="请输入角色名称"
                :disabled="isSuperadmin"
                maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色编码" prop="roleCode">
              <el-input
                v-model="formData.roleCode"
                placeholder="小写字母+数字+下划线"
                :disabled="isEdit || isSuperadmin"
                maxlength="30"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="roleDesc">
          <el-input
            v-model="formData.roleDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="启用状态" prop="isEnabled">
              <el-switch v-model="formData.isEnabled" :disabled="isSuperadmin" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number
                v-model="formData.sortOrder"
                :min="0"
                :max="999"
                :disabled="isSuperadmin"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">数据权限</el-divider>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select
            v-model="formData.dataScope"
            :disabled="isSuperadmin"
            style="width: 240px"
            @change="handleDataScopeChange"
          >
            <el-option label="全部数据" value="all" />
            <el-option label="本部门" value="dept" />
            <el-option label="本部门及下级" value="dept_and_below" />
            <el-option label="仅本人" value="self" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <template v-if="formData.dataScope === 'custom'">
          <el-form-item label="自定义部门">
            <el-tree
              ref="deptTreeRef"
              :data="deptTree"
              :props="{ label: 'name', children: 'children' }"
              node-key="id"
              show-checkbox
              check-strictly
              :default-checked-keys="formData.customDeptIds"
              @check="handleDeptCheck"
            />
          </el-form-item>
          <el-form-item label="自定义人员">
            <el-select
              v-model="formData.customUserIds"
              multiple
              filterable
              remote
              reserve-keyword
              placeholder="请输入人员姓名搜索"
              :remote-method="searchUsers"
              :loading="userSearchLoading"
              style="width: 100%"
            >
              <el-option
                v-for="user in userOptions"
                :key="user.id"
                :label="user.realName || user.username"
                :value="user.id"
              />
            </el-select>
          </el-form-item>
        </template>

        <el-divider content-position="left">角色关系</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="父角色">
              <el-select
                v-model="formData.parentRoleId"
                placeholder="请选择父角色(可选)"
                clearable
                :disabled="isSuperadmin"
                style="width: 100%"
              >
                <el-option
                  v-for="role in availableParentRoles"
                  :key="role.id"
                  :label="role.roleName"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="互斥角色">
              <el-select
                v-model="formData.exclusionRoleIds"
                multiple
                placeholder="请选择互斥角色"
                clearable
                :disabled="isSuperadmin"
                style="width: 100%"
              >
                <el-option
                  v-for="role in availableExclusionRoles"
                  :key="role.id"
                  :label="role.roleName"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="form-footer">
      <el-button @click="handleBack">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit"> 确定 </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import {
  createRole,
  updateRole,
  getRoleDetail,
  checkRoleCode,
  getRoleList,
  getRoleExclusions,
  getParentRoleIds,
  getChildRoleIds,
  addInheritance,
  removeInheritance,
  addExclusion,
  removeExclusion
} from '@/api/modules/role'
import { getDeptTree } from '@/api/modules/system'
import { getUserPageList } from '@/api/modules/user'
import type { UserListItem } from '@/types/user'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => route.query.mode === 'edit')
const editId = computed(() => (route.query.id ? Number(route.query.id) : null))
const isSuperadmin = ref(false)

const formRef = ref<FormInstance>()
const submitting = ref(false)
const userSearchLoading = ref(false)

interface RoleFormData {
  roleName: string
  roleCode: string
  roleDesc: string
  dataScope: string
  isEnabled: boolean
  sortOrder: number
  parentRoleId: number | null
  exclusionRoleIds: number[]
  customDeptIds: number[]
  customUserIds: number[]
}

const defaultForm = (): RoleFormData => ({
  roleName: '',
  roleCode: '',
  roleDesc: '',
  dataScope: 'all',
  isEnabled: true,
  sortOrder: 0,
  parentRoleId: null,
  exclusionRoleIds: [],
  customDeptIds: [],
  customUserIds: []
})

const formData = reactive<RoleFormData>(defaultForm())

const formRules: FormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度为 2-50 字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 30, message: '角色编码长度为 2-30 字符', trigger: 'blur' },
    {
      pattern: /^[a-z0-9_]+$/,
      message: '角色编码仅支持小写字母、数字和下划线',
      trigger: 'blur'
    },
    {
      validator: (_rule, value: string, callback) => {
        if (!value || isEdit.value) {
          callback()
          return
        }
        checkRoleCode(value)
          .then((exists) => {
            if (exists) {
              callback(new Error('角色编码已存在'))
            } else {
              callback()
            }
          })
          .catch(() => callback())
      },
      trigger: 'blur'
    }
  ]
}

// ---- reference data ----

interface DeptNode {
  id: number
  name: string
  children?: DeptNode[]
}

const deptTree = ref<DeptNode[]>([])

interface RoleOption {
  id: number
  roleCode: string
  roleName: string
  roleDesc?: string
  isEnabled: boolean
}

const allRoles = ref<RoleOption[]>([])
const childRoleIds = ref<number[]>([])
const userOptions = ref<UserListItem[]>([])

// original values from edit load, for diff on save
const originalParentRoleId = ref<number | null>(null)
const originalExclusionRoleIds = ref<number[]>([])

const availableParentRoles = computed(() =>
  allRoles.value.filter((r) => r.id !== editId.value && !childRoleIds.value.includes(r.id))
)

const availableExclusionRoles = computed(() => allRoles.value.filter((r) => r.id !== editId.value))

// ---- lifecycle ----

onMounted(async () => {
  await Promise.all([fetchDeptTree(), fetchAllRoles()])
  if (isEdit.value && editId.value) {
    await loadRoleDetail(editId.value)
  }
})

async function fetchDeptTree(): Promise<void> {
  try {
    deptTree.value = await getDeptTree()
  } catch {
    deptTree.value = []
  }
}

async function fetchAllRoles(): Promise<void> {
  try {
    allRoles.value = await getRoleList()
  } catch {
    allRoles.value = []
  }
}

async function loadRoleDetail(id: number): Promise<void> {
  try {
    const [detail, parents, exclusions, children] = await Promise.all([
      getRoleDetail(id),
      getParentRoleIds(id).catch(() => [] as number[]),
      getRoleExclusions(id).catch(() => [] as number[]),
      getChildRoleIds(id).catch(() => [] as number[])
    ])

    formData.roleName = detail.roleName
    formData.roleCode = detail.roleCode
    formData.roleDesc = detail.roleDesc || ''
    formData.dataScope = detail.dataScope || 'all'
    formData.isEnabled = detail.isEnabled
    formData.sortOrder = detail.sortOrder ?? 0

    if (parents.length > 0) {
      formData.parentRoleId = parents[0]
      originalParentRoleId.value = parents[0]
    }
    formData.exclusionRoleIds = exclusions
    originalExclusionRoleIds.value = [...exclusions]
    childRoleIds.value = children

    if (detail.roleCode === 'superadmin') {
      isSuperadmin.value = true
    }
  } catch {
    ElMessage.error('加载角色详情失败')
    router.back()
  }
}

// ---- event handlers ----

function handleDataScopeChange(value: string): void {
  if (value !== 'custom') {
    formData.customDeptIds = []
    formData.customUserIds = []
  }
}

function handleDeptCheck(_node: unknown, checked: { checkedKeys: (string | number)[] }): void {
  formData.customDeptIds = checked.checkedKeys.map(Number)
}

async function searchUsers(query: string): Promise<void> {
  if (!query) {
    userOptions.value = []
    return
  }
  userSearchLoading.value = true
  try {
    const result = await getUserPageList({ pageNum: 1, pageSize: 20, keyword: query })
    userOptions.value = result.list ?? []
  } catch {
    userOptions.value = []
  } finally {
    userSearchLoading.value = false
  }
}

function handleBack(): void {
  router.back()
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const baseData = {
      roleCode: formData.roleCode,
      roleName: formData.roleName,
      roleDesc: formData.roleDesc || undefined,
      dataScope: formData.dataScope,
      isEnabled: formData.isEnabled,
      sortOrder: formData.sortOrder
    }

    let roleId: number

    if (isEdit.value && editId.value) {
      await updateRole(editId.value, { id: editId.value, ...baseData })
      roleId = editId.value
    } else {
      roleId = await createRole(baseData)
    }

    await saveInheritance(roleId)
    await saveExclusions(roleId)

    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    router.back()
  } catch {
    // handled by request interceptor
  } finally {
    submitting.value = false
  }
}

async function saveInheritance(roleId: number): Promise<void> {
  const newParentId = formData.parentRoleId
  const oldParentId = originalParentRoleId.value

  if (oldParentId && oldParentId !== newParentId) {
    await removeInheritance(oldParentId, roleId).catch(() => {})
  }
  if (newParentId && newParentId !== oldParentId) {
    await addInheritance(newParentId, roleId).catch(() => {})
  }
}

async function saveExclusions(roleId: number): Promise<void> {
  const newIds = formData.exclusionRoleIds
  const oldIds = originalExclusionRoleIds.value

  const toAdd = newIds.filter((id) => !oldIds.includes(id))
  const toRemove = oldIds.filter((id) => !newIds.includes(id))

  for (const exclusionId of toRemove) {
    await removeExclusion(roleId, exclusionId).catch(() => {})
  }
  for (const exclusionId of toAdd) {
    await addExclusion(roleId, exclusionId).catch(() => {})
  }
}
</script>

<style scoped lang="scss">
.role-form-page {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;

  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-subtitle {
      margin: 0;
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }

  .form-card {
    background: var(--el-bg-color);
    border: 1px solid var(--el-border-color-light);
    border-radius: 8px;
    padding: 24px;
  }

  .form-footer {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}
</style>
