<template>
  <div class="finance-workbench-page">
    <div class="page-header">
      <h2>{{ $t('finance.workbench.title') }}</h2>
      <p class="page-desc">{{ $t('finance.workbench.desc') }}</p>
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
            <h3>{{ $t('finance.workbench.kpiTitle') }}</h3>
          </div>
          <el-row :gutter="16" class="kpi-row">
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">{{ data.currencyRateCount ?? 0 }}</div>
                <div class="kpi-label">{{ $t('finance.workbench.currencyRateCount') }}</div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">
                  {{ data.activeBankAccountCount ?? 0 }}
                  <span class="kpi-sub">/ {{ data.bankAccountCount ?? 0 }}</span>
                </div>
                <div class="kpi-label">{{ $t('finance.workbench.bankAccountCount') }}</div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">
                  {{ data.leafAccountCount ?? 0 }}
                  <span class="kpi-sub">/ {{ data.accountCount ?? 0 }}</span>
                </div>
                <div class="kpi-label">{{ $t('finance.workbench.accountCount') }}</div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4">
              <el-card shadow="never" class="kpi-card">
                <div class="kpi-value">
                  {{ data.activeVoucherWordCount ?? 0 }}
                  <span class="kpi-sub">/ {{ data.voucherWordCount ?? 0 }}</span>
                </div>
                <div class="kpi-label">{{ $t('finance.workbench.voucherWordCount') }}</div>
              </el-card>
            </el-col>
          </el-row>
        </section>

        <!-- 图表区 -->
        <section class="workbench-section">
          <div class="section-header">
            <h3>{{ $t('finance.workbench.chartTitle') }}</h3>
          </div>
          <el-row :gutter="16" class="chart-row">
            <el-col :xs="24" :md="14">
              <el-card shadow="never">
                <template #header>
                  <span class="card-title">{{ $t('finance.workbench.trendTitle') }}</span>
                </template>
                <div ref="trendChartRef" class="chart-container"></div>
              </el-card>
            </el-col>
            <el-col :xs="24" :md="10">
              <el-card shadow="never">
                <template #header>
                  <span class="card-title">{{ $t('finance.workbench.distTitle') }}</span>
                </template>
                <div ref="distChartRef" class="chart-container"></div>
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
import * as echarts from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getFinanceWorkbenchApi, type FinanceWorkbenchVO } from '@/api/modules/finance-workbench'

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const loading = ref(false)
const error = ref(false)
const data = ref<FinanceWorkbenchVO | null>(null)

const trendChartRef = ref<HTMLElement | null>(null)
const distChartRef = ref<HTMLElement | null>(null)
let trendChartInstance: echarts.ECharts | null = null
let distChartInstance: echarts.ECharts | null = null

async function loadData(): Promise<void> {
  loading.value = true
  error.value = false
  try {
    data.value = await getFinanceWorkbenchApi()
    await nextTick()
    renderCharts()
  } catch {
    error.value = true
    ElMessage.error('加载工作台数据失败')
  } finally {
    loading.value = false
  }
}

function renderCharts(): void {
  if (!data.value) return
  renderTrendChart()
  renderDistChart()
}

function renderTrendChart(): void {
  if (!trendChartRef.value || !data.value) return

  if (!trendChartInstance) {
    trendChartInstance = echarts.init(trendChartRef.value)
  }

  const trend = data.value.monthlyTrend || []
  trendChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: trend.map((item) => item.month),
      boundaryGap: false
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '创建数量',
        type: 'line',
        data: trend.map((item) => item.count),
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        itemStyle: { color: '#409EFF' }
      }
    ]
  })
}

function renderDistChart(): void {
  if (!distChartRef.value || !data.value) return

  if (!distChartInstance) {
    distChartInstance = echarts.init(distChartRef.value)
  }

  const distribution = data.value.accountTypeDistribution || {}
  const pieData = Object.entries(distribution).map(([name, value]) => ({ name, value }))

  distChartInstance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        name: '科目类型',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        data: pieData.length > 0 ? pieData : [{ name: '暂无数据', value: 0 }],
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
        },
        label: { show: false },
        labelLine: { show: false }
      }
    ]
  })
}

function handleResize(): void {
  trendChartInstance?.resize()
  distChartInstance?.resize()
}

onMounted(async () => {
  await loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  distChartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.finance-workbench-page {
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
