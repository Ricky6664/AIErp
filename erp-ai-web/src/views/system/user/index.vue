<template>
  <div class="user-list-page">
    <div class="page-header">
      <h2>用户管理</h2>
      <p class="page-desc">管理系统用户，支持用户查询、新增、编辑、删除及密码重置</p>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchKeyword"
          placeholder="用户名 / 昵称"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
        >
          <template #prepend>关键字</template>
        </el-input>
        <el-select
          v-model="searchStatus"
          placeholder="用户状态"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="正常" value="normal" />
          <el-option label="锁定" value="locked" />
          <el-option label="禁用" value="disabled" />
        </el-select>
        <el-tree-select
          v-model="searchDeptId"
          :data="deptTreeData"
          :props="{ label: 'menuName', value: 'id', children: 'children' }"
          placeholder="选择部门"
          clearable
          check-strictly
          filterable
          style="width: 200px"
          @change="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button v-permission="'system:user:add'" type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
        <el-button
          v-permission="'system:user:delete'"
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
    </div>

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
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="{ row }">
          <el-button
            v-permission="'system:user:edit'"
            type="primary"
            link
            size="small"
            @click="handleEdit(row)"
          >
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button
            v-permission="'system:user:delete'"
            type="danger"
            link
            size="small"
            @click="handleDelete(row)"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
          <el-button
            v-permission="'system:user:password-reset'"
            type="warning"
            link
            size="small"
            @click="handleResetPwd(row)"
          >
            <el-icon><Lock /></el-icon>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download, Edit, Lock } from '@element-plus/icons-vue'
import { debounce } from 'lodash-es'
import type { UserListItem } from '@/types/user'
import type { MenuItem } from '@/api/types/menu'
import { getUserPageList, deleteUser, resetUserPassword } from '@/api/modules/user'
import { getMenuTree } from '@/api/modules/menu'

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
  ElMessage.info('新增用户功能将在后续任务中实现')
}

type RowData = { id?: number; username?: string }

function handleEdit(row: RowData): void {
  ElMessage.info(`编辑用户 ${row.username ?? ''} 功能将在后续任务中实现`)
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
.user-list-page {
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
      flex-wrap: wrap;
    }

    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .pagination-wrapper {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
