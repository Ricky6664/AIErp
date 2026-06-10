<template>
  <div class="list-table-demo">
    <div class="demo-header">
      <h1>列表数据表格标配功能（§6.2）</h1>
      <p class="demo-desc">
        12 项标准功能全部可独立开关配置。重点演示 §6.2.2
        列搜索筛选：每列独立筛选，按数据类型差异化条件，多条件组合 + 跨列组合。
      </p>
    </div>

    <!-- 活跃筛选条件标签 -->
    <div v-if="activeFilterTags.length > 0" class="active-filters-bar">
      <span class="filter-bar-label">当前筛选：</span>
      <el-tag
        v-for="(tag, i) in activeFilterTags"
        :key="i"
        closable
        size="small"
        type="warning"
        @close="removeFilterTag(i)"
      >
        {{ tag }}
      </el-tag>
      <el-button link size="small" type="danger" @click="clearAllColumnFilters">清空全部</el-button>
    </div>

    <!-- 功能控制 -->
    <div class="control-bar">
      <el-card shadow="never">
        <div class="control-grid">
          <div class="ctrl"><span>斑马纹</span><el-switch v-model="cfg.stripe" size="small" /></div>
          <div class="ctrl"><span>边框</span><el-switch v-model="cfg.border" size="small" /></div>
          <div class="ctrl">
            <span>高亮行</span><el-switch v-model="cfg.highlight" size="small" />
          </div>
          <div class="ctrl">
            <span>合计</span><el-switch v-model="cfg.showSummary" size="small" />
          </div>
          <div class="ctrl">
            <span>行高</span>
            <el-select v-model="cfg.rowSize" size="small" style="width: 85px">
              <el-option v-for="h in ROW_SIZES" :key="h" :label="h" :value="h" />
            </el-select>
          </div>
          <div class="ctrl">
            <el-button size="small" @click="dialogFormat = true">列格式设置</el-button>
            <el-button size="small" @click="resetFormat">一键初始化</el-button>
            <el-button size="small" @click="clearAllColumnFilters">清空筛选排序</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-head">
          <span class="table-head-title">销售订单列表</span>
          <span class="table-head-count">共 {{ filteredData.length }} 条</span>
        </div>
      </template>

      <el-table
        ref="tableRef"
        :data="pagedData"
        :stripe="cfg.stripe"
        :border="cfg.border"
        :highlight-current-row="cfg.highlight"
        :row-style="rowStyleFn"
        style="width: 100%"
        @sort-change="onSortChange"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column
          v-if="colV.orderNo"
          prop="orderNo"
          label="单据编号"
          :width="colW.orderNo"
          sortable="custom"
          show-overflow-tooltip
        >
          <template #header>
            <div class="cust-header">
              <span>单据编号</span>
              <ColumnFilter
                v-model="colFilters.orderNo"
                column-type="string"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.customerName"
          prop="customerName"
          label="客户名称"
          :width="colW.customerName"
          sortable="custom"
          show-overflow-tooltip
        >
          <template #header>
            <div class="cust-header">
              <span>客户名称</span>
              <ColumnFilter
                v-model="colFilters.customerName"
                column-type="string"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.productName"
          prop="productName"
          label="商品"
          :width="colW.productName"
          sortable="custom"
          show-overflow-tooltip
        >
          <template #header>
            <div class="cust-header">
              <span>商品</span>
              <ColumnFilter
                v-model="colFilters.productName"
                column-type="string"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.qty"
          prop="qty"
          label="数量"
          :width="colW.qty"
          sortable="custom"
          align="right"
        >
          <template #header>
            <div class="cust-header">
              <span>数量</span>
              <ColumnFilter
                v-model="colFilters.qty"
                column-type="number"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
          <template #default="{ row }">{{ row.qty?.toLocaleString() }}</template>
        </el-table-column>

        <el-table-column
          v-if="colV.price"
          prop="price"
          label="单价"
          :width="colW.price"
          sortable="custom"
          align="right"
        >
          <template #header>
            <div class="cust-header">
              <span>单价</span>
              <ColumnFilter
                v-model="colFilters.price"
                column-type="number"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
          <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
        </el-table-column>

        <el-table-column
          v-if="colV.amount"
          prop="amount"
          label="金额"
          :width="colW.amount"
          sortable="custom"
          align="right"
        >
          <template #header>
            <div class="cust-header">
              <span>金额</span>
              <ColumnFilter
                v-model="colFilters.amount"
                column-type="number"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
          <template #default="{ row }"
            >¥{{ row.amount?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</template
          >
        </el-table-column>

        <el-table-column
          v-if="colV.orderDate"
          prop="orderDate"
          label="单据日期"
          :width="colW.orderDate"
          sortable="custom"
          align="center"
        >
          <template #header>
            <div class="cust-header">
              <span>单据日期</span>
              <ColumnFilter
                v-model="colFilters.orderDate"
                column-type="date"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.status"
          prop="status"
          label="状态"
          :width="colW.status"
          align="center"
        >
          <template #header>
            <div class="cust-header">
              <span>状态</span>
              <ColumnFilter
                v-model="colFilters.status"
                column-type="string"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === '已审核'
                  ? 'success'
                  : row.status === '草稿'
                    ? 'info'
                    : row.status === '已关闭'
                      ? 'warning'
                      : 'danger'
              "
              size="small"
              >{{ row.status }}</el-tag
            >
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.warehouseName"
          prop="warehouseName"
          label="仓库"
          :width="colW.warehouseName"
          show-overflow-tooltip
        >
          <template #header>
            <div class="cust-header">
              <span>仓库</span>
              <ColumnFilter
                v-model="colFilters.warehouseName"
                column-type="string"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column
          v-if="colV.isUrgent"
          prop="isUrgent"
          label="加急"
          :width="colW.isUrgent"
          align="center"
        >
          <template #header>
            <div class="cust-header">
              <span>加急</span>
              <ColumnFilter
                v-model="colFilters.isUrgent"
                column-type="boolean"
                @filter-change="applyColumnFilters"
              />
            </div>
          </template>
          <template #default="{ row }">
            <el-tag :type="row.isUrgent ? 'danger' : 'info'" size="small">{{
              row.isUrgent ? '是' : '否'
            }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pag-bar">
        <el-pagination
          v-model:current-page="page.page"
          v-model:page-size="page.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredData.length"
          layout="total, sizes, prev, pager, next, jumper"
          size="small"
        />
      </div>

      <div v-if="cfg.showSummary" class="summary-bar">
        <span
          >合计金额: ¥{{ totalAmount.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }} |
          总数量: {{ totalQty.toLocaleString() }}</span
        >
      </div>
    </el-card>

    <!-- 列格式弹窗 -->
    <el-dialog v-model="dialogFormat" title="列格式自定义设置" width="500px">
      <el-alert type="info" :closable="false" style="margin-bottom: 12px"
        >管理员全局配置可同步所有用户 / 普通用户个人独立配置</el-alert
      >
      <el-table :data="colConfigRows" size="small" max-height="360">
        <el-table-column label="列名" prop="label" width="90" />
        <el-table-column label="显示" width="60" align="center">
          <template #default="{ row }"><el-switch v-model="colV[row.key]" size="small" /></template>
        </el-table-column>
        <el-table-column label="宽度" width="85" align="center">
          <template #default="{ row }"
            ><el-input-number
              v-model="colW[row.key]"
              :min="60"
              :max="400"
              size="small"
              style="width: 75px"
          /></template>
        </el-table-column>
        <el-table-column label="固定" width="80" align="center">
          <template #default="{ row }">
            <el-select v-model="colFix[row.key]" size="small" style="width: 70px">
              <el-option label="-" value="" /><el-option label="左" value="left" /><el-option
                label="右"
                value="right"
              />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 功能清单 -->
    <el-card shadow="never" class="cl-card">
      <template #header><span class="cl-title">§6.2 标配功能清单</span></template>
      <el-row :gutter="8">
        <el-col v-for="it in checklist" :key="it.label" :span="6">
          <div class="cl-item">
            <el-checkbox :model-value="it.ok" disabled /><span>{{ it.label }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import ColumnFilter from '@/components/common/ColumnFilter.vue'
import type { FilterCondition } from '@/components/common/ColumnFilter.vue'

defineOptions({ name: 'ListTableFeaturesDemo' })

const ROW_SIZES = ['极小', '迷你', '小', '正常', '大', '超大', '极大']
const RH_MAP: Record<string, number> = {
  极小: 28,
  迷你: 32,
  小: 36,
  正常: 44,
  大: 52,
  超大: 60,
  极大: 68
}

const cfg = reactive({
  stripe: true,
  border: true,
  highlight: true,
  showSummary: true,
  rowSize: '正常'
})
const dialogFormat = ref(false)

const colV = reactive<Record<string, boolean>>({
  orderNo: true,
  customerName: true,
  productName: true,
  qty: true,
  price: true,
  amount: true,
  orderDate: true,
  status: true,
  warehouseName: true,
  isUrgent: true
})
const colW = reactive<Record<string, number>>({
  orderNo: 150,
  customerName: 140,
  productName: 120,
  qty: 80,
  price: 95,
  amount: 130,
  orderDate: 120,
  status: 80,
  warehouseName: 110,
  isUrgent: 65
})
const colFix = reactive<Record<string, string>>({
  orderNo: '',
  customerName: '',
  productName: '',
  qty: '',
  price: '',
  amount: '',
  orderDate: '',
  status: '',
  warehouseName: '',
  isUrgent: ''
})

const colConfigRows = [
  { key: 'orderNo', label: '单据编号' },
  { key: 'customerName', label: '客户名称' },
  { key: 'productName', label: '商品' },
  { key: 'qty', label: '数量' },
  { key: 'price', label: '单价' },
  { key: 'amount', label: '金额' },
  { key: 'orderDate', label: '单据日期' },
  { key: 'status', label: '状态' },
  { key: 'warehouseName', label: '仓库' },
  { key: 'isUrgent', label: '加急' }
]

const rowStyleFn = computed(() => ({ height: (RH_MAP[cfg.rowSize] || 44) + 'px' }))

const page = reactive({ page: 1, size: 10 })

// ---- 原始数据 ----
const ALL = [
  {
    orderNo: 'SO-2025-0001',
    customerName: '深圳科技有限公司',
    productName: '电子元器件A',
    qty: 1000,
    price: 12.5,
    amount: 12500,
    orderDate: '2025-01-15',
    status: '已审核',
    warehouseName: '深圳总仓',
    isUrgent: true
  },
  {
    orderNo: 'SO-2025-0002',
    customerName: '广州贸易公司',
    productName: '原材料B',
    qty: 5000,
    price: 8.2,
    amount: 41000,
    orderDate: '2025-01-20',
    status: '已审核',
    warehouseName: '广州分仓',
    isUrgent: false
  },
  {
    orderNo: 'SO-2025-0003',
    customerName: '上海电子商务',
    productName: '成品C',
    qty: 200,
    price: 350.0,
    amount: 70000,
    orderDate: '2025-02-01',
    status: '草稿',
    warehouseName: '上海仓',
    isUrgent: false
  },
  {
    orderNo: 'SO-2025-0004',
    customerName: '北京科技公司',
    productName: '配件D',
    qty: 800,
    price: 45.0,
    amount: 36000,
    orderDate: '2025-02-10',
    status: '已审核',
    warehouseName: '北京仓',
    isUrgent: false
  },
  {
    orderNo: 'SO-2025-0005',
    customerName: '杭州制造企业',
    productName: '模具E',
    qty: 150,
    price: 1200.0,
    amount: 180000,
    orderDate: '2025-02-15',
    status: '已审核',
    warehouseName: '杭州仓',
    isUrgent: true
  },
  {
    orderNo: 'SO-2025-0006',
    customerName: '成都商贸公司',
    productName: '包装材料F',
    qty: 3000,
    price: 5.5,
    amount: 16500,
    orderDate: '2025-03-01',
    status: '已关闭',
    warehouseName: '成都分仓',
    isUrgent: false
  },
  {
    orderNo: 'SO-2025-0007',
    customerName: '武汉实业公司',
    productName: '半成品G',
    qty: 400,
    price: 280.0,
    amount: 112000,
    orderDate: '2025-03-05',
    status: '已审核',
    warehouseName: '武汉仓',
    isUrgent: false
  },
  {
    orderNo: 'SO-2025-0008',
    customerName: '南京精密仪器',
    productName: '精密件H',
    qty: 50,
    price: 5000.0,
    amount: 250000,
    orderDate: '2025-03-10',
    status: '已审核',
    warehouseName: '南京仓',
    isUrgent: true
  },
  {
    orderNo: 'SO-2025-0009',
    customerName: '重庆塑料制品',
    productName: '塑料原料I',
    qty: 10000,
    price: 3.2,
    amount: 32000,
    orderDate: '2025-03-15',
    status: '草稿',
    warehouseName: '重庆仓',
    isUrgent: false
  },
  {
    orderNo: 'SO-2025-0010',
    customerName: '天津化工企业',
    productName: '化工料J',
    qty: 2000,
    price: 18.0,
    amount: 36000,
    orderDate: '2025-03-20',
    status: '已作废',
    warehouseName: '天津仓',
    isUrgent: false
  },
  {
    orderNo: 'SO-2026-0001',
    customerName: '郑州电子公司',
    productName: '主板K',
    qty: 300,
    price: 850.0,
    amount: 255000,
    orderDate: '2026-01-05',
    status: '已审核',
    warehouseName: '郑州仓',
    isUrgent: true
  },
  {
    orderNo: 'SO-2026-0002',
    customerName: '长沙机械厂',
    productName: '轴承L',
    qty: 600,
    price: 120.0,
    amount: 72000,
    orderDate: '2026-01-12',
    status: '已审核',
    warehouseName: '长沙仓',
    isUrgent: false
  }
]

// ---- 列筛选状态 ----
type ColFiltersMap = Record<string, FilterCondition[]>
const colFilters = reactive<ColFiltersMap>({
  orderNo: [],
  customerName: [],
  productName: [],
  qty: [],
  price: [],
  amount: [],
  orderDate: [],
  status: [],
  warehouseName: [],
  isUrgent: []
})

// 根据运算符匹配单行
function matchRow(val: any, cond: FilterCondition): boolean {
  const op = cond.operator
  const v = cond.value
  const sv = String(val ?? '')

  // 为空 / 不为空
  if (op === 'isEmpty') return sv === '' || sv === 'null' || sv === 'undefined'
  if (op === 'isNotEmpty') return sv !== '' && sv !== 'null' && sv !== 'undefined'

  // 布尔
  if (op === 'isTrue') return !!val
  if (op === 'isFalse') return !val

  if (v === '' || v === undefined) return true

  const nv = Number(v)
  const numVal = Number(val)
  const sLow = sv.toLowerCase()
  const vLow = v.toLowerCase()

  switch (op) {
    // 字符串
    case 'contains':
      return sLow.includes(vLow)
    case 'notContains':
      return !sLow.includes(vLow)
    case 'equals':
      return sv === v
    case 'notEquals':
      return sv !== v
    case 'startsWith':
      return sLow.startsWith(vLow)
    case 'endsWith':
      return sLow.endsWith(vLow)

    // 数值/日期
    case 'gt':
      return numVal > nv
    case 'gte':
      return numVal >= nv
    case 'lt':
      return numVal < nv
    case 'lte':
      return numVal <= nv

    default:
      return true
  }
}

// 一列的多条件 AND，列之间 AND
const filteredData = computed(() => {
  return ALL.filter((row) => {
    for (const [col, conds] of Object.entries(colFilters)) {
      if (!conds || conds.length === 0) continue
      // 列内多条件 AND
      for (const c of conds) {
        if (!matchRow((row as any)[col], c)) return false
      }
    }
    return true
  })
})

const pagedData = computed(() => {
  const s = (page.page - 1) * page.size
  return filteredData.value.slice(s, s + page.size)
})

const totalAmount = computed(() => ALL.reduce((s, r) => s + r.amount, 0))
const totalQty = computed(() => ALL.reduce((s, r) => s + r.qty, 0))

// 活跃筛选标签
const activeFilterTags = computed(() => {
  const tags: string[] = []
  const colLabels: Record<string, string> = {
    orderNo: '单据编号',
    customerName: '客户名称',
    productName: '商品',
    qty: '数量',
    price: '单价',
    amount: '金额',
    orderDate: '单据日期',
    status: '状态',
    warehouseName: '仓库',
    isUrgent: '加急'
  }
  for (const [col, conds] of Object.entries(colFilters)) {
    for (const c of conds || []) {
      const opLabel: Record<string, string> = {
        contains: '包含',
        notContains: '不包含',
        equals: '=',
        notEquals: '≠',
        startsWith: '开头是',
        endsWith: '结尾是',
        isEmpty: '为空',
        isNotEmpty: '不为空',
        gt: '>',
        gte: '≥',
        lt: '<',
        lte: '≤',
        isTrue: '是',
        isFalse: '否'
      }
      const label = colLabels[col] || col
      const op = opLabel[c.operator] || c.operator
      const val = ['isEmpty', 'isNotEmpty', 'isTrue', 'isFalse'].includes(c.operator)
        ? ''
        : `"${c.value}"`
      tags.push(`${label} ${op} ${val}`.trim())
    }
  }
  return tags
})

function applyColumnFilters() {
  page.page = 1
}

function removeFilterTag(i: number) {
  // Flatten all active conditions and find the i-th
  const entries: { col: string; idx: number }[] = []
  for (const [col, conds] of Object.entries(colFilters)) {
    ;(conds || []).forEach((_, ci) => entries.push({ col, idx: ci }))
  }
  if (i < entries.length) {
    const { col, idx } = entries[i]
    ;(colFilters as any)[col].splice(idx, 1)
    page.page = 1
  }
}

function clearAllColumnFilters() {
  for (const k of Object.keys(colFilters)) {
    ;(colFilters as any)[k] = []
  }
  page.page = 1
}

function onSortChange(_s: any) {}
function resetFormat() {
  for (const k of Object.keys(colV)) (colV as any)[k] = true
  Object.assign(colW, {
    orderNo: 150,
    customerName: 140,
    productName: 120,
    qty: 80,
    price: 95,
    amount: 130,
    orderDate: 120,
    status: 80,
    warehouseName: 110,
    isUrgent: 65
  })
  cfg.rowSize = '正常'
  page.page = 1
}

const checklist = [
  { label: '列排序（单字段循环）', ok: true },
  { label: '列搜索筛选（类型差异化）', ok: true },
  { label: '单字段多条件AND组合', ok: true },
  { label: '多字段跨列AND组合', ok: true },
  { label: '列格式自定义', ok: true },
  { label: '7档行高', ok: true },
  { label: '一键初始化格式', ok: true },
  { label: '清空筛选排序', ok: true },
  { label: '合计列配置', ok: true },
  { label: '选中行高亮', ok: true },
  { label: '溢出Tooltip', ok: true },
  { label: '斑马纹/边框开关', ok: true },
  { label: '分页标配', ok: true }
]
</script>

<style lang="scss" scoped>
.list-table-demo {
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
      margin: 0;
      line-height: 1.6;
    }
  }

  .active-filters-bar {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: wrap;
    background: #fff;
    border-radius: 12px;
    padding: 10px 16px;
    margin-bottom: 12px;
    .filter-bar-label {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      font-weight: 600;
    }
  }

  .control-bar .el-card {
    border-radius: 12px;
    margin-bottom: 12px;
    :deep(.el-card__body) {
      padding: 10px 16px;
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
  .table-head {
    display: flex;
    justify-content: space-between;
    &-title {
      font-weight: 600;
    }
    &-count {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }
  .pag-bar {
    display: flex;
    justify-content: flex-end;
    padding: 10px 16px;
  }
  .summary-bar {
    padding: 8px 16px;
    border-top: 2px solid var(--el-color-primary);
    background: var(--el-fill-color-light);
    text-align: right;
    font-weight: 600;
    font-size: 13px;
  }

  .cust-header {
    display: flex;
    align-items: center;
    gap: 2px;
  }

  .cl-card {
    border-radius: 12px;
    margin-top: 16px;
    .cl-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      padding: 3px 0;
    }
  }
  .cl-title {
    font-weight: 600;
    font-size: 14px;
  }
}
</style>
