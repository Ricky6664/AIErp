<template>
  <div
    class="action-bar"
    :class="[`action-bar--${props.mode}`, { 'action-bar--disabled': props.disabled }]"
  >
    <!-- prefix 插槽 -->
    <div v-if="$slots.prefix" class="action-bar__prefix">
      <slot name="prefix" />
    </div>

    <!-- 左侧操作按钮组（表单模式下隐藏，所有按钮放右侧） -->
    <div v-if="props.mode === 'list'" class="action-bar__left">
      <template v-for="item in leftActions" :key="item.key">
        <!-- 下拉菜单按钮 -->
        <el-dropdown
          v-if="item.children && item.children.length > 0"
          :disabled="item.disabled || props.disabled"
          trigger="click"
          @command="(cmd: string) => handleDropdownCommand(item, cmd)"
        >
          <el-button
            :type="item.type || 'default'"
            :disabled="item.disabled || props.disabled"
            :loading="item.loading"
            v-bind="getButtonProps(item)"
          >
            {{ item.label }}
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="child in item.children"
                :key="child.key"
                :command="child.key"
                :disabled="child.disabled || props.disabled"
              >
                {{ child.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 普通按钮 -->
        <el-button
          v-else
          :type="item.type || 'default'"
          :disabled="item.disabled || props.disabled"
          :loading="item.loading"
          v-bind="getButtonProps(item)"
          @click="handleActionClick(item)"
        >
          {{ item.label }}
        </el-button>
      </template>
    </div>

    <!-- 右侧操作按钮组 -->
    <div class="action-bar__right" :class="{ 'action-bar__right--form': props.mode === 'form' }">
      <template v-for="item in allActions" :key="item.key">
        <el-dropdown
          v-if="item.children && item.children.length > 0"
          :disabled="item.disabled || props.disabled"
          trigger="click"
          @command="(cmd: string) => handleDropdownCommand(item, cmd)"
        >
          <el-button
            :type="item.type || 'default'"
            :disabled="item.disabled || props.disabled"
            :loading="item.loading"
            v-bind="getButtonProps(item)"
          >
            {{ item.label }}
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="child in item.children"
                :key="child.key"
                :command="child.key"
                :disabled="child.disabled || props.disabled"
              >
                {{ child.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <el-button
          v-else
          :type="item.type || 'default'"
          :disabled="item.disabled || props.disabled"
          :loading="item.loading"
          v-bind="getButtonProps(item)"
          @click="handleActionClick(item)"
        >
          {{ item.label }}
        </el-button>
      </template>
    </div>

    <!-- suffix 插槽 -->
    <div v-if="$slots.suffix" class="action-bar__suffix">
      <slot name="suffix" />
    </div>

    <!-- default 插槽 -->
    <div v-if="$slots.default" class="action-bar__default">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ActionItem, ActionBarMode } from '@/types/action-bar'

const props = withDefaults(
  defineProps<{
    modelValue: Record<string, unknown>
    fieldConfig: ActionItem[]
    disabled?: boolean
    placeholder?: string
    mode?: ActionBarMode
  }>(),
  {
    disabled: false,
    placeholder: '',
    mode: 'list'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: Record<string, unknown>]
  change: [action: string, item: ActionItem]
  focus: [key: string]
  blur: [key: string]
}>()

/** 左侧操作按钮（primary/danger 等强调类型 + 前一半默认类型） */
const leftActions = computed<ActionItem[]>(() => {
  const visible = props.fieldConfig.filter((item) => !item.hidden)
  const primaryActions = visible.filter((item) => item.type && item.type !== 'default')
  const defaultActions = visible.filter((item) => !item.type || item.type === 'default')
  const midPoint = Math.ceil(defaultActions.length / 2)
  return [...primaryActions, ...defaultActions.slice(0, midPoint)]
})

/** 右侧操作按钮（后一半默认类型） */
const rightActions = computed<ActionItem[]>(() => {
  const visible = props.fieldConfig.filter((item) => !item.hidden)
  const defaultActions = visible.filter((item) => !item.type || item.type === 'default')
  const midPoint = Math.ceil(defaultActions.length / 2)
  return defaultActions.slice(midPoint)
})

/** 表单模式：全部可见按钮（右侧对齐布局） */
const allActions = computed<ActionItem[]>(() => {
  if (props.mode === 'form') {
    return props.fieldConfig.filter((item) => !item.hidden)
  }
  return rightActions.value
})

function getButtonProps(item: ActionItem): Record<string, unknown> {
  const bindings: Record<string, unknown> = {}
  if (item.tooltip) {
    bindings.title = item.tooltip
  }
  return bindings
}

async function handleActionClick(item: ActionItem): Promise<void> {
  try {
    if (props.disabled || item.disabled) return

    if (item.confirm) {
      try {
        await ElMessageBox.confirm(item.confirm.message, item.confirm.title, {
          confirmButtonText: item.confirm.confirmText || '确定',
          cancelButtonText: item.confirm.cancelText || '取消',
          type: 'warning'
        })
      } catch {
        return
      }
    }

    emit('change', item.action || item.key, item)
    emit('update:modelValue', { ...props.modelValue, [item.key]: true })
  } catch (err) {
    console.error('[ActionBar] action error:', err)
    ElMessage.error('操作执行失败')
  }
}

function handleDropdownCommand(parent: ActionItem, childKey: string): void {
  try {
    const child = parent.children?.find((c) => c.key === childKey)
    if (!child) return
    emit('change', child.action || child.key, child)
    emit('update:modelValue', { ...props.modelValue, [child.key]: true })
  } catch (err) {
    console.error('[ActionBar] dropdown error:', err)
    ElMessage.error('操作执行失败')
  }
}
</script>

<style scoped lang="scss">
.action-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);

  &--disabled {
    opacity: 0.6;
    pointer-events: none;
  }

  &--form {
    justify-content: flex-end;
  }

  &__prefix {
    margin-right: 4px;
  }

  &__left {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 0 0 auto;
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-left: auto;
    flex: 0 0 auto;

    &--form {
      margin-left: 0;
    }
  }

  &__suffix {
    margin-left: 4px;
  }

  &__default {
    width: 100%;
  }
}
</style>
