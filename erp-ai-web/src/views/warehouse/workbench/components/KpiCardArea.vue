<template>
  <div class="kpi-card-area">
    <el-row :gutter="16">
      <el-col v-for="card in kpiCards" :key="card.key" :xs="24" :sm="12" :lg="6">
        <KpiCard
          :icon="card.icon"
          :label="card.label"
          :value="safeValue(card.value)"
          :color="card.color"
          :to="card.to"
          :trend="card.trend"
          :compare-label="card.compareLabel"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, inject, onMounted, onUnmounted, type Ref } from 'vue'
import { Box, CircleCheck, Grid } from '@element-plus/icons-vue'
import KpiCard from '@/components/KpiCard/index.vue'
import { getWarehouseWorkbenchKpiApi } from '@/api/modules/warehouse-workbench'
import type { WarehouseWorkbenchKpiVO, TimeRange } from '@/api/modules/warehouse-workbench'
import { WORKBENCH_CONTEXT_KEY, type WorkbenchContext } from '../types'

const loading = ref(false)
const kpiData = ref<WarehouseWorkbenchKpiVO>({
  warehouseTotal: 0,
  warehouseEnabled: 0,
  locationTotal: 0,
  locationEnabled: 0
})

interface KpiCardConfig {
  key: string
  icon: any
  label: string
  value: number
  color: 'blue' | 'green' | 'orange' | 'purple'
  to?: string
  trend?: number
  compareLabel?: string
}

const kpiCards = computed<KpiCardConfig[]>(() => [
  {
    key: 'warehouseTotal',
    icon: Box,
    label: '仓库总数',
    value: kpiData.value.warehouseTotal,
    color: 'blue',
    to: '/warehouse/warehouse',
    trend: kpiData.value.warehouseTotalTrend
  },
  {
    key: 'warehouseEnabled',
    icon: CircleCheck,
    label: '启用仓库',
    value: kpiData.value.warehouseEnabled,
    color: 'green',
    to: '/warehouse/warehouse',
    trend: kpiData.value.warehouseEnabledTrend
  },
  {
    key: 'locationTotal',
    icon: Grid,
    label: '库位总数',
    value: kpiData.value.locationTotal,
    color: 'purple',
    to: '/warehouse/location',
    trend: kpiData.value.locationTotalTrend
  },
  {
    key: 'locationEnabled',
    icon: CircleCheck,
    label: '启用库位',
    value: kpiData.value.locationEnabled,
    color: 'orange',
    to: '/warehouse/location',
    trend: kpiData.value.locationEnabledTrend
  }
])

function safeValue(val: number | null | undefined): number {
  if (val === null || val === undefined || isNaN(val)) return 0
  return val
}

async function fetchKpiData(): Promise<void> {
  loading.value = true
  try {
    const res = await getWarehouseWorkbenchKpiApi()
    if (res) {
      kpiData.value = res
    }
  } catch {
    // keep defaults on error
  } finally {
    loading.value = false
  }
}

// Inject workbench context for time range reactivity
const context = inject<WorkbenchContext | null>(WORKBENCH_CONTEXT_KEY, null)

let unwatchTimeRange: (() => void) | null = null

onMounted(() => {
  fetchKpiData()

  // Watch time range changes from parent workbench
  if (context?.timeRange) {
    unwatchTimeRange = watch(
      () => (context.timeRange as Ref<TimeRange>).value,
      () => {
        fetchKpiData()
      }
    )
  }
})

onUnmounted(() => {
  if (unwatchTimeRange) {
    unwatchTimeRange()
    unwatchTimeRange = null
  }
})
</script>

<style scoped lang="scss">
.kpi-card-area {
  .el-row {
    margin-bottom: 16px;
  }
}
</style>
