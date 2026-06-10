<template>
  <PageP10Report view-id="demo-p10" page-type="P10" :config="pageConfig" :permissions="[]">
    <template #filter-panel>
      <el-form :inline="true">
        <el-form-item label="会计期间">
          <el-select v-model="filter.period" style="width: 160px">
            <el-option label="2026年6月" value="202606" />
            <el-option label="2026年5月" value="202605" />
          </el-select>
        </el-form-item>
        <el-form-item label="核算组织">
          <el-select v-model="filter.org" style="width: 180px">
            <el-option label="XX科技有限公司" value="org1" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目范围">
          <el-input v-model="filter.accountRange" placeholder="1001-1999" style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>
    </template>

    <template #action-bar>
      <div class="action-left">
        <el-button>打印</el-button>
        <el-button>导出Excel</el-button>
        <el-button>导出PDF</el-button>
      </div>
    </template>

    <template #main-content>
      <div class="report-container">
        <h3 class="report-title">科目余额表</h3>
        <h4 class="report-subtitle">2026年6月 | XX科技有限公司 | 单位：元</h4>
        <el-table :data="reportData" border stripe size="small" :span-method="spanMethod">
          <el-table-column prop="accountCode" label="科目编码" width="140" />
          <el-table-column prop="accountName" label="科目名称" />
          <el-table-column prop="beginDebit" label="期初借方" width="140" align="right" />
          <el-table-column prop="beginCredit" label="期初贷方" width="140" align="right" />
          <el-table-column prop="debit" label="本期借方" width="140" align="right" />
          <el-table-column prop="credit" label="本期贷方" width="140" align="right" />
          <el-table-column prop="endDebit" label="期末借方" width="140" align="right" />
          <el-table-column prop="endCredit" label="期末贷方" width="140" align="right" />
        </el-table>
      </div>
    </template>
  </PageP10Report>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageP10Report from '@/components/page-base/PageP10Report.vue'

const filter = ref({ period: '202606', org: 'org1', accountRange: '' })

const pageConfig = {
  title: '科目余额表（P10）',
  reportType: 'table' as const,
  showFilterPanel: true,
  showActionBar: true,
  showPrint: true,
  showExportExcel: true,
  showExportPdf: true
}

const reportData = [
  {
    accountCode: '1001',
    accountName: '库存现金',
    beginDebit: 50000,
    beginCredit: 0,
    debit: 20000,
    credit: 15000,
    endDebit: 55000,
    endCredit: 0
  },
  {
    accountCode: '1002',
    accountName: '银行存款',
    beginDebit: 2800000,
    beginCredit: 0,
    debit: 500000,
    credit: 320000,
    endDebit: 2980000,
    endCredit: 0
  },
  {
    accountCode: '1122',
    accountName: '应收账款',
    beginDebit: 650000,
    beginCredit: 0,
    debit: 180000,
    credit: 120000,
    endDebit: 710000,
    endCredit: 0
  },
  {
    accountCode: '1403',
    accountName: '原材料',
    beginDebit: 420000,
    beginCredit: 0,
    debit: 80000,
    credit: 60000,
    endDebit: 440000,
    endCredit: 0
  },
  {
    accountCode: '2001',
    accountName: '短期借款',
    beginDebit: 0,
    beginCredit: 500000,
    debit: 100000,
    credit: 0,
    endDebit: 0,
    endCredit: 400000
  },
  {
    accountCode: '2202',
    accountName: '应付账款',
    beginDebit: 0,
    beginCredit: 380000,
    debit: 90000,
    credit: 150000,
    endDebit: 0,
    endCredit: 440000
  },
  {
    accountCode: '4001',
    accountName: '实收资本',
    beginDebit: 0,
    beginCredit: 5000000,
    debit: 0,
    credit: 0,
    endDebit: 0,
    endCredit: 5000000
  },
  {
    accountCode: '5001',
    accountName: '主营业务收入',
    beginDebit: 0,
    beginCredit: 0,
    debit: 0,
    credit: 850000,
    endDebit: 0,
    endCredit: 850000
  }
]

function spanMethod({
  rowIndex,
  columnIndex
}: {
  rowIndex: number
  columnIndex: number
}): [number, number] | void {
  if (rowIndex === reportData.length - 1 && columnIndex === 0) return [1, 2]
}
</script>

<style scoped lang="scss">
.action-left {
  display: flex;
  gap: 8px;
}
.report-container {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px;
}
.report-title {
  text-align: center;
  font-size: 18px;
  margin: 0 0 4px;
}
.report-subtitle {
  text-align: center;
  font-size: 13px;
  font-weight: 400;
  color: var(--el-text-color-secondary);
  margin: 0 0 20px;
}
</style>
