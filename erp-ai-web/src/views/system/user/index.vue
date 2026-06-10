<template>
  <PageP04SimpleList view-id="system-user-list" page-type="P04" :config="pageConfig">
    <!-- 查询区：双行结构 — 上：模糊搜索，下：常用字段 -->
    <template #query-panel>
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键字">
          <el-input
            v-model="searchKeyword"
            placeholder="用户名 / 昵称 / 邮箱"
            clearable
            style="width: 260px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchStatus"
            placeholder="全部"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="正常" value="normal" />
            <el-option label="锁定" value="locked" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="searchDeptId"
            :data="deptTreeData"
            :props="{ label: 'menuName', children: 'children' }"
            node-key="id"
            placeholder="全部"
            clearable
            check-strictly
            filterable
            style="width: 180px"
            @change="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </template>

    <!-- 操作栏：左右分排 -->
    <template #action-bar>
      <div class="action-left">
        <el-button v-permission="'system:user:add'" type="primary" @click="handleAdd">
          新增用户
        </el-button>
        <el-button
          v-permission="'system:user:delete'"
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
        <el-button
          v-permission="'system:user:unlock'"
          type="success"
          :disabled="selectedIds.length === 0"
          @click="handleBatchUnlock"
        >
          批量解锁
        </el-button>
      </div>
      <div class="action-right">
        <el-button @click="handleExport">导出</el-button>
        <span class="record-count">共 {{ total }} 条</span>
      </div>
    </template>

    <!-- 数据表格 -->
    <template #main-content>
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="userList"
        border
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="mobile" label="手机号" width="140" />
        <el-table-column prop="employeeName" label="所属部门" width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status, row.statusName) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录时间" width="180" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:user:edit'"
              type="primary"
              link
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 'locked'"
              v-permission="'system:user:unlock'"
              type="success"
              link
              size="small"
              @click="handleUnlock(row)"
            >
              解锁
            </el-button>
            <el-button
              v-permission="'system:user:delete'"
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-button
              v-permission="'system:user:password-reset'"
              type="warning"
              link
              size="small"
              @click="handleResetPwd(row)"
            >
              重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </template>
  </PageP04SimpleList>

  <UserForm v-model:visible="formVisible" :user-id="editUserId" @success="handleFormSuccess" />
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { debounce } from 'lodash-es'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import type { UserListItem } from '@/types/user'
import type { MenuItem } from '@/api/types/menu'
import { getUserPageList, deleteUser, resetUserPassword, unlockUser } from '@/api/modules/user'
import { getMenuTree } from '@/api/modules/menu'
import UserForm from './UserForm.vue'

const pageConfig: SimpleListPageConfig = {
  title: '用户管理',
  showQueryPanel: true,
  showActionBar: true
}

const loading = ref(false)
const userList = ref<UserListItem[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')
const searchStatus = ref('')
const searchDeptId = ref<number | null>(null)
const selectedIds = ref<number[]>([])
const deptTreeData = ref<MenuItem[]>([])
const formVisible = ref(false)
const editUserId = ref<number | undefined>(undefined)

function statusTagType(status: string): 'primary' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'info'> = {
    normal: 'success',
    locked: 'danger',
    disabled: 'info'
  }
  return map[status] ?? 'primary'
}

function statusLabel(status: string, statusName: string): string {
  return statusName || { normal: '正常', locked: '锁定', disabled: '禁用' }[status] || status
}

async function fetchDeptTree(): Promise<void> {
  try {
    const res = await getMenuTree()
    deptTreeData.value = Array.isArray(res) ? res : []
  } catch {
    deptTreeData.value = []
  }
}

async function fetchUserList(): Promise<void> {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (searchStatus.value) params.status = searchStatus.value
    if (searchDeptId.value) params.deptId = searchDeptId.value
    const result = await getUserPageList(params as any)
    userList.value = result.list ?? []
    total.value = result.total ?? 0
  } catch {
    userList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  pageNum.value = 1
  fetchUserList()
}

function handleReset(): void {
  searchKeyword.value = ''
  searchStatus.value = ''
  searchDeptId.value = null
  pageNum.value = 1
  fetchUserList()
}

function handlePageChange(): void {
  fetchUserList()
}

function handleSizeChange(): void {
  pageNum.value = 1
  fetchUserList()
}

function handleSelectionChange(rows: UserListItem[]): void {
  selectedIds.value = rows.map((r) => r.id)
}

function handleAdd(): void {
  editUserId.value = undefined
  formVisible.value = true
}

type RowData = { id?: number; username?: string; status?: string }

function handleEdit(row: RowData): void {
  editUserId.value = row.id
  formVisible.value = true
}

function handleFormSuccess(): void {
  fetchUserList()
}

function handleDelete(row: RowData): void {
  const name = row.username ?? ''
  const id = row.id
  if (!id) return
  ElMessageBox.confirm(`确定删除用户 ${name}？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deleteUser(id)
      ElMessage.success('删除成功')
      await fetchUserList()
    })
    .catch(() => {
      // user cancelled
    })
}

function handleBatchDelete(): void {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个用户？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      for (const id of selectedIds.value) {
        await deleteUser(id)
      }
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      await fetchUserList()
    })
    .catch(() => {
      // user cancelled
    })
}

function handleUnlock(row: RowData): void {
  const name = row.username ?? ''
  const id = row.id
  if (!id) return
  ElMessageBox.confirm(`确定解锁用户 ${name}？解锁后该用户可立即登录。`, '解锁确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await unlockUser(id)
      ElMessage.success(`用户 ${name} 已解锁`)
      await fetchUserList()
    })
    .catch(() => {
      // user cancelled
    })
}

function handleBatchUnlock(): void {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  ElMessageBox.confirm(`确定解锁选中的 ${selectedIds.value.length} 个用户？`, '批量解锁确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      for (const id of selectedIds.value) {
        await unlockUser(id)
      }
      ElMessage.success('批量解锁成功')
      selectedIds.value = []
      await fetchUserList()
    })
    .catch(() => {
      // user cancelled
    })
}

function handleResetPwd(row: RowData): void {
  const name = row.username ?? ''
  const id = row.id
  if (!id) return
  ElMessageBox.confirm(`确定重置用户 ${name} 的密码？重置后将生成新的随机密码。`, '重置密码确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      const newPwd = await resetUserPassword(id)
      ElMessage.success(`密码已重置，新密码: ${newPwd}`)
    })
    .catch(() => {
      // user cancelled
    })
}

function handleExport(): void {
  ElMessage.info('导出功能将在后续任务中实现')
}

const debouncedSearch = debounce(() => {
  pageNum.value = 1
  fetchUserList()
}, 300)

watch(searchKeyword, () => {
  debouncedSearch()
})

onMounted(() => {
  fetchDeptTree()
  fetchUserList()
})
</script>

<style scoped lang="scss">
.action-left {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.record-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0 0;
}
</style>
