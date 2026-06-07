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
import { ref, onMounted, computed } from 'vue'
import { Box, CircleCheck, Grid } from '@element-plus/icons-vue'
import KpiCard from '@/components/KpiCard/index.vue'
import { getWarehouseWorkbenchKpiApi } from '@/api/modules/warehouse-workbench'
import type { WarehouseWorkbenchKpiVO } from '@/api/modules/warehouse-workbench'

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
    to: '/warehouse/warehouse'
  },
  {
    key: 'warehouseEnabled',
    icon: CircleCheck,
    label: '启用仓库',
    value: kpiData.value.warehouseEnabled,
    color: 'green',
    to: '/warehouse/warehouse'
  },
  {
    key: 'locationTotal',
    icon: Grid,
    label: '库位总数',
    value: kpiData.value.locationTotal,
    color: 'purple',
    to: '/warehouse/location'
  },
  {
    key: 'locationEnabled',
    icon: CircleCheck,
    label: '启用库位',
    value: kpiData.value.locationEnabled,
    color: 'orange',
    to: '/warehouse/location'
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

onMounted(() => {
  fetchKpiData()
})
</script>

<style scoped lang="scss">
.kpi-card-area {
  .el-row {
    margin-bottom: 16px;
  }
}
</style>
