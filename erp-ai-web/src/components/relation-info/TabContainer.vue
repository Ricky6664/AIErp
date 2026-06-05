<template>
  <div class="tab-container" :class="{ 'tab-container--disabled': props.disabled }">
    <!-- prefix 插槽 -->
    <div v-if="$slots.prefix" class="tab-container__prefix">
      <slot name="prefix" />
    </div>

    <!-- 标签页头部 -->
    <div v-if="visibleTabs.length > 0" class="tab-container__header">
      <div
        v-for="tab in visibleTabs"
        :key="tab.key"
        class="tab-container__tab"
        :class="{
          'tab-container__tab--active': isActive(tab.key),
          'tab-container__tab--disabled': tab.disabled || props.disabled
        }"
        @click="handleTabClick(tab)"
        @focus="emit('focus', tab.key)"
        @blur="emit('blur', tab.key)"
      >
        <span class="tab-container__tab-label">{{ tab.label }}</span>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="tab-container__empty">
      {{ props.placeholder || '暂无标签页' }}
    </div>

    <!-- 内容区域 -->
    <div v-if="visibleTabs.length > 0" class="tab-container__content">
      <slot />
    </div>

    <!-- suffix 插槽 -->
    <div v-if="$slots.suffix" class="tab-container__suffix">
      <slot name="suffix" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { RelatedTab } from '@/types/relation-info'

const props = withDefaults(
  defineProps<{
    modelValue?: string
    tabs?: RelatedTab[]
    activeGroup?: string
    disabled?: boolean
    placeholder?: string
  }>(),
  {
    modelValue: '',
    tabs: () => [],
    activeGroup: '',
    disabled: false,
    placeholder: ''
  }
)

const emit = defineEmits<{
  'update:modelValue': [key: string]
  change: [key: string, tab: RelatedTab]
  focus: [key: string]
  blur: [key: string]
}>()

/** 可见的标签页（过滤隐藏项，按 activeGroup 筛选） */
const visibleTabs = computed<RelatedTab[]>(() => {
  let list = props.tabs.filter((t) => !t.hidden)
  if (props.activeGroup) {
    list = list.filter((t) => t.group === props.activeGroup)
  }
  return list
})

/** 判断标签页是否激活 */
function isActive(key: string): boolean {
  return props.modelValue === key
}

/** 处理标签页点击 */
function handleTabClick(tab: RelatedTab): void {
  try {
    if (tab.disabled || props.disabled) return

    if (tab.key !== props.modelValue) {
      emit('update:modelValue', tab.key)
      emit('change', tab.key, tab)
    }
  } catch (err) {
    console.error('[TabContainer] click error:', err)
  }
}

/** 获取当前激活标签页 key */
function getActiveKey(): string | undefined {
  return props.modelValue || undefined
}

/** 设置激活标签页 */
function setActiveKey(key: string): void {
  const tab = props.tabs.find((t) => t.key === key && !t.hidden)
  if (tab) {
    emit('update:modelValue', key)
    emit('change', key, tab)
  }
}

/** 获取当前 group 的可见标签页列表 */
function getVisibleTabs(): RelatedTab[] {
  return visibleTabs.value
}

defineExpose({
  getActiveKey,
  setActiveKey,
  getVisibleTabs
})
</script>

<style scoped lang="scss">
.tab-container {
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
    padding: 12px;
  }

  &__suffix {
    flex-shrink: 0;
    padding: 8px 12px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}
</style>
