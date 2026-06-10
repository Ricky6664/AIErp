<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`用户角色分配 - ${username}`"
    width="560px"
    destroy-on-close
    @closed="handleClosed"
  >
    <div class="role-assign-body">
      <div class="role-toolbar">
        <el-button size="small" @click="selectAll">全选</el-button>
        <el-button size="small" @click="clearAll">清空</el-button>
      </div>

      <el-checkbox-group v-model="selectedRoleIds" class="role-checkbox-group">
        <div v-for="role in roleList" :key="role.id" class="role-item">
          <el-checkbox
            :value="role.id"
            :disabled="isSuperadminRole(role) && !isCurrentUserSuperadmin"
          >
            <span class="role-name">{{ role.roleName }}</span>
            <span class="role-code">{{ role.roleCode }}</span>
            <span v-if="role.roleDesc" class="role-desc">{{ role.roleDesc }}</span>
          </el-checkbox>
          <el-alert
            v-if="exclusionWarnings.get(role.id)"
            :title="exclusionWarnings.get(role.id)"
            type="error"
            :closable="false"
            class="exclusion-warning"
          />
        </div>
      </el-checkbox-group>

      <el-empty v-if="roleList.length === 0" description="暂无可分配的角色" />

      <el-alert
        v-if="changeSummary"
        :title="changeSummary"
        type="info"
        :closable="false"
        show-icon
        class="change-summary"
      />
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" :disabled="!hasChanges" @click="handleSave">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { RoleItem } from '@/api/modules/role'
import { getRoleList, getRoleExclusions } from '@/api/modules/role'
import { getUserDetail, assignUserRoles } from '@/api/modules/user'

const props = defineProps<{
  visible: boolean
  userId: number
  username: string
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
    if (val) initDialog()
  }
)
watch(dialogVisible, (val) => emit('update:visible', val))

const roleList = ref<RoleItem[]>([])
const selectedRoleIds = ref<number[]>([])
const initialRoleIds = ref<number[]>([])
const exclusionMap = ref<Map<number, Set<number>>>(new Map())
const saving = ref(false)

const hasChanges = computed(() => {
  if (initialRoleIds.value.length !== selectedRoleIds.value.length) return true
  const sorted1 = [...initialRoleIds.value].sort()
  const sorted2 = [...selectedRoleIds.value].sort()
  return sorted1.some((id, i) => id !== sorted2[i])
})

const changeSummary = computed(() => {
  if (!hasChanges.value) return ''
  const added = selectedRoleIds.value.filter((id) => !initialRoleIds.value.includes(id))
  const removed = initialRoleIds.value.filter((id) => !selectedRoleIds.value.includes(id))
  const parts: string[] = []
  if (added.length > 0) {
    const names = added.map((id) => roleList.value.find((r) => r.id === id)?.roleName ?? String(id))
    parts.push(`新增 ${added.length} 个角色: ${names.join('、')}`)
  }
  if (removed.length > 0) {
    const names = removed.map(
      (id) => roleList.value.find((r) => r.id === id)?.roleName ?? String(id)
    )
    parts.push(`移除 ${removed.length} 个角色: ${names.join('、')}`)
  }
  return parts.join('；')
})

const isCurrentUserSuperadmin = computed(() => {
  return initialRoleIds.value.some((id) => {
    const role = roleList.value.find((r) => r.id === id)
    return role?.roleCode === 'superadmin'
  })
})

function isSuperadminRole(role: RoleItem): boolean {
  return role.roleCode === 'superadmin'
}

const exclusionWarnings = computed(() => {
  const warnings = new Map<number, string>()
  for (let i = 0; i < selectedRoleIds.value.length; i++) {
    const roleId = selectedRoleIds.value[i]
    const excluded = exclusionMap.value.get(roleId)
    if (!excluded) continue
    for (let j = i + 1; j < selectedRoleIds.value.length; j++) {
      const otherId = selectedRoleIds.value[j]
      if (excluded.has(otherId)) {
        const roleA = roleList.value.find((r) => r.id === roleId)?.roleName ?? String(roleId)
        const roleB = roleList.value.find((r) => r.id === otherId)?.roleName ?? String(otherId)
        warnings.set(roleId, `角色互斥：${roleA} 与 ${roleB} 不可同时分配`)
        warnings.set(otherId, `角色互斥：${roleA} 与 ${roleB} 不可同时分配`)
      }
    }
  }
  return warnings
})

async function initDialog(): Promise<void> {
  selectedRoleIds.value = []
  initialRoleIds.value = []
  exclusionMap.value = new Map()
  await Promise.all([fetchRoleList(), fetchUserRoles()])
}

async function fetchRoleList(): Promise<void> {
  try {
    roleList.value = await getRoleList(true)
    await fetchExclusions()
  } catch {
    roleList.value = []
  }
}

async function fetchUserRoles(): Promise<void> {
  if (!props.userId) return
  try {
    const user = (await getUserDetail(props.userId)) as unknown as Record<string, unknown>
    const roleNames = (user.roleNames as string[]) ?? []
    const ids = roleNames
      .map((name) => roleList.value.find((r) => r.roleName === name)?.id)
      .filter((id): id is number => id !== undefined)
    selectedRoleIds.value = [...ids]
    initialRoleIds.value = [...ids]
  } catch {
    selectedRoleIds.value = []
    initialRoleIds.value = []
  }
}

async function fetchExclusions(): Promise<void> {
  const map = new Map<number, Set<number>>()
  const results = await Promise.allSettled(roleList.value.map((role) => getRoleExclusions(role.id)))
  results.forEach((result, index) => {
    if (result.status === 'fulfilled' && result.value.length > 0) {
      map.set(roleList.value[index].id, new Set(result.value))
    }
  })
  exclusionMap.value = map
}

function selectAll(): void {
  selectedRoleIds.value = roleList.value
    .filter((r) => !isSuperadminRole(r) || isCurrentUserSuperadmin.value)
    .map((r) => r.id)
}

function clearAll(): void {
  selectedRoleIds.value = []
}

async function handleSave(): Promise<void> {
  if (exclusionWarnings.value.size > 0) {
    ElMessage.warning('存在互斥角色，请先取消互斥的角色选择')
    return
  }
  try {
    await ElMessageBox.confirm('确定保存角色分配变更？', '保存确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  saving.value = true
  try {
    await assignUserRoles(props.userId, selectedRoleIds.value)
    ElMessage.success('角色分配成功')
    initialRoleIds.value = [...selectedRoleIds.value]
    emit('success')
    dialogVisible.value = false
  } catch {
    // error handled by request interceptor
  } finally {
    saving.value = false
  }
}

function handleClosed(): void {
  roleList.value = []
  selectedRoleIds.value = []
  initialRoleIds.value = []
  exclusionMap.value = new Map()
}
</script>

<style scoped lang="scss">
.role-assign-body {
  .role-toolbar {
    margin-bottom: 12px;
    display: flex;
    gap: 8px;
  }

  .role-checkbox-group {
    display: flex;
    flex-direction: column;
    gap: 4px;
    max-height: 360px;
    overflow-y: auto;
  }

  .role-item {
    padding: 8px 12px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 4px;

    &:hover {
      background-color: var(--el-fill-color-light);
    }
  }

  .role-name {
    font-weight: 600;
    margin-right: 8px;
  }

  .role-code {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    background: var(--el-fill-color);
    padding: 1px 6px;
    border-radius: 3px;
    margin-right: 8px;
  }

  .role-desc {
    font-size: 12px;
    color: var(--el-text-color-placeholder);
  }

  .exclusion-warning {
    margin-top: 6px;
  }

  .change-summary {
    margin-top: 12px;
  }
}
</style>
