<template>
  <div
    class="basic-input basic-input--rich-editor"
    :class="{
      'basic-input--loading': loading,
      'basic-input--disabled': disabled,
      'basic-input--invalid': !isValid,
      [`basic-input--${size}`]: size && size !== 'default'
    }"
  >
    <div v-if="fieldConfig?.title || $slots.header" class="basic-input__header">
      <label v-if="fieldConfig?.title" class="basic-input__label">
        {{ fieldConfig.title }}
      </label>
      <slot name="header" />
    </div>

    <div class="basic-input__content">
      <div v-if="loading" class="basic-input__loading">
        <el-skeleton :rows="5" animated />
      </div>
      <div
        v-else
        class="basic-input__editor-wrapper"
        :style="{ minHeight: (height || 300) + 'px' }"
      >
        <!-- 简化版：使用 el-input textarea 作为富文本的降级方案 -->
        <!-- 正式环境接入 TinyMCE 时替换此处 -->
        <el-input
          type="textarea"
          :model-value="innerValue"
          :placeholder="placeholder"
          :disabled="disabled"
          :rows="Math.ceil((height || 300) / 22)"
          @update:model-value="handleInput"
          @blur="handleBlur"
        />
      </div>
    </div>

    <div
      v-if="errorMessages.length > 0 || $slots.footer"
      class="basic-input__footer"
      :title="errorMsg"
    >
      <ul v-if="errorMessages.length > 0" class="basic-input__errors">
        <li v-for="(msg, index) in errorMessages" :key="index" class="basic-input__error-item">
          {{ msg }}
        </li>
      </ul>
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onBeforeUnmount } from 'vue'
import type {
  ErpRichEditorProps,
  ErpRichEditorEmits,
  ErpRichEditorExpose,
  ValidatorRule
} from '@/types/basic-input'

const props = withDefaults(defineProps<ErpRichEditorProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请输入内容',
  height: 300,
  size: 'default'
})

const emit = defineEmits<ErpRichEditorEmits>()

const innerValue = ref(props.modelValue)
const errorMessages = ref<string[]>([])

watch(
  () => props.modelValue,
  (val) => {
    innerValue.value = val
  }
)

watch(
  () => props.fieldConfig,
  () => {
    errorMessages.value = []
  }
)

const isValid = computed(() => errorMessages.value.length === 0)

const errorMsg = computed(() => errorMessages.value.join('; '))

function handleInput(value: unknown): void {
  innerValue.value = value
  errorMessages.value = []
  emit('update:modelValue', value)
}

function handleBlur(): void {
  emit('change', innerValue.value)
}

function collectErrors(value: unknown, rules?: ValidatorRule[]): string[] {
  if (!rules || rules.length === 0) return []
  const errors: string[] = []
  for (const rule of rules) {
    if (rule.required && (value === undefined || value === null || value === '')) {
      errors.push(rule.message || '此项为必填')
    }
  }
  return errors
}

async function validate(): Promise<boolean> {
  const errors = collectErrors(innerValue.value, props.rules)
  errorMessages.value = errors
  const valid = errors.length === 0
  emit('validate', valid)
  return valid
}

function reset(): void {
  errorMessages.value = []
  innerValue.value = ''
  emit('update:modelValue', '')
}

onMounted(() => {
  innerValue.value = props.modelValue
  errorMessages.value = []
})

onBeforeUnmount(() => {
  errorMessages.value = []
})

defineExpose<ErpRichEditorExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--rich-editor {
  width: 100%;

  &__editor-wrapper {
    border: 1px solid var(--el-border-color, #dcdfe6);
    border-radius: var(--el-border-radius-base, 4px);
    overflow: hidden;

    :deep(.el-textarea__inner) {
      border: none;
      resize: vertical;
      min-height: 200px;
    }
  }
}
</style>
