<template>
  <div class="page-p03-master-list">
    <!-- 查询区 -->
    <div v-if="config.showQueryPanel !== false" class="query-area">
      <slot name="query-panel">
        <div class="area-placeholder">
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

    <!-- 主内容区：左侧列表 + 右侧表单 -->
    <div class="main-content-area">
      <div class="list-panel" :style="listPanelStyle">
        <slot name="list-content">
          <div class="area-placeholder">
            <el-icon :size="48"><Document /></el-icon>
            <span>列表区 — 可通过 list-content 插槽自定义</span>
          </div>
        </slot>
      </div>
      <div class="form-panel">
        <slot name="form-content">
          <div class="area-placeholder">
            <el-icon :size="48"><Edit /></el-icon>
            <span>表单区 — 可通过 form-content 插槽自定义</span>
          </div>
        </slot>
      </div>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { Search, Operation, Document, Edit } from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, MasterListPageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<MasterListPageConfig>(() => {
  return (props.config || {}) as MasterListPageConfig
})

const listPanelStyle = computed<Record<string, string>>(() => {
  const percent = config.value.listWidthPercent ?? 40
  return {
    flex: `0 0 ${percent}%`,
    maxWidth: `${percent}%`
  }
})

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p03-master-list {
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

// 操作栏
.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  min-height: 48px;
  display: flex;
  align-items: center;
}

// 主内容：左右分栏
.main-content-area {
  flex: 1;
  display: flex;
  gap: 12px;
  min-height: 0;
}

.list-panel {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  overflow: auto;
}

.form-panel {
  flex: 1;
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  overflow: auto;
  min-width: 0;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
