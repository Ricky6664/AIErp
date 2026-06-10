<template>
  <PageP06MasterForm view-id="demo-p06" page-type="P06" :config="pageConfig" :permissions="[]">
    <template #action-bar>
      <div class="action-left">
        <el-button type="primary">新增</el-button>
        <el-button type="success">保存</el-button>
        <el-button type="danger">删除</el-button>
        <el-button>重置</el-button>
        <el-dropdown style="margin-left: 4px">
          <el-button
            >更多 <el-icon><ArrowDown /></el-icon
          ></el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>导入</el-dropdown-item>
              <el-dropdown-item>复制</el-dropdown-item>
              <el-dropdown-item>存为草稿</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="action-right">
        <el-button type="warning">审核</el-button>
        <el-button>反审</el-button>
        <el-divider direction="vertical" />
        <el-button>打印</el-button>
        <el-button>导出</el-button>
        <el-divider direction="vertical" />
        <el-button>引入</el-button>
        <el-button>下推</el-button>
      </div>
    </template>

    <template #main-content>
      <div class="form-area">
        <!-- 主表单 -->
        <div class="master-form-section">
          <h3 class="section-label">基本信息</h3>
          <el-form :model="form" label-width="120px">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="单据编号"
                  ><el-input v-model="form.no" placeholder="自动生成" disabled
                /></el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="单据日期" required
                  ><el-date-picker v-model="form.date" type="date" style="width: 100%"
                /></el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="客户名称" required
                  ><el-input v-model="form.customer" placeholder="请选择客户"
                /></el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="业务员"><el-input v-model="form.salesman" /></el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="部门"><el-input v-model="form.dept" /></el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="币种">
                  <el-select v-model="form.currency" style="width: 100%">
                    <el-option label="人民币 (CNY)" value="CNY" />
                    <el-option label="美元 (USD)" value="USD" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注"
                  ><el-input v-model="form.remark" type="textarea" :rows="2"
                /></el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <!-- 明细从表区 -->
        <div class="detail-section">
          <el-tabs type="border-card">
            <el-tab-pane label="物料明细">
              <div class="detail-toolbar">
                <el-button type="primary" size="small">添加行</el-button>
                <el-button size="small">删除行</el-button>
              </div>
              <el-table :data="detailData" border stripe size="small">
                <el-table-column type="index" width="50" label="#" />
                <el-table-column prop="productCode" label="物料编码" width="140" />
                <el-table-column prop="productName" label="物料名称" />
                <el-table-column prop="unit" label="单位" width="80" />
                <el-table-column prop="qty" label="数量" width="120" />
                <el-table-column prop="price" label="单价" width="120" />
                <el-table-column prop="amount" label="金额" width="140" />
              </el-table>
              <div class="detail-summary">
                合计金额：<strong>¥{{ totalAmount.toLocaleString() }}</strong>
              </div>
            </el-tab-pane>
            <el-tab-pane label="自定义信息">
              <el-form :model="form" label-width="100px">
                <el-form-item label="自定义字段1"><el-input /></el-form-item>
                <el-form-item label="自定义字段2"><el-input /></el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </template>
  </PageP06MasterForm>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import PageP06MasterForm from '@/components/page-base/PageP06MasterForm.vue'

const pageConfig = { title: '销售订单（P06）', showActionBar: true, formMaxWidth: '100%' }

const form = ref({
  no: 'SO-2026-0042',
  date: new Date(),
  customer: 'XX科技有限公司',
  salesman: '王经理',
  dept: '销售一部',
  currency: 'CNY',
  remark: ''
})

const detailData = [
  {
    productCode: 'P-001',
    productName: '工业传感器A型',
    unit: '个',
    qty: 100,
    price: 580,
    amount: 58000
  },
  {
    productCode: 'P-002',
    productName: '控制器模块B型',
    unit: '台',
    qty: 50,
    price: 1200,
    amount: 60000
  },
  {
    productCode: 'P-003',
    productName: '连接线缆C型',
    unit: '根',
    qty: 200,
    price: 45,
    amount: 9000
  }
]

const totalAmount = computed(() => detailData.reduce((sum, d) => sum + d.amount, 0))
</script>

<style scoped lang="scss">
.action-left {
  display: flex;
  gap: 8px;
  align-items: center;
}
.action-right {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-left: auto;
}
.form-area {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 16px;
}
.master-form-section {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px 24px;
  .section-label {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 16px;
  }
}
.detail-section {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  :deep(.el-tabs__content) {
    overflow: auto;
  }
}
.detail-toolbar {
  display: flex;
  gap: 8px;
  padding: 8px 0;
}
.detail-summary {
  text-align: right;
  padding: 12px 0 0;
  font-size: 15px;
}
</style>
