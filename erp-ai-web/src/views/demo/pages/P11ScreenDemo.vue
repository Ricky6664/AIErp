<template>
  <PageP11Screen view-id="demo-p11" page-type="P11" :config="pageConfig" :permissions="[]">
    <template #main-content>
      <div class="screen-container">
        <!-- 顶部标题栏 -->
        <div class="screen-header">
          <h1 class="screen-title">ERP运营数据大屏</h1>
          <span class="screen-time">{{ currentTime }}</span>
          <el-button text class="screen-fullscreen-btn" @click="toggleFullscreen">
            <el-icon :size="20"><FullScreen /></el-icon>
          </el-button>
        </div>

        <!-- KPI 指标行 -->
        <div class="screen-kpi-row">
          <div v-for="kpi in kpis" :key="kpi.label" class="screen-kpi-card">
            <div class="screen-kpi-label">{{ kpi.label }}</div>
            <div class="screen-kpi-value" :style="{ color: kpi.color }">{{ kpi.value }}</div>
            <div v-if="kpi.trend" class="screen-kpi-trend">
              <span :class="kpi.trend > 0 ? 'trend-up' : 'trend-down'">
                {{ kpi.trend > 0 ? '↑' : '↓' }} {{ Math.abs(kpi.trend) }}%
              </span>
            </div>
          </div>
        </div>

        <!-- 图表网格 -->
        <div class="screen-chart-grid">
          <div class="screen-chart-card chart-wide">
            <div class="screen-chart-header">销售趋势（近12月）</div>
            <div class="chart-placeholder">
              <el-icon :size="56"><DataAnalysis /></el-icon>
              <span>折线图 — ECharts</span>
            </div>
          </div>
          <div class="screen-chart-card">
            <div class="screen-chart-header">订单来源分布</div>
            <div class="chart-placeholder">
              <el-icon :size="48"><PieChart /></el-icon>
              <span>饼图 — ECharts</span>
            </div>
          </div>
          <div class="screen-chart-card">
            <div class="screen-chart-header">部门业绩排名</div>
            <div class="chart-placeholder">
              <el-icon :size="48"><DataAnalysis /></el-icon>
              <span>柱状图 — ECharts</span>
            </div>
          </div>
          <div class="screen-chart-card chart-tall">
            <div class="screen-chart-header">实时订单滚动</div>
            <div class="scroll-list">
              <div v-for="i in 8" :key="i" class="scroll-item">
                <span class="scroll-no">SO-2026-{{ String(i).padStart(4, '0') }}</span>
                <span class="scroll-name">客户订单 #{{ i }}</span>
                <span class="scroll-amount">¥{{ (Math.random() * 100000).toFixed(0) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </PageP11Screen>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { FullScreen, DataAnalysis, PieChart } from '@element-plus/icons-vue'
import PageP11Screen from '@/components/page-base/PageP11Screen.vue'

const currentTime = ref('')
let timer: ReturnType<typeof setInterval>

const pageConfig = {
  title: '运营数据大屏（P11）',
  darkTheme: true,
  showFullscreenBtn: true,
  showKpiArea: true,
  refreshInterval: 30
}

const kpis = [
  { label: '今日订单', value: '1,286', color: '#409EFF', trend: 12.5 },
  { label: '今日销售额', value: '¥856万', color: '#67C23A', trend: -3.2 },
  { label: '在线用户', value: '342', color: '#E6A23C', trend: 8.7 },
  { label: '库存预警', value: '23', color: '#F56C6C', trend: -15.0 },
  { label: '生产完成率', value: '94.2%', color: '#409EFF', trend: 2.1 },
  { label: '准时交付率', value: '97.8%', color: '#67C23A', trend: 0.5 }
]

function toggleFullscreen(): void {
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    document.documentElement.requestFullscreen()
  }
}

function updateTime(): void {
  currentTime.value = new Date().toLocaleString('zh-CN')
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onBeforeUnmount(() => {
  clearInterval(timer)
})
</script>

<style scoped lang="scss">
.screen-container {
  min-height: 100%;
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.screen-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  position: relative;
  .screen-title {
    font-size: 28px;
    font-weight: 700;
    color: #fff;
    margin: 0;
    letter-spacing: 4px;
  }
  .screen-time {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.7);
  }
  .screen-fullscreen-btn {
    position: absolute;
    right: 0;
    color: rgba(255, 255, 255, 0.7);
  }
}
.screen-kpi-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}
.screen-kpi-card {
  background: rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.1);
  .screen-kpi-label {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 8px;
  }
  .screen-kpi-value {
    font-size: 28px;
    font-weight: 700;
  }
  .screen-kpi-trend {
    margin-top: 4px;
    font-size: 12px;
  }
  .trend-up {
    color: #f56c6c;
  }
  .trend-down {
    color: #67c23a;
  }
}
.screen-chart-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  min-height: 0;
}
.screen-chart-card {
  background: rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
  &.chart-wide {
    grid-column: span 2;
  }
  &.chart-tall {
    grid-row: span 1;
  }
  .screen-chart-header {
    font-size: 14px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.85);
    margin-bottom: 12px;
  }
}
.chart-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.3);
  font-size: 14px;
  min-height: 180px;
}
.scroll-list {
  overflow: hidden;
}
.scroll-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}
</style>
