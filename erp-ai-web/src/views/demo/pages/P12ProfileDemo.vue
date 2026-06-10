<template>
  <PageP12Profile view-id="demo-p12" page-type="P12" :config="pageConfig" :permissions="[]">
    <template #main-content>
      <div class="profile-container">
        <!-- 实体概览 -->
        <div class="profile-overview">
          <div class="profile-avatar-area">
            <el-avatar :size="80" style="font-size: 32px; background: var(--el-color-primary)"
              >XX</el-avatar
            >
            <div class="profile-name-area">
              <h2 class="profile-name">XX科技有限公司</h2>
              <div class="profile-tags">
                <el-tag size="small" type="success">VIP客户</el-tag>
                <el-tag size="small">制造业</el-tag>
                <el-tag size="small">A级信用</el-tag>
              </div>
            </div>
          </div>
          <div class="profile-summary-cards">
            <div class="summary-card">
              <span class="summary-label">累计交易额</span>
              <span class="summary-value">¥1,285万</span>
            </div>
            <div class="summary-card">
              <span class="summary-label">合作年限</span>
              <span class="summary-value">5年</span>
            </div>
            <div class="summary-card">
              <span class="summary-label">信用评分</span>
              <span class="summary-value" style="color: var(--el-color-success)">92分</span>
            </div>
            <div class="summary-card">
              <span class="summary-label">逾期次数</span>
              <span class="summary-value" style="color: var(--el-color-success)">0次</span>
            </div>
          </div>
        </div>

        <!-- 多维度分析 -->
        <div class="profile-analysis-grid">
          <div class="analysis-card">
            <h4 class="analysis-title">交易趋势</h4>
            <div class="chart-placeholder">
              <el-icon :size="40"><DataAnalysis /></el-icon><span>折线图</span>
            </div>
          </div>
          <div class="analysis-card">
            <h4 class="analysis-title">TOP 商品</h4>
            <div class="top-list">
              <div v-for="(item, idx) in topProducts" :key="item.name" class="top-item">
                <span class="top-rank">{{ idx + 1 }}</span>
                <span class="top-name">{{ item.name }}</span>
                <span class="top-value">{{ item.value }}</span>
              </div>
            </div>
          </div>
          <div class="analysis-card">
            <h4 class="analysis-title">应收账款</h4>
            <div class="chart-placeholder">
              <el-icon :size="40"><PieChart /></el-icon><span>饼图</span>
            </div>
          </div>
          <div class="analysis-card">
            <h4 class="analysis-title">活跃度趋势</h4>
            <div class="chart-placeholder">
              <el-icon :size="40"><TrendCharts /></el-icon><span>面积图</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </PageP12Profile>
</template>

<script setup lang="ts">
import { DataAnalysis, PieChart, TrendCharts } from '@element-plus/icons-vue'
import PageP12Profile from '@/components/page-base/PageP12Profile.vue'

const pageConfig = {
  title: '客户画像（P12）',
  userName: 'XX科技有限公司',
  userRole: '企业客户 · 制造业',
  description: '成立于2015年，主营工业自动化设备制造，是公司核心战略合作伙伴。',
  tabs: [
    { id: 'overview', label: '全景画像' },
    { id: 'transaction', label: '交易分析' },
    { id: 'finance', label: '财务分析' },
    { id: 'risk', label: '风险评估' }
  ],
  activeTab: 'overview'
}

const topProducts = [
  { name: '传感器A型', value: '¥320万' },
  { name: '控制器B型', value: '¥280万' },
  { name: '线缆C型', value: '¥195万' },
  { name: '外壳D型', value: '¥150万' },
  { name: 'PCB-E型', value: '¥120万' }
]
</script>

<style scoped lang="scss">
.profile-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.profile-overview {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 24px;
}
.profile-avatar-area {
  display: flex;
  align-items: center;
  gap: 16px;
}
.profile-name-area {
  .profile-name {
    margin: 0 0 8px;
    font-size: 22px;
  }
  .profile-tags {
    display: flex;
    gap: 6px;
  }
}
.profile-summary-cards {
  display: flex;
  gap: 24px;
}
.summary-card {
  text-align: center;
  .summary-label {
    display: block;
    font-size: 13px;
    color: var(--el-text-color-secondary);
    margin-bottom: 4px;
  }
  .summary-value {
    font-size: 20px;
    font-weight: 700;
  }
}
.profile-analysis-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.analysis-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px;
  .analysis-title {
    font-size: 15px;
    font-weight: 600;
    margin: 0 0 12px;
  }
}
.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 160px;
  gap: 8px;
  color: var(--el-text-color-placeholder);
}
.top-list {
  .top-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    gap: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    .top-rank {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 600;
    }
    .top-name {
      flex: 1;
      font-size: 14px;
    }
    .top-value {
      font-weight: 600;
      font-size: 14px;
    }
  }
}
</style>
