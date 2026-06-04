<template>
  <div class="auth-config-workbench-page">
    <div class="page-header">
      <h2>权限配置工作台</h2>
      <p class="page-desc">认证配置概览，包括认证方式、密码策略统计、登录方式分布及每日登录趋势</p>
      <el-button :icon="RefreshRight" :loading="loading" @click="handleRefresh">刷新数据</el-button>
    </div>

    <el-row v-loading="loading && !workbenchData" :gutter="16" class="kpi-row">
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="Lock"
          label="认证方式总数"
          :value="workbenchData?.totalAuthMethods ?? 0"
          color="blue"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="CircleCheck"
          label="已启用认证方式"
          :value="workbenchData?.enabledAuthMethods ?? 0"
          color="green"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="Key"
          label="密码策略总数"
          :value="workbenchData?.totalPasswordPolicies ?? 0"
          color="orange"
        />
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <KpiCard
          :icon="Monitor"
          label="在线设备数"
          :value="workbenchData?.onlineDeviceCount ?? 0"
          color="purple"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="14">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">每日登录统计（近7天）</span>
          </template>
          <div ref="dailyLoginContainer" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">登录方式分布</span>
          </template>
          <div ref="loginDistContainer" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="detail-row">
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value success">{{ workbenchData?.todayLoginSuccessCount ?? 0 }}</div>
            <div class="detail-label">今日登录成功</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value danger">{{ workbenchData?.todayLoginFailCount ?? 0 }}</div>
            <div class="detail-label">今日登录失败</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value">{{ workbenchData?.enabledPasswordPolicies ?? 0 }}</div>
            <div class="detail-label">已启用密码策略</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card shadow="never">
          <div class="detail-stat">
            <div class="detail-value">{{ workbenchData?.ssoConfigCount ?? 0 }}</div>
            <div class="detail-label">SSO配置数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Lock, CircleCheck, Key, Monitor, RefreshRight } from '@element-plus/icons-vue'
import KpiCard from '@/components/KpiCard/index.vue'
import { useAuthConfigWorkbench } from '@/composables/useAuthConfigWorkbench'

const {
  loading,
  workbenchData,
  fetchData,
  initLoginDistChart,
  updateLoginDistChart,
  initDailyLoginChart,
  updateDailyLoginChart,
  resizeCharts,
  disposeCharts
} = useAuthConfigWorkbench()

const loginDistContainer = ref<HTMLElement | null>(null)
const dailyLoginContainer = ref<HTMLElement | null>(null)

watch(workbenchData, (data) => {
  if (!data) return
  if (loginDistContainer.value) {
    updateLoginDistChart(data.loginMethodDistribution)
  }
  if (dailyLoginContainer.value) {
    updateDailyLoginChart(data.dailyLoginStats)
  }
})

async function handleRefresh(): Promise<void> {
  await fetchData()
  ElMessage.success('数据已刷新')
}

function handleResize(): void {
  resizeCharts()
}

onMounted(async () => {
  await fetchData()
  if (loginDistContainer.value) {
    initLoginDistChart(loginDistContainer.value)
  }
  if (dailyLoginContainer.value) {
    initDailyLoginChart(dailyLoginContainer.value)
  }
  if (workbenchData.value) {
    if (loginDistContainer.value) {
      updateLoginDistChart(workbenchData.value.loginMethodDistribution)
    }
    if (dailyLoginContainer.value) {
      updateDailyLoginChart(workbenchData.value.dailyLoginStats)
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
.auth-config-workbench-page {
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
  }

  .kpi-row {
    margin-bottom: 16px;
  }

  .chart-row {
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

  .detail-row {
    margin-bottom: 16px;

    .detail-stat {
      text-align: center;
      padding: 8px 0;

      .detail-value {
        font-size: 24px;
        font-weight: 700;
        color: var(--el-text-color-primary);

        &.success {
          color: #67c23a;
        }

        &.danger {
          color: #f56c6c;
        }
      }

      .detail-label {
        font-size: 13px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }
    }
  }
}
</style>
