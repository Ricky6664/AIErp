<template>
  <el-card shadow="hover" class="kpi-card">
    <div v-if="loading" class="kpi-skeleton">
      <el-skeleton animated>
        <template #template>
          <div class="skeleton-content">
            <el-skeleton-item variant="text" style="width: 60%; height: 32px" />
            <el-skeleton-item variant="text" style="width: 40%; height: 20px" />
            <el-skeleton-item variant="text" style="width: 30%; height: 16px" />
          </div>
        </template>
      </el-skeleton>
    </div>
    <div v-else class="kpi-content">
      <div class="kpi-value">{{ formatNumber(value) }}</div>
      <div class="kpi-title">{{ title }}</div>
      <div class="kpi-trend" :class="trendClass">
        <el-icon :size="14">
          <ArrowUp v-if="trend > 0" />
          <ArrowDown v-else-if="trend < 0" />
          <Minus v-else />
        </el-icon>
        <span class="trend-value">{{ Math.abs(trend) }}%</span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowUp, ArrowDown, Minus } from '@element-plus/icons-vue'
import { formatQty } from '@/utils/number'

const props = withDefaults(
  defineProps<{
    title: string
    value: number
    trend: number
    loading: boolean
  }>(),
  {
    title: '',
    value: 0,
    trend: 0,
    loading: false
  }
)

const trendClass = computed(() => {
  if (props.trend > 0) return 'trend-up'
  if (props.trend < 0) return 'trend-down'
  return 'trend-flat'
})

function formatNumber(val: number): string {
  return formatQty(val)
}
</script>

<style scoped lang="scss">
.kpi-card {
  :deep(.el-card__body) {
    padding: 20px;
  }

  .kpi-skeleton {
    .skeleton-content {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
  }

  .kpi-content {
    .kpi-value {
      font-size: 28px;
      font-weight: 700;
      line-height: 1.2;
      color: var(--el-text-color-primary);
    }

    .kpi-title {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin-top: 8px;
    }

    .kpi-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-top: 8px;
      font-size: 13px;

      .trend-value {
        font-weight: 500;
      }

      &.trend-up {
        color: #67c23a;
      }

      &.trend-down {
        color: #f56c6c;
      }

      &.trend-flat {
        color: #909399;
      }
    }
  }
}
</style>
