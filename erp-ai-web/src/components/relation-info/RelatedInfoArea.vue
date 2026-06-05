<template>
  <div class="related-info-area" :class="{ 'related-info-area--disabled': props.disabled }">
    <!-- 左侧分组导航栏 -->
    <div class="related-info-area__nav">
      <slot name="nav-prefix" />
      <GroupNav
        v-model="activeGroup"
        :field-config="props.groups"
        :disabled="props.disabled"
        :placeholder="props.placeholder"
        @focus="emit('focus')"
        @blur="emit('blur')"
      />
      <slot name="nav-suffix" />
    </div>

    <!-- 右侧标签页容器 -->
    <div class="related-info-area__main">
      <TabContainer
        v-model="activeTabModel"
        :tabs="props.tabs"
        :active-group="activeGroup"
        :disabled="props.disabled"
        :placeholder="props.placeholder"
        @focus="emit('focus')"
        @blur="emit('blur')"
      >
        <template v-if="$slots.prefix" #prefix>
          <slot name="prefix" />
        </template>
        <slot />
        <template v-if="$slots.suffix" #suffix>
          <slot name="suffix" />
        </template>
      </TabContainer>
    </div>
  </div>
</template>

<script lang="ts">
import type { InjectionKey } from 'vue'
import type { RelatedInfoRefreshContext } from '@/types/relation-info'

/** 数据刷新上下文注入键（子组件通过此 key 注入刷新信号） */
export const RELATED_INFO_REFRESH_KEY: InjectionKey<RelatedInfoRefreshContext> =
  Symbol('relatedInfoRefresh')
</script>

<script setup lang="ts">
import { ref, computed, watch, provide, toRef } from 'vue'
import GroupNav from '@/components/relation-info/GroupNav.vue'
import TabContainer from '@/components/relation-info/TabContainer.vue'
import type { NavGroup, RelatedTab } from '@/types/relation-info'

// ============================================================
// Props
// ============================================================
const props = withDefaults(
  defineProps<{
    modelValue?: string
    mainRow?: Record<string, unknown> | null
    groups?: NavGroup[]
    tabs?: RelatedTab[]
    disabled?: boolean
    placeholder?: string
    viewCode?: string
  }>(),
  {
    modelValue: '',
    mainRow: null,
    groups: () => [],
    tabs: () => [],
    disabled: false,
    placeholder: '',
    viewCode: ''
  }
)

// ============================================================
// Emits
// ============================================================
const emit = defineEmits<{
  'update:modelValue': [key: string]
  change: [key: string]
  focus: []
  blur: []
}>()

// ============================================================
// 数据刷新机制
// ============================================================

/** 各标签页的独立刷新计数（key → 计数） */
const tabRefreshKeys = ref<Record<string, number>>({})

/** 全局刷新计数（refreshAllTabs 调用时递增） */
const globalRefreshKey = ref(0)

/** 主行数据（转为 Ref 供子组件注入后 watch） */
const mainRowRef = toRef(props, 'mainRow')

/** 向子组件提供数据刷新上下文 */
provide(RELATED_INFO_REFRESH_KEY, {
  tabRefreshKeys,
  globalRefreshKey,
  mainRow: mainRowRef
})

/** 监听主行数据变更 → 自动刷新当前标签页 */
watch(mainRowRef, (newVal, oldVal) => {
  if (newVal !== oldVal && newVal != null && props.modelValue) {
    try {
      const key = props.modelValue
      tabRefreshKeys.value = {
        ...tabRefreshKeys.value,
        [key]: (tabRefreshKeys.value[key] || 0) + 1
      }
    } catch (err) {
      console.error('[RelatedInfoArea] mainRow watch error:', err)
    }
  }
})

// ============================================================
// 激活状态
// ============================================================

/** 当前激活的分组 key（内部状态，不对外暴露为 v-model） */
const activeGroup = ref<string>('')

/** v-model 计算属性：当前激活的标签页 key */
const activeTabModel = computed({
  get: () => props.modelValue || '',
  set: (val: string) => {
    try {
      emit('update:modelValue', val)
      emit('change', val)
    } catch (err) {
      console.error('[RelatedInfoArea] activeTab set error:', err)
    }
  }
})

/**
 * 分组切换时，若当前标签页不在新分组内，则自动选中第一个可见标签页
 */
watch(activeGroup, (newGroup) => {
  try {
    if (!props.tabs || props.tabs.length === 0) return

    const firstVisible = props.tabs.find(
      (t) => !t.hidden && !t.disabled && (newGroup ? t.group === newGroup : true)
    )

    if (firstVisible) {
      const currentTabInGroup =
        !newGroup || props.tabs.some((t) => t.key === props.modelValue && t.group === newGroup)
      if (!currentTabInGroup && firstVisible.key !== props.modelValue) {
        activeTabModel.value = firstVisible.key
      }
    }
  } catch (err) {
    console.error('[RelatedInfoArea] activeGroup watch error:', err)
  }
})

// ============================================================
// 暴露方法
// ============================================================

/** 刷新当前激活的标签页数据 */
function refreshCurrentTab(): void {
  try {
    const key = props.modelValue
    if (!key) return
    tabRefreshKeys.value = {
      ...tabRefreshKeys.value,
      [key]: (tabRefreshKeys.value[key] || 0) + 1
    }
  } catch (err) {
    console.error('[RelatedInfoArea] refreshCurrentTab error:', err)
  }
}

/** 刷新所有标签页数据 */
function refreshAllTabs(): void {
  try {
    globalRefreshKey.value++
  } catch (err) {
    console.error('[RelatedInfoArea] refreshAllTabs error:', err)
  }
}

/** 获取当前主行数据 */
function getMainRow(): Record<string, unknown> | null {
  return props.mainRow ?? null
}

defineExpose({
  refreshCurrentTab,
  refreshAllTabs,
  getMainRow
})
</script>

<style scoped lang="scss">
.related-info-area {
  display: flex;
  width: 100%;
  height: 100%;
  background: var(--el-bg-color);
  overflow: hidden;

  &--disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  // ---- 左侧分组导航栏 ----
  &__nav {
    display: flex;
    flex-direction: column;
    width: 200px;
    min-width: 160px;
    flex-shrink: 0;
    border-right: 1px solid var(--el-border-color-light);
    overflow: hidden;
  }

  // ---- 右侧标签页容器 ----
  &__main {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-width: 0;
    overflow: hidden;
  }
}
</style>
