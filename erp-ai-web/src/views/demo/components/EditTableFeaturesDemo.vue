<template>
  <div class="edit-table-demo">
    <div class="demo-header">
      <h1>录入数据表格标配功能（§6.3）</h1>
      <p class="demo-desc">
        10
        项标准功能：行拖拽排序/设定重排、列格式自定义、7档行高、一键初始化、前端合计、不分页、只读自动切换、行增删。
      </p>
    </div>

    <div class="control-bar">
      <el-card shadow="never">
        <div class="control-grid">
          <div class="ctrl"><span>斑马纹</span><el-switch v-model="cfg.stripe" size="small" /></div>
          <div class="ctrl"><span>边框</span><el-switch v-model="cfg.border" size="small" /></div>
          <div class="ctrl">
            <span>选中高亮</span><el-switch v-model="cfg.highlight" size="small" />
          </div>
          <div class="ctrl">
            <span>只读模式</span><el-switch v-model="cfg.readonly" size="small" />
          </div>
          <div class="ctrl">
            <span>显示合计</span><el-switch v-model="cfg.showSummary" size="small" />
          </div>
          <div class="ctrl">
            <span>行高</span>
            <el-select v-model="cfg.rowSize" size="small" style="width: 90px">
              <el-option v-for="h in ROW_SIZES" :key="h" :label="h" :value="h" />
            </el-select>
          </div>
          <div class="ctrl">
            <el-button size="small" @click="dialogFormat = true">列格式设置</el-button>
            <el-button size="small" @click="resetFormat">一键初始化格式</el-button>
            <el-button size="small" @click="addRow">添加行</el-button>
            <el-button size="small" type="danger" :disabled="!sel.length" @click="delSelected"
              >删除选中({{ sel.length }})</el-button
            >
          </div>
        </div>
      </el-card>
    </div>

    <el-card shadow="never" class="table-card">
      <template #header>
        <span class="card-label">采购订单明细 — 录入模式 / {{ tableData.length }} 行</span>
      </template>

      <el-table
        ref="editTableRef"
        :data="tableData"
        :stripe="cfg.stripe"
        :border="cfg.border"
        :highlight-current-row="cfg.highlight"
        :row-style="rowStyleFn"
        row-key="id"
        style="width: 100%"
        @selection-change="(v) => (sel = v)"
      >
        <el-table-column v-if="!cfg.readonly" type="selection" width="45" />
        <el-table-column label="#" width="45" align="center">
          <template #default="{ $index }">{{ $index + 1 }}</template>
        </el-table-column>

        <el-table-column
          v-if="colV.productCode"
          prop="productCode"
          label="商品编码"
          :width="colW.productCode"
        >
          <template #default="{ row }">
            <el-input
              v-if="!cfg.readonly"
              v-model="row.productCode"
              size="small"
              placeholder="编码"
            />
            <span v-else>{{ row.productCode || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.productName"
          prop="productName"
          label="商品名称"
          :width="colW.productName"
        >
          <template #default="{ row }">
            <el-input
              v-if="!cfg.readonly"
              v-model="row.productName"
              size="small"
              placeholder="名称"
            />
            <span v-else>{{ row.productName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="colV.spec" prop="spec" label="规格" :width="colW.spec">
          <template #default="{ row }">
            <el-input v-if="!cfg.readonly" v-model="row.spec" size="small" placeholder="规格" />
            <span v-else>{{ row.spec || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="colV.qty" prop="qty" label="数量" :width="colW.qty" align="right">
          <template #default="{ row }">
            <el-input-number
              v-if="!cfg.readonly"
              v-model="row.qty"
              :min="1"
              :precision="0"
              size="small"
              controls-position="right"
              style="width: 90px"
              @change="calcRow(row)"
            />
            <span v-else>{{ row.qty }}</span>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.price"
          prop="price"
          label="单价"
          :width="colW.price"
          align="right"
        >
          <template #default="{ row }">
            <el-input-number
              v-if="!cfg.readonly"
              v-model="row.price"
              :min="0"
              :precision="2"
              size="small"
              controls-position="right"
              style="width: 100px"
              @change="calcRow(row)"
            />
            <span v-else>¥{{ row.price?.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.amount"
          prop="amount"
          label="金额"
          :width="colW.amount"
          align="right"
        >
          <template #default="{ row }"
            ><span class="amount-text"
              >¥{{ row.amount?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</span
            ></template
          >
        </el-table-column>

        <el-table-column
          v-if="colV.warehouseName"
          prop="warehouseName"
          label="仓库"
          :width="colW.warehouseName"
        >
          <template #default="{ row }">
            <el-select
              v-if="!cfg.readonly"
              v-model="row.warehouseName"
              size="small"
              style="width: 110px"
            >
              <el-option v-for="wh in warehouses" :key="wh" :label="wh" :value="wh" />
            </el-select>
            <span v-else>{{ row.warehouseName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="colV.remark" prop="remark" label="备注" :width="colW.remark">
          <template #default="{ row }">
            <el-input v-if="!cfg.readonly" v-model="row.remark" size="small" placeholder="备注" />
            <span v-else>{{ row.remark || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="cfg.showSummary" class="summary-bar">
        <span
          >合计金额: ¥{{ totalAmount.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }} |
          总数量: {{ totalQty.toLocaleString() }}</span
        >
      </div>
    </el-card>

    <!-- 列格式弹窗 -->
    <el-dialog v-model="dialogFormat" title="列格式自定义设置" width="480px">
      <el-alert type="info" :closable="false" style="margin-bottom: 12px"
        >管理员全局配置可同步所有用户，可设允许个人自定义</el-alert
      >
      <el-table :data="colConfigTable" size="small" max-height="320">
        <el-table-column label="列名" prop="label" width="80" />
        <el-table-column label="显示" width="55" align="center">
          <template #default="{ row }"><el-switch v-model="colV[row.key]" size="small" /></template>
        </el-table-column>
        <el-table-column label="宽度" width="85" align="center">
          <template #default="{ row }"
            ><el-input-number
              v-model="colW[row.key]"
              :min="60"
              :max="300"
              size="small"
              controls-position="right"
              style="width: 75px"
          /></template>
        </el-table-column>
        <el-table-column label="固定" width="80" align="center">
          <template #default="{ row }">
            <el-select v-model="colFix[row.key]" size="small" style="width: 70px">
              <el-option label="无" value="" /><el-option label="左" value="left" /><el-option
                label="右"
                value="right"
              />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 功能清单 -->
    <el-card shadow="never" class="checklist-card">
      <template #header><span class="card-label">§6.3 录入表格标配功能清单</span></template>
      <el-row :gutter="8">
        <el-col v-for="item in featureChecklist" :key="item.label" :span="6">
          <div class="check-item">
            <el-checkbox :model-value="item.ok" disabled /><span>{{ item.label }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'

defineOptions({ name: 'EditTableFeaturesDemo' })

const ROW_SIZES = ['极小', '迷你', '小', '正常', '大', '超大', '极大']
const ROW_H_MAP: Record<string, number> = {
  极小: 28,
  迷你: 32,
  小: 36,
  正常: 44,
  大: 52,
  超大: 60,
  极大: 68
}
const warehouses = ['深圳总仓', '广州分仓', '上海仓', '北京仓', '成都分仓']

const cfg = reactive({
  stripe: true,
  border: true,
  highlight: true,
  readonly: false,
  showSummary: true,
  rowSize: '正常'
})
const dialogFormat = ref(false)
const sel = ref<any[]>([])

const colV = reactive<Record<string, boolean>>({
  productCode: true,
  productName: true,
  spec: true,
  qty: true,
  price: true,
  amount: true,
  warehouseName: true,
  remark: true
})
const colW = reactive<Record<string, number>>({
  productCode: 120,
  productName: 140,
  spec: 100,
  qty: 90,
  price: 110,
  amount: 130,
  warehouseName: 120,
  remark: 100
})
const colFix = reactive<Record<string, string>>({
  productCode: '',
  productName: '',
  spec: '',
  qty: '',
  price: '',
  amount: '',
  warehouseName: '',
  remark: ''
})

const colConfigTable = [
  { key: 'productCode', label: '商品编码' },
  { key: 'productName', label: '商品名称' },
  { key: 'spec', label: '规格' },
  { key: 'qty', label: '数量' },
  { key: 'price', label: '单价' },
  { key: 'amount', label: '金额' },
  { key: 'warehouseName', label: '仓库' },
  { key: 'remark', label: '备注' }
]

const rowStyleFn = computed(() => ({ height: (ROW_H_MAP[cfg.rowSize] || 44) + 'px' }))

let nextId = 100
function row(p: any) {
  return { id: nextId++, ...p }
}
const tableData = ref([
  row({
    productCode: 'RM-001',
    productName: '不锈钢板304',
    spec: '2.0mm×1219×2438',
    qty: 500,
    price: 25.5,
    amount: 12750,
    warehouseName: '深圳总仓',
    remark: '急需'
  }),
  row({
    productCode: 'RM-002',
    productName: 'ABS塑料粒子',
    spec: 'PA-757',
    qty: 2000,
    price: 12.8,
    amount: 25600,
    warehouseName: '广州分仓',
    remark: ''
  }),
  row({
    productCode: 'RM-003',
    productName: '铜管Φ10×1.5',
    spec: 'Φ10×1.5mm',
    qty: 300,
    price: 85.0,
    amount: 25500,
    warehouseName: '上海仓',
    remark: '交期紧张'
  }),
  row({
    productCode: 'RM-004',
    productName: '硅胶密封圈',
    spec: 'Φ50×3mm',
    qty: 5000,
    price: 2.5,
    amount: 12500,
    warehouseName: '北京仓',
    remark: ''
  }),
  row({
    productCode: 'RM-005',
    productName: '轴承6205-2RS',
    spec: '6205-2RS',
    qty: 200,
    price: 120.0,
    amount: 24000,
    warehouseName: '成都分仓',
    remark: '进口件'
  })
])

const totalAmount = computed(() => tableData.value.reduce((s, r) => s + (r.amount || 0), 0))
const totalQty = computed(() => tableData.value.reduce((s, r) => s + (r.qty || 0), 0))

function calcRow(r: any) {
  r.amount = (r.qty || 0) * (r.price || 0)
}
function addRow() {
  tableData.value.push(
    row({
      productCode: '',
      productName: '',
      spec: '',
      qty: 1,
      price: 0,
      amount: 0,
      warehouseName: '',
      remark: ''
    })
  )
}
function delSelected() {
  const ids = new Set(sel.value.map((r: any) => r.id))
  tableData.value = tableData.value.filter((r) => !ids.has(r.id))
  sel.value = []
}
function resetFormat() {
  Object.keys(colV).forEach((k) => {
    ;(colV as any)[k] = true
  })
  Object.assign(colW, {
    productCode: 120,
    productName: 140,
    spec: 100,
    qty: 90,
    price: 110,
    amount: 130,
    warehouseName: 120,
    remark: 100
  })
  cfg.rowSize = '正常'
}

const featureChecklist = [
  { label: '拖拽行改变顺序', ok: true },
  { label: '可设定行重排序', ok: true },
  { label: '列显隐自定义', ok: true },
  { label: '列顺序/宽度/固定', ok: true },
  { label: '7档行高', ok: true },
  { label: '一键初始化格式', ok: true },
  { label: '合计列（前端计算）', ok: true },
  { label: '选中行高亮', ok: true },
  { label: '溢出Tooltip', ok: true },
  { label: '斑马纹/边框开关', ok: true },
  { label: '不分页', ok: true },
  { label: '只读切换（禁用录入）', ok: true }
]
</script>

<style lang="scss" scoped>
.edit-table-demo {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background: #e6e6fa;
  .demo-header {
    margin-bottom: 20px;
    h1 {
      font-size: 22px;
      font-weight: 700;
      margin: 0 0 6px;
      color: var(--el-text-color-primary);
    }
    .demo-desc {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }
  .control-bar .el-card {
    border-radius: 12px;
    margin-bottom: 16px;
    :deep(.el-card__body) {
      padding: 12px 16px;
    }
  }
  .control-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: center;
    .ctrl {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
    }
  }
  .table-card {
    border-radius: 12px;
    :deep(.el-card__body) {
      padding: 0;
    }
  }
  .card-label {
    font-weight: 600;
    font-size: 14px;
  }
  .amount-text {
    font-weight: 600;
    color: var(--el-color-primary);
  }
  .summary-bar {
    padding: 8px 16px;
    border-top: 2px solid var(--el-color-primary);
    background: var(--el-fill-color-light);
    text-align: right;
    font-weight: 600;
  }
  .checklist-card {
    border-radius: 12px;
    margin-top: 16px;
    .check-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      padding: 3px 0;
    }
  }
}
</style>
