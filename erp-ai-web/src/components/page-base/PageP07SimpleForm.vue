<template>
  <div class="page-p07-simple-form">
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

    <!-- 主表单内容区 -->
    <div class="main-content-area">
      <div class="form-wrapper" :style="formWrapperStyle">
        <slot name="main-content">
          <div class="area-placeholder">
            <el-icon :size="48"><Edit /></el-icon>
            <span>主表单区 — 可通过 main-content 插槽自定义</span>
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
import { Search, Operation, Edit } from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, SimpleFormPageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<SimpleFormPageConfig>(() => {
  return (props.config || {}) as SimpleFormPageConfig
})

const formWrapperStyle = computed<Record<string, string>>(() => {
  const maxWidth = config.value.formMaxWidth ?? 960
  const width = typeof maxWidth === 'number' ? `${maxWidth}px` : maxWidth
  return {
    maxWidth: width
  }
})

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p07-simple-form {
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

// 主内容区
.main-content-area {
  flex: 1;
  display: flex;
  justify-content: center;
  min-height: 0;
}

.form-wrapper {
  width: 100%;
  max-width: 960px;
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px 32px;
  overflow: auto;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
