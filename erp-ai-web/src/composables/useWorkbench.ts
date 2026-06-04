import { ref, shallowRef } from 'vue'
import type { WorkbenchData, LoginTrendItem, RoleDistributionItem } from '@/api/types/workbench'
import { getWorkbenchDataApi } from '@/api/modules/workbench'
import * as echarts from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

export function useWorkbench() {
  const loading = ref(false)
  const workbenchData = ref<WorkbenchData | null>(null)
  const trendChartRef = shallowRef<echarts.ECharts | null>(null)
  const pieChartRef = shallowRef<echarts.ECharts | null>(null)

  async function fetchData(): Promise<void> {
    loading.value = true
    try {
      workbenchData.value = await getWorkbenchDataApi()
    } finally {
      loading.value = false
    }
  }

  function initTrendChart(container: HTMLElement): void {
    const chart = echarts.init(container)
    trendChartRef.value = chart
  }

  function updateTrendChart(data: LoginTrendItem[]): void {
    if (!trendChartRef.value) return
    trendChartRef.value.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: data.map((item) => item.date),
        boundaryGap: false
      },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          name: '登录次数',
          type: 'line',
          data: data.map((item) => item.count),
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

  function initPieChart(container: HTMLElement): void {
    const chart = echarts.init(container)
    pieChartRef.value = chart
  }

  function updatePieChart(data: RoleDistributionItem[]): void {
    if (!pieChartRef.value) return
    pieChartRef.value.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, type: 'scroll' },
      series: [
        {
          name: '角色分布',
          type: 'pie',
          radius: ['45%', '70%'],
          center: ['50%', '45%'],
          data: data.map((item) => ({ name: item.roleName, value: item.userCount })),
          emphasis: {
            itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
          },
          label: { show: false },
          labelLine: { show: false }
        }
      ]
    })
  }

  function getStatusType(status: string): 'success' | 'danger' {
    return status === 'success' ? 'success' : 'danger'
  }

  function resizeCharts(): void {
    trendChartRef.value?.resize()
    pieChartRef.value?.resize()
  }

  function disposeCharts(): void {
    trendChartRef.value?.dispose()
    pieChartRef.value?.dispose()
  }

  return {
    loading,
    workbenchData,
    fetchData,
    initTrendChart,
    updateTrendChart,
    initPieChart,
    updatePieChart,
    getStatusType,
    resizeCharts,
    disposeCharts
  }
}
