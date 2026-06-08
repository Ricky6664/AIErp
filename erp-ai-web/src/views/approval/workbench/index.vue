<template>
  <div class="approval-workbench-page">
    <div class="page-header">
      <h2>审批工作台</h2>
      <p class="page-desc">审批流程概览与快速操作入口</p>
      <div class="header-actions">
        <el-button-group>
          <el-button :icon="RefreshRight" :loading="loading" @click="loadData">刷新数据</el-button>
        </el-button-group>
        <el-button type="primary" :icon="Plus" @click="handleNewApproval">发起审批</el-button>
      </div>
    </div>

    <div v-loading="loading" class="workbench-content">
      <div v-if="error" class="area-error">
        <el-result icon="error" sub-title="数据加载失败">
          <template #extra>
            <el-button type="primary" size="small" @click="loadData">重试</el-button>
          </template>
        </el-result>
      </div>

      <template v-else>
        <!-- KPI 卡片区 -->
        <section class="workbench-section">
          <div class="section-header">
            <h3>全局审批概览</h3>
          </div>
          <el-row :gutter="16" class="kpi-row">
            <el-col :xs="12" :sm="8" :md="6">
              <KpiCard title="总实例数" :value="stats.totalInstances ?? 0" :loading="loading" />
            </el-col>
            <el-col :xs="12" :sm="8" :md="6">
              <KpiCard title="待审批" :value="stats.pendingCount ?? 0" :loading="loading" />
            </el-col>
            <el-col :xs="12" :sm="8" :md="6">
              <KpiCard title="已通过" :value="stats.approvedCount ?? 0" :loading="loading" />
            </el-col>
            <el-col :xs="12" :sm="8" :md="6">
              <KpiCard title="已驳回" :value="stats.rejectedCount ?? 0" :loading="loading" />
            </el-col>
          </el-row>
        </section>

        <!-- 个人统计 KPI -->
        <section class="workbench-section">
          <div class="section-header">
            <h3>个人审批统计</h3>
          </div>
          <el-row :gutter="16" class="kpi-row">
            <el-col :xs="12" :sm="8">
              <KpiCard title="我的待审批" :value="stats.myPendingCount ?? 0" :loading="loading" />
            </el-col>
            <el-col :xs="12" :sm="8">
              <KpiCard title="我已审核" :value="stats.myReviewedCount ?? 0" :loading="loading" />
            </el-col>
            <el-col :xs="12" :sm="8">
              <KpiCard title="我的申请" :value="stats.mySubmittedCount ?? 0" :loading="loading" />
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
              <el-card
                shadow="hover"
                class="quick-card"
                @click="navigateTo('/approval/my?tab=pending')"
              >
                <div class="quick-card__body">
                  <div class="quick-card__icon pending">
                    <el-icon size="28"><Clock /></el-icon>
                  </div>
                  <div class="quick-card__text">
                    <div class="quick-card__title">待审批</div>
                    <div class="quick-card__desc">处理待审批事项</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6">
              <el-card
                shadow="hover"
                class="quick-card"
                @click="navigateTo('/approval/my?tab=submitted')"
              >
                <div class="quick-card__body">
                  <div class="quick-card__icon submitted">
                    <el-icon size="28"><DocumentAdd /></el-icon>
                  </div>
                  <div class="quick-card__text">
                    <div class="quick-card__title">我的申请</div>
                    <div class="quick-card__desc">查看我提交的申请</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6">
              <el-card shadow="hover" class="quick-card" @click="navigateTo('/approval/instance')">
                <div class="quick-card__body">
                  <div class="quick-card__icon instance">
                    <el-icon size="28"><List /></el-icon>
                  </div>
                  <div class="quick-card__text">
                    <div class="quick-card__title">审批实例</div>
                    <div class="quick-card__desc">查看所有审批实例</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6">
              <el-card
                shadow="hover"
                class="quick-card"
                @click="navigateTo('/approval/definition')"
              >
                <div class="quick-card__body">
                  <div class="quick-card__icon definition">
                    <el-icon size="28"><Setting /></el-icon>
                  </div>
                  <div class="quick-card__text">
                    <div class="quick-card__title">审批定义</div>
                    <div class="quick-card__desc">配置审批流程</div>
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
                :option="statusPieOption"
                :height="340"
                :loading="loading"
                empty-text="暂无审批数据"
              />
            </el-col>
            <el-col :xs="24" :md="12">
              <ChartPanel
                :option="defBarOption"
                :height="340"
                :loading="loading"
                empty-text="暂无审批定义统计"
              />
            </el-col>
          </el-row>
        </section>

        <!-- 最近待审批列表 -->
        <section class="workbench-section">
          <div class="section-header">
            <h3>最近待审批</h3>
            <el-button
              text
              type="primary"
              :icon="ArrowRight"
              @click="navigateTo('/approval/my?tab=pending')"
            >
              查看全部
            </el-button>
          </div>
          <el-card shadow="never">
            <el-table
              v-loading="loading"
              :data="recentPendingList"
              border
              stripe
              style="width: 100%"
              empty-text="暂无待审批事项"
            >
              <el-table-column prop="instanceId" label="实例ID" width="80" />
              <el-table-column
                prop="definitionName"
                label="审批定义"
                min-width="140"
                show-overflow-tooltip
              />
              <el-table-column prop="businessType" label="业务类型" width="120" />
              <el-table-column prop="applicantName" label="申请人" width="100" />
              <el-table-column
                prop="currentNodeName"
                label="当前节点"
                width="120"
                show-overflow-tooltip
              />
              <el-table-column label="状态" width="100" align="center">
                <template #default>
                  <el-tag type="warning" size="small">待审批</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="提交时间" width="180" />
            </el-table>
          </el-card>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  RefreshRight,
  Plus,
  Clock,
  DocumentAdd,
  List,
  Setting,
  ArrowRight
} from '@element-plus/icons-vue'
import type { EChartsOption } from 'echarts'
import KpiCard from './components/KpiCard.vue'
import ChartPanel from './components/ChartPanel.vue'
import { getStatistics, getMyApprovalPage } from '@/api/modules/approval'
import type { ApprovalStatisticsVO, MyApprovalVO } from '@/api/types/approval'

const router = useRouter()

const loading = ref(false)
const error = ref(false)
const stats = reactive<Partial<ApprovalStatisticsVO>>({})
const recentPendingList = ref<MyApprovalVO[]>([])

const statusLabelMap: Record<string, string> = {
  PENDING: '待审批',
  APPROVED: '已通过',
  REJECTED: '已驳回',
  WITHDRAWN: '已撤回'
}

const statusPieOption = computed<EChartsOption>(() => {
  const dist = stats.statusDistribution || {}
  const entries = Object.entries(dist)
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
          entries.length > 0
            ? entries.map(([key, value]) => ({ name: statusLabelMap[key] || key, value }))
            : [{ name: '暂无数据', value: 0 }]
      }
    ]
  }
})

const defBarOption = computed<EChartsOption>(() => {
  const counts = stats.definitionCounts || {}
  const entries = Object.entries(counts).sort((a, b) => b[1] - a[1])
  return {
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
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#409EFF' },
              { offset: 1, color: '#79bbff' }
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
    const [statisticsRes, pendingRes] = await Promise.all([
      getStatistics(),
      getMyApprovalPage({ tab: 'pending', pageNum: 1, pageSize: 5 })
    ])
    Object.assign(stats, statisticsRes)
    recentPendingList.value = pendingRes.records || []
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

function handleNewApproval(): void {
  router.push('/approval/definition')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.approval-workbench-page {
  padding: 20px;

  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-desc {
      flex: 1;
      margin: 0;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }

    .header-actions {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
    }
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

      &.pending {
        background: linear-gradient(135deg, #e6a23c, #f3d19e);
      }

      &.submitted {
        background: linear-gradient(135deg, #409eff, #79bbff);
      }

      &.instance {
        background: linear-gradient(135deg, #67c23a, #b3e19d);
      }

      &.definition {
        background: linear-gradient(135deg, #909399, #b4b6bb);
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
}
</style>
