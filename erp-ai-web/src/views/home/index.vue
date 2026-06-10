<template>
  <PageP01Dashboard
    view-id="home"
    page-type="P01"
    :config="pageConfig"
    :permissions="permissions"
    @navigate="handleNavigate"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import {
  User,
  OfficeBuilding,
  Money,
  DataAnalysis,
  Box,
  Message,
  Bell,
  Tickets
} from '@element-plus/icons-vue'
import PageP01Dashboard from '@/components/page-base/PageP01Dashboard.vue'
import { useUserStore } from '@/stores/modules/user'
import { getWorkbenchDataApi } from '@/api/modules/workbench'
import type {
  DashboardPageConfig,
  KpiCardConfig,
  QuickEntryConfig,
  TodoItemConfig,
  RecentVisitConfig
} from '@/types/page-base.d.ts'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const apiStats = ref<{
  userTotal: number
  roleTotal: number
  onlineCount: number
  todayLoginCount: number
} | null>(null)

const permissions = computed(() => userStore.permissions || [])

const kpiCards = computed<KpiCardConfig[]>(() => {
  const u = apiStats.value
  return [
    {
      id: 'users',
      label: '用户总数',
      value: u?.userTotal ?? 0,
      icon: markRaw(User),
      color: 'blue',
      to: '/system/user'
    },
    {
      id: 'roles',
      label: '角色总数',
      value: u?.roleTotal ?? 0,
      icon: markRaw(OfficeBuilding),
      color: 'green',
      to: '/system/role'
    },
    {
      id: 'online',
      label: '在线用户',
      value: u?.onlineCount ?? 0,
      icon: markRaw(DataAnalysis),
      color: 'orange'
    },
    {
      id: 'today-login',
      label: '今日登录',
      value: u?.todayLoginCount ?? 0,
      icon: markRaw(Bell),
      color: 'purple'
    }
  ]
})

const quickEntries = ref<QuickEntryConfig[]>([
  { id: 'warehouse', label: '仓库管理', icon: markRaw(Box), to: '/warehouse/workbench' },
  { id: 'org', label: '组织架构', icon: markRaw(OfficeBuilding), to: '/org/workbench' },
  { id: 'finance', label: '财务管理', icon: markRaw(Money), to: '/finance/workbench' },
  { id: 'hrm', label: '人力资源', icon: markRaw(User), to: '/hrm/workbench' },
  { id: 'approval', label: '审批工作台', icon: markRaw(Tickets), to: '/approval/workbench' },
  { id: 'msg', label: '消息工作台', icon: markRaw(Message), to: '/msg/workbench' }
])

const todoItems = ref<TodoItemConfig[]>([
  { id: 'my-approval', title: '我的审批', type: 'approval', count: 0, to: '/approval/my' },
  { id: 'warnings', title: '业务预警', type: 'alert', count: 0, to: '/msg/warning-dashboard' },
  { id: 'messages', title: '未读消息', type: 'message', count: 0, to: '/msg/message' }
])

const recentVisits = ref<RecentVisitConfig[]>([
  { id: 'rv-1', label: '用户管理', to: '/system/user', visitedAt: '最近访问' },
  { id: 'rv-2', label: '员工中心', to: '/hrm/employeecenter', visitedAt: '最近访问' },
  { id: 'rv-3', label: '财务工作台', to: '/finance/workbench', visitedAt: '最近访问' },
  { id: 'rv-4', label: '仓库管理', to: '/warehouse/workbench', visitedAt: '最近访问' }
])

const pageConfig = computed<DashboardPageConfig>(() => ({
  welcomeText: '欢迎回来',
  userName: userStore.nickname,
  kpiCards: kpiCards.value,
  quickEntries: quickEntries.value,
  todoItems: todoItems.value,
  recentVisits: recentVisits.value
}))

function handleNavigate(payload: { to: string; query?: Record<string, string> }) {
  router.push({ path: payload.to, query: payload.query })
}

onMounted(async () => {
  loading.value = true
  try {
    const data = await getWorkbenchDataApi()
    apiStats.value = {
      userTotal: data.userTotal,
      roleTotal: data.roleTotal,
      onlineCount: data.onlineCount,
      todayLoginCount: data.todayLoginCount
    }
  } catch {
    // Fall back to static KPI values (0 shown via defaults)
    console.warn('[HomePage] Failed to load workbench stats, using static defaults')
  } finally {
    loading.value = false
  }
})
</script>
