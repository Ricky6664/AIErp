<template>
  <div class="online-device-list-page">
    <div class="page-header">
      <h2>在线设备管理</h2>
      <p class="page-desc">
        查看和管理当前在线设备，支持按用户名、设备类型、状态筛选，可强制下线异常设备
      </p>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchUsername"
          placeholder="用户名"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="searchDeviceType"
          placeholder="设备类型"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="PC" value="PC" />
          <el-option label="Mobile" value="Mobile" />
          <el-option label="Tablet" value="Tablet" />
        </el-select>
        <el-select
          v-model="searchStatus"
          placeholder="状态"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="在线" value="online" />
          <el-option label="已下线" value="offline" />
          <el-option label="已踢出" value="kicked" />
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
        <el-button
          v-permission="'system:online-device:kick'"
          type="danger"
          :disabled="selectedIds.length === 0"
          @click="handleBatchKick"
        >
          <el-icon><SwitchButton /></el-icon>
          批量强制下线 ({{ selectedIds.length }})
        </el-button>
      </div>
    </div>

    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="deviceList"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column label="用户名" width="120">
        <template #default="{ row }">
          {{ row.username || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="设备类型" width="100" align="center">
        <template #default="{ row }">
          <el-tooltip :content="deviceTypeLabel(row.deviceType)" placement="top">
            <span class="device-type-icon">
              <el-icon v-if="row.deviceType === 'PC'" :size="20"><Monitor /></el-icon>
              <el-icon v-else-if="row.deviceType === 'Mobile'" :size="20"><Phone /></el-icon>
              <el-icon v-else-if="row.deviceType === 'Tablet'" :size="20"><Platform /></el-icon>
              <el-tag v-else size="small">{{ row.deviceType }}</el-tag>
            </span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="设备名称" width="180">
        <template #default="{ row }">
          {{ row.deviceName || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作系统" width="130">
        <template #default="{ row }">
          {{ row.os || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="浏览器" width="120">
        <template #default="{ row }">
          {{ row.browser || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="IP地址" width="150">
        <template #default="{ row }">
          {{ row.ipAddress || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="登录时间" width="180">
        <template #default="{ row }">
          {{ row.loginTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="最后活跃时间" width="180">
        <template #default="{ row }">
          <span :class="{ 'inactive-warning': isInactive(row.lastActiveTime) }">
            {{ row.lastActiveTime || '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template #default="{ row }">
          <el-popconfirm
            title="确定将该设备强制下线？"
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleKick(row as OnlineDeviceItem)"
          >
            <template #reference>
              <el-button
                v-permission="'system:onlineDevice:kick'"
                type="danger"
                size="small"
                :disabled="row.status !== 'online'"
              >
                强制下线
              </el-button>
            </template>
          </el-popconfirm>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, SwitchButton, Monitor, Phone, Platform } from '@element-plus/icons-vue'
import type { OnlineDeviceItem, OnlineDeviceQuery } from '@/api/types/onlineDevice'
import {
  getOnlineDevicePageApi,
  kickDeviceApi,
  batchKickDeviceApi
} from '@/api/modules/onlineDevice'

const loading = ref(false)
const deviceList = ref<OnlineDeviceItem[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const searchUsername = ref('')
const searchDeviceType = ref('')
const searchStatus = ref('')
const selectedIds = ref<string[]>([])
let refreshTimer: ReturnType<typeof setInterval> | null = null

const deviceTypeMap: Record<string, string> = {
  PC: '电脑',
  Mobile: '手机',
  Tablet: '平板'
}

const statusMap: Record<string, string> = {
  online: '在线',
  offline: '已下线',
  kicked: '已踢出'
}

const statusColorMap: Record<string, 'success' | 'info' | 'danger'> = {
  online: 'success',
  offline: 'info',
  kicked: 'danger'
}

function deviceTypeLabel(type: string): string {
  return deviceTypeMap[type] || type
}

function statusLabel(status: string): string {
  return statusMap[status] || status
}

function statusTagType(status: string): 'success' | 'info' | 'danger' {
  return statusColorMap[status] || 'info'
}

function isInactive(lastActiveTime: string): boolean {
  if (!lastActiveTime) return false
  const diffMs = Date.now() - new Date(lastActiveTime).getTime()
  return diffMs > 10 * 60 * 1000
}

function buildQueryParams(): OnlineDeviceQuery {
  const params: OnlineDeviceQuery = {
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
  if (searchUsername.value) params.username = searchUsername.value
  if (searchDeviceType.value) params.deviceType = searchDeviceType.value
  if (searchStatus.value) params.status = searchStatus.value
  return params
}

async function fetchDeviceList(): Promise<void> {
  loading.value = true
  try {
    const result = await getOnlineDevicePageApi(buildQueryParams())
    const records = (result.records ?? []).map((item: OnlineDeviceItem) => ({
      ...item,
      tokenId: item.sessionTokenId || item.tokenId
    }))
    deviceList.value = records
    total.value = result.total ?? 0
  } catch {
    deviceList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  pageNum.value = 1
  fetchDeviceList()
}

function handleReset(): void {
  searchUsername.value = ''
  searchDeviceType.value = ''
  searchStatus.value = ''
  pageNum.value = 1
  fetchDeviceList()
}

function handlePageChange(): void {
  fetchDeviceList()
}

function handleSizeChange(): void {
  pageNum.value = 1
  fetchDeviceList()
}

function handleSelectionChange(selection: OnlineDeviceItem[]): void {
  selectedIds.value = selection.map((item) => item.tokenId).filter(Boolean)
}

async function handleKick(row: OnlineDeviceItem): Promise<void> {
  try {
    await kickDeviceApi(row.tokenId)
    ElMessage.success('设备已强制下线')
    await fetchDeviceList()
  } catch {
    ElMessage.error('强制下线失败')
  }
}

async function handleBatchKick(): Promise<void> {
  if (selectedIds.value.length === 0) return
  try {
    await ElMessageBox.confirm(
      `确定将选中的 ${selectedIds.value.length} 台设备强制下线？`,
      '批量强制下线',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await batchKickDeviceApi(selectedIds.value)
    ElMessage.success(`已强制下线 ${selectedIds.value.length} 台设备`)
    selectedIds.value = []
    await fetchDeviceList()
  } catch {
    // 用户取消或操作失败
  }
}

function startAutoRefresh(): void {
  refreshTimer = setInterval(() => {
    fetchDeviceList()
  }, 30000)
}

function stopAutoRefresh(): void {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

onMounted(() => {
  fetchDeviceList()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped lang="scss">
.online-device-list-page {
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

  .device-type-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-regular);
  }

  .inactive-warning {
    color: var(--el-color-warning);
    font-weight: 500;
  }

  .pagination-wrapper {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
