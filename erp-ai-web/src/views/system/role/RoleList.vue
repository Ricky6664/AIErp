<template>
  <PageP04SimpleList view-id="system-role-list" page-type="P04" :config="pageConfig">
    <template #query-panel>
      <el-input
        v-model="searchKeyword"
        placeholder="角色名称 / 角色编码"
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
      <el-select
        v-model="searchDataScope"
        placeholder="数据范围"
        clearable
        style="width: 160px"
        @change="handleSearch"
      >
        <el-option label="全部" value="" />
        <el-option label="全部数据" value="all" />
        <el-option label="本部门" value="dept" />
        <el-option label="本部门及下级" value="dept_and_below" />
        <el-option label="仅本人" value="self" />
        <el-option label="自定义" value="custom" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button @click="handleReset">
        <el-icon><Refresh /></el-icon>
        重置
      </el-button>
    </template>

    <template #action-bar>
      <div class="action-left">
        <el-button v-permission="'system:role:add'" type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
        <el-button
          v-permission="'system:role:delete'"
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
      <div class="action-right">
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
    </template>

    <template #main-content>
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="roleList"
        border
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleCode" label="角色编码" width="140" />
        <el-table-column label="数据范围" width="140" align="center">
          <template #default="{ row }">
            <el-tag :type="dataScopeTagType(row.dataScope)" size="small">
              {{ dataScopeLabel(row.dataScope, row.dataScopeName) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roleDesc" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="启用状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled ? 'success' : 'info'" size="small">
              {{ row.isEnabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:role:edit'"
              type="primary"
              link
              size="small"
              :disabled="row.roleCode === 'superadmin'"
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="success"
              link
              size="small"
              :disabled="row.roleCode === 'superadmin'"
              @click="handlePermissionConfig(row)"
            >
              <el-icon><Setting /></el-icon>
              权限配置
            </el-button>
            <el-button
              v-permission="'system:role:delete'"
              type="danger"
              link
              size="small"
              :disabled="row.roleCode === 'superadmin'"
              @click="handleDelete(row)"
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
    </template>
  </PageP04SimpleList>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, Setting } from '@element-plus/icons-vue'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import type { RoleListItem } from '@/api/types/role'
import { getRolePageList, deleteRole } from '@/api/modules/role'

const router = useRouter()

const pageConfig: SimpleListPageConfig = {
  title: '角色管理',
  showQueryPanel: true,
  showActionBar: true
}

const loading = ref(false)
const roleList = ref<RoleListItem[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')
const searchEnabled = ref<boolean | undefined>(undefined)
const searchDataScope = ref('')
const selectedIds = ref<number[]>([])

const dataScopeMap: Record<string, string> = {
  all: '全部数据',
  dept: '本部门',
  dept_and_below: '本部门及下级',
  self: '仅本人',
  custom: '自定义'
}

const dataScopeTypeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
  all: 'primary',
  dept: 'success',
  dept_and_below: 'warning',
  self: 'info',
  custom: 'danger'
}

function dataScopeTagType(
  dataScope: string
): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  return dataScopeTypeMap[dataScope] ?? 'primary'
}

function dataScopeLabel(dataScope: string, dataScopeName: string): string {
  return dataScopeName || dataScopeMap[dataScope] || dataScope
}

async function fetchRoleList(): Promise<void> {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchKeyword.value) {
      params.roleName = searchKeyword.value
      params.roleCode = searchKeyword.value
    }
    if (searchEnabled.value !== undefined) {
      params.isEnabled = searchEnabled.value
    }
    if (searchDataScope.value) {
      params.dataScope = searchDataScope.value
    }
    const result = await getRolePageList(params as any)
    roleList.value = result.records ?? []
    total.value = result.total ?? 0
  } catch {
    roleList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  pageNum.value = 1
  fetchRoleList()
}

function handleReset(): void {
  searchKeyword.value = ''
  searchEnabled.value = undefined
  searchDataScope.value = ''
  pageNum.value = 1
  fetchRoleList()
}

function handlePageChange(): void {
  fetchRoleList()
}

function handleSizeChange(): void {
  pageNum.value = 1
  fetchRoleList()
}

function handleSelectionChange(rows: RoleListItem[]): void {
  selectedIds.value = rows.map((r) => r.id)
}

function handleAdd(): void {
  router.push({ name: 'RoleForm', query: { mode: 'add' } })
}

function handleEdit(row: { id?: number; roleCode?: string; roleName?: string }): void {
  router.push({ name: 'RoleForm', query: { mode: 'edit', id: row.id } })
}

function handlePermissionConfig(row: { id?: number; roleCode?: string; roleName?: string }): void {
  router.push({ name: 'RoleMenuPermission', query: { roleId: row.id, roleName: row.roleName } })
}

function handleDelete(row: { id?: number; roleCode?: string; roleName?: string }): void {
  if (row.roleCode === 'superadmin') {
    ElMessage.warning('系统内置角色不可操作')
    return
  }
  if (!row.id) return
  ElMessageBox.confirm(`确定删除角色 ${row.roleName}？若角色下有用户关联，请先移除。`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deleteRole(row.id!)
      ElMessage.success('删除成功')
      await fetchRoleList()
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
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个角色？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      for (const id of selectedIds.value) {
        await deleteRole(id)
      }
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      await fetchRoleList()
    })
    .catch(() => {
      // user cancelled
    })
}

onMounted(() => {
  fetchRoleList()
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
