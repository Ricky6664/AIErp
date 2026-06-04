<template>
  <div class="auth-config-workbench-page">
    <div class="page-header">
      <h2>权限配置工作台</h2>
      <p class="page-desc">认证配置概览，包括认证方式、密码策略统计、登录方式分布及每日登录趋势</p>
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
          :icon="Lock"
          label="认证方式总数"
          :value="workbenchData?.totalAuthMethods ?? 0"
          color="blue"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="CircleCheck"
          label="已启用认证方式"
          :value="workbenchData?.enabledAuthMethods ?? 0"
          color="green"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="Key"
          label="密码策略总数"
          :value="workbenchData?.totalPasswordPolicies ?? 0"
          color="orange"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="Monitor"
          label="在线设备数"
          :value="workbenchData?.onlineDeviceCount ?? 0"
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
              <span class="card-title">登录方式分布（饼图）</span>
              <el-button size="small" text @click="handleExportPieChart">导出PNG</el-button>
            </div>
          </template>
          <el-skeleton :loading="loading && !workbenchData" animated :rows="6">
            <div ref="loginDistContainer" class="chart-container"></div>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="14">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">每日登录统计（柱状图）</span>
              <el-button size="small" text @click="handleExportBarChart">导出PNG</el-button>
            </div>
          </template>
          <el-skeleton :loading="loading && !workbenchData" animated :rows="6">
            <div ref="barChartContainer" class="chart-container"></div>
          </el-skeleton>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">登录方式分布（雷达图）</span>
              <el-button size="small" text @click="handleExportRadarChart">导出PNG</el-button>
            </div>
          </template>
          <el-skeleton :loading="loading && !workbenchData" animated :rows="6">
            <div ref="radarChartContainer" class="chart-container"></div>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="detail-row">
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value success">{{ workbenchData?.todayLoginSuccessCount ?? 0 }}</div>
            <div class="detail-label">今日登录成功</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value danger">{{ workbenchData?.todayLoginFailCount ?? 0 }}</div>
            <div class="detail-label">今日登录失败</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value">{{ workbenchData?.enabledPasswordPolicies ?? 0 }}</div>
            <div class="detail-label">已启用密码策略</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value">{{ workbenchData?.ssoConfigCount ?? 0 }}</div>
            <div class="detail-label">SSO配置数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Lock, CircleCheck, Key, Monitor, RefreshRight } from '@element-plus/icons-vue'
import KpiCard from '@/components/KpiCard/index.vue'
import { useAuthConfigWorkbench } from '@/composables/useAuthConfigWorkbench'
import type { DimensionType } from '@/composables/useAuthConfigWorkbench'

const {
  loading,
  workbenchData,
  dimension,
  dailyLoginChartRef,
  loginDistChartRef,
  barChartRef,
  radarChartRef,
  fetchData,
  initLoginDistChart,
  updateLoginDistChart,
  initDailyLoginChart,
  updateDailyLoginChart,
  initBarChart,
  updateBarChart,
  initRadarChart,
  updateRadarChart,
  exportChartAsImage,
  resizeCharts,
  disposeCharts
} = useAuthConfigWorkbench()

const loginDistContainer = ref<HTMLElement | null>(null)
const dailyLoginContainer = ref<HTMLElement | null>(null)
const barChartContainer = ref<HTMLElement | null>(null)
const radarChartContainer = ref<HTMLElement | null>(null)

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
  if (barChartContainer.value) updateBarChart(data.dailyLoginStats)
  if (radarChartContainer.value) updateRadarChart(data.loginMethodDistribution)
}

watch(workbenchData, (data) => {
  updateAllCharts(data)
})

async function handleRefresh(): Promise<void> {
  await fetchData(getTimeParams())
  ElMessage.success('数据已刷新')
}

function handleResize(): void {
  resizeCharts()
}

function handleExportLineChart(): void {
  exportChartAsImage(dailyLoginChartRef.value, '每日登录统计-趋势图')
}

function handleExportPieChart(): void {
  exportChartAsImage(loginDistChartRef.value, '登录方式分布-饼图')
}

function handleExportBarChart(): void {
  exportChartAsImage(barChartRef.value, '每日登录统计-柱状图')
}

function handleExportRadarChart(): void {
  exportChartAsImage(radarChartRef.value, '登录方式分布-雷达图')
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
  if (barChartContainer.value) {
    initBarChart(barChartContainer.value)
  }
  if (radarChartContainer.value) {
    initRadarChart(radarChartContainer.value)
  }

  updateAllCharts(workbenchData.value)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
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

  .detail-row {
    margin-bottom: 16px;

    .detail-stat {
      text-align: center;
      padding: 8px 0;

      .detail-value {
        font-size: 24px;
        font-weight: 700;
        color: var(--el-text-color-primary);

        &.success {
          color: #67c23a;
        }

        &.danger {
          color: #f56c6c;
        }
      }

      .detail-label {
        font-size: 13px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }
    }
  }
}
</style>
