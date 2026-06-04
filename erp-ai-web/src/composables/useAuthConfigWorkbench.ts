import { ref, shallowRef } from 'vue'
import type {
  AuthConfigWorkbenchData,
  LoginMethodDistItem,
  DailyLoginStatItem
} from '@/api/types/authConfig'
import { getAuthConfigWorkbenchApi } from '@/api/modules/authConfig'
import type { WorkbenchQueryParams } from '@/api/modules/authConfig'
import * as echarts from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

export function useAuthConfigWorkbench() {
  const loading = ref(false)
  const workbenchData = ref<AuthConfigWorkbenchData | null>(null)
  const loginDistChartRef = shallowRef<echarts.ECharts | null>(null)
  const dailyLoginChartRef = shallowRef<echarts.ECharts | null>(null)

  async function fetchData(params?: WorkbenchQueryParams): Promise<void> {
    loading.value = true
    try {
      workbenchData.value = await getAuthConfigWorkbenchApi(params)
    } finally {
      loading.value = false
    }
  }

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

  function resizeCharts(): void {
    loginDistChartRef.value?.resize()
    dailyLoginChartRef.value?.resize()
  }

  function disposeCharts(): void {
    loginDistChartRef.value?.dispose()
    dailyLoginChartRef.value?.dispose()
  }

  return {
    loading,
    workbenchData,
    fetchData,
    initLoginDistChart,
    updateLoginDistChart,
    initDailyLoginChart,
    updateDailyLoginChart,
    resizeCharts,
    disposeCharts
  }
}
