<template>
  <div class="detail-table" :class="{ 'detail-table--disabled': props.disabled }">
    <!-- prefix 插槽 -->
    <div v-if="$slots.prefix" class="detail-table__prefix">
      <slot name="prefix" />
    </div>

    <!-- 标签页头部 -->
    <div v-if="visibleTabs.length > 0" class="detail-table__header">
      <div
        v-for="tab in visibleTabs"
        :key="tab.key"
        class="detail-table__tab"
        :class="{
          'detail-table__tab--active': isActive(tab.key),
          'detail-table__tab--disabled': tab.disabled || props.disabled
        }"
        @click="handleTabClick(tab)"
        @focus="emit('focus', tab.key)"
        @blur="emit('blur', tab.key)"
      >
        <span class="detail-table__tab-label">{{ tab.label }}</span>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="detail-table__empty">
      {{ props.placeholder || '暂无明细从表' }}
    </div>

    <!-- 内容区域：懒加载，仅渲染激活标签页 -->
    <div v-if="visibleTabs.length > 0" class="detail-table__content">
      <div
        v-for="tab in visibleTabs"
        v-show="isActive(tab.key)"
        :key="tab.key"
        class="detail-table__pane"
      >
        <template v-if="isActive(tab.key) || !(tab.lazy !== false)">
          <slot :name="tab.key" :tab="tab" :active="isActive(tab.key)" />
        </template>
      </div>
      <!-- 默认插槽（后备内容） -->
      <div class="detail-table__pane">
        <slot />
      </div>
    </div>

    <!-- suffix 插槽 -->
    <div v-if="$slots.suffix" class="detail-table__suffix">
      <slot name="suffix" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { DetailTableTab } from '@/types/detail-table'

// ============================================================
// Props
// ============================================================
const props = withDefaults(
  defineProps<{
    modelValue?: string
    fieldConfig?: DetailTableTab[]
    disabled?: boolean
    placeholder?: string
  }>(),
  {
    modelValue: '',
    fieldConfig: () => [],
    disabled: false,
    placeholder: ''
  }
)

// ============================================================
// Emits
// ============================================================
const emit = defineEmits<{
  'update:modelValue': [key: string]
  change: [key: string, tab: DetailTableTab]
  focus: [key: string]
  blur: [key: string]
}>()

// ============================================================
// 计算属性
// ============================================================

/** 可见的标签页（过滤隐藏项） */
const visibleTabs = computed<DetailTableTab[]>(() => {
  return (props.fieldConfig || []).filter((t) => !t.hidden)
})

// ============================================================
// 方法
// ============================================================

/** 判断标签页是否激活 */
function isActive(key: string): boolean {
  return props.modelValue === key
}

/** 处理标签页点击 */
function handleTabClick(tab: DetailTableTab): void {
  try {
    if (tab.disabled || props.disabled) return

    if (tab.key !== props.modelValue) {
      emit('update:modelValue', tab.key)
      emit('change', tab.key, tab)
    }
  } catch (err) {
    console.error('[DetailTable] click error:', err)
  }
}

/** 获取当前激活标签页 key */
function getActiveKey(): string | undefined {
  return props.modelValue || undefined
}

/** 设置激活标签页 */
function setActiveKey(key: string): void {
  try {
    const tab = props.fieldConfig?.find((t) => t.key === key && !t.hidden && !t.disabled)
    if (tab) {
      emit('update:modelValue', key)
      emit('change', key, tab)
    }
  } catch (err) {
    console.error('[DetailTable] setActiveKey error:', err)
  }
}

/** 获取可见标签页列表 */
function getVisibleTabs(): DetailTableTab[] {
  return visibleTabs.value
}

// ============================================================
// 暴露方法
// ============================================================
defineExpose({
  getActiveKey,
  setActiveKey,
  getVisibleTabs
})
</script>

<style scoped lang="scss">
.detail-table {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background: var(--el-bg-color);

  &--disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  &__prefix {
    flex-shrink: 0;
    padding: 8px 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  &__header {
    display: flex;
    align-items: center;
    gap: 0;
    flex-shrink: 0;
    border-bottom: 2px solid var(--el-border-color-lighter);
    padding: 0 8px;
    background: var(--el-bg-color);
    overflow-x: auto;
    overflow-y: hidden;

    &::-webkit-scrollbar {
      height: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--el-border-color-darker);
      border-radius: 2px;
    }
  }

  &__tab {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 10px 16px;
    cursor: pointer;
    color: var(--el-text-color-regular);
    font-size: 13px;
    white-space: nowrap;
    user-select: none;
    border-bottom: 2px solid transparent;
    margin-bottom: -2px;
    transition:
      color 0.2s,
      border-color 0.2s,
      background-color 0.2s;

    &:hover:not(&--disabled) {
      color: var(--el-color-primary);
      background: var(--el-color-primary-light-9);
    }

    &--active {
      color: var(--el-color-primary);
      border-bottom-color: var(--el-color-primary);
      font-weight: 500;
    }

    &--disabled {
      cursor: not-allowed;
      opacity: 0.5;
    }
  }

  &__tab-label {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__empty {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-placeholder);
    font-size: 13px;
    padding: 24px;
  }

  &__content {
    flex: 1;
    overflow: auto;
  }

  &__pane {
    width: 100%;
    height: 100%;
  }

  &__suffix {
    flex-shrink: 0;
    padding: 8px 12px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}
</style>
