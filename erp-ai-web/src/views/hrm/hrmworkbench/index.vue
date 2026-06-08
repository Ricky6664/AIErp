<template>
  <div class="hrm-workbench-page">
    <div class="page-header">
      <h2>{{ $t('hrm.workbench.title') }}</h2>
      <p class="page-desc">{{ $t('hrm.workbench.desc') }}</p>
      <el-button :icon="RefreshRight" :loading="loading" @click="loadData">
        {{ $t('common.refresh') }}
      </el-button>
    </div>

    <div v-loading="loading" class="workbench-content">
      <div v-if="error" class="area-error">
        <el-result icon="error" sub-title="数据加载失败">
          <template #extra>
            <el-button type="primary" size="small" @click="loadData">重试</el-button>
          </template>
        </el-result>
      </div>

      <template v-else-if="data">
        <!-- KPI卡片区 -->
        <section class="workbench-section">
          <div class="section-header">
            <h3>{{ $t('hrm.workbench.kpiTitle') }}</h3>
          </div>
          <el-row :gutter="16" class="kpi-row">
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">
                  {{ data.activeEmployees ?? 0 }}
                  <span class="kpi-sub">/ {{ data.totalEmployees ?? 0 }}</span>
                </div>
                <div class="kpi-label">{{ $t('hrm.workbench.employeeCount') }}</div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">{{ data.newHiresThisMonth ?? 0 }}</div>
                <div class="kpi-label">{{ $t('hrm.workbench.newHires') }}</div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">{{ data.openRecruitments ?? 0 }}</div>
                <div class="kpi-label">{{ $t('hrm.workbench.openRecruitments') }}</div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value salary-value">
                  {{ formatSalary(data.totalMonthlySalary) }}
                </div>
                <div class="kpi-label">{{ $t('hrm.workbench.monthlySalary') }}</div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">{{ data.departmentCount ?? 0 }}</div>
                <div class="kpi-label">{{ $t('hrm.workbench.departmentCount') }}</div>
              </el-card>
            </el-col>
          </el-row>
        </section>

        <!-- 图表区-第一行 -->
        <section class="workbench-section">
          <div class="section-header">
            <h3>{{ $t('hrm.workbench.chartTitle') }}</h3>
          </div>
          <el-row :gutter="16" class="chart-row">
            <el-col :xs="24" :md="14">
              <el-card shadow="never">
                <template #header>
                  <span class="card-title">{{ $t('hrm.workbench.employeeTrend') }}</span>
                </template>
                <div ref="employeeTrendRef" class="chart-container"></div>
              </el-card>
            </el-col>
            <el-col :xs="24" :md="10">
              <el-card shadow="never">
                <template #header>
                  <span class="card-title">{{ $t('hrm.workbench.deptDist') }}</span>
                </template>
                <div ref="deptDistRef" class="chart-container"></div>
              </el-card>
            </el-col>
          </el-row>
        </section>

        <!-- 图表区-第二行 -->
        <section class="workbench-section">
          <el-row :gutter="16" class="chart-row">
            <el-col :xs="24" :md="14">
              <el-card shadow="never">
                <template #header>
                  <span class="card-title">{{ $t('hrm.workbench.attendanceTrend') }}</span>
                </template>
                <div ref="attendanceTrendRef" class="chart-container"></div>
              </el-card>
            </el-col>
            <el-col :xs="24" :md="10">
              <el-card shadow="never">
                <template #header>
                  <span class="card-title">{{ $t('hrm.workbench.recruitStatus') }}</span>
                </template>
                <div ref="recruitStatusRef" class="chart-container"></div>
              </el-card>
            </el-col>
          </el-row>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { RefreshRight } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import * as echarts from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getHrmWorkbenchApi, type HrmWorkbenchVO } from '@/api/modules/hrm-workbench'

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const { t } = useI18n()

const loading = ref(false)
const error = ref(false)
const data = ref<HrmWorkbenchVO | null>(null)

const employeeTrendRef = ref<HTMLElement | null>(null)
const deptDistRef = ref<HTMLElement | null>(null)
const attendanceTrendRef = ref<HTMLElement | null>(null)
const recruitStatusRef = ref<HTMLElement | null>(null)

let employeeTrendInstance: echarts.ECharts | null = null
let deptDistInstance: echarts.ECharts | null = null
let attendanceTrendInstance: echarts.ECharts | null = null
let recruitStatusInstance: echarts.ECharts | null = null

function formatSalary(val: number | undefined): string {
  if (val == null) return '--'
  if (val >= 10000) return (val / 10000).toFixed(1) + '万'
  return val.toLocaleString()
}

async function loadData(): Promise<void> {
  loading.value = true
  error.value = false
  try {
    data.value = await getHrmWorkbenchApi()
    await nextTick()
    renderCharts()
  } catch {
    error.value = true
    ElMessage.error(t('hrm.workbench.loadError'))
  } finally {
    loading.value = false
  }
}

function renderCharts(): void {
  if (!data.value) return
  renderEmployeeTrendChart()
  renderDeptDistChart()
  renderAttendanceTrendChart()
  renderRecruitStatusChart()
}

function renderLineChart(
  el: HTMLElement | null,
  instance: echarts.ECharts | null,
  setter: (v: echarts.ECharts | null) => void,
  trend: { month: string; count: number }[],
  seriesName: string,
  color: string
): void {
  if (!el) return
  if (!instance) {
    instance = echarts.init(el)
    setter(instance)
  }
  instance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
    xAxis: { type: 'category', data: trend.map((item) => item.month), boundaryGap: false },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: seriesName,
        type: 'line',
        data: trend.map((item) => item.count),
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: color.replace('1)', '0.3)').replace(')', ', 0.3)') },
            { offset: 1, color: color.replace('1)', '0.05)').replace(')', ', 0.05)') }
          ])
        },
        itemStyle: { color }
      }
    ]
  })
}

function renderEmployeeTrendChart(): void {
  if (!data.value) return
  const trend = data.value.employeeMonthlyTrend || []
  renderLineChart(
    employeeTrendRef.value,
    employeeTrendInstance,
    (v) => {
      employeeTrendInstance = v
    },
    trend,
    t('hrm.workbench.newEmployee'),
    '#409EFF'
  )
}

function renderAttendanceTrendChart(): void {
  if (!data.value) return
  const trend = data.value.attendanceMonthlyTrend || []
  renderLineChart(
    attendanceTrendRef.value,
    attendanceTrendInstance,
    (v) => {
      attendanceTrendInstance = v
    },
    trend,
    t('hrm.workbench.attendanceRecord'),
    '#67C23A'
  )
}

function renderPieChart(
  el: HTMLElement | null,
  instance: echarts.ECharts | null,
  setter: (v: echarts.ECharts | null) => void,
  distribution: Record<string, number>,
  seriesName: string
): void {
  if (!el) return
  if (!instance) {
    instance = echarts.init(el)
    setter(instance)
  }
  const pieData = Object.entries(distribution || {}).map(([name, value]) => ({ name, value }))
  instance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        name: seriesName,
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        data: pieData.length > 0 ? pieData : [{ name: t('hrm.workbench.noData'), value: 0 }],
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
        },
        label: { show: false },
        labelLine: { show: false }
      }
    ]
  })
}

function renderDeptDistChart(): void {
  if (!data.value) return
  renderPieChart(
    deptDistRef.value,
    deptDistInstance,
    (v) => {
      deptDistInstance = v
    },
    data.value.departmentDistribution || {},
    t('hrm.workbench.deptDistribution')
  )
}

function renderRecruitStatusChart(): void {
  if (!data.value) return
  renderPieChart(
    recruitStatusRef.value,
    recruitStatusInstance,
    (v) => {
      recruitStatusInstance = v
    },
    data.value.recruitmentStatusDistribution || {},
    t('hrm.workbench.recruitStatusDistribution')
  )
}

function handleResize(): void {
  employeeTrendInstance?.resize()
  deptDistInstance?.resize()
  attendanceTrendInstance?.resize()
  recruitStatusInstance?.resize()
}

onMounted(async () => {
  await loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  employeeTrendInstance?.dispose()
  deptDistInstance?.dispose()
  attendanceTrendInstance?.dispose()
  recruitStatusInstance?.dispose()
})
</script>

<style scoped lang="scss">
.hrm-workbench-page {
  padding: 20px;

  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;

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

  .workbench-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .workbench-section {
    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;

      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }
  }

  .kpi-row {
    .kpi-card {
      text-align: center;
      cursor: default;

      .kpi-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--el-color-primary);
        line-height: 1.2;

        &.salary-value {
          font-size: 22px;
        }

        .kpi-sub {
          font-size: 16px;
          font-weight: 400;
          color: var(--el-text-color-secondary);
        }
      }

      .kpi-label {
        margin-top: 8px;
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .chart-row {
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

  .area-error {
    padding: 20px;
    background: var(--el-bg-color);
    border-radius: 8px;
  }
}
</style>
