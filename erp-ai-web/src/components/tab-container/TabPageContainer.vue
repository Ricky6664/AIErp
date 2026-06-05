<template>
  <div class="tab-page-container" :class="{ 'tab-page-container--disabled': props.disabled }">
    <!-- prefix 插槽 -->
    <div v-if="$slots.prefix" class="tab-page-container__prefix">
      <slot name="prefix" />
    </div>

    <!-- 标签页头部 -->
    <div v-if="visibleTabs.length > 0" class="tab-page-container__header">
      <div
        v-for="tab in visibleTabs"
        :key="tab.key"
        class="tab-page-container__tab"
        :class="{
          'tab-page-container__tab--active': isActive(tab.key),
          'tab-page-container__tab--disabled': tab.disabled || props.disabled
        }"
        @click="handleTabClick(tab)"
        @focus="emit('focus', tab.key)"
        @blur="emit('blur', tab.key)"
      >
        <el-icon v-if="tab.icon" class="tab-page-container__tab-icon">
          <component :is="tab.icon" />
        </el-icon>
        <span class="tab-page-container__tab-label">{{ tab.label }}</span>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="tab-page-container__empty">
      {{ props.placeholder || '暂无标签页' }}
    </div>

    <!-- 内容区域 -->
    <div v-if="visibleTabs.length > 0" class="tab-page-container__content">
      <slot />
    </div>

    <!-- suffix 插槽 -->
    <div v-if="$slots.suffix" class="tab-page-container__suffix">
      <slot name="suffix" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TabItem } from '@/types/tab-container'

const props = withDefaults(
  defineProps<{
    modelValue: string
    fieldConfig: TabItem[]
    activeGroup?: string
    disabled?: boolean
    placeholder?: string
  }>(),
  {
    modelValue: '',
    fieldConfig: () => [],
    activeGroup: '',
    disabled: false,
    placeholder: ''
  }
)

const emit = defineEmits<{
  'update:modelValue': [key: string]
  change: [key: string, tab: TabItem]
  focus: [key: string]
  blur: [key: string]
}>()

/** 可见标签页（过滤 hidden、按 activeGroup 筛选） */
const visibleTabs = computed<TabItem[]>(() => {
  let tabs = props.fieldConfig.filter((t) => !t.hidden)
  if (props.activeGroup) {
    tabs = tabs.filter((t) => !t.group || t.group === props.activeGroup)
  }
  return tabs
})

/** 判断标签页是否激活 */
function isActive(key: string): boolean {
  return props.modelValue === key
}

/** 处理标签页点击 */
function handleTabClick(tab: TabItem): void {
  try {
    if (tab.disabled || props.disabled) return

    if (tab.key !== props.modelValue) {
      emit('update:modelValue', tab.key)
      emit('change', tab.key, tab)
    }
  } catch (err) {
    console.error('[TabPageContainer] click error:', err)
  }
}

/** 查找标签页（不区分可见性） */
function findTab(key: string): TabItem | undefined {
  return props.fieldConfig.find((t) => t.key === key && !t.disabled && !t.hidden)
}

/** 获取当前激活标签页 key */
function getActiveKey(): string | undefined {
  return props.modelValue || undefined
}

/** 设置激活标签页 */
function setActiveKey(key: string): void {
  const tab = findTab(key)
  if (tab) {
    emit('update:modelValue', key)
    emit('change', key, tab)
  }
}

/** 获取当前 group 的可见标签页列表 */
function getVisibleTabs(): TabItem[] {
  return visibleTabs.value
}

const permissionHiddenCount = 0

defineExpose({
  getActiveKey,
  setActiveKey,
  getVisibleTabs,
  permissionHiddenCount
})
</script>

<style scoped lang="scss">
.tab-page-container {
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

  &__tab-icon {
    font-size: 14px;
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
