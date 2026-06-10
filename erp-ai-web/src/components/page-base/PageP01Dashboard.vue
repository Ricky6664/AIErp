<template>
  <div class="page-p01-dashboard">
    <!-- 顶部欢迎栏 -->
    <div class="welcome-bar">
      <div class="welcome-greeting">
        <h2 class="welcome-text">{{ config.welcomeText || '欢迎回来' }}</h2>
        <span v-if="config.userName" class="welcome-user">{{ config.userName }}</span>
        <span class="welcome-date">{{ currentDate }}</span>
      </div>
      <div class="welcome-search">
        <el-input v-model="searchKeyword" placeholder="快捷搜索" clearable class="search-input" />
      </div>
    </div>

    <!-- KPI 指标卡片区 -->
    <div v-if="config.kpiCards && config.kpiCards.length > 0" class="kpi-section">
      <div class="kpi-grid">
        <KpiCard
          v-for="card in config.kpiCards"
          :key="card.id"
          :icon="card.icon"
          :label="card.label"
          :value="card.value"
          :color="card.color"
          :to="card.to"
          :trend="card.trend"
          :compare-label="card.compareLabel"
          @click="handleKpiClick(card)"
        />
      </div>
    </div>

    <!-- 快捷入口区 -->
    <div v-if="config.quickEntries && config.quickEntries.length > 0" class="quick-entries-section">
      <h3 class="section-title">快捷入口</h3>
      <div class="quick-entries-grid">
        <div
          v-for="entry in config.quickEntries"
          :key="entry.id"
          class="quick-entry-item"
          @click="handleNavigate(entry.to)"
        >
          <el-icon :size="24">
            <component :is="entry.icon" />
          </el-icon>
          <span class="entry-label">{{ entry.label }}</span>
        </div>
      </div>
    </div>

    <!-- 图表看板区 -->
    <div v-if="config.charts && config.charts.length > 0" class="charts-section">
      <h3 class="section-title">图表看板</h3>
      <div class="charts-grid">
        <div
          v-for="chart in config.charts"
          :key="chart.id"
          class="chart-card"
          :style="chartSpanStyle(chart)"
        >
          <div class="chart-header">
            <span class="chart-title">{{ chart.title }}</span>
          </div>
          <div class="chart-body">
            <slot :name="`chart-${chart.id}`" :chart="chart">
              <div class="chart-placeholder">
                <el-icon :size="48"><DataAnalysis /></el-icon>
                <span>图表组件待配置</span>
              </div>
            </slot>
          </div>
        </div>
      </div>
    </div>

    <!-- 下方双栏：待办/消息区 + 最近访问区 -->
    <div class="bottom-section">
      <div v-if="config.todoItems && config.todoItems.length > 0" class="todo-section">
        <h3 class="section-title">待办与消息</h3>
        <div class="todo-list">
          <div
            v-for="item in config.todoItems"
            :key="item.id"
            class="todo-item"
            @click="item.to && handleNavigate(item.to)"
          >
            <el-badge
              :value="item.count"
              :type="todoBadgeType(item.type)"
              :hidden="item.count === 0"
            >
              <span class="todo-title">{{ item.title }}</span>
            </el-badge>
          </div>
        </div>
      </div>

      <div v-if="config.recentVisits && config.recentVisits.length > 0" class="recent-section">
        <h3 class="section-title">最近访问</h3>
        <div class="recent-list">
          <div
            v-for="visit in config.recentVisits"
            :key="visit.id"
            class="recent-item"
            @click="handleNavigate(visit.to)"
          >
            <el-icon :size="16"><Clock /></el-icon>
            <span class="recent-label">{{ visit.label }}</span>
            <span v-if="visit.visitedAt" class="recent-time">{{ visit.visitedAt }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 额外区域插槽 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { DataAnalysis, Clock } from '@element-plus/icons-vue'
import KpiCard from '@/components/KpiCard/index.vue'
import type {
  PageBaseProps,
  PageBaseEmits,
  DashboardPageConfig,
  KpiCardConfig,
  ChartConfig
} from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<DashboardPageConfig>(() => {
  return (props.config || {}) as DashboardPageConfig
})

const searchKeyword = ref('')

const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

function chartSpanStyle(chart: ChartConfig): Record<string, string> {
  if (chart.span) {
    return {
      gridColumn: `span ${chart.span.col}`,
      gridRow: `span ${chart.span.row}`
    }
  }
  return {}
}

function todoBadgeType(type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  switch (type) {
    case 'approval':
      return 'warning'
    case 'alert':
      return 'danger'
    case 'message':
      return 'primary'
    default:
      return 'info'
  }
}

function handleKpiClick(card: KpiCardConfig): void {
  if (card.to) {
    emit('navigate', { to: card.to })
  }
}

function handleNavigate(to: string): void {
  emit('navigate', { to })
}

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p01-dashboard {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0 0 12px 0;
  }
}

// 顶部欢迎栏
.welcome-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: var(--el-bg-color);
  border-radius: 12px;

  .welcome-greeting {
    display: flex;
    align-items: baseline;
    gap: 12px;

    .welcome-text {
      font-size: 22px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin: 0;
    }

    .welcome-user {
      font-size: 22px;
      color: var(--el-color-primary);
      font-weight: 600;
    }

    .welcome-date {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .welcome-search {
    .search-input {
      width: 280px;
    }
  }
}

// KPI 卡片区
.kpi-section {
  .kpi-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 16px;
  }
}

// 快捷入口区
.quick-entries-section {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;

  .quick-entries-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 12px;

    .quick-entry-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      padding: 12px 8px;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: var(--el-fill-color-light);
      }

      .entry-label {
        font-size: 13px;
        color: var(--el-text-color-regular);
        text-align: center;
      }
    }
  }
}

// 图表看板区
.charts-section {
  .charts-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .chart-card {
      background: var(--el-bg-color);
      border-radius: 12px;
      padding: 16px 20px;

      .chart-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 12px;

        .chart-title {
          font-size: 15px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }
      }

      .chart-body {
        min-height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;

        .chart-placeholder {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 8px;
          color: var(--el-text-color-placeholder);
          font-size: 14px;
        }
      }
    }
  }
}

// 下方双栏
.bottom-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;

  .todo-section,
  .recent-section {
    background: var(--el-bg-color);
    border-radius: 12px;
    padding: 16px 20px;
  }

  .todo-list {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .todo-item {
      padding: 10px 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: var(--el-fill-color-light);
      }

      .todo-title {
        font-size: 14px;
        color: var(--el-text-color-regular);
      }
    }
  }

  .recent-list {
    display: flex;
    flex-direction: column;
    gap: 6px;

    .recent-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: var(--el-fill-color-light);
      }

      .recent-label {
        font-size: 14px;
        color: var(--el-text-color-regular);
        flex: 1;
      }

      .recent-time {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
      }
    }
  }
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
