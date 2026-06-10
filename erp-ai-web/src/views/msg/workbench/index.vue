<template>
  <PageP02Workbench view-id="msg-workbench" page-type="P02" :config="pageConfig" :permissions="[]">
    <template #header-extra>
      <p class="page-desc">消息管理概览与快速操作入口</p>
      <div class="header-actions">
        <el-button-group>
          <el-button :icon="RefreshRight" :loading="loading" @click="loadData">刷新数据</el-button>
        </el-button-group>
      </div>
    </template>

    <template #main-content>
      <div v-loading="loading" class="workbench-content">
        <div v-if="error" class="area-error">
          <el-result icon="error" sub-title="数据加载失败">
            <template #extra>
              <el-button type="primary" size="small" @click="loadData">重试</el-button>
            </template>
          </el-result>
        </div>

        <template v-else>
          <!-- KPI 卡片区 - 消息统计 -->
          <section class="workbench-section">
            <div class="section-header">
              <h3>消息概览</h3>
            </div>
            <el-row :gutter="16" class="kpi-row">
              <el-col :xs="12" :sm="8" :md="6">
                <KpiCard title="全部未读" :value="unreadCount.all ?? 0" :loading="loading" />
              </el-col>
              <el-col :xs="12" :sm="8" :md="6">
                <KpiCard title="系统消息" :value="unreadCount.system ?? 0" :loading="loading" />
              </el-col>
              <el-col :xs="12" :sm="8" :md="6">
                <KpiCard title="业务消息" :value="unreadCount.business ?? 0" :loading="loading" />
              </el-col>
              <el-col :xs="12" :sm="8" :md="6">
                <KpiCard title="预警消息" :value="unreadCount.warning ?? 0" :loading="loading" />
              </el-col>
            </el-row>
          </section>

          <!-- KPI 卡片区 - 待办统计 -->
          <section class="workbench-section">
            <div class="section-header">
              <h3>待办统计</h3>
            </div>
            <el-row :gutter="16" class="kpi-row">
              <el-col :xs="12" :sm="8">
                <KpiCard
                  title="待审批"
                  :value="todoCount.pending_approval ?? 0"
                  :loading="loading"
                />
              </el-col>
              <el-col :xs="12" :sm="8">
                <KpiCard title="待处理" :value="todoCount.pending_handle ?? 0" :loading="loading" />
              </el-col>
              <el-col :xs="12" :sm="8">
                <KpiCard
                  title="待确认"
                  :value="todoCount.pending_confirm ?? 0"
                  :loading="loading"
                />
              </el-col>
            </el-row>
          </section>

          <!-- 快速入口 -->
          <section class="workbench-section">
            <div class="section-header">
              <h3>快速入口</h3>
            </div>
            <el-row :gutter="16" class="quick-row">
              <el-col :xs="12" :sm="8" :md="6">
                <el-card shadow="hover" class="quick-card" @click="navigateTo('/msg/message')">
                  <div class="quick-card__body">
                    <div class="quick-card__icon message">
                      <el-icon size="28"><Bell /></el-icon>
                    </div>
                    <div class="quick-card__text">
                      <div class="quick-card__title">消息中心</div>
                      <div class="quick-card__desc">查看与管理消息</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="12" :sm="8" :md="6">
                <el-card shadow="hover" class="quick-card" @click="navigateTo('/msg/template')">
                  <div class="quick-card__body">
                    <div class="quick-card__icon template">
                      <el-icon size="28"><Document /></el-icon>
                    </div>
                    <div class="quick-card__text">
                      <div class="quick-card__title">消息模板</div>
                      <div class="quick-card__desc">配置消息推送模板</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="12" :sm="8" :md="6">
                <el-card shadow="hover" class="quick-card" @click="navigateTo('/msg/type')">
                  <div class="quick-card__body">
                    <div class="quick-card__icon type">
                      <el-icon size="28"><Grid /></el-icon>
                    </div>
                    <div class="quick-card__text">
                      <div class="quick-card__title">消息类型</div>
                      <div class="quick-card__desc">管理消息分类体系</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="12" :sm="8" :md="6">
                <el-card shadow="hover" class="quick-card" @click="navigateTo('/msg/todo')">
                  <div class="quick-card__body">
                    <div class="quick-card__icon todo">
                      <el-icon size="28"><List /></el-icon>
                    </div>
                    <div class="quick-card__text">
                      <div class="quick-card__title">单据待办</div>
                      <div class="quick-card__desc">处理待办审批事项</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="12" :sm="8" :md="6">
                <el-card
                  shadow="hover"
                  class="quick-card"
                  @click="navigateTo('/msg/warning-dashboard')"
                >
                  <div class="quick-card__body">
                    <div class="quick-card__icon warning">
                      <el-icon size="28"><WarningFilled /></el-icon>
                    </div>
                    <div class="quick-card__text">
                      <div class="quick-card__title">业务预警</div>
                      <div class="quick-card__desc">查看业务预警看板</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </section>

          <!-- 图表区 -->
          <section class="workbench-section">
            <div class="section-header">
              <h3>数据可视化</h3>
            </div>
            <el-row :gutter="16" class="chart-row">
              <el-col :xs="24" :md="12">
                <ChartPanel
                  :option="distributionPieOption"
                  :height="340"
                  :loading="loading"
                  empty-text="暂无预警分布数据"
                />
              </el-col>
              <el-col :xs="24" :md="12">
                <ChartPanel
                  :option="trendBarOption"
                  :height="340"
                  :loading="loading"
                  empty-text="暂无预警趋势数据"
                />
              </el-col>
            </el-row>
          </section>

          <!-- 最近待办列表 -->
          <section class="workbench-section">
            <div class="section-header">
              <h3>最近待办</h3>
              <el-button text type="primary" :icon="ArrowRight" @click="navigateTo('/msg/todo')">
                查看全部
              </el-button>
            </div>
            <el-card shadow="never">
              <el-table
                v-loading="loading"
                :data="recentTodoList"
                border
                stripe
                style="width: 100%"
                empty-text="暂无待办事项"
              >
                <el-table-column prop="businessType" label="业务类型" width="120" />
                <el-table-column
                  prop="title"
                  label="待办标题"
                  min-width="180"
                  show-overflow-tooltip
                />
                <el-table-column prop="todoType" label="待办类型" width="120" />
                <el-table-column prop="originatorName" label="发起人" width="100" />
                <el-table-column prop="dueTime" label="截止时间" width="180" />
                <el-table-column prop="createTime" label="创建时间" width="180" />
              </el-table>
            </el-card>
          </section>
        </template>
      </div>
    </template>
  </PageP02Workbench>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  RefreshRight,
  Bell,
  Document,
  Grid,
  List,
  WarningFilled,
  ArrowRight
} from '@element-plus/icons-vue'
import type { EChartsOption } from 'echarts'
import PageP02Workbench from '@/components/page-base/PageP02Workbench.vue'
import type { WorkbenchPageConfig } from '@/types/page-base.d.ts'
import KpiCard from './components/KpiCard.vue'
import ChartPanel from './components/ChartPanel.vue'
import { getUnreadCount } from '@/api/msg/message'
import { getTodoCount, getTodoPage } from '@/api/msg/todo'
import { getWarningDashboard } from '@/api/msg/warning'
import type { UnreadCountVO, TodoCountVO, WarningDashboardVO, TodoListVO } from '@/types/msg'
import type { PageResult } from '@/types/api'

const pageConfig: WorkbenchPageConfig = {
  title: '消息工作台',
  showStatCards: false
}

const router = useRouter()

const loading = ref(false)
const error = ref(false)
const unreadCount = reactive<Partial<UnreadCountVO>>({})
const todoCount = reactive<Partial<TodoCountVO>>({})
const warningDashboard = reactive<Partial<WarningDashboardVO>>({
  moduleCounts: [],
  trendData: [],
  distributionData: [],
  warningList: []
})
const recentTodoList = ref<TodoListVO[]>([])

const distributionPieOption = computed<EChartsOption>(() => {
  const data = warningDashboard.distributionData || []
  return {
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
        data:
          data.length > 0
            ? data.map((item) => ({ name: item.name, value: item.value }))
            : [{ name: '暂无数据', value: 0 }]
      }
    ]
  }
})

const trendBarOption = computed<EChartsOption>(() => {
  const data = warningDashboard.trendData || []
  const sorted = [...data].sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: sorted.map((item) => item.date),
      axisLabel: { rotate: 30, fontSize: 11 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        type: 'bar',
        data: sorted.map((item) => item.cnt),
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#e6a23c' },
              { offset: 1, color: '#f3d19e' }
            ]
          },
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }
})

async function loadData(): Promise<void> {
  loading.value = true
  error.value = false
  try {
    const [unreadRes, todoCountRes, warningRes, todoPageRes] = await Promise.all([
      getUnreadCount(),
      getTodoCount(),
      getWarningDashboard(),
      getTodoPage({ pageNum: 1, pageSize: 5 })
    ])
    Object.assign(unreadCount, unreadRes)
    Object.assign(todoCount, todoCountRes)
    Object.assign(warningDashboard, warningRes)
    recentTodoList.value = (todoPageRes as PageResult<TodoListVO>).records || []
  } catch {
    error.value = true
    ElMessage.error('数据加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function navigateTo(path: string): void {
  router.push(path)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.page-desc {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.header-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.workbench-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.workbench-section {
  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}

.kpi-row {
  .kpi-card {
    margin-bottom: 0;
  }
}

.quick-row {
  .quick-card {
    cursor: pointer;
    transition:
      transform 0.2s,
      box-shadow 0.2s;

    &:hover {
      transform: translateY(-2px);
    }

    :deep(.el-card__body) {
      padding: 16px;
    }
  }

  .quick-card__body {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .quick-card__icon {
    width: 48px;
    height: 48px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    flex-shrink: 0;

    &.message {
      background: linear-gradient(135deg, #409eff, #79bbff);
    }

    &.template {
      background: linear-gradient(135deg, #67c23a, #b3e19d);
    }

    &.type {
      background: linear-gradient(135deg, #909399, #b4b6bb);
    }

    &.todo {
      background: linear-gradient(135deg, #e6a23c, #f3d19e);
    }

    &.warning {
      background: linear-gradient(135deg, #f56c6c, #fab6b6);
    }
  }

  .quick-card__text {
    .quick-card__title {
      font-size: 15px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .quick-card__desc {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-top: 2px;
    }
  }
}

.chart-row {
  .chart-container {
    width: 100%;
    height: 340px;
  }
}

.area-error {
  padding: 20px;
  background: var(--el-bg-color);
  border-radius: 8px;
}
</style>
