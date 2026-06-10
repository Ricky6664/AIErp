<template>
  <PageP02Workbench view-id="user-workbench" page-type="P02" :config="pageConfig" :permissions="[]">
    <template #header-extra>
      <p class="page-desc">用户管理概览，包括KPI统计、活跃度趋势、角色分布及最近登录日志</p>
      <el-button :icon="RefreshRight" :loading="loading" @click="handleRefresh">刷新数据</el-button>
    </template>

    <template #main-content>
      <el-row v-loading="loading && !workbenchData" :gutter="16" class="kpi-row">
        <el-col :xs="12" :sm="12" :md="6">
          <div class="kpi-card kpi-card--blue">
            <div class="kpi-icon">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="kpi-info">
              <div class="kpi-value">{{ animatedUserTotal }}</div>
              <div class="kpi-label">用户总数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="kpi-card kpi-card--green">
            <div class="kpi-icon">
              <el-icon :size="32"><Avatar /></el-icon>
            </div>
            <div class="kpi-info">
              <div class="kpi-value">{{ animatedRoleTotal }}</div>
              <div class="kpi-label">角色总数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="kpi-card kpi-card--orange">
            <div class="kpi-icon">
              <el-icon :size="32"><Connection /></el-icon>
            </div>
            <div class="kpi-info">
              <div class="kpi-value">{{ animatedOnlineCount }}</div>
              <div class="kpi-label">在线用户数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <div class="kpi-card kpi-card--purple">
            <div class="kpi-icon">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="kpi-info">
              <div class="kpi-value">{{ animatedTodayLogin }}</div>
              <div class="kpi-label">今日登录次数</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :md="14">
          <el-card shadow="never">
            <template #header>
              <span class="card-title">用户活跃度趋势（近7天）</span>
            </template>
            <div ref="trendContainer" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="10">
          <el-card shadow="never">
            <template #header>
              <span class="card-title">角色分布</span>
            </template>
            <div ref="pieContainer" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="action-row">
        <el-col :span="24">
          <el-card shadow="never">
            <template #header>
              <span class="card-title">快捷操作</span>
            </template>
            <el-space wrap>
              <el-button type="primary" :icon="Plus" @click="handleQuickAction('user-add')"
                >新增用户</el-button
              >
              <el-button type="success" :icon="Avatar" @click="handleQuickAction('role-manage')"
                >角色管理</el-button
              >
              <el-button type="warning" :icon="Lock" @click="handleQuickAction('permission-config')"
                >权限配置</el-button
              >
            </el-space>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="table-row">
        <el-col :span="24">
          <el-card shadow="never">
            <template #header>
              <span class="card-title">最近登录日志</span>
            </template>
            <el-table
              :data="recentLogins"
              border
              stripe
              empty-text="暂无登录记录"
              style="width: 100%"
            >
              <el-table-column prop="username" label="用户名" width="140" />
              <el-table-column prop="loginTime" label="登录时间" min-width="180" />
              <el-table-column prop="ip" label="IP地址" width="160" />
              <el-table-column prop="status" label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small">
                    {{ row.status === 'success' ? '成功' : '失败' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </PageP02Workbench>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  Avatar,
  Connection,
  TrendCharts,
  RefreshRight,
  Plus,
  Lock
} from '@element-plus/icons-vue'
import PageP02Workbench from '@/components/page-base/PageP02Workbench.vue'
import type { WorkbenchPageConfig } from '@/types/page-base.d.ts'
import { useWorkbench } from '@/composables/useWorkbench'
import type { RecentLoginItem } from '@/api/types/workbench'

const pageConfig: WorkbenchPageConfig = {
  title: '用户管理工作台',
  showStatCards: false
}

const router = useRouter()

const {
  loading,
  workbenchData,
  fetchData,
  initTrendChart,
  updateTrendChart,
  initPieChart,
  updatePieChart,
  getStatusType,
  resizeCharts,
  disposeCharts
} = useWorkbench()

const trendContainer = ref<HTMLElement | null>(null)
const pieContainer = ref<HTMLElement | null>(null)

const animDuration = 1000
const animSteps = 30
const animInterval = animDuration / animSteps

const animatedUserTotal = ref(0)
const animatedRoleTotal = ref(0)
const animatedOnlineCount = ref(0)
const animatedTodayLogin = ref(0)

const recentLogins = ref<RecentLoginItem[]>([])

function animateValue(
  target: number,
  refKey: 'animatedUserTotal' | 'animatedRoleTotal' | 'animatedOnlineCount' | 'animatedTodayLogin'
): void {
  const start = 0
  const increment = target / animSteps
  let current = 0
  let step = 0

  const timer = setInterval(() => {
    step++
    current = Math.min(Math.round(start + increment * step), target)
    if (refKey === 'animatedUserTotal') animatedUserTotal.value = current
    else if (refKey === 'animatedRoleTotal') animatedRoleTotal.value = current
    else if (refKey === 'animatedOnlineCount') animatedOnlineCount.value = current
    else animatedTodayLogin.value = current

    if (step >= animSteps) {
      clearInterval(timer)
      if (refKey === 'animatedUserTotal') animatedUserTotal.value = target
      else if (refKey === 'animatedRoleTotal') animatedRoleTotal.value = target
      else if (refKey === 'animatedOnlineCount') animatedOnlineCount.value = target
      else animatedTodayLogin.value = target
    }
  }, animInterval)
}

function startAnimations(): void {
  if (!workbenchData.value) return
  animateValue(workbenchData.value.userTotal, 'animatedUserTotal')
  animateValue(workbenchData.value.roleTotal, 'animatedRoleTotal')
  animateValue(workbenchData.value.onlineCount, 'animatedOnlineCount')
  animateValue(workbenchData.value.todayLoginCount, 'animatedTodayLogin')
}

watch(workbenchData, (data) => {
  if (!data) return
  recentLogins.value = data.recentLogins
  startAnimations()
  if (trendContainer.value) {
    updateTrendChart(data.loginTrend)
  }
  if (pieContainer.value) {
    updatePieChart(data.roleDistribution)
  }
})

async function handleRefresh(): Promise<void> {
  await fetchData()
  ElMessage.success('数据已刷新')
}

function handleQuickAction(target: string): void {
  switch (target) {
    case 'user-add':
      router.push('/user/list')
      break
    case 'role-manage':
      router.push('/role/list')
      break
    case 'permission-config':
      router.push('/permission/config')
      break
  }
}

function handleResize(): void {
  resizeCharts()
}

onMounted(async () => {
  await fetchData()
  if (trendContainer.value) {
    initTrendChart(trendContainer.value)
  }
  if (pieContainer.value) {
    initPieChart(pieContainer.value)
  }
  if (workbenchData.value) {
    if (trendContainer.value) {
      updateTrendChart(workbenchData.value.loginTrend)
    }
    if (pieContainer.value) {
      updatePieChart(workbenchData.value.roleDistribution)
    }
  }
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})
</script>

<style scoped lang="scss">
.page-desc {
  margin: 0;
  flex: 1;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.kpi-row {
  margin-bottom: 16px;
}

.kpi-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--el-bg-color);
  border-radius: 8px;
  margin-bottom: 16px;

  .kpi-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 56px;
    height: 56px;
    border-radius: 12px;
    color: #fff;
  }

  .kpi-info {
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

.chart-row,
.action-row,
.table-row {
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.chart-container {
  width: 100%;
  height: 320px;
}
</style>
