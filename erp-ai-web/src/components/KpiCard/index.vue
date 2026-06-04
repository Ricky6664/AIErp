<template>
  <div
    class="kpi-card"
    :class="[`kpi-card--${color}`, { 'kpi-card--clickable': clickable }]"
    @click="handleClick"
  >
    <div class="kpi-icon">
      <el-icon :size="32">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="kpi-info">
      <div class="kpi-value">{{ displayValue }}</div>
      <div class="kpi-label">{{ label }}</div>
      <div v-if="trend !== undefined" class="kpi-trend">
        <span :class="trend >= 0 ? 'trend-up' : 'trend-down'">
          <el-icon :size="14">
            <CaretTop v-if="trend >= 0" />
            <CaretBottom v-else />
          </el-icon>
          {{ Math.abs(trend) }}%
        </span>
        <span class="trend-compare">{{ compareLabel }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { CaretTop, CaretBottom } from '@element-plus/icons-vue'
import { formatQty } from '@/utils/number'

const props = withDefaults(
  defineProps<{
    icon: any
    label: string
    value: number
    color?: 'blue' | 'green' | 'orange' | 'purple'
    to?: string
    trend?: number
    compareLabel?: string
    animated?: boolean
    formatValue?: (value: number) => string
  }>(),
  {
    color: 'blue',
    animated: true,
    compareLabel: '较上期'
  }
)

const emit = defineEmits<{
  click: []
}>()

const router = useRouter()

const animatedValue = ref(0)

const clickable = computed(() => !!(props.to || emit))

const displayValue = computed(() => {
  const val = props.animated ? animatedValue.value : props.value
  if (props.formatValue) {
    return props.formatValue(val)
  }
  return formatQty(val)
})

function animateTo(target: number): void {
  if (!props.animated || target === 0) {
    animatedValue.value = target
    return
  }
  const duration = 800
  const steps = 30
  const interval = duration / steps
  const increment = target / steps
  let current = 0
  let step = 0

  const timer = setInterval(() => {
    step++
    current = Math.min(Math.round(increment * step), target)
    animatedValue.value = current
    if (step >= steps) {
      clearInterval(timer)
      animatedValue.value = target
    }
  }, interval)
}

function handleClick(): void {
  if (props.to) {
    router.push(props.to)
  }
}

watch(
  () => props.value,
  (val) => {
    animateTo(val)
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
.kpi-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--el-bg-color);
  border-radius: 8px;
  transition:
    box-shadow 0.3s,
    transform 0.2s;

  &--clickable {
    cursor: pointer;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }
  }

  .kpi-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 56px;
    height: 56px;
    border-radius: 12px;
    color: #fff;
    flex-shrink: 0;
  }

  .kpi-info {
    min-width: 0;

    .kpi-value {
      font-size: 28px;
      font-weight: 700;
      line-height: 1.2;
      color: var(--el-text-color-primary);
    }

    .kpi-label {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin-top: 4px;
    }

    .kpi-trend {
      margin-top: 6px;
      font-size: 13px;

      .trend-up {
        color: #67c23a;
      }

      .trend-down {
        color: #f56c6c;
      }

      .trend-compare {
        color: var(--el-text-color-placeholder);
        margin-left: 4px;
      }
    }
  }

  &--blue .kpi-icon {
    background: linear-gradient(135deg, #409eff, #66b1ff);
  }

  &--green .kpi-icon {
    background: linear-gradient(135deg, #67c23a, #85ce61);
  }

  &--orange .kpi-icon {
    background: linear-gradient(135deg, #e6a23c, #ebb563);
  }

  &--purple .kpi-icon {
    background: linear-gradient(135deg, #a855f7, #c084fc);
  }
}
</style>
