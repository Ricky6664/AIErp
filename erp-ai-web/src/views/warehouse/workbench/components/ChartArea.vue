<template>
  <div class="chart-area">
    <div class="chart-toolbar">
      <el-radio-group v-model="timeRange" size="small" @change="handleRangeChange">
        <el-radio-button value="day">日</el-radio-button>
        <el-radio-button value="week">周</el-radio-button>
        <el-radio-button value="month">月</el-radio-button>
      </el-radio-group>
    </div>
    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">仓库创建趋势</div>
          <div ref="warehouseTrendRef" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">库位创建趋势</div>
          <div ref="locationTrendRef" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">仓库库位数量对比</div>
          <div ref="compareBarRef" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="12">
        <div class="chart-card">
          <div class="chart-header">库位分布</div>
          <div ref="locationDistRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getWarehouseWorkbenchChartApi } from '@/api/modules/warehouse-workbench'
import type {
  TimeRange,
  ChartTrendItem,
  ChartDistributionItem
} from '@/api/modules/warehouse-workbench'

const timeRange = ref<TimeRange>('day')

const warehouseTrendRef = ref<HTMLElement>()
const locationTrendRef = ref<HTMLElement>()
const compareBarRef = ref<HTMLElement>()
const locationDistRef = ref<HTMLElement>()

let warehouseTrendInstance: echarts.ECharts | null = null
let locationTrendInstance: echarts.ECharts | null = null
let compareBarInstance: echarts.ECharts | null = null
let locationDistInstance: echarts.ECharts | null = null

const observers: ResizeObserver[] = []

function makeResizeHandler(instance: echarts.ECharts | null): ResizeObserver {
  const obs = new ResizeObserver(() => {
    instance?.resize()
  })
  observers.push(obs)
  return obs
}

function buildTrendOption(data: ChartTrendItem[]): echarts.EChartsOption {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: { show: false },
    grid: { top: 16, right: 16, bottom: 24, left: 40 },
    xAxis: {
      type: 'category',
      data: data.map((d) => d.date),
      axisLabel: { fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontSize: 11 }
    },
    series: [
      {
        type: 'line',
        data: data.map((d) => d.count),
        smooth: true,
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.25)' },
            { offset: 1, color: 'rgba(64,158,255,0.02)' }
          ])
        }
      }
    ]
  }
}

function buildBarOption(data: ChartDistributionItem[]): echarts.EChartsOption {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: { show: false },
    grid: { top: 16, right: 16, bottom: 24, left: 40 },
    xAxis: {
      type: 'category',
      data: data.map((d) => d.name),
      axisLabel: { fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { fontSize: 11 }
    },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.value),
        barWidth: '50%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67C23A' },
            { offset: 1, color: '#95D475' }
          ])
        }
      }
    ]
  }
}

function buildPieOption(data: ChartDistributionItem[]): echarts.EChartsOption {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: { bottom: 0, textStyle: { fontSize: 11 } },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        center: ['50%', '48%'],
        data,
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        }
      }
    ]
  }
}

function initChart(
  container: HTMLElement | undefined,
  option: echarts.EChartsOption
): echarts.ECharts | null {
  if (!container) return null
  const instance = echarts.init(container)
  instance.setOption(option)
  const obs = makeResizeHandler(instance)
  obs.observe(container)
  return instance
}

function updateTrendChart(instance: echarts.ECharts | null, data: ChartTrendItem[]): void {
  if (!instance) return
  instance.setOption(buildTrendOption(data), { notMerge: false })
}

function updateBarChart(instance: echarts.ECharts | null, data: ChartDistributionItem[]): void {
  if (!instance) return
  instance.setOption(buildBarOption(data), { notMerge: false })
}

function updatePieChart(instance: echarts.ECharts | null, data: ChartDistributionItem[]): void {
  if (!instance) return
  instance.setOption(buildPieOption(data), { notMerge: false })
}

async function fetchChartData(): Promise<void> {
  // show loading on all charts
  warehouseTrendInstance?.showLoading()
  locationTrendInstance?.showLoading()
  compareBarInstance?.showLoading()
  locationDistInstance?.showLoading()

  try {
    const res = await getWarehouseWorkbenchChartApi(timeRange.value)
    if (res) {
      updateTrendChart(warehouseTrendInstance, res.warehouseTrend)
      updateTrendChart(locationTrendInstance, res.locationTrend)
      updateBarChart(compareBarInstance, res.warehouseDistribution)
      updatePieChart(locationDistInstance, res.locationDistribution)
    }
  } catch {
    // keep empty charts on error
  } finally {
    warehouseTrendInstance?.hideLoading()
    locationTrendInstance?.hideLoading()
    compareBarInstance?.hideLoading()
    locationDistInstance?.hideLoading()
  }
}

function handleRangeChange(): void {
  fetchChartData()
}

onMounted(async () => {
  await nextTick()
  warehouseTrendInstance = initChart(warehouseTrendRef.value, buildTrendOption([]))
  locationTrendInstance = initChart(locationTrendRef.value, buildTrendOption([]))
  compareBarInstance = initChart(compareBarRef.value, buildBarOption([]))
  locationDistInstance = initChart(locationDistRef.value, buildPieOption([]))
  fetchChartData()
})

onUnmounted(() => {
  warehouseTrendInstance?.dispose()
  locationTrendInstance?.dispose()
  compareBarInstance?.dispose()
  locationDistInstance?.dispose()
  observers.forEach((obs) => obs.disconnect())
})
</script>

<style scoped lang="scss">
.chart-area {
  .chart-toolbar {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 12px;
  }

  .chart-card {
    background: var(--el-bg-color, #fff);
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  }

  .chart-header {
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-primary, #303133);
    margin-bottom: 8px;
  }

  .chart-container {
    width: 100%;
    min-height: 280px;
  }
}
</style>
