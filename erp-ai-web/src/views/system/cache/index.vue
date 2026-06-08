<template>
  <div class="cache-manage-page">
    <div class="page-header">
      <h2>缓存管理</h2>
      <p class="page-desc">管理 Redis 缓存，支持 Key 搜索、Value 查看、缓存清除及实时统计</p>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.keyCount ?? '-' }}</div>
          <div class="stat-label">Key 总数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--memory">
          <div class="stat-value">{{ formatMemory(stats.usedMemory) }}</div>
          <div class="stat-label">内存占用</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--version">
          <div class="stat-value">{{ stats.serverVersion ?? '-' }}</div>
          <div class="stat-label">Redis 版本</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card--uptime">
          <div class="stat-value">{{ formatUptime(stats.uptimeInSeconds) }}</div>
          <div class="stat-label">运行时长</div>
        </el-card>
      </el-col>
    </el-row>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchPattern"
          placeholder="Key 匹配模式（如 user:*，默认 *）"
          clearable
          style="width: 360px"
          @keyup.enter="handleSearch"
        >
          <template #prepend>Key 模式</template>
        </el-input>
        <el-button type="primary" :loading="loading" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button v-if="selectedKeys.length > 0" type="danger" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除 ({{ selectedKeys.length }})
        </el-button>
        <el-button @click="handleRefreshStats">
          <el-icon><Refresh /></el-icon>
          刷新统计
        </el-button>
      </div>
    </div>

    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="keyList"
      border
      stripe
      empty-text="请输入 Key 模式后点击查询"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="key" label="Key 名称" min-width="300" show-overflow-tooltip />
      <el-table-column prop="type" label="数据类型" width="120" align="center">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.type || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ttl" label="TTL (秒)" width="120" align="center">
        <template #default="{ row }">
          <span v-if="row.ttl === -1" class="ttl-forever">永久</span>
          <span v-else-if="row.ttl === -2" class="ttl-expired">已过期</span>
          <span v-else>{{ row.ttl }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小 (bytes)" width="120" align="center" />
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleViewValue(row)">
            <el-icon><Document /></el-icon>
            查看
          </el-button>
          <el-popconfirm
            :title="`确定删除 Key「${row.key}」？`"
            confirm-button-text="确认删除"
            cancel-button-text="取消"
            @confirm="handleDelete(row.key)"
          >
            <template #reference>
              <el-button type="danger" link size="small">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="valueDialogVisible" title="缓存 Value 详情" width="700px" destroy-on-close>
      <div class="value-key-info">
        <span class="label">Key：</span>
        <span class="key-text">{{ currentKey }}</span>
      </div>
      <div v-if="valueLoading" class="value-loading">
        <el-skeleton :rows="6" animated />
      </div>
      <div v-else class="value-content">
        <pre class="json-viewer">{{ formattedValue }}</pre>
      </div>
      <template #footer>
        <el-button @click="valueDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Search, Refresh, Delete, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCacheStatsApi,
  getCacheKeysApi,
  getCacheValueApi,
  deleteCacheKeyApi,
  type CacheStatsVO,
  type CacheKeyVO
} from '@/api/modules/cache'

const searchPattern = ref('*')
const loading = ref(false)
const valueLoading = ref(false)
const keyList = ref<CacheKeyVO[]>([])
const selectedKeys = ref<CacheKeyVO[]>([])
const tableRef = ref()
const valueDialogVisible = ref(false)
const currentKey = ref('')
const currentValue = ref('')
let statsTimer: ReturnType<typeof setInterval> | null = null

const stats = reactive<CacheStatsVO>({
  keyCount: 0,
  serverVersion: '',
  usedMemory: 0,
  uptimeInSeconds: 0
})

function formatMemory(bytes: number | undefined | null): string {
  if (bytes == null) return '-'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(2)} GB`
}

function formatUptime(seconds: number | undefined | null): string {
  if (seconds == null) return '-'
  const d = Math.floor(seconds / 86400)
  const h = Math.floor((seconds % 86400) / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (d > 0) return `${d}天 ${h}时 ${m}分`
  if (h > 0) return `${h}时 ${m}分`
  return `${m}分`
}

const formattedValue = ref('')

async function fetchStats() {
  try {
    const data = await getCacheStatsApi()
    Object.assign(stats, data)
  } catch {
    // stats fetch failure is non-blocking
  }
}

async function handleSearch() {
  loading.value = true
  try {
    const pattern = searchPattern.value.trim() || '*'
    keyList.value = await getCacheKeysApi(pattern)
    if (keyList.value.length === 0) {
      ElMessage.info('未找到匹配的 Key')
    }
  } catch {
    // error handled by request interceptor
  } finally {
    loading.value = false
  }
}

function handleSelectionChange(selection: CacheKeyVO[]) {
  selectedKeys.value = selection
}

async function handleViewValue(row: CacheKeyVO) {
  currentKey.value = row.key
  valueDialogVisible.value = true
  valueLoading.value = true
  currentValue.value = ''
  formattedValue.value = ''
  try {
    const raw = await getCacheValueApi(row.key)
    currentValue.value = raw
    try {
      const parsed = JSON.parse(raw)
      formattedValue.value = JSON.stringify(parsed, null, 2)
    } catch {
      formattedValue.value = raw
    }
  } catch {
    formattedValue.value = '(获取失败)'
  } finally {
    valueLoading.value = false
  }
}

async function handleDelete(key: string) {
  try {
    await deleteCacheKeyApi(key)
    ElMessage.success(`Key「${key}」已删除`)
    keyList.value = keyList.value.filter((item) => item.key !== key)
    await fetchStats()
  } catch {
    // error handled by interceptor
  }
}

async function handleBatchDelete() {
  if (selectedKeys.value.length === 0) {
    ElMessage.warning('请先选择要删除的 Key')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确认删除选中的 ${selectedKeys.value.length} 个 Key？此操作不可恢复。`,
      '批量删除确认',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' }
    )
    for (const item of selectedKeys.value) {
      await deleteCacheKeyApi(item.key)
    }
    ElMessage.success(`已删除 ${selectedKeys.value.length} 个 Key`)
    selectedKeys.value = []
    await handleSearch()
    await fetchStats()
  } catch {
    // user cancelled or error
  }
}

async function handleRefreshStats() {
  await fetchStats()
  ElMessage.success('统计信息已刷新')
}

onMounted(() => {
  fetchStats()
  statsTimer = setInterval(fetchStats, 30000)
})

onUnmounted(() => {
  if (statsTimer) {
    clearInterval(statsTimer)
    statsTimer = null
  }
})
</script>

<style scoped>
.cache-manage-page {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-card--memory {
  border-left: 3px solid #409eff;
}

.stat-card--version {
  border-left: 3px solid #67c23a;
}

.stat-card--uptime {
  border-left: 3px solid #e6a23c;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ttl-forever {
  color: #67c23a;
}

.ttl-expired {
  color: #f56c6c;
}

.value-key-info {
  margin-bottom: 16px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  word-break: break-all;
}

.value-key-info .label {
  font-weight: 600;
  color: #606266;
}

.value-key-info .key-text {
  color: #409eff;
  font-family: monospace;
}

.value-loading {
  padding: 20px 0;
}

.value-content {
  max-height: 480px;
  overflow: auto;
}

.json-viewer {
  margin: 0;
  padding: 16px;
  background: #1e1e1e;
  color: #d4d4d4;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  overflow-x: auto;
}
</style>
