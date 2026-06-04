import { ref, shallowRef } from 'vue'
import type {
  AuthConfigWorkbenchData,
  LoginMethodDistItem,
  DailyLoginStatItem
} from '@/api/types/authConfig'
import { getAuthConfigWorkbenchApi } from '@/api/modules/authConfig'
import type { WorkbenchQueryParams } from '@/api/modules/authConfig'
import * as echarts from 'echarts/core'
import { LineChart, BarChart, PieChart, RadarChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([
  LineChart,
  BarChart,
  PieChart,
  RadarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  CanvasRenderer
])

export type DimensionType = 'day' | 'week' | 'month'

export function useAuthConfigWorkbench() {
  const loading = ref(false)
  const workbenchData = ref<AuthConfigWorkbenchData | null>(null)
  const loginDistChartRef = shallowRef<echarts.ECharts | null>(null)
  const dailyLoginChartRef = shallowRef<echarts.ECharts | null>(null)
  const barChartRef = shallowRef<echarts.ECharts | null>(null)
  const radarChartRef = shallowRef<echarts.ECharts | null>(null)
  const dimension = ref<DimensionType>('day')

  const allChartRefs = () =>
    [loginDistChartRef, dailyLoginChartRef, barChartRef, radarChartRef]
      .map((r) => r.value)
      .filter(Boolean) as echarts.ECharts[]

  async function fetchData(params?: WorkbenchQueryParams): Promise<void> {
    loading.value = true
    try {
      workbenchData.value = await getAuthConfigWorkbenchApi(params)
    } finally {
      loading.value = false
    }
  }

  // ---- Pie: 登录方式分布 ----
  function initLoginDistChart(container: HTMLElement): void {
    loginDistChartRef.value = echarts.init(container)
  }

  function updateLoginDistChart(data: LoginMethodDistItem[]): void {
    if (!loginDistChartRef.value) return
    loginDistChartRef.value.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, type: 'scroll' },
      series: [
        {
          name: '登录方式分布',
          type: 'pie',
          radius: ['45%', '70%'],
          center: ['50%', '45%'],
          data: data.map((item) => ({ name: item.loginMethod, value: item.count })),
          emphasis: {
            itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
          },
          label: { show: false },
          labelLine: { show: false }
        }
      ]
    })
  }

  // ---- Line: 每日登录统计 ----
  function initDailyLoginChart(container: HTMLElement): void {
    dailyLoginChartRef.value = echarts.init(container)
  }

  function updateDailyLoginChart(data: DailyLoginStatItem[]): void {
    if (!dailyLoginChartRef.value) return
    const reversed = [...data].reverse()
    dailyLoginChartRef.value.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['登录成功', '登录失败'], bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '12%', containLabel: true },
      xAxis: {
        type: 'category',
        data: reversed.map((item) => item.loginDate),
        boundaryGap: false
      },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          name: '登录成功',
          type: 'line',
          data: reversed.map((item) => item.successCount),
          smooth: true,
          itemStyle: { color: '#67c23a' }
        },
        {
          name: '登录失败',
          type: 'line',
          data: reversed.map((item) => item.failCount),
          smooth: true,
          itemStyle: { color: '#f56c6c' }
        }
      ]
    })
  }

  // ---- Bar: 每日登录统计（柱状图） ----
  function initBarChart(container: HTMLElement): void {
    barChartRef.value = echarts.init(container)
  }

  function updateBarChart(data: DailyLoginStatItem[]): void {
    if (!barChartRef.value) return
    const reversed = [...data].reverse()
    barChartRef.value.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: { data: ['登录成功', '登录失败'], bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '12%', containLabel: true },
      xAxis: {
        type: 'category',
        data: reversed.map((item) => item.loginDate)
      },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          name: '登录成功',
          type: 'bar',
          data: reversed.map((item) => item.successCount),
          itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] },
          barMaxWidth: 32
        },
        {
          name: '登录失败',
          type: 'bar',
          data: reversed.map((item) => item.failCount),
          itemStyle: { color: '#f56c6c', borderRadius: [4, 4, 0, 0] },
          barMaxWidth: 32
        }
      ]
    })
  }

  // ---- Radar: 登录方式雷达图 ----
  function initRadarChart(container: HTMLElement): void {
    radarChartRef.value = echarts.init(container)
  }

  function updateRadarChart(data: LoginMethodDistItem[]): void {
    if (!radarChartRef.value || data.length === 0) return
    const maxVal = Math.max(...data.map((d) => d.count), 1)
    radarChartRef.value.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      radar: {
        center: ['50%', '45%'],
        radius: '60%',
        indicator: data.map((item) => ({
          name: item.loginMethod,
          max: maxVal * 1.2
        }))
      },
      series: [
        {
          name: '登录方式分布',
          type: 'radar',
          data: [
            {
              value: data.map((item) => item.count),
              name: '登录方式',
              areaStyle: { color: 'rgba(64, 158, 255, 0.2)' },
              lineStyle: { color: '#409eff' },
              itemStyle: { color: '#409eff' }
            }
          ]
        }
      ]
    })
  }

  // ---- Export ----
  function exportChartAsImage(chartRef: echarts.ECharts | null, filename: string): void {
    if (!chartRef) return
    const url = chartRef.getDataURL({ type: 'png', pixelRatio: 2, backgroundColor: '#fff' })
    const link = document.createElement('a')
    link.href = url
    link.download = `${filename}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  // ---- Resize / Dispose ----
  function resizeCharts(): void {
    allChartRefs().forEach((c) => c.resize())
  }

  function disposeCharts(): void {
    allChartRefs().forEach((c) => c.dispose())
  }

  return {
    loading,
    workbenchData,
    dimension,
    loginDistChartRef,
    dailyLoginChartRef,
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
  }
}
