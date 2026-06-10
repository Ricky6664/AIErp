<template>
  <div class="login-log-list-page">
    <div class="page-header">
      <h2>登录日志</h2>
      <p class="page-desc">查看和管理系统登录日志，支持按用户名、状态、时间范围、IP筛选</p>
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
          v-model="searchStatus"
          placeholder="登录状态"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option label="全部" value="" />
          <el-option label="成功" value="success" />
          <el-option label="失败" value="fail" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 260px"
          @change="handleSearch"
        />
        <el-input
          v-model="searchIp"
          placeholder="IP地址"
          clearable
          style="width: 150px"
          @keyup.enter="handleSearch"
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
        <el-button
          v-permission="'system:loginLog:export'"
          type="success"
          :loading="exporting"
          @click="handleExport"
        >
          <el-icon><Download /></el-icon>
          导出Excel
        </el-button>
        <el-popconfirm
          title="确定清空所有登录日志？此操作不可恢复。"
          confirm-button-text="确定"
          cancel-button-text="取消"
          @confirm="handleClear"
        >
          <template #reference>
            <el-button v-if="isSuperAdmin" type="danger">
              <el-icon><Delete /></el-icon>
              清空日志
            </el-button>
          </template>
        </el-popconfirm>
      </div>
    </div>

    <el-table v-loading="loading" :data="logList" border stripe style="width: 100%">
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="loginTime" label="登录时间" width="180" />
      <el-table-column prop="logoutTime" label="登出时间" width="180">
        <template #default="{ row }">
          {{ row.logoutTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="IP地址" width="180">
        <template #default="{ row }">
          {{ row.ipAddress }}<template v-if="row.ipLocation"> ({{ row.ipLocation }})</template>
        </template>
      </el-table-column>
      <el-table-column label="登录方式" width="110" align="center">
        <template #default="{ row }">
          <el-tag :type="loginMethodTagType(row.loginMethod)" size="small">
            {{ loginMethodLabel(row.loginMethod) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="browser" label="浏览器" width="120" />
      <el-table-column prop="os" label="操作系统" width="130" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small">
            {{ row.status === 'success' ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="在线时长" width="120">
        <template #default="{ row }">
          <template v-if="row.logoutTime">
            {{ formatOnlineDuration(row.loginTime, row.logoutTime) }}
          </template>
          <el-tag v-else type="success" size="small">在线中</el-tag>
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Download, Delete } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/modules/user'
import type { LoginLogItem } from '@/api/types/loginLog'
import { getLoginLogPageApi, clearLoginLogApi, exportLoginLogApi } from '@/api/modules/loginLog'

const userStore = useUserStore()

const loading = ref(false)
const exporting = ref(false)
const logList = ref<LoginLogItem[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const searchUsername = ref('')
const searchStatus = ref('')
const dateRange = ref<string[] | null>(null)
const searchIp = ref('')

const isSuperAdmin = ref(userStore.roles?.includes('superadmin') ?? false)

const loginMethodMap: Record<string, string> = {
  password: '密码登录',
  sms: '短信登录',
  oauth2: 'OAuth2',
  sso: 'SSO'
}

const loginMethodColorMap: Record<string, 'primary' | 'success' | 'warning' | 'info'> = {
  password: 'primary',
  sms: 'success',
  oauth2: 'warning',
  sso: 'info'
}

function loginMethodLabel(method: string): string {
  return loginMethodMap[method] || method
}

function loginMethodTagType(method: string): 'primary' | 'success' | 'warning' | 'info' {
  return loginMethodColorMap[method] || 'info'
}

function formatOnlineDuration(loginTime: string, logoutTime: string): string {
  const login = new Date(loginTime).getTime()
  const logout = new Date(logoutTime).getTime()
  const diffMs = logout - login
  if (diffMs <= 0) return '0分钟'
  const totalMinutes = Math.floor(diffMs / 60000)
  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60
  if (hours > 0) {
    return minutes > 0 ? `${hours}小时${minutes}分钟` : `${hours}小时`
  }
  return `${minutes}分钟`
}

function buildQueryParams(): Record<string, unknown> {
  const params: Record<string, unknown> = {
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
  if (searchUsername.value) params.username = searchUsername.value
  if (searchStatus.value) params.status = searchStatus.value
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }
  if (searchIp.value) params.ip = searchIp.value
  return params
}

async function fetchLogList(): Promise<void> {
  loading.value = true
  try {
    const result = await getLoginLogPageApi(buildQueryParams() as any)
    logList.value = result.records ?? []
    total.value = result.total ?? 0
  } catch {
    logList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  pageNum.value = 1
  fetchLogList()
}

function handleReset(): void {
  searchUsername.value = ''
  searchStatus.value = ''
  dateRange.value = null
  searchIp.value = ''
  pageNum.value = 1
  fetchLogList()
}

function handlePageChange(): void {
  fetchLogList()
}

function handleSizeChange(): void {
  pageNum.value = 1
  fetchLogList()
}

function downloadCsv(data: LoginLogItem[], filename: string): void {
  const headers = [
    '用户名',
    '登录时间',
    '登出时间',
    'IP地址',
    '登录方式',
    '浏览器',
    '操作系统',
    '状态',
    '在线时长'
  ]
  const rows = data.map((item) => [
    item.username,
    item.loginTime,
    item.logoutTime || '',
    item.ipAddress + (item.ipLocation ? ` (${item.ipLocation})` : ''),
    loginMethodLabel(item.loginMethod),
    item.browser,
    item.os,
    item.status === 'success' ? '成功' : '失败',
    item.logoutTime ? formatOnlineDuration(item.loginTime, item.logoutTime) : '在线中'
  ])
  const bom = '﻿'
  const csvContent =
    bom + [headers, ...rows].map((row) => row.map((cell) => `"${cell}"`).join(',')).join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

async function handleExport(): Promise<void> {
  exporting.value = true
  try {
    const data = await exportLoginLogApi({
      username: searchUsername.value || undefined,
      status: searchStatus.value || undefined,
      startTime: dateRange.value?.[0] || undefined,
      endTime: dateRange.value?.[1] || undefined,
      ip: searchIp.value || undefined
    })
    const today = new Date().toISOString().slice(0, 10).replace(/-/g, '')
    downloadCsv(data, `登录日志_${today}.csv`)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

async function handleClear(): Promise<void> {
  try {
    await clearLoginLogApi()
    ElMessage.success('登录日志已清空')
    await fetchLogList()
  } catch {
    ElMessage.error('清空失败')
  }
}

onMounted(() => {
  fetchLogList()
})
</script>

<style scoped lang="scss">
.login-log-list-page {
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
