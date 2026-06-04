<template>
  <div class="auth-config-workbench-page">
    <div class="page-header">
      <h2>认证配置工作台</h2>
      <p class="page-desc">认证配置概览，包括在线设备、登录统计、认证方式分布及最近登录日志</p>
      <el-button :icon="RefreshRight" :loading="loading" @click="handleRefresh">刷新数据</el-button>
    </div>

    <div class="filter-row">
      <div class="filter-left">
        <span class="filter-label">时间范围：</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :disabled-date="disabledDate"
          @change="handleDateRangeChange"
        />
      </div>
      <div class="filter-right">
        <span class="filter-label">统计维度：</span>
        <el-radio-group v-model="currentDimension" size="small" @change="handleDimensionChange">
          <el-radio-button value="day">日</el-radio-button>
          <el-radio-button value="week">周</el-radio-button>
          <el-radio-button value="month">月</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <el-row v-loading="loading && !workbenchData" :gutter="16" class="kpi-row">
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="Monitor"
          label="在线设备数"
          :value="workbenchData?.onlineDeviceCount ?? 0"
          color="blue"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="TrendCharts"
          label="今日登录成功"
          :value="workbenchData?.todayLoginSuccessCount ?? 0"
          color="green"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="WarningFilled"
          label="今日登录失败"
          :value="workbenchData?.todayLoginFailCount ?? 0"
          color="orange"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="Connection"
          label="SSO配置数"
          :value="workbenchData?.ssoConfigCount ?? 0"
          color="purple"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="14">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">每日登录统计（趋势图）</span>
              <el-button size="small" text @click="handleExportLineChart">导出PNG</el-button>
            </div>
          </template>
          <el-skeleton :loading="loading && !workbenchData" animated :rows="6">
            <div ref="dailyLoginContainer" class="chart-container"></div>
          </el-skeleton>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">认证方式分布（环形图）</span>
              <el-button size="small" text @click="handleExportPieChart">导出PNG</el-button>
            </div>
          </template>
          <el-skeleton :loading="loading && !workbenchData" animated :rows="6">
            <div ref="loginDistContainer" class="chart-container"></div>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="action-row">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">快捷操作</span>
          </template>
          <el-space wrap>
            <el-button type="primary" :icon="Key" @click="handleQuickAction('password-policy')">
              密码策略配置
            </el-button>
            <el-button type="success" :icon="Lock" @click="handleQuickAction('auth-method')">
              认证方式管理
            </el-button>
            <el-button type="warning" :icon="Connection" @click="handleQuickAction('sso-config')">
              SSO配置
            </el-button>
            <el-button type="info" :icon="Monitor" @click="handleQuickAction('online-device')">
              在线设备
            </el-button>
          </el-space>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="table-row">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">最近登录日志</span>
          </template>
          <el-table
            :data="workbenchData?.recentLogins ?? []"
            border
            stripe
            empty-text="暂无登录记录"
            style="width: 100%"
          >
            <el-table-column prop="username" label="用户名" width="140" />
            <el-table-column prop="loginTime" label="登录时间" min-width="180" />
            <el-table-column prop="ip" label="IP地址" width="160" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Lock,
  Key,
  Monitor,
  Connection,
  TrendCharts,
  WarningFilled,
  RefreshRight
} from '@element-plus/icons-vue'
import KpiCard from '@/components/KpiCard/index.vue'
import { useAuthConfigWorkbench } from '@/composables/useAuthConfigWorkbench'
import type { DimensionType } from '@/composables/useAuthConfigWorkbench'

const router = useRouter()

const {
  loading,
  workbenchData,
  dimension,
  loginDistChartRef,
  dailyLoginChartRef,
  fetchData,
  initLoginDistChart,
  updateLoginDistChart,
  initDailyLoginChart,
  updateDailyLoginChart,
  exportChartAsImage,
  resizeCharts,
  disposeCharts,
  startAutoRefresh,
  stopAutoRefresh
} = useAuthConfigWorkbench()

const loginDistContainer = ref<HTMLElement | null>(null)
const dailyLoginContainer = ref<HTMLElement | null>(null)

const currentDimension = ref<DimensionType>('day')
const dateRange = ref<[string, string] | null>(null)

function disabledDate(date: Date): boolean {
  return date.getTime() > Date.now()
}

function getTimeParams(): { startTime?: string; endTime?: string } {
  if (!dateRange.value) return {}
  const [start, end] = dateRange.value
  return {
    startTime: `${start} 00:00:00`,
    endTime: `${end} 23:59:59`
  }
}

function getStatusType(status: string): 'success' | 'danger' {
  return status === 'SUCCESS' ? 'success' : 'danger'
}

async function handleDateRangeChange(): Promise<void> {
  await fetchData(getTimeParams())
}

async function handleDimensionChange(val: string | number | boolean | undefined): Promise<void> {
  const dim = (val as DimensionType) || 'day'
  dimension.value = dim
  const now = new Date()
  let start: Date
  switch (dim) {
    case 'week': {
      start = new Date(now)
      start.setDate(start.getDate() - 7)
      break
    }
    case 'month': {
      start = new Date(now)
      start.setMonth(start.getMonth() - 1)
      break
    }
    default: {
      start = new Date(now)
      start.setDate(start.getDate() - 7)
      break
    }
  }
  const fmt = (d: Date) => d.toISOString().slice(0, 10)
  dateRange.value = [fmt(start), fmt(now)]
  await fetchData({
    startTime: `${fmt(start)} 00:00:00`,
    endTime: `${fmt(now)} 23:59:59`
  })
}

function updateAllCharts(data: typeof workbenchData.value): void {
  if (!data) return
  if (loginDistContainer.value) updateLoginDistChart(data.loginMethodDistribution)
  if (dailyLoginContainer.value) updateDailyLoginChart(data.dailyLoginStats)
}

watch(workbenchData, (data) => {
  updateAllCharts(data)
})

async function handleRefresh(): Promise<void> {
  await fetchData(getTimeParams())
  ElMessage.success('数据已刷新')
}

function handleQuickAction(target: string): void {
  switch (target) {
    case 'password-policy':
      router.push('/auth/config/password-policy')
      break
    case 'auth-method':
      router.push('/auth/config/auth-method')
      break
    case 'sso-config':
      router.push('/auth/config/sso')
      break
    case 'online-device':
      router.push('/auth/config/online-device')
      break
  }
}

function handleResize(): void {
  resizeCharts()
}

function handleExportLineChart(): void {
  exportChartAsImage(dailyLoginChartRef.value, '每日登录统计-趋势图')
}

function handleExportPieChart(): void {
  exportChartAsImage(loginDistChartRef.value, '认证方式分布-环形图')
}

onMounted(async () => {
  const now = new Date()
  const start = new Date(now)
  start.setDate(start.getDate() - 7)
  const fmt = (d: Date) => d.toISOString().slice(0, 10)
  dateRange.value = [fmt(start), fmt(now)]

  await fetchData({
    startTime: `${fmt(start)} 00:00:00`,
    endTime: `${fmt(now)} 23:59:59`
  })

  if (loginDistContainer.value) {
    initLoginDistChart(loginDistContainer.value)
  }
  if (dailyLoginContainer.value) {
    initDailyLoginChart(dailyLoginContainer.value)
  }

  updateAllCharts(workbenchData.value)

  window.addEventListener('resize', handleResize)

  startAutoRefresh(60000, getTimeParams)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  stopAutoRefresh()
  disposeCharts()
})
</script>

<style scoped lang="scss">
.auth-config-workbench-page {
  padding: 20px;

  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-desc {
      flex: 1;
      margin: 0;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .filter-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    padding: 12px 16px;
    background: var(--el-fill-color-light);
    border-radius: 8px;

    .filter-left,
    .filter-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .filter-label {
      font-size: 14px;
      color: var(--el-text-color-regular);
      white-space: nowrap;
    }
  }

  .kpi-row {
    margin-bottom: 16px;
  }

  .chart-row {
    margin-bottom: 16px;
  }

  .action-row {
    margin-bottom: 16px;
  }

  .table-row {
    margin-bottom: 16px;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .chart-container {
    width: 100%;
    height: 320px;
  }
}
</style>
