<template>
  <PageP02Workbench view-id="demo-p02" page-type="P02" :config="pageConfig" :permissions="[]">
    <template #header-extra>
      <el-button type="primary" size="small">新建报价单</el-button>
      <el-button type="success" size="small">新建销售订单</el-button>
    </template>

    <template #main-content>
      <!-- 模块KPI卡片 -->
      <div class="demo-kpi-grid">
        <KpiCard icon="Tickets" label="今日订单" :value="45" color="blue" />
        <KpiCard icon="Money" label="今日销售额" :value="328500" color="green" />
        <KpiCard icon="Box" label="待发货" :value="12" color="orange" />
        <KpiCard icon="Warning" label="逾期应收" :value="3" color="red" />
      </div>

      <!-- 快捷操作 + 图表区 -->
      <div class="demo-mid-grid">
        <div class="demo-card">
          <h3 class="demo-card-title">快捷操作</h3>
          <div class="demo-quick-actions">
            <el-button v-for="a in quickActions" :key="a.key" :type="a.type" :icon="a.icon">
              {{ a.label }}
            </el-button>
          </div>
        </div>
        <div class="demo-card">
          <h3 class="demo-card-title">本月销售趋势</h3>
          <div class="chart-placeholder">
            <el-icon :size="48"><DataAnalysis /></el-icon>
            <span>图表组件（ECharts）</span>
          </div>
        </div>
      </div>

      <!-- 待处理列表 -->
      <div class="demo-card">
        <h3 class="demo-card-title">待处理业务</h3>
        <el-table :data="todoData" stripe size="small">
          <el-table-column prop="type" label="类型" width="120" />
          <el-table-column prop="no" label="单据编号" width="200" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="row.statusType" size="small">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="date" label="日期" width="140" />
        </el-table>
      </div>
    </template>
  </PageP02Workbench>
</template>

<script setup lang="ts">
import { markRaw } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import PageP02Workbench from '@/components/page-base/PageP02Workbench.vue'
import KpiCard from '@/components/KpiCard/index.vue'

const quickActions = [
  { key: 'quote', label: '新建报价单', type: 'primary', icon: markRaw(DataAnalysis) },
  { key: 'order', label: '新建销售订单', type: 'success', icon: markRaw(DataAnalysis) },
  { key: 'ship', label: '新建发货通知', type: 'warning', icon: markRaw(DataAnalysis) },
  { key: 'export', label: '导出报表', type: 'info', icon: markRaw(DataAnalysis) }
]

const todoData = [
  {
    type: '销售订单',
    no: 'SO-2026-0001',
    title: 'XX公司设备采购订单',
    status: '待审批',
    statusType: 'warning',
    date: '2026-06-10'
  },
  {
    type: '发货通知',
    no: 'DN-2026-0003',
    title: '紧急发货 — YY集团',
    status: '待处理',
    statusType: 'danger',
    date: '2026-06-09'
  },
  {
    type: '出库单',
    no: 'OUT-2026-0012',
    title: 'ZZ科技标准件出库',
    status: '待审核',
    statusType: 'info',
    date: '2026-06-08'
  },
  {
    type: '报价单',
    no: 'QT-2026-0008',
    title: 'AA公司项目报价',
    status: '草稿',
    statusType: '',
    date: '2026-06-07'
  },
  {
    type: '销售订单',
    no: 'SO-2026-0015',
    title: 'BB制造采购订单',
    status: '已通过',
    statusType: 'success',
    date: '2026-06-06'
  }
]
</script>

<style scoped lang="scss">
.demo-kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.demo-mid-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 16px;
  margin-top: 16px;
}
.demo-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
.demo-card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: var(--el-text-color-primary);
}
.demo-quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 180px;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
}
</style>
