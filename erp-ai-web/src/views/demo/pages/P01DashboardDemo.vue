<template>
  <PageP01Dashboard view-id="demo-p01" page-type="P01" :config="pageConfig" :permissions="[]" />
</template>

<script setup lang="ts">
import { computed, markRaw } from 'vue'
import {
  User,
  Money,
  Box,
  Tickets,
  Message,
  OfficeBuilding,
  TrendCharts
} from '@element-plus/icons-vue'
import PageP01Dashboard from '@/components/page-base/PageP01Dashboard.vue'
import type { DashboardPageConfig } from '@/types/page-base.d.ts'

const pageConfig = computed<DashboardPageConfig>(() => ({
  welcomeText: '欢迎回来',
  userName: '管理员',
  kpiCards: [
    {
      id: 'orders',
      label: '今日订单',
      value: 128,
      icon: markRaw(Tickets),
      color: 'blue',
      trend: 12.5,
      compareLabel: '较昨日',
      to: '/demo/pages/p04'
    },
    {
      id: 'sales',
      label: '本月销售额',
      value: 856200,
      icon: markRaw(Money),
      color: 'green',
      trend: -3.2,
      compareLabel: '较上月',
      to: '/demo/pages/p10'
    },
    {
      id: 'inventory',
      label: '库存预警',
      value: 23,
      icon: markRaw(Box),
      color: 'orange',
      to: '/demo/pages/p09'
    },
    {
      id: 'employees',
      label: '在职员工',
      value: 342,
      icon: markRaw(User),
      color: 'purple',
      trend: 5.1,
      compareLabel: '较上季度'
    }
  ],
  quickEntries: [
    { id: 'qe-1', label: '销售管理', icon: markRaw(TrendCharts), to: '/demo/pages/p02' },
    { id: 'qe-2', label: '采购管理', icon: markRaw(Box), to: '/demo/pages/p04' },
    { id: 'qe-3', label: '库存管理', icon: markRaw(Box), to: '/demo/pages/p09' },
    { id: 'qe-4', label: '财务管理', icon: markRaw(Money), to: '/demo/pages/p10' },
    { id: 'qe-5', label: '人力资源', icon: markRaw(User), to: '/demo/pages/p03' },
    { id: 'qe-6', label: '审批流程', icon: markRaw(Tickets), to: '/demo/pages/p08' },
    { id: 'qe-7', label: '消息中心', icon: markRaw(Message), to: '/demo/pages/p14' },
    { id: 'qe-8', label: '组织架构', icon: markRaw(OfficeBuilding), to: '/demo/pages/p05' }
  ],
  charts: [
    { id: 'chart-1', title: '销售趋势', type: 'line', span: { col: 1, row: 1 } },
    { id: 'chart-2', title: '订单分布', type: 'pie', span: { col: 1, row: 1 } },
    { id: 'chart-3', title: '部门业绩', type: 'bar', span: { col: 2, row: 1 } }
  ],
  todoItems: [
    { id: 'todo-1', title: '待审批单据', type: 'approval', count: 8, to: '/demo/pages/p08' },
    { id: 'todo-2', title: '库存预警', type: 'alert', count: 5, to: '/demo/pages/p09' },
    { id: 'todo-3', title: '未读消息', type: 'message', count: 12, to: '/demo/pages/p14' }
  ],
  recentVisits: [
    { id: 'rv-1', label: '用户管理', to: '/system/user', visitedAt: '10分钟前' },
    { id: 'rv-2', label: '会计科目', to: '/finance/account', visitedAt: '30分钟前' },
    { id: 'rv-3', label: '仓库管理', to: '/warehouse/workbench', visitedAt: '1小时前' },
    { id: 'rv-4', label: '员工中心', to: '/hrm/employeecenter', visitedAt: '2小时前' }
  ]
}))
</script>
