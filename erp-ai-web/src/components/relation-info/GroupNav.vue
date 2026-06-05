<template>
  <div class="group-nav" :class="{ 'group-nav--disabled': props.disabled }">
    <!-- prefix 插槽 -->
    <div v-if="$slots.prefix" class="group-nav__prefix">
      <slot name="prefix" />
    </div>

    <!-- 分组列表 -->
    <div v-if="visibleGroups.length > 0" class="group-nav__list">
      <div v-for="group in visibleGroups" :key="group.key" class="group-nav__item-wrapper">
        <!-- 分组项 -->
        <div
          class="group-nav__item"
          :class="{
            'group-nav__item--active': isActive(group.key),
            'group-nav__item--disabled': group.disabled || props.disabled,
            'group-nav__item--has-children': hasVisibleChildren(group)
          }"
          @click="handleGroupClick(group)"
          @focus="emit('focus', group.key)"
          @blur="emit('blur', group.key)"
        >
          <!-- 展开/折叠箭头（有子分组时显示） -->
          <span
            v-if="hasVisibleChildren(group)"
            class="group-nav__arrow"
            :class="{ 'group-nav__arrow--expanded': isExpanded(group.key) }"
          >
            <el-icon :size="12">
              <ArrowRight />
            </el-icon>
          </span>

          <!-- 图标 -->
          <span v-if="group.icon" class="group-nav__icon">
            <el-icon :size="16">
              <component :is="group.icon" />
            </el-icon>
          </span>

          <!-- 标签 -->
          <span class="group-nav__label">{{ group.label }}</span>

          <!-- 角标 -->
          <el-badge
            v-if="group.badge && group.badge > 0"
            :value="group.badge"
            :max="99"
            class="group-nav__badge"
          />
        </div>

        <!-- 子分组列表 -->
        <div v-if="hasVisibleChildren(group) && isExpanded(group.key)" class="group-nav__children">
          <div
            v-for="child in getVisibleChildren(group)"
            :key="child.key"
            class="group-nav__child-item"
            :class="{
              'group-nav__child-item--active': isActive(child.key),
              'group-nav__child-item--disabled': child.disabled || props.disabled
            }"
            @click.stop="handleGroupClick(child)"
          >
            <span v-if="child.icon" class="group-nav__child-icon">
              <el-icon :size="14">
                <component :is="child.icon" />
              </el-icon>
            </span>
            <span class="group-nav__child-label">{{ child.label }}</span>
            <el-badge
              v-if="child.badge && child.badge > 0"
              :value="child.badge"
              :max="99"
              class="group-nav__badge"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="group-nav__empty">
      {{ props.placeholder || '暂无分组' }}
    </div>

    <!-- suffix 插槽 -->
    <div v-if="$slots.suffix" class="group-nav__suffix">
      <slot name="suffix" />
    </div>

    <!-- default 插槽 -->
    <div v-if="$slots.default" class="group-nav__default">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import type { NavGroup } from '@/types/relation-info'

const props = withDefaults(
  defineProps<{
    modelValue?: string
    fieldConfig?: NavGroup[]
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

const emit = defineEmits<{
  'update:modelValue': [key: string]
  change: [key: string, group: NavGroup]
  focus: [key: string]
  blur: [key: string]
}>()

/** 当前展开的分组 key 集合 */
const expandedKeys = ref<Set<string>>(new Set())

/** 可见的顶层分组（排除 hidden） */
const visibleGroups = computed<NavGroup[]>(() => {
  return props.fieldConfig.filter((g) => !g.hidden)
})

/** 判断分组是否处于选中态 */
function isActive(key: string): boolean {
  return props.modelValue === key
}

/** 判断分组是否已展开 */
function isExpanded(key: string): boolean {
  return expandedKeys.value.has(key)
}

/** 判断分组是否有可见的子分组 */
function hasVisibleChildren(group: NavGroup): boolean {
  return !!(group.children && group.children.some((c) => !c.hidden))
}

/** 获取可见的子分组 */
function getVisibleChildren(group: NavGroup): NavGroup[] {
  if (!group.children) return []
  return group.children.filter((c) => !c.hidden)
}

/** 处理分组点击 */
function handleGroupClick(group: NavGroup): void {
  try {
    if (group.disabled || props.disabled) return

    const hasChildren = hasVisibleChildren(group)

    if (hasChildren) {
      toggleExpand(group.key)
    }

    if (group.key !== props.modelValue) {
      emit('update:modelValue', group.key)
      emit('change', group.key, group)
    }
  } catch (err) {
    console.error('[GroupNav] click error:', err)
  }
}

/** 切换分组展开/折叠状态 */
function toggleExpand(key: string): void {
  const newSet = new Set(expandedKeys.value)
  if (newSet.has(key)) {
    newSet.delete(key)
  } else {
    newSet.add(key)
  }
  expandedKeys.value = newSet
}

/** 获取当前选中分组 key */
function getActiveKey(): string | undefined {
  return props.modelValue || undefined
}

/** 设置选中分组 */
function setActiveKey(key: string): void {
  const group = findGroup(key)
  if (group) {
    emit('update:modelValue', key)
    emit('change', key, group)
  }
}

/** 展开指定分组 */
function expand(key: string): void {
  const newSet = new Set(expandedKeys.value)
  newSet.add(key)
  expandedKeys.value = newSet
}

/** 折叠指定分组 */
function collapse(key: string): void {
  const newSet = new Set(expandedKeys.value)
  newSet.delete(key)
  expandedKeys.value = newSet
}

/** 获取当前展开的所有分组 key */
function getExpandedKeys(): string[] {
  return Array.from(expandedKeys.value)
}

/** 递归查找分组 */
function findGroup(key: string, groups?: NavGroup[]): NavGroup | undefined {
  const list = groups || props.fieldConfig
  for (const g of list) {
    if (g.key === key) return g
    if (g.children) {
      const found = findGroup(key, g.children)
      if (found) return found
    }
  }
  return undefined
}

defineExpose({
  getActiveKey,
  setActiveKey,
  expand,
  collapse,
  getExpandedKeys
})
</script>

<style scoped lang="scss">
.group-nav {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background: var(--el-bg-color);
  border-right: 1px solid var(--el-border-color-light);
  overflow-y: auto;

  &--disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  &__prefix {
    padding: 8px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  &__list {
    flex: 1;
    padding: 4px 0;
  }

  &__item {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 12px;
    cursor: pointer;
    color: var(--el-text-color-regular);
    font-size: 14px;
    transition:
      background-color 0.2s,
      color 0.2s;
    user-select: none;
    border-left: 3px solid transparent;

    &:hover:not(&--disabled) {
      background: var(--el-fill-color-light);
    }

    &--active {
      color: var(--el-color-primary);
      background: var(--el-color-primary-light-9);
      border-left-color: var(--el-color-primary);
      font-weight: 500;
    }

    &--disabled {
      cursor: not-allowed;
      opacity: 0.5;
    }
  }

  &__arrow {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    height: 16px;
    transition: transform 0.2s ease;
    flex-shrink: 0;

    &--expanded {
      transform: rotate(90deg);
    }
  }

  &__icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    flex-shrink: 0;
  }

  &__label {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__badge {
    flex-shrink: 0;
  }

  &__children {
    overflow: hidden;
  }

  &__child-item {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 12px 8px 40px;
    cursor: pointer;
    color: var(--el-text-color-regular);
    font-size: 13px;
    transition:
      background-color 0.2s,
      color 0.2s;
    user-select: none;
    border-left: 3px solid transparent;

    &:hover:not(&--disabled) {
      background: var(--el-fill-color-light);
    }

    &--active {
      color: var(--el-color-primary);
      background: var(--el-color-primary-light-9);
      border-left-color: var(--el-color-primary);
      font-weight: 500;
    }

    &--disabled {
      cursor: not-allowed;
      opacity: 0.5;
    }
  }

  &__child-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 18px;
    height: 18px;
    flex-shrink: 0;
  }

  &__child-label {
    flex: 1;
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

  &__suffix {
    padding: 8px;
    border-top: 1px solid var(--el-border-color-lighter);
  }

  &__default {
    width: 100%;
  }
}
</style>
