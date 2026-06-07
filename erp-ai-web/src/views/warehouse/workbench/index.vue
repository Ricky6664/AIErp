<template>
  <div class="warehouse-workbench-page">
    <div class="page-header">
      <h2>{{ $t('warehouse.workbench.title') }}</h2>
      <p class="page-desc">{{ $t('warehouse.workbench.desc') }}</p>
      <el-select
        v-model="timeRange"
        :placeholder="$t('common.timeRange')"
        style="width: 120px"
        @change="handleTimeRangeChange"
      >
        <el-option
          v-for="item in timeRangeOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-button :icon="RefreshRight" :loading="isGlobalLoading" @click="handleRefreshAll">
        {{ $t('common.refresh') }}
      </el-button>
    </div>

    <div v-loading="isGlobalLoading" class="workbench-content">
      <!-- KPI卡片区 -->
      <section class="workbench-section">
        <div class="section-header">
          <h3>{{ $t('warehouse.workbench.kpiTitle') }}</h3>
        </div>
        <div v-if="kpiError" class="area-error">
          <el-result icon="error" sub-title="KPI数据加载失败">
            <template #extra>
              <el-button type="primary" size="small" @click="loadKpiArea">重试</el-button>
            </template>
          </el-result>
        </div>
        <Suspense v-else>
          <KpiCardArea ref="kpiCardAreaRef" />
        </Suspense>
      </section>

      <!-- 图表区 -->
      <section class="workbench-section">
        <div class="section-header">
          <h3>{{ $t('warehouse.workbench.chartTitle') }}</h3>
        </div>
        <div v-if="chartError" class="area-error">
          <el-result icon="error" sub-title="图表数据加载失败">
            <template #extra>
              <el-button type="primary" size="small" @click="loadChartArea">重试</el-button>
            </template>
          </el-result>
        </div>
        <el-row v-else :gutter="16" class="chart-row">
          <el-col :xs="24" :md="14">
            <el-card shadow="never">
              <template #header>
                <span class="card-title">{{ $t('warehouse.workbench.trendTitle') }}</span>
              </template>
              <div ref="trendChartRef" class="chart-container"></div>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="10">
            <el-card shadow="never">
              <template #header>
                <span class="card-title">{{ $t('warehouse.workbench.distTitle') }}</span>
              </template>
              <div ref="distChartRef" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>
      </section>

      <!-- 待办区 -->
      <section class="workbench-section">
        <div class="section-header">
          <h3>{{ $t('warehouse.workbench.todoTitle') }}</h3>
        </div>
        <div v-if="todoError" class="area-error">
          <el-result icon="error" sub-title="待办数据加载失败">
            <template #extra>
              <el-button type="primary" size="small" @click="loadTodoArea">重试</el-button>
            </template>
          </el-result>
        </div>
        <el-card v-else shadow="never">
          <el-table :data="todoItems" border stripe empty-text="暂无待办事项" style="width: 100%">
            <el-table-column
              prop="title"
              :label="$t('warehouse.workbench.todoTitle')"
              min-width="200"
            />
            <el-table-column
              prop="type"
              :label="$t('warehouse.workbench.todoType')"
              width="120"
              align="center"
            >
              <template #default="{ row }">
                <el-tag :type="getTodoTagType(row.type)" size="small">
                  {{ row.typeLabel }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="createTime"
              :label="$t('warehouse.workbench.todoTime')"
              width="180"
            />
            <el-table-column :label="$t('common.action')" width="120" align="center">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleTodoClick(row)">
                  {{ $t('common.process') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, provide, readonly } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { RefreshRight } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import KpiCardArea from './components/KpiCardArea.vue'
import {
  getWarehouseWorkbenchChartApi,
  type TimeRange,
  type WarehouseWorkbenchChartVO
} from '@/api/modules/warehouse-workbench'
import { WORKBENCH_CONTEXT_KEY, type WorkbenchContext } from './types'

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const timeRange = ref<TimeRange>('week')
const loadingCount = ref(0)
const isGlobalLoading = ref(false)
const kpiError = ref(false)
const chartError = ref(false)
const todoError = ref(false)

const trendChartRef = ref<HTMLElement | null>(null)
const distChartRef = ref<HTMLElement | null>(null)
let trendChartInstance: echarts.ECharts | null = null
let distChartInstance: echarts.ECharts | null = null

const router = useRouter()

const timeRangeOptions = [
  { label: '今日', value: 'day' as TimeRange },
  { label: '近7天', value: 'week' as TimeRange },
  { label: '近30天', value: 'month' as TimeRange }
]

interface TodoItem {
  id: string
  title: string
  type: string
  typeLabel: string
  createTime: string
  route?: string
}

const kpiCardAreaRef = ref<InstanceType<typeof KpiCardArea> | null>(null)
const todoItems = ref<TodoItem[]>([])

// ========== 全局loading计数器 ==========
function incrementLoading(): void {
  loadingCount.value++
  isGlobalLoading.value = true
}

function decrementLoading(): void {
  loadingCount.value = Math.max(0, loadingCount.value - 1)
  if (loadingCount.value === 0) {
    isGlobalLoading.value = false
  }
}

// ========== 区域数据加载 ==========
async function loadKpiArea(): Promise<void> {
  kpiError.value = false
  incrementLoading()
  try {
    await kpiCardAreaRef.value?.loadData()
  } catch {
    kpiError.value = true
  } finally {
    decrementLoading()
  }
}

async function loadChartArea(): Promise<void> {
  chartError.value = false
  incrementLoading()
  try {
    const data = await getWarehouseWorkbenchChartApi(timeRange.value)
    renderCharts(data)
  } catch {
    chartError.value = true
  } finally {
    decrementLoading()
  }
}

async function loadTodoArea(): Promise<void> {
  todoError.value = false
  incrementLoading()
  try {
    // 待办数据暂用模拟数据，后续对接后端API
    todoItems.value = getDefaultTodoItems()
  } catch {
    todoError.value = true
  } finally {
    decrementLoading()
  }
}

function getDefaultTodoItems(): TodoItem[] {
  return [
    {
      id: '1',
      title: '仓库A库位使用率超过80%',
      type: 'warning',
      typeLabel: '预警',
      createTime: new Date().toLocaleString()
    },
    {
      id: '2',
      title: '仓库B待审核入库单3笔',
      type: 'info',
      typeLabel: '待处理',
      createTime: new Date().toLocaleString()
    }
  ]
}

// ========== 并行加载所有区域 ==========
async function loadAllAreas(): Promise<void> {
  const results = await Promise.allSettled([loadKpiArea(), loadChartArea(), loadTodoArea()])

  const failedCount = results.filter((r) => r.status === 'rejected').length
  if (failedCount > 0) {
    console.warn(`[Workbench] ${failedCount} area(s) failed to load`)
  }
}

// ========== ECharts渲染 ==========
function renderCharts(data: WarehouseWorkbenchChartVO): void {
  renderTrendChart(data)
  renderDistChart(data)
}

function renderTrendChart(data: WarehouseWorkbenchChartVO): void {
  if (!trendChartRef.value) return

  if (!trendChartInstance) {
    trendChartInstance = echarts.init(trendChartRef.value)
  }

  trendChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['仓库趋势', '库位趋势'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '12%', containLabel: true },
    xAxis: {
      type: 'category',
      data: (data.warehouseTrend || []).map((item) => item.date),
      boundaryGap: false
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '仓库趋势',
        type: 'line',
        data: (data.warehouseTrend || []).map((item) => item.count),
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '库位趋势',
        type: 'line',
        data: (data.locationTrend || []).map((item) => item.count),
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        itemStyle: { color: '#67C23A' }
      }
    ]
  })
}

function renderDistChart(data: WarehouseWorkbenchChartVO): void {
  if (!distChartRef.value) return

  if (!distChartInstance) {
    distChartInstance = echarts.init(distChartRef.value)
  }

  distChartInstance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        name: '仓库分布',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        data: (data.warehouseDistribution || []).map((item) => ({
          name: item.name,
          value: item.value
        })),
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
        },
        label: { show: false },
        labelLine: { show: false }
      }
    ]
  })
}

// ========== 事件处理 ==========
async function handleRefreshAll(): Promise<void> {
  await loadAllAreas()
  ElMessage.success('数据已刷新')
}

function handleTimeRangeChange(): void {
  loadAllAreas()
}

function getTodoTagType(type: string): 'warning' | 'info' | 'danger' | 'success' {
  const map: Record<string, 'warning' | 'info' | 'danger' | 'success'> = {
    warning: 'warning',
    info: 'info',
    danger: 'danger',
    success: 'success'
  }
  return map[type] || 'info'
}

function handleTodoClick(row: TodoItem): void {
  if (row.route) {
    router.push(row.route)
  }
}

function handleResize(): void {
  trendChartInstance?.resize()
  distChartInstance?.resize()
}

// ========== provide context ==========
provide(WORKBENCH_CONTEXT_KEY, {
  timeRange: readonly(timeRange),
  refresh: loadAllAreas
} as WorkbenchContext)

// ========== 生命周期 ==========
onMounted(async () => {
  await loadAllAreas()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChartInstance?.dispose()
  distChartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.warehouse-workbench-page {
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
