<template>
  <div class="page-p10-report">
    <!-- 页面标题 -->
    <div v-if="config.title" class="page-header">
      <h2 class="page-title">{{ config.title }}</h2>
    </div>

    <!-- 查询区：报表条件过滤面板 -->
    <div v-if="config.showFilterPanel !== false" class="query-area">
      <slot name="query-panel">
        <el-form :model="filterForm" label-width="auto" class="filter-form">
          <el-row :gutter="16">
            <el-col v-for="field in config.filterFields" :key="field.id" :span="field.span ?? 6">
              <el-form-item :label="field.label" :prop="field.field">
                <el-input
                  v-if="field.type === 'input'"
                  v-model="filterForm[field.field]"
                  :placeholder="field.placeholder ?? `请输入${field.label}`"
                  clearable
                />
                <el-input-number
                  v-else-if="field.type === 'number'"
                  v-model="filterForm[field.field]"
                  :placeholder="field.placeholder ?? `请输入${field.label}`"
                  controls-position="right"
                  style="width: 100%"
                />
                <el-select
                  v-else-if="field.type === 'select'"
                  v-model="filterForm[field.field]"
                  :placeholder="field.placeholder ?? `请选择${field.label}`"
                  clearable
                  style="width: 100%"
                >
                  <el-option
                    v-for="opt in field.options"
                    :key="opt.value"
                    :label="opt.label"
                    :value="opt.value"
                  />
                </el-select>
                <el-date-picker
                  v-else-if="field.type === 'date'"
                  v-model="filterForm[field.field]"
                  type="date"
                  :placeholder="field.placeholder ?? `请选择${field.label}`"
                  style="width: 100%"
                />
                <el-date-picker
                  v-else-if="field.type === 'date-range'"
                  v-model="filterForm[field.field]"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="config.filterFields && config.filterFields.length > 0" justify="end">
            <el-col :span="24" style="text-align: right">
              <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
              <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
            </el-col>
          </el-row>
        </el-form>
        <div
          v-if="!config.filterFields || config.filterFields.length === 0"
          class="area-placeholder"
        >
          <el-icon :size="18"><Search /></el-icon>
          <span>查询区 — 可通过 query-panel 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 操作栏：报表专用按钮 -->
    <div v-if="config.showActionBar !== false" class="action-area">
      <slot name="action-bar">
        <div class="action-bar-left">
          <slot name="action-bar-left" />
        </div>
        <div class="action-bar-right">
          <slot name="action-bar-right">
            <el-button v-if="config.showPrint !== false" :icon="Printer" @click="handlePrint"
              >打印</el-button
            >
            <el-button
              v-if="config.showExportExcel !== false"
              :icon="Download"
              @click="handleExportExcel"
              >导出Excel</el-button
            >
            <el-button
              v-if="config.showExportPdf !== false"
              :icon="Document"
              @click="handleExportPdf"
              >导出PDF</el-button
            >
          </slot>
        </div>
      </slot>
    </div>

    <!-- 报表主内容区 -->
    <div class="main-content-area">
      <slot name="main-content">
        <!-- 树形报表 -->
        <div v-if="config.reportType === 'tree'" class="report-tree-layout">
          <div class="report-tree-panel" :style="treePanelStyle">
            <div class="report-tree-header">
              <span>科目树</span>
            </div>
            <div class="report-tree-body">
              <div class="area-placeholder">
                <el-icon :size="18"><FolderOpened /></el-icon>
                <span>树形报表 — 可通过 main-content 插槽自定义</span>
              </div>
            </div>
          </div>
          <div class="report-data-panel">
            <div class="report-data-header">
              <span>报表数据</span>
            </div>
            <div class="report-data-body">
              <div class="area-placeholder">
                <el-icon :size="18"><Document /></el-icon>
                <span>报表数据区 — 可通过 main-content 插槽自定义</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 账簿报表 -->
        <div v-else-if="config.reportType === 'ledger'" class="report-ledger-layout">
          <div class="report-ledger-header">
            <div class="ledger-col ledger-col--date">日期</div>
            <div class="ledger-col ledger-col--voucher">凭证号</div>
            <div class="ledger-col ledger-col--summary">摘要</div>
            <div class="ledger-col ledger-col--amount">借方金额</div>
            <div class="ledger-col ledger-col--amount">贷方金额</div>
            <div class="ledger-col ledger-col--balance">余额</div>
          </div>
          <div class="report-ledger-body">
            <div class="area-placeholder">
              <el-icon :size="18"><Notebook /></el-icon>
              <span>账簿报表 — 可通过 main-content 插槽自定义</span>
            </div>
          </div>
        </div>

        <!-- 表格报表（默认） -->
        <div v-else class="report-table-layout">
          <div v-if="config.columns && config.columns.length > 0" class="report-table-wrapper">
            <div class="report-table-header">
              <span class="report-count">共 {{ config.rows?.length ?? 0 }} 行</span>
            </div>
            <div class="report-table-body">
              <el-table :data="config.rows ?? []" border stripe height="100%">
                <el-table-column
                  v-for="col in config.columns"
                  :key="col.id"
                  :prop="col.field"
                  :label="col.label"
                  :width="col.width"
                  :align="col.align ?? 'left'"
                  :fixed="col.fixed"
                />
              </el-table>
              <div v-if="!config.rows || config.rows.length === 0" class="table-empty">
                <span>暂无数据</span>
              </div>
            </div>
          </div>
          <div v-else class="area-placeholder">
            <el-icon :size="48"><DataAnalysis /></el-icon>
            <span>报表展示区 — 可通过 main-content 插槽自定义</span>
          </div>
        </div>
      </slot>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import {
  Search,
  RefreshRight,
  Printer,
  Download,
  Document,
  FolderOpened,
  Notebook,
  DataAnalysis
} from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, ReportPageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<ReportPageConfig>(() => {
  return (props.config || {}) as ReportPageConfig
})

const filterForm = reactive<Record<string, unknown>>({})

function initFilterForm() {
  Object.keys(filterForm).forEach((k) => delete filterForm[k])
  if (config.value.filterFields) {
    for (const field of config.value.filterFields) {
      filterForm[field.field] = undefined
    }
  }
}

initFilterForm()

const treePanelStyle = computed<Record<string, string>>(() => {
  return {
    flex: `0 0 240px`,
    width: '240px'
  }
})

function handleQuery() {
  emit('data-change', { source: 'report-query', data: { ...filterForm } })
}

function handleReset() {
  initFilterForm()
  emit('data-change', { source: 'report-reset', data: { ...filterForm } })
}

function handlePrint() {
  emit('data-change', { source: 'report-print', data: {} })
}

function handleExportExcel() {
  emit('data-change', { source: 'report-export-excel', data: {} })
}

function handleExportPdf() {
  emit('data-change', { source: 'report-export-pdf', data: {} })
}

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p10-report {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.area-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  min-height: 80px;
}

// 页面标题
.page-header {
  margin-bottom: 4px;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

// 查询区
.query-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  min-height: 56px;
}

.filter-form {
  width: 100%;
}

// 操作栏
.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.action-bar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-bar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

// 主内容区
.main-content-area {
  flex: 1;
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  min-height: 0;
  overflow: auto;
  display: flex;
  flex-direction: column;
}

// 树形报表布局
.report-tree-layout {
  display: flex;
  gap: 12px;
  height: 100%;
  min-height: 0;
}

.report-tree-panel {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.report-tree-header {
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-light);
  flex-shrink: 0;
}

.report-tree-body {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.report-data-panel {
  flex: 1;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.report-data-header {
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-light);
  flex-shrink: 0;
}

.report-data-body {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

// 账簿报表布局
.report-ledger-layout {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  overflow: hidden;
}

.report-ledger-header {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: var(--el-fill-color-light);
  border-bottom: 2px solid var(--el-border-color);
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  flex-shrink: 0;
  gap: 4px;
}

.ledger-col {
  padding: 0 8px;
  flex-shrink: 0;

  &--date {
    width: 100px;
  }
  &--voucher {
    width: 120px;
  }
  &--summary {
    flex: 1;
    min-width: 0;
  }
  &--amount {
    width: 120px;
    text-align: right;
  }
  &--balance {
    width: 120px;
    text-align: right;
  }
}

.report-ledger-body {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

// 表格报表布局
.report-table-layout {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.report-table-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.report-table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  flex-shrink: 0;
}

.report-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.report-table-body {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.table-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  color: var(--el-text-color-placeholder);
  font-size: 13px;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
