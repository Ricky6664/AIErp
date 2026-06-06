<template>
  <div
    v-if="visible"
    class="erp-field-renderer"
    :class="{ 'erp-field-renderer--loading': loading }"
  >
    <!-- 加载态 -->
    <div v-if="loading" class="erp-field-renderer__loading">
      <el-skeleton :rows="1" animated />
    </div>

    <!-- 动态渲染字段组件 -->
    <component
      :is="resolvedComponent"
      v-else
      ref="fieldRef"
      v-bind="componentBindings"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @linkage="handleLinkage"
      @focus="handleFocus"
      @blur="handleBlur"
    >
      <template v-if="$slots.prefix" #prefix>
        <slot name="prefix" />
      </template>
      <template v-if="$slots.suffix" #suffix>
        <slot name="suffix" />
      </template>
      <template v-if="$slots.default" #default>
        <slot />
      </template>
      <template v-if="$slots.append" #append>
        <slot name="append" />
      </template>
    </component>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type {
  ErpFieldRendererProps,
  ErpFieldRendererEmits,
  ErpFieldRendererExpose,
  FieldTypeComponentMapping,
  ErpInputLinkageEvent
} from '@/types/basic-input'
import ErpInput from './index.vue'
import ErpTextarea from './ErpTextarea.vue'

const FIELD_TYPE_MAP: FieldTypeComponentMapping = {
  text: 'ErpInput',
  textarea: 'ErpTextarea'
}

const props = withDefaults(defineProps<ErpFieldRendererProps>(), {
  disabled: false,
  visible: true,
  loading: false,
  placeholder: '请输入',
  size: 'default'
})

const emit = defineEmits<ErpFieldRendererEmits>()

const fieldRef = ref<InstanceType<typeof ErpInput> | null>(null)

const resolvedComponent = computed(() => {
  const fieldType = props.fieldConfig?.fieldType || 'text'
  const componentName = FIELD_TYPE_MAP[fieldType]
  if (componentName === 'ErpTextarea') return ErpTextarea
  return ErpInput
})

const componentBindings = computed(() => ({
  modelValue: props.modelValue,
  fieldConfig: props.fieldConfig,
  disabled: props.disabled,
  loading: false,
  placeholder: props.placeholder,
  size: props.size
}))

function handleUpdate(value: unknown): void {
  emit('update:modelValue', value)
}

function handleChange(value: unknown): void {
  emit('change', value)
}

function handleLinkage(event: ErpInputLinkageEvent): void {
  emit('linkage', event)
}

function handleFocus(event: FocusEvent): void {
  emit('focus', event)
}

function handleBlur(event: FocusEvent): void {
  emit('blur', event)
}

async function validate(): Promise<boolean> {
  if (!props.visible) return true
  try {
    const child = fieldRef.value as { validate?: () => Promise<boolean> } | null
    if (child && typeof child.validate === 'function') {
      return await child.validate()
    }
    return true
  } catch {
    return false
  }
}

function reset(): void {
  if (!props.visible) return
  try {
    const child = fieldRef.value as { reset?: () => void } | null
    if (child && typeof child.reset === 'function') {
      child.reset()
    }
  } catch {
    // 静默处理
  }
}

defineExpose<ErpFieldRendererExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.erp-field-renderer {
  width: 100%;

  &--loading {
    .erp-field-renderer__loading {
      min-height: 32px;
      display: flex;
      align-items: center;
    }
  }
}
</style>
