<template>
  <div class="user-group-list-page">
    <div class="page-header">
      <h2>用户组管理</h2>
      <p class="page-desc">管理用户组，支持用户组查询、新增、编辑、删除及状态切换</p>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchKeyword"
          placeholder="组名称 / 组编码"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
        >
          <template #prepend>关键字</template>
        </el-input>
        <el-select
          v-model="searchEnabled"
          placeholder="启用状态"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
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
        <el-button v-permission="'system:user-group:add'" type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户组
        </el-button>
        <el-button
          v-permission="'system:user-group:delete'"
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="groupList"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column prop="groupName" label="组名称" width="160" />
      <el-table-column prop="groupCode" label="组编码" width="140" />
      <el-table-column prop="groupDesc" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column label="成员数量" width="100" align="center">
        <template #default="{ row }">
          <el-tag type="info" size="small">{{ row.memberCount }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="启用状态" width="100" align="center">
        <template #default="{ row }">
          <el-switch
            :model-value="row.isEnabled"
            :active-value="true"
            :inactive-value="false"
            @change="
              (val: string | number | boolean) =>
                handleStatusChange(row as UserGroupListItem, val as boolean)
            "
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="310" align="center" fixed="right">
        <template #default="{ row }">
          <el-button
            v-permission="'system:user-group:edit'"
            type="primary"
            link
            size="small"
            @click="handleEdit(row as { id: number })"
          >
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button
            v-permission="'system:user-group:member'"
            type="warning"
            link
            size="small"
            @click="handleMemberManage(row as { id: number })"
          >
            <el-icon><UserFilled /></el-icon>
            成员管理
          </el-button>
          <el-button
            v-permission="'system:user-group:delete'"
            type="danger"
            link
            size="small"
            @click="handleDelete(row as { id: number; groupName: string })"
          >
            <el-icon><Delete /></el-icon>
            删除
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

    <UserGroupForm
      v-model:visible="formVisible"
      :group-id="editGroupId"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, UserFilled } from '@element-plus/icons-vue'
import { debounce } from 'lodash-es'
import type { UserGroupListItem } from '@/types/userGroup'
import {
  getUserGroupPageList,
  deleteUserGroup,
  updateUserGroupStatus
} from '@/api/modules/userGroup'
import UserGroupForm from './UserGroupForm.vue'

const router = useRouter()

const loading = ref(false)
const groupList = ref<UserGroupListItem[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')
const searchEnabled = ref<boolean>()
const selectedIds = ref<number[]>([])
const formVisible = ref(false)
const editGroupId = ref<number | undefined>(undefined)

async function fetchGroupList(): Promise<void> {
  loading.value = true
  try {
    const params: { pageNum: number; pageSize: number; keyword?: string; enabled?: boolean } = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (searchEnabled.value !== undefined) params.enabled = searchEnabled.value
    const result = await getUserGroupPageList(params)
    groupList.value = result.records ?? []
    total.value = result.total ?? 0
  } catch {
    groupList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  pageNum.value = 1
  fetchGroupList()
}

function handleReset(): void {
  searchKeyword.value = ''
  searchEnabled.value = undefined
  pageNum.value = 1
  fetchGroupList()
}

function handlePageChange(): void {
  fetchGroupList()
}

function handleSizeChange(): void {
  pageNum.value = 1
  fetchGroupList()
}

function handleSelectionChange(rows: UserGroupListItem[]): void {
  selectedIds.value = rows.map((r) => r.id)
}

function handleAdd(): void {
  editGroupId.value = undefined
  formVisible.value = true
}

function handleEdit(row: { id: number }): void {
  editGroupId.value = row.id
  formVisible.value = true
}

function handleMemberManage(row: { id: number }): void {
  router.push(`/system/user-group/${row.id}/members`)
}

function handleFormSuccess(): void {
  fetchGroupList()
}

async function handleStatusChange(row: UserGroupListItem, isEnabled: boolean): Promise<void> {
  try {
    await updateUserGroupStatus(row.id, isEnabled)
    row.isEnabled = isEnabled
    ElMessage.success(isEnabled ? '已启用' : '已禁用')
  } catch {
    // revert on failure
    fetchGroupList()
  }
}

function handleDelete(row: { id: number; groupName: string }): void {
  ElMessageBox.confirm(`确定删除用户组 ${row.groupName}？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deleteUserGroup(row.id)
      ElMessage.success('删除成功')
      await fetchGroupList()
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
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个用户组？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      for (const id of selectedIds.value) {
        await deleteUserGroup(id)
      }
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      await fetchGroupList()
    })
    .catch(() => {
      // user cancelled
    })
}

const debouncedSearch = debounce(() => {
  pageNum.value = 1
  fetchGroupList()
}, 300)

watch(searchKeyword, () => {
  debouncedSearch()
})

onMounted(() => {
  fetchGroupList()
})
</script>

<style scoped lang="scss">
.user-group-list-page {
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
