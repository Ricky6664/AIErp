<template>
  <el-card shadow="hover" class="kpi-card">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="kpi-skeleton">
          <el-skeleton-item variant="text" style="width: 60%; height: 32px" />
          <el-skeleton-item variant="text" style="width: 40%; height: 18px; margin-top: 8px" />
          <el-skeleton-item variant="text" style="width: 30%; height: 16px; margin-top: 8px" />
        </div>
      </template>
      <template #default>
        <div class="kpi-body">
          <div class="kpi-value">{{ formatNumber(value) }}</div>
          <div class="kpi-title">{{ title }}</div>
          <div v-if="trend !== undefined" class="kpi-trend" :class="trendClass">
            <el-icon><component :is="trendIcon" /></el-icon>
            <span class="kpi-trend__text">{{ Math.abs(trend) }}%</span>
          </div>
        </div>
      </template>
    </el-skeleton>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowUp, ArrowDown, Minus } from '@element-plus/icons-vue'

const props = withDefaults(
  defineProps<{
    title: string
    value: number
    trend?: number
    loading?: boolean
  }>(),
  {
    loading: false,
    trend: undefined
  }
)

const trendClass = computed(() => {
  if (props.trend === undefined || props.trend === 0) return 'trend-flat'
  return props.trend > 0 ? 'trend-up' : 'trend-down'
})

const trendIcon = computed(() => {
  if (props.trend === undefined || props.trend === 0) return Minus
  return props.trend > 0 ? ArrowUp : ArrowDown
})

function formatNumber(num: number): string {
  if (num === undefined || num === null) return '-'
  return num.toLocaleString()
}
</script>

<style scoped>
.kpi-card :deep(.el-card__body) {
  padding: 20px;
}

.kpi-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.kpi-skeleton {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.kpi-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
  color: #303133;
}

.kpi-title {
  font-size: 14px;
  color: #909399;
}

.kpi-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
  margin-top: 2px;
}

.kpi-trend__text {
  line-height: 1;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

.trend-flat {
  color: #909399;
}
</style>
