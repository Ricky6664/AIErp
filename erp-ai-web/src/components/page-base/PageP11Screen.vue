<template>
  <div class="page-p11-screen" :class="{ 'theme-dark': isDarkTheme, 'theme-light': !isDarkTheme }">
    <!-- 页面头部 -->
    <div v-if="isDarkTheme && config.title" class="screen-header">
      <h1 class="screen-title">{{ config.title }}</h1>
      <div class="screen-header-right">
        <span v-if="config.refreshInterval && config.refreshInterval > 0" class="refresh-timer"
          >每 {{ config.refreshInterval }}s 刷新</span
        >
        <el-button
          v-if="config.showFullscreenBtn !== false"
          :icon="isFullscreen ? 'Close' : FullScreen"
          circle
          class="fullscreen-btn"
          @click="toggleFullscreen"
        />
      </div>
    </div>

    <!-- KPI 卡片区 -->
    <div
      v-if="config.showKpiArea !== false && config.kpiCards && config.kpiCards.length > 0"
      class="kpi-area"
    >
      <div
        v-for="card in config.kpiCards"
        :key="card.id"
        class="kpi-card"
        :class="card.color ? `kpi-card--${card.color}` : ''"
      >
        <div class="kpi-card-icon">
          <el-icon v-if="card.icon" :size="28">
            <component :is="card.icon" />
          </el-icon>
          <span v-else class="kpi-card-dot" />
        </div>
        <div class="kpi-card-body">
          <div class="kpi-card-label">{{ card.label }}</div>
          <div class="kpi-card-value">
            <span ref="kpiCounters" class="kpi-card-number">{{
              animatedValues[card.id] ?? card.value
            }}</span>
            <span v-if="card.unit" class="kpi-card-unit">{{ card.unit }}</span>
          </div>
          <div v-if="card.trend !== undefined" class="kpi-card-trend">
            <el-icon :size="12">
              <CaretTop v-if="card.trend >= 0" />
              <CaretBottom v-else />
            </el-icon>
            <span :class="card.trend >= 0 ? 'trend-up' : 'trend-down'">
              {{ Math.abs(card.trend) }}%
            </span>
            <span class="trend-label">较上期</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表网格区 -->
    <div class="chart-grid-area">
      <slot name="main-content">
        <div v-if="config.charts && config.charts.length > 0" class="chart-grid" :style="gridStyle">
          <div
            v-for="chart in config.charts"
            :key="chart.id"
            class="chart-panel"
            :class="chartPanelClass(chart)"
            :style="chartPanelStyle(chart)"
          >
            <div class="chart-panel-header">
              <div class="chart-panel-title-group">
                <span class="chart-panel-title">{{ chart.title }}</span>
                <span v-if="chart.subtitle" class="chart-panel-subtitle">{{ chart.subtitle }}</span>
              </div>
              <el-tag v-if="chart.type" size="small" class="chart-type-tag">
                {{ chartTypeLabel(chart.type) }}
              </el-tag>
            </div>
            <div class="chart-panel-body">
              <div class="area-placeholder">
                <el-icon :size="36"><DataAnalysis /></el-icon>
                <span>{{ chart.title }} — 可通过 main-content 插槽自定义</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="area-placeholder chart-empty">
          <el-icon :size="48"><TrendCharts /></el-icon>
          <span>图表区 — 可通过 main-content 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import {
  FullScreen,
  DataAnalysis,
  TrendCharts,
  CaretTop,
  CaretBottom
} from '@element-plus/icons-vue'
import type {
  PageBaseProps,
  PageBaseEmits,
  ScreenPageConfig,
  ScreenChartItemConfig
} from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<ScreenPageConfig>(() => {
  return (props.config || {}) as ScreenPageConfig
})

const isDarkTheme = computed(() => config.value.darkTheme !== false)

const isFullscreen = ref(config.value.fullscreen ?? false)
const refreshTimerId = ref<ReturnType<typeof setInterval> | null>(null)
const isAutoRefreshing = ref(false)
const kpiCounters = ref<HTMLElement[]>([])
const animatedValues = reactive<Record<string, number>>({})

const gridStyle = computed<Record<string, string>>(() => {
  const cols = config.value.gridCols ?? 12
  return {
    gridTemplateColumns: `repeat(${cols}, 1fr)`
  }
})

function chartPanelClass(chart: ScreenChartItemConfig): string {
  const classes: string[] = []
  if (chart.colSpan) {
    classes.push(`chart-col-span-${chart.colSpan}`)
  }
  if (chart.rowSpan) {
    classes.push(`chart-row-span-${chart.rowSpan}`)
  }
  return classes.join(' ')
}

function chartPanelStyle(chart: ScreenChartItemConfig): Record<string, string> {
  const style: Record<string, string> = {}
  if (chart.colSpan) {
    style.gridColumn = `span ${chart.colSpan}`
  }
  if (chart.rowSpan) {
    style.gridRow = `span ${chart.rowSpan}`
  }
  return style
}

function chartTypeLabel(type: ScreenChartItemConfig['type']): string {
  const map: Record<string, string> = {
    line: '折线图',
    bar: '柱状图',
    pie: '饼图',
    number: '数字',
    gauge: '仪表盘',
    map: '地图'
  }
  return map[type] ?? type
}

function animateValue(cardId: string, targetValue: number, duration: number) {
  const startValue = animatedValues[cardId] ?? 0
  const startTime = performance.now()
  const diff = targetValue - startValue

  function step(currentTime: number) {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    animatedValues[cardId] = Math.round(startValue + diff * eased)
    if (progress < 1) {
      requestAnimationFrame(step)
    }
  }

  requestAnimationFrame(step)
}

function startKpiAnimation() {
  if (config.value.kpiCards) {
    for (const card of config.value.kpiCards) {
      const duration = card.animationDuration ?? 1000
      const numVal = typeof card.value === 'number' ? card.value : parseFloat(String(card.value))
      if (!isNaN(numVal)) {
        animateValue(card.id, numVal, duration)
      } else {
        animatedValues[card.id] = card.value as unknown as number
      }
    }
  }
}

function startRefreshTimer() {
  stopRefreshTimer()
  const interval = config.value.refreshInterval
  if (interval && interval > 0) {
    refreshTimerId.value = setInterval(() => {
      emit('data-change', { source: 'screen-auto-refresh', data: { timestamp: Date.now() } })
      startKpiAnimation()
    }, interval * 1000)
    isAutoRefreshing.value = true
  }
}

function stopRefreshTimer() {
  if (refreshTimerId.value !== null) {
    clearInterval(refreshTimerId.value)
    refreshTimerId.value = null
  }
  isAutoRefreshing.value = false
}

function toggleFullscreen() {
  const el = document.querySelector('.page-p11-screen') as HTMLElement
  if (!document.fullscreenElement) {
    el?.requestFullscreen?.()
    isFullscreen.value = true
  } else {
    document.exitFullscreen?.()
    isFullscreen.value = false
  }
  emit('data-change', {
    source: 'screen-fullscreen-toggle',
    data: { fullscreen: isFullscreen.value }
  })
}

function handleFullscreenChange() {
  isFullscreen.value = !!document.fullscreenElement
}

watch(
  () => config.value.refreshInterval,
  () => {
    startRefreshTimer()
  }
)

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  startKpiAnimation()
  startRefreshTimer()
})

onBeforeUnmount(() => {
  stopRefreshTimer()
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped lang="scss">
.page-p11-screen {
  width: 100%;
  height: 100%;
  overflow: auto;
  display: flex;
  flex-direction: column;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;

  &.theme-dark {
    background: linear-gradient(135deg, #0a1628 0%, #0f1f3a 50%, #0d1b33 100%);
    color: #e0e6ed;
  }

  &.theme-light {
    background: var(--el-bg-color-page);
    color: var(--el-text-color-primary);
    padding: 16px;
    gap: 12px;
  }
}

// 页面头部
.screen-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 32px 12px;
  flex-shrink: 0;
  position: relative;
}

.screen-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 4px;
  color: #fff;
  text-shadow: 0 2px 12px rgba(64, 158, 255, 0.4);
}

.screen-header-right {
  position: absolute;
  right: 32px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.refresh-timer {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
  letter-spacing: 1px;
}

.fullscreen-btn {
  color: rgba(255, 255, 255, 0.65);
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.06);

  &:hover {
    color: #fff;
    border-color: rgba(255, 255, 255, 0.4);
    background: rgba(255, 255, 255, 0.12);
  }
}

// KPI 区域
.kpi-area {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 20px 32px 8px;
  flex-shrink: 0;
}

.kpi-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 24px;
  border-radius: 12px;
  flex: 1;
  min-width: 220px;
  max-width: 320px;
  transition:
    transform 0.2s,
    box-shadow 0.2s;

  .theme-dark & {
    background: linear-gradient(135deg, rgba(30, 60, 120, 0.5), rgba(20, 40, 90, 0.4));
    border: 1px solid rgba(64, 158, 255, 0.15);
    box-shadow: 0 2px 16px rgba(0, 0, 0, 0.2);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 24px rgba(64, 158, 255, 0.1);
    }
  }

  .theme-light & {
    background: var(--el-bg-color);
    border-radius: 12px;
    padding: 14px 18px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    }
  }

  &--blue {
    .theme-dark & {
      border-left: 3px solid #409eff;
    }
    .kpi-card-dot {
      background: #409eff;
    }
  }
  &--green {
    .theme-dark & {
      border-left: 3px solid #67c23a;
    }
    .kpi-card-dot {
      background: #67c23a;
    }
  }
  &--orange {
    .theme-dark & {
      border-left: 3px solid #e6a23c;
    }
    .kpi-card-dot {
      background: #e6a23c;
    }
  }
  &--purple {
    .theme-dark & {
      border-left: 3px solid #a855f7;
    }
    .kpi-card-dot {
      background: #a855f7;
    }
  }
  &--red {
    .theme-dark & {
      border-left: 3px solid #f56c6c;
    }
    .kpi-card-dot {
      background: #f56c6c;
    }
  }
}

.kpi-card-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 12px;

  .theme-dark & {
    background: rgba(64, 158, 255, 0.1);
    color: #409eff;
  }

  .theme-light & {
    background: var(--el-fill-color-light);
    color: var(--el-color-primary);
  }
}

.kpi-card-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.kpi-card-body {
  flex: 1;
  min-width: 0;
}

.kpi-card-label {
  font-size: 13px;
  margin-bottom: 4px;

  .theme-dark & {
    color: rgba(224, 230, 237, 0.6);
  }

  .theme-light & {
    color: var(--el-text-color-secondary);
  }
}

.kpi-card-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 6px;
}

.kpi-card-number {
  font-size: 28px;
  font-weight: 700;
  font-family: 'DIN', 'Monaco', 'Menlo', monospace;
  line-height: 1.2;

  .theme-dark & {
    color: #e0e6ed;
  }

  .theme-light & {
    color: var(--el-text-color-primary);
  }
}

.kpi-card-unit {
  font-size: 13px;

  .theme-dark & {
    color: rgba(224, 230, 237, 0.45);
  }

  .theme-light & {
    color: var(--el-text-color-placeholder);
  }
}

.kpi-card-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
}

.trend-up {
  color: #67c23a;
  font-weight: 500;
}

.trend-down {
  color: #f56c6c;
  font-weight: 500;
}

.trend-label {
  color: rgba(224, 230, 237, 0.35);
  margin-left: 4px;

  .theme-light & {
    color: var(--el-text-color-placeholder);
  }
}

// 图表网格区域
.chart-grid-area {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 16px 32px 20px;
}

.chart-grid {
  display: grid;
  gap: 16px;
  width: 100%;
  min-height: 100%;
  align-content: start;
}

.chart-panel {
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
  min-height: 260px;

  .theme-dark & {
    background: linear-gradient(180deg, rgba(30, 60, 110, 0.45), rgba(18, 35, 70, 0.4));
    border: 1px solid rgba(64, 158, 255, 0.1);
  }

  .theme-light & {
    background: var(--el-bg-color);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
  }
}

.chart-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid;
  flex-shrink: 0;

  .theme-dark & {
    border-bottom-color: rgba(64, 158, 255, 0.1);
  }

  .theme-light & {
    border-bottom-color: var(--el-border-color-lighter);
  }
}

.chart-panel-title-group {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.chart-panel-title {
  font-size: 14px;
  font-weight: 600;

  .theme-dark & {
    color: #e0e6ed;
  }

  .theme-light & {
    color: var(--el-text-color-primary);
  }
}

.chart-panel-subtitle {
  font-size: 12px;

  .theme-dark & {
    color: rgba(224, 230, 237, 0.45);
  }

  .theme-light & {
    color: var(--el-text-color-placeholder);
  }
}

.chart-type-tag {
  .theme-dark & {
    background: rgba(64, 158, 255, 0.12);
    border-color: rgba(64, 158, 255, 0.2);
    color: #409eff;
  }
}

.chart-panel-body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 0;
}

.area-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;

  .theme-dark & {
    color: rgba(224, 230, 237, 0.3);
  }

  .theme-light & {
    color: var(--el-text-color-placeholder);
  }
}

.chart-empty {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

// 额外区域
.extra-area {
  padding: 12px 32px 20px;
  flex-shrink: 0;

  .theme-light & {
    background: var(--el-bg-color);
    border-radius: 12px;
    padding: 16px 20px;
  }
}
</style>
