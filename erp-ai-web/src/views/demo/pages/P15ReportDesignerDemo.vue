<template>
  <div class="designer-demo">
    <div class="demo-header">
      <h1>数据报表设计器（P15-报表）</h1>
      <p class="demo-desc">
        可视化设计数据报表：定义数据源、配置查询条件、设计报表布局、设置图表与汇总，所见即所得生成业务报表。
      </p>
    </div>

    <div class="designer-layout">
      <div class="toolbar">
        <div class="toolbar__left">
          <el-button type="primary" :icon="Plus" size="small">新建报表</el-button>
          <el-button :icon="Check" size="small" type="success">保存</el-button>
          <el-button :icon="View" size="small" @click="previewVisible = true">预览</el-button>
          <el-divider direction="vertical" />
          <el-button :icon="RefreshLeft" size="small">撤销</el-button>
          <el-button :icon="RefreshRight" size="small">重做</el-button>
        </div>
        <div class="toolbar__right">
          <el-select v-model="reportType" size="small" style="width: 140px">
            <el-option v-for="t in reportTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </div>
      </div>

      <div class="main-panels">
        <!-- 左侧：数据源 + 字段 -->
        <div class="panel panel--left">
          <div class="panel__title">数据源</div>
          <div class="ds-section">
            <el-select
              v-model="selectedDataSource"
              size="small"
              style="width: 100%"
              placeholder="选择数据视图"
            >
              <el-option
                v-for="ds in dataSources"
                :key="ds.value"
                :label="ds.label"
                :value="ds.value"
              />
            </el-select>
            <div v-if="selectedDataSource" class="field-list">
              <div class="field-group__name">可用字段（拖拽至布局区）</div>
              <div
                v-for="f in availableFields"
                :key="f.name"
                class="field-item"
                draggable="true"
                @dragstart="onFieldDrag($event, f)"
              >
                <el-tag :type="fieldTagType(f.type)" size="small" effect="plain">{{
                  f.type
                }}</el-tag>
                <span>{{ f.label }}</span>
              </div>
            </div>
          </div>

          <el-divider style="margin: 8px 0" />

          <div class="panel__title">查询条件</div>
          <div class="filter-section">
            <div v-for="(f, fi) in queryFilters" :key="fi" class="filter-row">
              <el-select v-model="f.field" size="small" style="width: 70px" placeholder="字段"
                ><el-option
                  v-for="af in availableFields"
                  :key="af.name"
                  :label="af.label"
                  :value="af.name"
              /></el-select>
              <el-select v-model="f.op" size="small" style="width: 55px"
                ><el-option v-for="o in filterOps" :key="o" :label="o" :value="o"
              /></el-select>
              <el-input
                v-model="f.defaultVal"
                size="small"
                style="width: 60px"
                placeholder="默认值"
              />
              <el-button link size="small" @click="queryFilters.splice(fi, 1)"
                ><el-icon :size="12"><Close /></el-icon
              ></el-button>
            </div>
            <el-button
              size="small"
              style="width: 100%"
              @click="queryFilters.push({ field: '', op: '=', defaultVal: '' })"
              >+ 添加条件</el-button
            >
          </div>
        </div>

        <!-- 中间：报表布局设计区 -->
        <div class="panel panel--center">
          <div class="panel__title">报表布局</div>
          <div class="layout-area">
            <!-- 表头区 -->
            <div
              class="layout-zone"
              @drop.prevent="onDropToZone('header', $event)"
              @dragover.prevent
            >
              <div class="zone-title">表头区（分组维度）</div>
              <div class="zone-fields">
                <el-tag
                  v-for="(f, i) in layoutHeader"
                  :key="f.name"
                  closable
                  size="small"
                  @close="layoutHeader.splice(i, 1)"
                  >{{ f.label }}</el-tag
                >
                <span v-if="!layoutHeader.length" class="zone-hint">拖拽字段到此处</span>
              </div>
            </div>
            <!-- 数据区 -->
            <div class="layout-zone" @drop.prevent="onDropToZone('data', $event)" @dragover.prevent>
              <div class="zone-title">数据区（指标/度量）</div>
              <div class="zone-fields">
                <el-tag
                  v-for="(f, i) in layoutData"
                  :key="f.name"
                  closable
                  size="small"
                  type="success"
                  @close="layoutData.splice(i, 1)"
                  >{{ f.label }}
                  <span class="tag-agg">({{ f.agg || 'SUM' }})</span>
                </el-tag>
                <span v-if="!layoutData.length" class="zone-hint">拖拽数值字段到此处</span>
              </div>
            </div>
            <!-- 图表区 -->
            <div class="layout-zone">
              <div class="zone-title">图表配置</div>
              <el-select
                v-model="chartType"
                size="small"
                style="width: 100%"
                clearable
                placeholder="选择图表类型（可选）"
              >
                <el-option
                  v-for="ct in chartTypes"
                  :key="ct.value"
                  :label="ct.label"
                  :value="ct.value"
                />
              </el-select>
            </div>
          </div>

          <!-- 预览表格 -->
          <div class="preview-table-section">
            <div class="panel__title" style="border-top: 1px solid var(--el-border-color-lighter)">
              数据预览
            </div>
            <el-table
              :data="previewData"
              size="small"
              border
              stripe
              max-height="200"
              style="width: 100%"
            >
              <el-table-column
                v-for="col in previewColumns"
                :key="col"
                :prop="col"
                :label="col"
                show-overflow-tooltip
              />
            </el-table>
          </div>
        </div>

        <!-- 右侧：属性面板 -->
        <div class="panel panel--right">
          <div class="panel__title">报表属性</div>
          <el-form label-width="90px" size="small">
            <el-form-item label="报表名称"><el-input v-model="reportName" /></el-form-item>
            <el-form-item label="报表编码"><el-input v-model="reportCode" /></el-form-item>
            <el-form-item label="报表分类">
              <el-select v-model="reportCategory" style="width: 100%"
                ><el-option v-for="c in categories" :key="c" :label="c" :value="c"
              /></el-select>
            </el-form-item>
            <el-form-item label="显示小计"
              ><el-switch v-model="showSubtotal" size="small"
            /></el-form-item>
            <el-form-item label="显示总计"
              ><el-switch v-model="showGrandTotal" size="small"
            /></el-form-item>
            <el-form-item label="数据钻取"
              ><el-switch v-model="enableDrill" size="small"
            /></el-form-item>
            <el-form-item label="自动刷新"
              ><el-switch v-model="autoRefresh" size="small"
            /></el-form-item>
            <el-form-item v-if="autoRefresh" label="刷新间隔(s)"
              ><el-input-number v-model="refreshInterval" :min="10" :max="3600" size="small"
            /></el-form-item>
            <el-divider />
            <el-form-item label="行数限制"
              ><el-input-number v-model="rowLimit" :min="0" :max="100000" size="small"
            /></el-form-item>
            <el-form-item label="默认排序列"
              ><el-input v-model="defaultSort" size="small" placeholder="字段名"
            /></el-form-item>
            <el-form-item label="排序方向"
              ><el-select v-model="sortDir" size="small"
                ><el-option label="升序" value="asc" /><el-option
                  label="降序"
                  value="desc" /></el-select
            ></el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <el-dialog v-model="previewVisible" title="报表预览" width="900px" top="3vh">
      <div class="preview-container">
        <div class="preview-title">{{ reportName || '未命名报表' }}</div>
        <div class="preview-filters">
          <el-tag
            v-for="f in queryFilters.filter((f) => f.field)"
            :key="f.field"
            size="small"
            effect="plain"
            style="margin: 2px"
            >{{ f.field }} {{ f.op }} {{ f.defaultVal || '?' }}</el-tag
          >
        </div>
        <el-table :data="previewData" border stripe size="small">
          <el-table-column
            v-for="col in previewColumns"
            :key="col"
            :prop="col"
            :label="col"
            sortable
            show-overflow-tooltip
          />
        </el-table>
        <div v-if="showGrandTotal" class="preview-total">总计行数: {{ previewData.length }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { Plus, Check, View, RefreshLeft, RefreshRight, Close } from '@element-plus/icons-vue'

defineOptions({ name: 'ReportDesignerDemo' })

const previewVisible = ref(false)
const reportName = ref('月度销售分析报表')
const reportCode = ref('RPT_SALES_MONTHLY')
const reportCategory = ref('销售报表')
const reportType = ref('table')
const showSubtotal = ref(true)
const showGrandTotal = ref(true)
const enableDrill = ref(true)
const autoRefresh = ref(false)
const refreshInterval = ref(60)
const rowLimit = ref(10000)
const defaultSort = ref('amount')
const sortDir = ref('desc')
const chartType = ref('bar')

const reportTypes = [
  { value: 'table', label: '表格报表' },
  { value: 'cross', label: '交叉报表' },
  { value: 'chart', label: '图表报表' },
  { value: 'mixed', label: '混合报表' }
]
const categories = ['销售报表', '采购报表', '库存报表', '财务报表', '生产报表', '自定义报表']
const chartTypes = [
  { value: 'bar', label: '柱状图' },
  { value: 'line', label: '折线图' },
  { value: 'pie', label: '饼图' },
  { value: 'area', label: '面积图' },
  { value: 'scatter', label: '散点图' }
]
const filterOps = ['=', '>', '<', '>=', '<=', '!=', 'LIKE', 'IN']

const dataSources = [
  { value: 'DV_SALE001', label: '销售订单主视图' },
  { value: 'DV_SALE002', label: '销售出库视图' },
  { value: 'DV_SALE003', label: '销售毛利视图' },
  { value: 'DV_INV001', label: '库存汇总视图' }
]
const selectedDataSource = ref('DV_SALE001')

const availableFields = computed(() => {
  if (selectedDataSource.value === 'DV_SALE001')
    return [
      { name: 'order_no', label: '单据编号', type: 'string' },
      { name: 'customer_name', label: '客户名称', type: 'string' },
      { name: 'product_name', label: '商品名称', type: 'string' },
      { name: 'qty', label: '数量', type: 'number' },
      { name: 'price', label: '单价', type: 'number' },
      { name: 'amount', label: '金额', type: 'number' },
      { name: 'order_date', label: '单据日期', type: 'date' },
      { name: 'status', label: '状态', type: 'string' },
      { name: 'warehouse_name', label: '仓库', type: 'string' },
      { name: 'salesman', label: '业务员', type: 'string' }
    ]
  return []
})

function fieldTagType(t: string) {
  return t === 'number' ? 'success' : t === 'date' ? 'warning' : 'info'
}

const queryFilters = reactive([
  { field: 'order_date', op: '>=', defaultVal: '2025-01-01' },
  { field: 'order_date', op: '<=', defaultVal: '2025-12-31' }
])
const layoutHeader = ref<any[]>([])
const layoutData = ref<any[]>([])

function onFieldDrag(e: DragEvent, f: any) {
  e.dataTransfer!.setData('field', JSON.stringify(f))
}
function onDropToZone(zone: string, e: DragEvent) {
  const f = JSON.parse(e.dataTransfer!.getData('field'))
  if (zone === 'header') {
    if (!layoutHeader.value.find((x) => x.name === f.name))
      layoutHeader.value.push({ ...f, agg: '' })
  } else {
    if (!layoutData.value.find((x) => x.name === f.name))
      layoutData.value.push({ ...f, agg: 'SUM' })
  }
}

const previewColumns = [
  'order_no',
  'customer_name',
  'product_name',
  'qty',
  'price',
  'amount',
  'order_date',
  'status'
]
const previewData = [
  {
    order_no: 'SO-2025-0001',
    customer_name: '深圳科技',
    product_name: '电子元器件A',
    qty: 1000,
    price: 12.5,
    amount: 12500,
    order_date: '2025-01-15',
    status: '已审核'
  },
  {
    order_no: 'SO-2025-0002',
    customer_name: '广州贸易',
    product_name: '原材料B',
    qty: 5000,
    price: 8.2,
    amount: 41000,
    order_date: '2025-01-20',
    status: '已审核'
  },
  {
    order_no: 'SO-2025-0003',
    customer_name: '上海电商',
    product_name: '成品C',
    qty: 200,
    price: 350.0,
    amount: 70000,
    order_date: '2025-02-01',
    status: '草稿'
  },
  {
    order_no: 'SO-2025-0004',
    customer_name: '北京科技',
    product_name: '配件D',
    qty: 800,
    price: 45.0,
    amount: 36000,
    order_date: '2025-02-10',
    status: '已审核'
  }
]
</script>

<style lang="scss" scoped>
.designer-demo {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #e6e6fa;

  .demo-header {
    padding: 16px 24px 0;
    h1 {
      font-size: 20px;
      font-weight: 700;
      margin: 0 0 4px;
    }
    .demo-desc {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }

  .designer-layout {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 16px;
    gap: 8px;
  }

  .toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fff;
    border-radius: 12px;
    padding: 8px 16px;
    flex-shrink: 0;
    &__left,
    &__right {
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }

  .main-panels {
    flex: 1;
    display: flex;
    gap: 8px;
    overflow: hidden;
  }

  .panel {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    &__title {
      padding: 10px 12px;
      font-weight: 600;
      font-size: 13px;
      border-bottom: 1px solid var(--el-border-color-lighter);
    }
    &--left {
      width: 220px;
      flex-shrink: 0;
      overflow-y: auto;
      padding: 8px;
    }
    &--center {
      flex: 1;
      overflow-y: auto;
      padding: 12px;
    }
    &--right {
      width: 240px;
      flex-shrink: 0;
      overflow-y: auto;
      padding: 8px 12px;
    }
  }

  .ds-section {
    margin-bottom: 8px;
  }
  .field-list {
    margin-top: 8px;
  }
  .field-group__name {
    font-size: 11px;
    font-weight: 600;
    color: var(--el-text-color-secondary);
    margin-bottom: 4px;
  }
  .field-item {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 4px 6px;
    border-radius: 4px;
    cursor: grab;
    font-size: 12px;
    &:hover {
      background: var(--el-fill-color-light);
    }
  }

  .filter-section {
    .filter-row {
      display: flex;
      gap: 4px;
      align-items: center;
      margin-bottom: 4px;
    }
  }

  .layout-area {
    margin-bottom: 12px;
  }
  .layout-zone {
    border: 2px dashed var(--el-border-color);
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 8px;
    min-height: 48px;
    .zone-title {
      font-size: 11px;
      color: var(--el-text-color-secondary);
      margin-bottom: 6px;
    }
  }
  .zone-fields {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    .zone-hint {
      font-size: 11px;
      color: var(--el-text-color-placeholder);
    }
    .tag-agg {
      font-size: 10px;
      color: var(--el-color-success);
    }
  }

  .preview-table-section {
    margin-top: 8px;
  }

  .preview-container {
    .preview-title {
      text-align: center;
      font-size: 18px;
      font-weight: 700;
      margin-bottom: 8px;
    }
    .preview-filters {
      margin-bottom: 12px;
    }
    .preview-total {
      margin-top: 8px;
      text-align: right;
      font-weight: 600;
    }
  }
}
</style>
