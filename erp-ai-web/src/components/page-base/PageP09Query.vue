<template>
  <div class="page-p09-query">
    <!-- 页面标题 -->
    <div v-if="config.title" class="page-header">
      <h2 class="page-title">{{ config.title }}</h2>
    </div>

    <!-- 查询区 -->
    <div v-if="config.showQueryPanel !== false" class="query-area">
      <slot name="query-panel">
        <el-form :model="queryForm" label-width="auto" class="query-form">
          <el-row :gutter="16">
            <el-col v-for="field in config.queryFields" :key="field.id" :span="field.span ?? 6">
              <el-form-item :label="field.label" :prop="field.field">
                <el-input
                  v-if="field.type === 'input'"
                  v-model="queryForm[field.field]"
                  :placeholder="field.placeholder ?? `请输入${field.label}`"
                  clearable
                />
                <el-input-number
                  v-else-if="field.type === 'number'"
                  v-model="queryForm[field.field]"
                  :placeholder="field.placeholder ?? `请输入${field.label}`"
                  controls-position="right"
                  style="width: 100%"
                />
                <el-select
                  v-else-if="field.type === 'select'"
                  v-model="queryForm[field.field]"
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
                  v-model="queryForm[field.field]"
                  type="date"
                  :placeholder="field.placeholder ?? `请选择${field.label}`"
                  style="width: 100%"
                />
                <el-date-picker
                  v-else-if="field.type === 'date-range'"
                  v-model="queryForm[field.field]"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  style="width: 100%"
                />
                <el-cascader
                  v-else-if="field.type === 'cascader'"
                  v-model="queryForm[field.field]"
                  :options="field.options"
                  :placeholder="field.placeholder ?? `请选择${field.label}`"
                  clearable
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="config.queryFields && config.queryFields.length > 0" justify="end">
            <el-col :span="24" style="text-align: right">
              <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
              <el-button :icon="RefreshRight" @click="handleReset">重置</el-button>
            </el-col>
          </el-row>
        </el-form>
        <div v-if="!config.queryFields || config.queryFields.length === 0" class="area-placeholder">
          <el-icon :size="18"><Search /></el-icon>
          <span>查询区 — 可通过 query-panel 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 操作栏 -->
    <div v-if="config.showActionBar !== false" class="action-area">
      <slot name="action-bar">
        <div class="area-placeholder">
          <el-icon :size="18"><Operation /></el-icon>
          <span>操作栏 — 可通过 action-bar 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 结果计数 -->
    <div v-if="config.showResultCount !== false" class="result-summary">
      <div class="result-count">
        <span v-if="totalCount > 0" class="count-value">{{ totalCount }}</span>
        <span>条记录</span>
      </div>
      <div class="result-actions">
        <slot name="result-actions" />
      </div>
    </div>

    <!-- 主内容区：查询结果 -->
    <div class="main-content-area">
      <slot name="main-content">
        <div class="area-placeholder">
          <el-icon :size="48"><Document /></el-icon>
          <span>查询结果区 — 可通过 main-content 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 分页区 -->
    <div v-if="config.pageSize && config.pageSize > 0" class="pagination-area">
      <slot name="pagination">
        <div class="area-placeholder">
          <span>分页区 — 可通过 pagination 插槽自定义</span>
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
import { Search, Operation, Document, RefreshRight } from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, QueryPageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<QueryPageConfig>(() => {
  return (props.config || {}) as QueryPageConfig
})

const queryForm = reactive<Record<string, unknown>>({})

function initQueryForm() {
  Object.keys(queryForm).forEach((k) => delete queryForm[k])
  if (config.value.queryFields) {
    for (const field of config.value.queryFields) {
      queryForm[field.field] = field.defaultValue ?? undefined
    }
  }
}

initQueryForm()

const totalCount = computed(() => {
  return 0
})

function handleSearch() {
  emit('data-change', { source: 'query-search', data: { ...queryForm } })
}

function handleReset() {
  initQueryForm()
  emit('data-change', { source: 'query-reset', data: { ...queryForm } })
}

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p09-query {
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

.query-form {
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
}

// 结果摘要
.result-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
  min-height: 28px;
}

.result-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);

  .count-value {
    font-weight: 600;
    color: var(--el-color-primary);
    margin-right: 4px;
  }
}

.result-actions {
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
}

// 分页区
.pagination-area {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-height: 36px;
  padding: 0 4px;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
