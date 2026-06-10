<template>
  <PageP09Query view-id="demo-p09" page-type="P09" :config="pageConfig" :permissions="[]">
    <template #query-panel>
      <el-form :inline="true">
        <el-form-item label="仓库"
          ><el-select v-model="query.warehouse" placeholder="全部" clearable style="width: 160px">
            <el-option label="原料仓" value="w1" /><el-option
              label="成品仓"
              value="w2"
            /> </el-select
        ></el-form-item>
        <el-form-item label="物料编码"
          ><el-input v-model="query.productCode" placeholder="编码" clearable style="width: 160px"
        /></el-form-item>
        <el-form-item label="物料名称"
          ><el-input v-model="query.productName" placeholder="名称" clearable style="width: 160px"
        /></el-form-item>
        <el-form-item label="批次号"
          ><el-input v-model="query.batchNo" placeholder="批次" clearable style="width: 160px"
        /></el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
          <el-button text type="primary">更多条件</el-button>
        </el-form-item>
      </el-form>
    </template>

    <template #action-bar>
      <div class="action-left">
        <el-button>导出</el-button>
        <el-button>打印</el-button>
      </div>
      <div class="action-right">
        <el-button text>格式</el-button>
        <el-button text>刷新</el-button>
        <span class="record-count">共 328 条</span>
      </div>
    </template>

    <template #main-content>
      <el-table :data="tableData" stripe border style="width: 100%" max-height="100%">
        <el-table-column prop="warehouse" label="仓库" width="120" />
        <el-table-column prop="location" label="库位" width="120" />
        <el-table-column prop="productCode" label="物料编码" width="140" />
        <el-table-column prop="productName" label="物料名称" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="qty" label="库存数量" width="100" align="right" />
        <el-table-column prop="availableQty" label="可用数量" width="100" align="right" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="lastUpdate" label="最后更新" width="160" />
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="1"
          :page-size="20"
          :total="328"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </template>
  </PageP09Query>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageP09Query from '@/components/page-base/PageP09Query.vue'

const query = ref({ warehouse: '', productCode: '', productName: '', batchNo: '' })

const pageConfig = { title: '库存查询（P09）', showQueryPanel: true, showActionBar: true }

const tableData = [
  {
    warehouse: '原料仓',
    location: 'A-01-01',
    productCode: 'RM-001',
    productName: '钢材Q235',
    batchNo: 'B20260601',
    qty: 5000,
    availableQty: 4800,
    unit: 'kg',
    lastUpdate: '2026-06-10 08:30'
  },
  {
    warehouse: '原料仓',
    location: 'A-01-02',
    productCode: 'RM-002',
    productName: '铜线0.5mm',
    batchNo: 'B20260602',
    qty: 1200,
    availableQty: 1100,
    unit: 'kg',
    lastUpdate: '2026-06-09 14:20'
  },
  {
    warehouse: '成品仓',
    location: 'C-03-01',
    productCode: 'FP-001',
    productName: '传感器A型',
    batchNo: 'B20260520',
    qty: 800,
    availableQty: 750,
    unit: '个',
    lastUpdate: '2026-06-08 16:00'
  },
  {
    warehouse: '成品仓',
    location: 'C-03-02',
    productCode: 'FP-002',
    productName: '控制器B型',
    batchNo: 'B20260525',
    qty: 300,
    availableQty: 280,
    unit: '台',
    lastUpdate: '2026-06-07 11:30'
  },
  {
    warehouse: '原料仓',
    location: 'B-02-01',
    productCode: 'RM-003',
    productName: '塑料粒子ABS',
    batchNo: 'B20260528',
    qty: 3000,
    availableQty: 2900,
    unit: 'kg',
    lastUpdate: '2026-06-06 09:15'
  }
]
</script>

<style scoped lang="scss">
.action-left {
  display: flex;
  gap: 8px;
}
.action-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}
.record-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0 0;
}
</style>
