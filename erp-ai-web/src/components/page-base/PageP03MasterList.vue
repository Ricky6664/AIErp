<template>
  <div class="page-p03-master-list">
    <!-- §5.3.1 常用查询区（顶部） -->
    <div v-if="config.showQueryPanel !== false" class="query-area">
      <slot name="query-panel">
        <div class="area-placeholder">
          <el-icon :size="18"><Search /></el-icon>
          <span>查询区 — 可通过 query-panel 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- §5.3.2 主数据列表区（中部） — 上下结构：操作栏 + 数据表格 -->
    <div class="list-area" :style="{ flex: listFlex }">
      <!-- 上部：功能操作区（左右分排） -->
      <div v-if="config.showActionBar !== false" class="action-bar">
        <div class="action-left">
          <slot name="action-left">
            <el-button type="primary" :icon="Plus">新增</el-button>
            <el-button :icon="Download">导出</el-button>
            <el-button :icon="TopRight" disabled>下推</el-button>
            <el-button :icon="More">更多</el-button>
          </slot>
        </div>
        <div class="action-right">
          <slot name="action-right">
            <el-tooltip content="格式设置"><el-button :icon="Setting" text /></el-tooltip>
            <el-tooltip content="刷新"><el-button :icon="Refresh" text /></el-tooltip>
            <el-tooltip content="行高"><el-button :icon="Sort" text /></el-tooltip>
            <el-tooltip content="铺满"
              ><el-button :icon="FullScreen" text @click="toggleListFull"
            /></el-tooltip>
          </slot>
        </div>
      </div>

      <!-- 下部：数据表格 -->
      <div class="table-wrapper">
        <slot name="main-content">
          <div class="area-placeholder">
            <el-icon :size="48"><Document /></el-icon>
            <span>数据表格区 — 可通过 main-content 插槽自定义</span>
          </div>
        </slot>
      </div>
    </div>

    <!-- §5.3.3 关联信息区（底部） — 左边分组导航 + 右边标签页容器 -->
    <div v-if="!isListFull" class="relation-area" :style="{ flex: relationFlex }">
      <div class="relation-header">
        <div class="relation-tabs">
          <slot name="relation-tabs">
            <el-tabs v-model="activeTab" type="border-card" class="relation-tabs-inner">
              <el-tab-pane label="关联1" name="tab1" />
              <el-tab-pane label="关联2" name="tab2" />
            </el-tabs>
          </slot>
        </div>
        <div class="relation-toolbar">
          <slot name="relation-toolbar">
            <el-tooltip content="新增"><el-button :icon="Plus" text size="small" /></el-tooltip>
            <el-tooltip content="放大查看"
              ><el-button :icon="FullScreen" text size="small" @click="toggleRelationFull"
            /></el-tooltip>
          </slot>
        </div>
      </div>
      <div class="relation-body">
        <slot name="relation-content">
          <div class="area-placeholder">
            <el-icon :size="48"><Connection /></el-icon>
            <span>关联信息区 — 可通过 relation-content 插槽自定义</span>
          </div>
        </slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  Search,
  Plus,
  Download,
  TopRight,
  More,
  Setting,
  Refresh,
  Sort,
  FullScreen,
  Document,
  Connection
} from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, MasterListPageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()
const emit = defineEmits<PageBaseEmits>()

const config = computed<MasterListPageConfig>(() => (props.config || {}) as MasterListPageConfig)
const activeTab = ref('tab1')

const isListFull = ref(false)
const isRelationFull = ref(false)

const listFlex = computed(() => {
  if (isRelationFull.value) return '1'
  if (isListFull.value) return '1'
  return config.value.listHeightPercent ? `${config.value.listHeightPercent}` : '3'
})

const relationFlex = computed(() => {
  if (isRelationFull.value) return '1'
  if (isListFull.value) return '0'
  return config.value.relationHeightPercent ? `${config.value.relationHeightPercent}` : '2'
})

function toggleListFull() {
  isListFull.value = !isListFull.value
  if (isListFull.value) isRelationFull.value = false
}

function toggleRelationFull() {
  isRelationFull.value = !isRelationFull.value
  if (isRelationFull.value) isListFull.value = false
}

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p03-master-list {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: hidden;
}

.area-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  min-height: 80px;
  flex-direction: column;
}

// §5.3.1 查询区
.query-area {
  background: #ffffff;
  border-radius: 12px;
  padding: 12px 20px;
  min-height: 48px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

// §5.3.2 主数据列表区
.list-area {
  background: #ffffff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}

.action-left {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-right {
  display: flex;
  gap: 4px;
  align-items: center;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
  padding: 0;
  min-height: 0;
}

// §5.3.3 关联信息区
.relation-area {
  background: #ffffff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.relation-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}

.relation-tabs {
  flex: 1;
  min-width: 0;
}

.relation-tabs-inner {
  :deep(.el-tabs__header) {
    margin: 0;
  }
}

.relation-toolbar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding-top: 4px;
  flex-shrink: 0;
}

.relation-body {
  flex: 1;
  overflow: auto;
  padding: 12px 16px;
  min-height: 0;
}
</style>
