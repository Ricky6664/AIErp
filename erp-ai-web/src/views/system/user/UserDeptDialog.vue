<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`用户部门关联 - ${username}`"
    width="600px"
    destroy-on-close
    @closed="handleClosed"
  >
    <div class="dept-body">
      <div class="dept-content">
        <div class="dept-tree-panel">
          <div class="panel-title">权限部门</div>
          <el-tree
            ref="treeRef"
            :data="deptTree"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            show-checkbox
            default-expand-all
            @check="handleTreeCheck"
          />
          <el-empty v-if="deptTree.length === 0" description="暂无可选部门" :image-size="60" />
        </div>

        <div class="dept-selected-panel">
          <div class="panel-title">主部门</div>
          <el-empty
            v-if="selectedDeptIds.length === 0"
            description="暂未选择部门"
            :image-size="60"
          />
          <el-radio-group v-else v-model="primaryDeptId" class="dept-radio-group">
            <div v-for="dept in selectedDepts" :key="dept.id" class="dept-radio-item">
              <el-radio :value="dept.id">{{ dept.name }}</el-radio>
            </div>
          </el-radio-group>
        </div>
      </div>

      <div class="dept-toolbar">
        <el-button
          size="small"
          type="danger"
          :disabled="selectedDeptIds.length === 0"
          @click="handleClearAll"
        >
          清除全部
        </el-button>
      </div>

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
import { ref, watch, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserDetail,
  assignUserDepts,
  clearUserDepts,
  type DeptTreeNode
} from '@/api/modules/user'
import { getDeptTree } from '@/api/modules/system'

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

const treeRef = ref<InstanceType<typeof import('element-plus').ElTree> | null>(null)
const deptTree = ref<DeptTreeNode[]>([])
const selectedDeptIds = ref<number[]>([])
const initialDeptIds = ref<number[]>([])
const initialPrimaryDeptId = ref<number | null>(null)
const primaryDeptId = ref<number | null>(null)
const saving = ref(false)

interface SelectedDept {
  id: number
  name: string
}

const selectedDepts = computed<SelectedDept[]>(() => {
  const nameMap = new Map<number, string>()
  function collectNames(nodes: DeptTreeNode[]) {
    for (const node of nodes) {
      nameMap.set(node.id, node.name)
      if (node.children) collectNames(node.children)
    }
  }
  collectNames(deptTree.value)
  return selectedDeptIds.value.map((id) => ({
    id,
    name: nameMap.get(id) ?? String(id)
  }))
})

const hasChanges = computed(() => {
  if (primaryDeptId.value !== initialPrimaryDeptId.value) return true
  const sorted1 = [...initialDeptIds.value].sort()
  const sorted2 = [...selectedDeptIds.value].sort()
  return sorted1.length !== sorted2.length || sorted1.some((id, i) => id !== sorted2[i])
})

const changeSummary = computed(() => {
  if (!hasChanges.value) return ''
  const added = selectedDeptIds.value.filter((id) => !initialDeptIds.value.includes(id))
  const removed = initialDeptIds.value.filter((id) => !selectedDeptIds.value.includes(id))
  const parts: string[] = []
  if (added.length > 0) {
    const names = added.map((id) => {
      const d = selectedDepts.value.find((s) => s.id === id)
      return d?.name ?? String(id)
    })
    parts.push(`新增 ${added.length} 个部门: ${names.join('、')}`)
  }
  if (removed.length > 0) {
    const nameMap = new Map<number, string>()
    function collectNames(nodes: DeptTreeNode[]) {
      for (const node of nodes) {
        nameMap.set(node.id, node.name)
        if (node.children) collectNames(node.children)
      }
    }
    collectNames(deptTree.value)
    const names = removed.map((id) => nameMap.get(id) ?? String(id))
    parts.push(`移除 ${removed.length} 个部门: ${names.join('、')}`)
  }
  if (primaryDeptId.value !== initialPrimaryDeptId.value && primaryDeptId.value != null) {
    const newPrimary = selectedDepts.value.find((d) => d.id === primaryDeptId.value)
    parts.push(`主部门变更为: ${newPrimary?.name ?? String(primaryDeptId.value)}`)
  }
  return parts.join('；')
})

function handleTreeCheck(
  _node: unknown,
  data: { checkedKeys: number[]; halfCheckedKeys: number[] }
): void {
  const { checkedKeys, halfCheckedKeys } = data
  const allChecked = [...checkedKeys, ...halfCheckedKeys]

  if (primaryDeptId.value != null && !allChecked.includes(primaryDeptId.value)) {
    ElMessage.warning('请先更换主部门，再取消当前主部门的勾选')
    nextTick(() => {
      setTreeCheckedKeys([...selectedDeptIds.value])
    })
    return
  }

  selectedDeptIds.value = allChecked

  if (primaryDeptId.value != null && !selectedDeptIds.value.includes(primaryDeptId.value)) {
    primaryDeptId.value = selectedDeptIds.value.length > 0 ? selectedDeptIds.value[0] : null
  }
}

function setTreeCheckedKeys(ids: number[]): void {
  const tree = treeRef.value
  if (!tree) return
  tree.setCheckedKeys(ids)
}

async function initDialog(): Promise<void> {
  selectedDeptIds.value = []
  initialDeptIds.value = []
  primaryDeptId.value = null
  initialPrimaryDeptId.value = null
  deptTree.value = []
  await Promise.all([fetchDeptTree(), fetchUserDepts()])
}

async function fetchDeptTree(): Promise<void> {
  try {
    deptTree.value = await getDeptTree()
  } catch {
    deptTree.value = []
  }
}

async function fetchUserDepts(): Promise<void> {
  if (!props.userId) return
  try {
    const user = (await getUserDetail(props.userId)) as unknown as Record<string, unknown>
    const deptIds = (user.deptIds as number[]) ?? []
    const primary = (user.primaryDeptId as number) ?? null
    selectedDeptIds.value = [...deptIds]
    initialDeptIds.value = [...deptIds]
    primaryDeptId.value = primary
    initialPrimaryDeptId.value = primary
    nextTick(() => {
      setTreeCheckedKeys(deptIds)
    })
  } catch {
    selectedDeptIds.value = []
    initialDeptIds.value = []
    primaryDeptId.value = null
    initialPrimaryDeptId.value = null
  }
}

async function handleClearAll(): Promise<void> {
  try {
    await ElMessageBox.confirm(`确定清除用户 ${props.username} 的所有部门关联？`, '清除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await clearUserDepts(props.userId)
    ElMessage.success('已清除所有部门关联')
    selectedDeptIds.value = []
    primaryDeptId.value = null
    initialDeptIds.value = []
    initialPrimaryDeptId.value = null
    nextTick(() => {
      setTreeCheckedKeys([])
    })
  } catch {
    // error handled by request interceptor
  }
}

async function handleSave(): Promise<void> {
  if (selectedDeptIds.value.length > 0 && primaryDeptId.value == null) {
    ElMessage.warning('请选择一个主部门')
    return
  }

  try {
    await ElMessageBox.confirm(`确定修改用户 ${props.username} 的部门关联？`, '保存确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  saving.value = true
  try {
    await assignUserDepts(props.userId, selectedDeptIds.value, primaryDeptId.value ?? 0)
    ElMessage.success('部门关联保存成功')
    initialDeptIds.value = [...selectedDeptIds.value]
    initialPrimaryDeptId.value = primaryDeptId.value
    emit('success')
    dialogVisible.value = false
  } catch {
    // error handled by request interceptor
  } finally {
    saving.value = false
  }
}

function handleClosed(): void {
  deptTree.value = []
  selectedDeptIds.value = []
  initialDeptIds.value = []
  primaryDeptId.value = null
  initialPrimaryDeptId.value = null
}
</script>

<style scoped lang="scss">
.dept-body {
  .dept-content {
    display: flex;
    gap: 16px;
    min-height: 300px;
    max-height: 400px;
  }

  .dept-tree-panel {
    flex: 1;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 4px;
    padding: 12px;
    overflow-y: auto;
  }

  .dept-selected-panel {
    flex: 1;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 4px;
    padding: 12px;
    overflow-y: auto;
  }

  .panel-title {
    font-size: 13px;
    font-weight: 600;
    color: var(--el-text-color-secondary);
    margin-bottom: 8px;
    padding-bottom: 8px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .dept-radio-group {
    display: flex;
    flex-direction: column;
    gap: 6px;

    .dept-radio-item {
      padding: 4px 0;
    }
  }

  .dept-toolbar {
    margin-top: 12px;
    display: flex;
    gap: 8px;
  }

  .change-summary {
    margin-top: 12px;
  }
}
</style>
