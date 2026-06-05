<template>
  <div class="header-toolbar" :class="{ 'header-toolbar--disabled': props.disabled }">
    <!-- prefix 插槽 -->
    <div v-if="$slots.prefix" class="header-toolbar__prefix">
      <slot name="prefix" />
    </div>

    <!-- 工具栏按钮组 -->
    <div class="header-toolbar__tools">
      <template v-for="(item, index) in visibleTools" :key="item.key">
        <!-- 行高调整下拉面板 -->
        <el-dropdown
          v-if="item.tool === 'row-height'"
          :disabled="item.disabled || props.disabled"
          trigger="click"
          @command="handleRowHeightChange"
        >
          <el-tooltip
            :content="item.tooltip || '行高设置'"
            :disabled="props.disabled"
            placement="bottom"
          >
            <el-button
              :disabled="item.disabled || props.disabled"
              text
              size="small"
              class="header-toolbar__btn"
              @focus="emit('focus', item.key)"
              @blur="emit('blur', item.key)"
            >
              <el-icon v-if="item.icon" :size="16">
                <component :is="item.icon" />
              </el-icon>
              <span v-if="item.label">{{ item.label }}</span>
              <el-icon class="el-icon--right" :size="12">
                <ArrowDown />
              </el-icon>
            </el-button>
          </el-tooltip>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="option in rowHeightOptions"
                :key="option.key"
                :command="option.key"
                :class="{ 'is-active': localState.rowHeight === option.key }"
              >
                <span>{{ option.label }}</span>
                <el-icon v-if="localState.rowHeight === option.key" class="header-toolbar__check">
                  <Check />
                </el-icon>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 普通工具按钮 -->
        <el-tooltip
          v-else
          :content="item.tooltip || ''"
          :disabled="!item.tooltip || props.disabled"
          placement="bottom"
        >
          <el-button
            :disabled="item.disabled || props.disabled"
            :type="getButtonType(item)"
            text
            size="small"
            class="header-toolbar__btn"
            @click="handleToolClick(item)"
            @focus="emit('focus', item.key)"
            @blur="emit('blur', item.key)"
          >
            <el-icon v-if="getIcon(item)" :size="16">
              <component :is="getIcon(item)" />
            </el-icon>
            <span v-if="item.label">{{ item.label }}</span>
          </el-button>
        </el-tooltip>

        <!-- 分隔线 -->
        <el-divider
          v-if="item.showDivider && index < visibleTools.length - 1"
          direction="vertical"
          class="header-toolbar__divider"
        />
      </template>
    </div>

    <!-- suffix 插槽 -->
    <div v-if="$slots.suffix" class="header-toolbar__suffix">
      <slot name="suffix" />
    </div>

    <!-- default 插槽 -->
    <div v-if="$slots.default" class="header-toolbar__default">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ArrowDown, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { HeaderToolbarState, HeaderToolbarItem, RowHeightPreset } from '@/types/action-bar'

const ROW_HEIGHT_PRESETS: { key: RowHeightPreset; label: string; height: number }[] = [
  { key: 'compact', label: '紧凑', height: 32 },
  { key: 'small', label: '较小', height: 36 },
  { key: 'default', label: '默认', height: 44 },
  { key: 'large', label: '较大', height: 52 },
  { key: 'extra-large', label: '超大', height: 60 }
]

const props = withDefaults(
  defineProps<{
    modelValue: HeaderToolbarState
    fieldConfig?: HeaderToolbarItem[]
    disabled?: boolean
    placeholder?: string
  }>(),
  {
    fieldConfig: () => [],
    disabled: false,
    placeholder: ''
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: HeaderToolbarState]
  change: [tool: string, state: HeaderToolbarState]
  focus: [key: string]
  blur: [key: string]
}>()

const localState = ref<HeaderToolbarState>({
  maximized: props.modelValue.maximized ?? false,
  rowHeight: props.modelValue.rowHeight ?? 'default',
  formatSettingsVisible: false
})

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      localState.value = {
        maximized: val.maximized ?? localState.value.maximized,
        rowHeight: val.rowHeight ?? localState.value.rowHeight,
        formatSettingsVisible: val.formatSettingsVisible ?? localState.value.formatSettingsVisible
      }
    }
  },
  { deep: true }
)

function syncState(): void {
  emit('update:modelValue', { ...localState.value })
}

const visibleTools = computed<HeaderToolbarItem[]>(() => {
  const config = props.fieldConfig
  if (config && config.length > 0) {
    return config.filter((item) => !item.hidden)
  }
  return getDefaultTools()
})

function getDefaultTools(): HeaderToolbarItem[] {
  return [
    { key: 'maximize', tool: 'maximize', label: '铺满', tooltip: '铺满/还原', icon: 'FullScreen' },
    { key: 'refresh', tool: 'refresh', label: '刷新', tooltip: '刷新数据', icon: 'Refresh' },
    {
      key: 'format-settings',
      tool: 'format-settings',
      label: '格式设置',
      tooltip: '列格式设置',
      icon: 'Setting'
    },
    {
      key: 'row-height',
      tool: 'row-height',
      label: '行高',
      tooltip: '行高调整',
      icon: 'Rank',
      showDivider: false
    }
  ]
}

const rowHeightOptions = computed(() => ROW_HEIGHT_PRESETS)

function getButtonType(item: HeaderToolbarItem): string {
  if (item.tool === 'maximize' && localState.value.maximized) return 'primary'
  return 'default'
}

function getIcon(item: HeaderToolbarItem): string {
  if (item.icon) return item.icon
  if (item.tool === 'maximize') return localState.value.maximized ? 'Minus' : 'FullScreen'
  if (item.tool === 'refresh') return 'Refresh'
  if (item.tool === 'format-settings') return 'Setting'
  return ''
}

async function handleToolClick(item: HeaderToolbarItem): Promise<void> {
  try {
    if (props.disabled || item.disabled) return

    const tool = item.tool || item.key

    switch (tool) {
      case 'maximize':
        localState.value.maximized = !localState.value.maximized
        syncState()
        emit('change', 'maximize', { ...localState.value })
        break
      case 'refresh':
        syncState()
        emit('change', 'refresh', { ...localState.value })
        break
      case 'format-settings':
        localState.value.formatSettingsVisible = !localState.value.formatSettingsVisible
        syncState()
        emit('change', 'format-settings', { ...localState.value })
        break
      default:
        syncState()
        emit('change', item.key, { ...localState.value })
    }
  } catch (err) {
    console.error('[HeaderToolbar] tool click error:', err)
    ElMessage.error('操作执行失败')
  }
}

function handleRowHeightChange(height: RowHeightPreset): void {
  try {
    localState.value.rowHeight = height
    syncState()
    emit('change', 'row-height', { ...localState.value })
  } catch (err) {
    console.error('[HeaderToolbar] row height change error:', err)
    ElMessage.error('行高设置失败')
  }
}
</script>

<style scoped lang="scss">
.header-toolbar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-lighter);
  min-height: 36px;

  &--disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  &__prefix {
    display: flex;
    align-items: center;
    margin-right: 8px;
  }

  &__tools {
    display: flex;
    align-items: center;
    gap: 2px;
    flex-wrap: wrap;
  }

  &__btn {
    font-size: 13px;
    color: var(--el-text-color-regular);
    transition: color 0.2s;

    &:hover {
      color: var(--el-color-primary);
    }
  }

  &__divider {
    height: 16px;
    margin: 0 4px;
  }

  &__check {
    margin-left: auto;
    color: var(--el-color-primary);
  }

  &__suffix {
    display: flex;
    align-items: center;
    margin-left: 8px;
  }

  &__default {
    flex: 1;
    min-width: 0;
  }
}

.is-active {
  color: var(--el-color-primary);
  font-weight: 500;
}
</style>
