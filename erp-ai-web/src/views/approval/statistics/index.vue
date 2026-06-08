<template>
  <div class="approval-statistics-page">
    <!-- 全局统计概览 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__body">
            <div class="stat-card__icon total">
              <el-icon size="32"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-card__info">
              <div class="stat-card__value">{{ data.totalInstances ?? 0 }}</div>
              <div class="stat-card__label">总实例数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__body">
            <div class="stat-card__icon pending">
              <el-icon size="32"><Clock /></el-icon>
            </div>
            <div class="stat-card__info">
              <div class="stat-card__value">{{ data.pendingCount ?? 0 }}</div>
              <div class="stat-card__label">待审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__body">
            <div class="stat-card__icon approved">
              <el-icon size="32"><Select /></el-icon>
            </div>
            <div class="stat-card__info">
              <div class="stat-card__value">{{ data.approvedCount ?? 0 }}</div>
              <div class="stat-card__label">已通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__body">
            <div class="stat-card__icon rejected">
              <el-icon size="32"><CloseBold /></el-icon>
            </div>
            <div class="stat-card__info">
              <div class="stat-card__value">{{ data.withdrawnCount ?? 0 }}</div>
              <div class="stat-card__label">已撤回</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 个人统计 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__body">
            <div class="stat-card__icon my-pending">
              <el-icon size="28"><Bell /></el-icon>
            </div>
            <div class="stat-card__info">
              <div class="stat-card__value">{{ data.myPendingCount ?? 0 }}</div>
              <div class="stat-card__label">我的待审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__body">
            <div class="stat-card__icon my-reviewed">
              <el-icon size="28"><Checked /></el-icon>
            </div>
            <div class="stat-card__info">
              <div class="stat-card__value">{{ data.myReviewedCount ?? 0 }}</div>
              <div class="stat-card__label">我已审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__body">
            <div class="stat-card__icon my-submitted">
              <el-icon size="28"><DocumentAdd /></el-icon>
            </div>
            <div class="stat-card__info">
              <div class="stat-card__value">{{ data.mySubmittedCount ?? 0 }}</div>
              <div class="stat-card__label">我的申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="charts-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="chart-card__title">审批状态分布</span>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="chart-card__title">审批定义维度统计</span>
          </template>
          <div ref="defChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import {
  DataAnalysis,
  Clock,
  Select,
  CloseBold,
  Bell,
  Checked,
  DocumentAdd
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStatistics } from '@/api/modules/approval'
import type { ApprovalStatisticsVO } from '@/api/types/approval'

const loading = ref(false)
const data = ref<Partial<ApprovalStatisticsVO>>({})

const statusChartRef = ref<HTMLDivElement | null>(null)
const defChartRef = ref<HTMLDivElement | null>(null)
let statusChart: echarts.ECharts | null = null
let defChart: echarts.ECharts | null = null

function initStatusChart(distribution: Record<string, number>) {
  if (!statusChartRef.value) return
  if (!statusChart) {
    statusChart = echarts.init(statusChartRef.value)
  }

  const entries = Object.entries(distribution)
  const statusLabelMap: Record<string, string> = {
    PENDING: '待审批',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    WITHDRAWN: '已撤回'
  }

  statusChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data: entries.map(([key, value]) => ({
          name: statusLabelMap[key] || key,
          value
        }))
      }
    ]
  })
}

function initDefChart(counts: Record<string, number>) {
  if (!defChartRef.value) return
  if (!defChart) {
    defChart = echarts.init(defChartRef.value)
  }

  const entries = Object.entries(counts).sort((a, b) => b[1] - a[1])

  defChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: entries.map(([key]) => key),
      axisLabel: { rotate: 30, fontSize: 11 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        type: 'bar',
        data: entries.map(([, value]) => value),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#79bbff' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  })
}

function handleResize() {
  statusChart?.resize()
  defChart?.resize()
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getStatistics()
    data.value = res
    await nextTick()
    if (res.statusDistribution) {
      initStatusChart(res.statusDistribution)
    }
    if (res.definitionCounts) {
      initDefChart(res.definitionCounts)
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
  defChart?.dispose()
})
</script>

<style scoped>
.approval-statistics-page {
  padding: 16px;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  margin-bottom: 0;
}

.stat-card :deep(.el-card__body) {
  padding: 16px;
}

.stat-card__body {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-card__icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-card__icon.total {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.stat-card__icon.pending {
  background: linear-gradient(135deg, #e6a23c, #f3d19e);
}

.stat-card__icon.approved {
  background: linear-gradient(135deg, #67c23a, #b3e19d);
}

.stat-card__icon.rejected {
  background: linear-gradient(135deg, #909399, #b4b6bb);
}

.stat-card__icon.my-pending {
  background: linear-gradient(135deg, #f56c6c, #fab6b6);
}

.stat-card__icon.my-reviewed {
  background: linear-gradient(135deg, #67c23a, #95d475);
}

.stat-card__icon.my-submitted {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
  color: #303133;
}

.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.chart-card {
  height: 100%;
}

.chart-card__title {
  font-size: 15px;
  font-weight: 600;
}

.chart-container {
  width: 100%;
  height: 360px;
}
</style>
