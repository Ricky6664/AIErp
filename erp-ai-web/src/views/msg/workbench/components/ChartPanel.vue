<template>
  <el-card shadow="hover" class="chart-panel">
    <el-skeleton :loading="loading" animated :throttle="200">
      <template #template>
        <div class="chart-skeleton">
          <el-skeleton-item variant="h3" style="width: 40%; height: 24px; margin-bottom: 16px" />
          <el-skeleton-item variant="rect" style="width: 100%; height: 240px" />
        </div>
      </template>
      <template #default>
        <div v-if="isEmpty" class="chart-empty">
          <el-empty :description="emptyText" :image-size="100" />
        </div>
        <div v-else ref="chartRef" class="chart-container" :style="{ height: height + 'px' }" />
      </template>
    </el-skeleton>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, computed, shallowRef } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

const props = withDefaults(
  defineProps<{
    option: EChartsOption
    height?: number
    loading?: boolean
    emptyText?: string
  }>(),
  {
    height: 300,
    loading: false,
    emptyText: '暂无数据'
  }
)

const chartRef = ref<HTMLElement | null>(null)
const chart = shallowRef<echarts.ECharts | null>(null)

const isEmpty = computed(() => {
  if (props.loading) return false
  if (!props.option) return true
  const series = props.option.series
  if (!series || (Array.isArray(series) && series.length === 0)) return true
  if (Array.isArray(series)) {
    return series.every((s: any) => {
      const d = (s as any).data
      return !d || (Array.isArray(d) && d.length === 0)
    })
  }
  return false
})

function onResize() {
  chart.value?.resize()
}

onMounted(() => {
  if (!chartRef.value) return
  chart.value = echarts.init(chartRef.value)
  chart.value.setOption(props.option)
  window.addEventListener('resize', onResize)
})

watch(
  () => props.option,
  (val) => {
    if (chart.value && val) {
      chart.value.setOption(val, { notMerge: true })
    }
  },
  { deep: true }
)

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  chart.value?.dispose()
  chart.value = null
})
</script>

<style scoped lang="scss">
.chart-panel {
  :deep(.el-card__body) {
    padding: 16px;
  }

  .chart-skeleton {
    display: flex;
    flex-direction: column;
  }

  .chart-container {
    width: 100%;
  }

  .chart-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 200px;
  }
}
</style>
