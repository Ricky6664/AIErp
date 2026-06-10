<template>
  <div class="page-p05-tree-list">
    <!-- 查询区 -->
    <div v-if="config.showQueryPanel !== false" class="query-area">
      <slot name="query-panel">
        <div class="area-placeholder">
          <el-icon :size="18"><Search /></el-icon>
          <span>查询区 — 可通过 query-panel 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 主内容区：左侧树 + 右侧列表 -->
    <div class="main-content-area">
      <!-- 左侧树形导航区 -->
      <div class="tree-panel" :style="treePanelStyle">
        <div v-if="config.showTreeSearch !== false" class="tree-search">
          <slot name="tree-search">
            <el-input
              v-model="treeSearchKeyword"
              placeholder="搜索树节点..."
              :prefix-icon="Search"
              clearable
              size="default"
            />
          </slot>
        </div>
        <div class="tree-content">
          <slot name="tree-content">
            <div class="area-placeholder">
              <el-icon :size="24"><FolderOpened /></el-icon>
              <span>树形区 — 可通过 tree-content 插槽自定义</span>
            </div>
          </slot>
        </div>
        <div class="tree-footer">
          <slot name="tree-footer">
            <span class="selected-path">当前选中：—</span>
          </slot>
        </div>
      </div>

      <!-- 右侧数据列表区 -->
      <div class="list-panel">
        <div v-if="config.showActionBar !== false" class="action-area">
          <slot name="action-bar">
            <div class="area-placeholder">
              <el-icon :size="18"><Operation /></el-icon>
              <span>操作栏 — 可通过 action-bar 插槽自定义</span>
            </div>
          </slot>
        </div>
        <div class="data-content">
          <slot name="main-content">
            <div class="area-placeholder">
              <el-icon :size="48"><Document /></el-icon>
              <span>主内容区 — 可通过 main-content 插槽自定义</span>
            </div>
          </slot>
        </div>
      </div>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Search, Operation, Document, FolderOpened } from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, TreeListPageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const treeSearchKeyword = ref('')

const config = computed<TreeListPageConfig>(() => {
  return (props.config || {}) as TreeListPageConfig
})

const treePanelStyle = computed<Record<string, string>>(() => {
  const width = config.value.treeWidth ?? 280
  return {
    flex: `0 0 ${width}px`,
    maxWidth: `${width}px`
  }
})

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p05-tree-list {
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

// 查询区
.query-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  min-height: 56px;
  display: flex;
  align-items: center;
}

// 主内容：左树 + 右列表
.main-content-area {
  flex: 1;
  display: flex;
  gap: 12px;
  min-height: 0;
}

// 左侧树形面板
.tree-panel {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.tree-search {
  margin-bottom: 8px;
}

.tree-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
}

.tree-footer {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--el-border-color-lighter);
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

// 右侧列表面板
.list-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  min-height: 48px;
  display: flex;
  align-items: center;
}

.data-content {
  flex: 1;
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  overflow: auto;
  min-height: 0;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
