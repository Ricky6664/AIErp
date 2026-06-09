<template>
  <div class="warning-dashboard">
    <el-row :gutter="20" class="kpi-row">
      <el-col v-for="card in kpiCards" :key="card.module" :span="6">
        <el-card
          shadow="hover"
          :class="['kpi-card', { active: activeModule === card.module }]"
          @click="onCardClick(card.module)"
        >
          <div class="kpi-icon" :style="{ backgroundColor: card.color }">
            <el-icon :size="32"><component :is="card.icon" /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-value">{{ card.count }}</div>
            <div class="kpi-label">{{ card.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>近30天预警趋势</template>
          <div ref="trendChartRef" style="height: 350px" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>预警分布</template>
          <div ref="pieChartRef" style="height: 350px" />
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        {{ activeModule ? moduleLabelMap[activeModule] + ' - ' : '' }}预警明细
      </template>
      <el-table :data="warningList" border>
        <el-table-column prop="warningType" label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ warningTypeLabel[row.warningType] || row.warningType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="预警标题" min-width="200" />
        <el-table-column prop="createTime" label="触发时间" width="170" />
        <el-table-column prop="isHandled" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isHandled ? 'success' : 'danger'">
              {{ row.isHandled ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="!row.isHandled" link @click="onHandleWarning(row.id)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getWarningDashboard, handleWarning as handleWarningApi } from '@/api/msg/warning'
import type { WarningListItemVO } from '@/types/msg'

const trendChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let refreshTimer: ReturnType<typeof setInterval> | null = null
const activeModule = ref('')
const warningList = ref<WarningListItemVO[]>([])

const kpiCards = reactive([
  { module: 'sale', label: '销售预警', count: 0, color: '#E6A23C', icon: 'Sell' },
  { module: 'purchase', label: '采购预警', count: 0, color: '#409EFF', icon: 'ShoppingCart' },
  { module: 'inventory', label: '库存预警', count: 0, color: '#F56C6C', icon: 'Box' },
  { module: 'finance', label: '财务预警', count: 0, color: '#67C23A', icon: 'Money' }
])

const moduleLabelMap: Record<string, string> = {
  sale: '销售',
  purchase: '采购',
  inventory: '库存',
  finance: '财务'
}
const warningTypeLabel: Record<string, string> = {
  inventory: '库存',
  receivable: '应收',
  payable: '应付',
  delivery: '交期'
}

const loadDashboard = async () => {
  const res = await getWarningDashboard(activeModule.value || undefined)
  const countMap: Record<string, number> = {}
  res.moduleCounts.forEach((item) => {
    countMap[item.module] = item.cnt
  })
  kpiCards.forEach((card) => {
    card.count = countMap[card.module] || 0
  })
  warningList.value = res.warningList

  const dates = res.trendData.map((item) => item.date)
  const counts = res.trendData.map((item) => item.cnt)
  renderTrendChart({ dates, counts })
  renderPieChart(res.distributionData)
}

const renderTrendChart = (data: { dates: string[]; counts: number[] }) => {
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value!)
  }
  trendChart.setOption({
    xAxis: { type: 'category', data: data.dates },
    yAxis: { type: 'value' },
    series: [
      {
        data: data.counts,
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 }
      }
    ],
    tooltip: { trigger: 'axis' }
  })
}

const renderPieChart = (data: { name: string; value: number }[]) => {
  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value!)
  }
  pieChart.setOption({
    series: [{ type: 'pie', radius: ['40%', '70%'], data }],
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 }
  })
}

const onCardClick = (module: string) => {
  activeModule.value = activeModule.value === module ? '' : module
  loadDashboard()
}

const onHandleWarning = async (id: number) => {
  await handleWarningApi(id)
  const item = warningList.value.find((w) => w.id === id)
  if (item) {
    item.isHandled = true
  }
}

const onResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  await nextTick()
  loadDashboard()
  refreshTimer = setInterval(loadDashboard, 60000)
  window.addEventListener('resize', onResize)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  trendChart?.dispose()
  pieChart?.dispose()
  window.removeEventListener('resize', onResize)
})
</script>

<style scoped lang="scss">
.warning-dashboard {
  padding: 16px;
  height: 100%;
}

.kpi-row {
  margin-bottom: 20px;
}

.kpi-card {
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.3s;

  &.active {
    border-color: var(--el-color-primary);
  }

  :deep(.el-card__body) {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
  }
}

.kpi-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.kpi-info {
  flex: 1;
  min-width: 0;
}

.kpi-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  line-height: 1.2;
}

.kpi-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
</style>
