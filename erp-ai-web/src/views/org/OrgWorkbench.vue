<template>
  <PageP02Workbench view-id="org-workbench" page-type="P02" :config="pageConfig" :permissions="[]">
    <template #header-extra>
      <el-button :icon="RefreshRight" :loading="loading" @click="loadData">
        {{ $t('refresh') }}
      </el-button>
    </template>

    <template #main-content>
      <div class="org-workbench-page">
        <div v-loading="loading" class="workbench-content">
          <div v-if="error" class="area-error">
            <el-result icon="error" :sub-title="$t('org.workbench.loadFailed')">
              <template #extra>
                <el-button type="primary" size="small" @click="loadData">{{
                  $t('org.workbench.retry')
                }}</el-button>
              </template>
            </el-result>
          </div>

          <template v-else-if="data">
            <!-- KPI 统计卡片区 -->
            <section class="workbench-section">
              <div class="section-header">
                <h3>{{ $t('org.workbench.kpiTitle') }}</h3>
              </div>
              <el-row :gutter="16" class="kpi-row">
                <el-col :xs="12" :sm="12" :md="6">
                  <el-card shadow="never" class="kpi-card">
                    <div class="kpi-value">{{ animatedCompanyCount }}</div>
                    <div class="kpi-label">{{ $t('org.workbench.companyCount') }}</div>
                  </el-card>
                </el-col>
                <el-col :xs="12" :sm="12" :md="6">
                  <el-card shadow="never" class="kpi-card">
                    <div class="kpi-value">{{ animatedDeptCount }}</div>
                    <div class="kpi-label">{{ $t('org.workbench.departmentCount') }}</div>
                  </el-card>
                </el-col>
                <el-col :xs="12" :sm="12" :md="6">
                  <el-card shadow="never" class="kpi-card">
                    <div class="kpi-value">{{ animatedPositionCount }}</div>
                    <div class="kpi-label">{{ $t('org.workbench.positionCount') }}</div>
                  </el-card>
                </el-col>
                <el-col :xs="12" :sm="12" :md="6">
                  <el-card shadow="never" class="kpi-card">
                    <div class="kpi-value">{{ animatedEmployeeCount }}</div>
                    <div class="kpi-label">{{ $t('org.workbench.employeeCount') }}</div>
                  </el-card>
                </el-col>
              </el-row>
            </section>

            <!-- 图表区 -->
            <section class="workbench-section">
              <div class="section-header">
                <h3>{{ $t('org.workbench.chartTitle') }}</h3>
              </div>
              <el-row :gutter="16" class="chart-row">
                <el-col :xs="24" :md="12">
                  <el-card shadow="never">
                    <template #header>
                      <span class="card-title">{{ $t('org.workbench.deptTypeDist') }}</span>
                    </template>
                    <div ref="pieChartRef" class="chart-container"></div>
                  </el-card>
                </el-col>
                <el-col :xs="24" :md="12">
                  <el-card shadow="never">
                    <template #header>
                      <span class="card-title">{{ $t('org.workbench.companyDeptCompare') }}</span>
                    </template>
                    <div ref="barChartRef" class="chart-container"></div>
                  </el-card>
                </el-col>
              </el-row>
            </section>

            <!-- 快捷操作区 -->
            <section class="workbench-section">
              <div class="section-header">
                <h3>{{ $t('org.workbench.quickActions') }}</h3>
              </div>
              <el-row :gutter="16" class="action-row">
                <el-col :xs="12" :sm="6">
                  <el-button
                    v-permission="'org:company:add'"
                    type="primary"
                    :icon="Plus"
                    class="action-btn"
                    @click="handleNavigate('/org/company')"
                  >
                    {{ $t('org.workbench.addCompany') }}
                  </el-button>
                </el-col>
                <el-col :xs="12" :sm="6">
                  <el-button
                    v-permission="'org:department:add'"
                    type="success"
                    :icon="Plus"
                    class="action-btn"
                    @click="handleNavigate('/org/department')"
                  >
                    {{ $t('org.workbench.addDepartment') }}
                  </el-button>
                </el-col>
                <el-col :xs="12" :sm="6">
                  <el-button
                    v-permission="'org:position:add'"
                    type="warning"
                    :icon="Plus"
                    class="action-btn"
                    @click="handleNavigate('/org/position')"
                  >
                    {{ $t('org.workbench.addPosition') }}
                  </el-button>
                </el-col>
                <el-col :xs="12" :sm="6">
                  <el-button
                    v-permission="'org:structure:view'"
                    type="info"
                    :icon="Share"
                    class="action-btn"
                    @click="handleNavigate('/org/structure')"
                  >
                    {{ $t('org.workbench.orgChart') }}
                  </el-button>
                </el-col>
              </el-row>
            </section>

            <!-- 最近新增记录 -->
            <section class="workbench-section">
              <div class="section-header">
                <h3>{{ $t('org.workbench.recentRecords') }}</h3>
              </div>
              <el-row :gutter="16">
                <el-col :xs="24" :md="8">
                  <el-card shadow="never" class="recent-card">
                    <template #header>
                      <span class="card-title">{{ $t('org.workbench.recentCompany') }}</span>
                    </template>
                    <div v-if="recentCompanies.length === 0" class="empty-tip">
                      {{ $t('noData') }}
                    </div>
                    <div v-for="item in recentCompanies" :key="item.id" class="recent-item">
                      <span class="recent-name">{{ item.name }}</span>
                      <span class="recent-time">{{ item.createTime }}</span>
                    </div>
                  </el-card>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-card shadow="never" class="recent-card">
                    <template #header>
                      <span class="card-title">{{ $t('org.workbench.recentDept') }}</span>
                    </template>
                    <div v-if="recentDepartments.length === 0" class="empty-tip">
                      {{ $t('noData') }}
                    </div>
                    <div v-for="item in recentDepartments" :key="item.id" class="recent-item">
                      <span class="recent-name">{{ item.name }}</span>
                      <span class="recent-time">{{ item.createTime }}</span>
                    </div>
                  </el-card>
                </el-col>
                <el-col :xs="24" :md="8">
                  <el-card shadow="never" class="recent-card">
                    <template #header>
                      <span class="card-title">{{ $t('org.workbench.recentPosition') }}</span>
                    </template>
                    <div v-if="recentPositions.length === 0" class="empty-tip">
                      {{ $t('noData') }}
                    </div>
                    <div v-for="item in recentPositions" :key="item.id" class="recent-item">
                      <span class="recent-name">{{ item.name }}</span>
                      <span class="recent-time">{{ item.createTime }}</span>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </section>
          </template>
        </div>
      </div>
    </template>
  </PageP02Workbench>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { RefreshRight, Plus, Share } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { PieChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getOrgWorkbenchApi, type OrgWorkbenchVO } from '@/api/org/workbench'
import { getCompanyPage } from '@/api/modules/org'
import request from '@/utils/request'
import PageP02Workbench from '@/components/page-base/PageP02Workbench.vue'
import type { WorkbenchPageConfig } from '@/types/page-base.d.ts'

echarts.use([PieChart, BarChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const router = useRouter()

const pageConfig: WorkbenchPageConfig = {
  title: '组织架构工作台',
  showStatCards: false,
  showQueryPanel: false,
  showActionBar: false
}

const loading = ref(false)
const error = ref(false)
const data = ref<OrgWorkbenchVO | null>(null)

const pieChartRef = ref<HTMLElement | null>(null)
const barChartRef = ref<HTMLElement | null>(null)
let pieChartInstance: echarts.ECharts | null = null
let barChartInstance: echarts.ECharts | null = null

// CountUp animation state
const animatedCompanyCount = ref(0)
const animatedDeptCount = ref(0)
const animatedPositionCount = ref(0)
const animatedEmployeeCount = ref(0)

interface RecentItem {
  id: number
  name: string
  createTime: string
}

const recentCompanies = ref<RecentItem[]>([])
const recentDepartments = ref<RecentItem[]>([])
const recentPositions = ref<RecentItem[]>([])

function animateValue(target: number, refObj: { value: number }, duration = 1000): void {
  const start = refObj.value
  const diff = target - start
  const startTime = performance.now()

  function step(currentTime: number): void {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    refObj.value = Math.round(start + diff * eased)
    if (progress < 1) {
      requestAnimationFrame(step)
    }
  }

  requestAnimationFrame(step)
}

function startAnimations(): void {
  if (!data.value) return
  animateValue(data.value.companyCount ?? 0, animatedCompanyCount as unknown as { value: number })
  animateValue(data.value.departmentCount ?? 0, animatedDeptCount as unknown as { value: number })
  animateValue(data.value.positionCount ?? 0, animatedPositionCount as unknown as { value: number })
  animateValue(data.value.employeeCount ?? 0, animatedEmployeeCount as unknown as { value: number })
}

async function loadData(): Promise<void> {
  loading.value = true
  error.value = false
  try {
    data.value = await getOrgWorkbenchApi()
    startAnimations()
    await nextTick()
    renderCharts()
    fetchRecentRecords()
  } catch {
    error.value = true
    ElMessage.error('Failed to load workbench data')
  } finally {
    loading.value = false
  }
}

async function fetchRecentRecords(): Promise<void> {
  try {
    const [companyRes] = await Promise.allSettled([getCompanyPage({ page: 1, pageSize: 5 } as any)])
    if (companyRes.status === 'fulfilled' && companyRes.value?.records) {
      recentCompanies.value = companyRes.value.records.slice(0, 5).map((r: any) => ({
        id: r.id,
        name: r.companyName ?? r.name,
        createTime: r.createTime ?? ''
      }))
    }
  } catch {
    // silent fail for non-critical recent records
  }

  try {
    const [deptRes, posRes] = await Promise.allSettled([
      request.get('/api/org/department/page', { params: { page: 1, pageSize: 5 } }),
      request.get('/api/org/position/page', { params: { page: 1, pageSize: 5 } })
    ])
    if (deptRes.status === 'fulfilled') {
      const deptData = deptRes.value as any
      recentDepartments.value = (deptData?.records ?? []).slice(0, 5).map((r: any) => ({
        id: r.id,
        name: r.deptName ?? r.name,
        createTime: r.createTime ?? ''
      }))
    }
    if (posRes.status === 'fulfilled') {
      const posData = posRes.value as any
      recentPositions.value = (posData?.records ?? []).slice(0, 5).map((r: any) => ({
        id: r.id,
        name: r.positionName ?? r.name,
        createTime: r.createTime ?? ''
      }))
    }
  } catch {
    // silent fail
  }
}

function renderCharts(): void {
  if (!data.value) return
  renderPieChart()
  renderBarChart()
}

function getDeptTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    business: '业务部门',
    functional: '职能部门',
    project: '项目部门'
  }
  return labels[type] ?? type
}

function renderPieChart(): void {
  if (!pieChartRef.value || !data.value) return

  if (!pieChartInstance) {
    pieChartInstance = echarts.init(pieChartRef.value)
  }

  const distribution = data.value.deptTypeDistribution || []
  const pieData = distribution.map((item) => ({
    name: getDeptTypeLabel(item.deptType),
    value: item.count
  }))

  pieChartInstance.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: 0,
      type: 'scroll'
    },
    series: [
      {
        name: '部门类型',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '45%'],
        data: pieData.length > 0 ? pieData : [{ name: '暂无数据', value: 0 }],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: { show: false },
        labelLine: { show: false }
      }
    ]
  })
}

function renderBarChart(): void {
  if (!barChartRef.value || !data.value) return

  if (!barChartInstance) {
    barChartInstance = echarts.init(barChartRef.value)
  }

  const companyData = data.value.companyDeptCount || []
  const names = companyData.map((item) => item.companyName)
  const counts = companyData.map((item) => item.deptCount)

  barChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: names.length > 0 ? names : ['暂无数据'],
      axisLabel: { rotate: 15 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '部门数量',
        type: 'bar',
        data: counts.length > 0 ? counts : [0],
        barWidth: '50%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#73C0DE' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: { color: '#337ECC' }
        }
      }
    ]
  })
}

function handleNavigate(path: string): void {
  router.push(path)
}

function handleResize(): void {
  pieChartInstance?.resize()
  barChartInstance?.resize()
}

onMounted(async () => {
  await loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChartInstance?.dispose()
  barChartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.org-workbench-page {
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
      text-align: center;
      cursor: default;

      .kpi-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--el-color-primary);
        line-height: 1.2;
      }

      .kpi-label {
        margin-top: 8px;
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .chart-row {
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .chart-container {
      width: 100%;
      height: 320px;
    }
  }

  .action-row {
    .action-btn {
      width: 100%;
      margin-bottom: 8px;
    }
  }

  .recent-card {
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .empty-tip {
      text-align: center;
      padding: 16px;
      color: var(--el-text-color-secondary);
      font-size: 14px;
    }

    .recent-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid var(--el-border-color-lighter);

      &:last-child {
        border-bottom: none;
      }

      .recent-name {
        font-size: 14px;
        color: var(--el-text-color-primary);
      }

      .recent-time {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .area-error {
    padding: 20px;
    background: var(--el-bg-color);
    border-radius: 8px;
  }
}
</style>
