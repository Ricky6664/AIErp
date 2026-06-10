<template>
  <div
    class="basic-input basic-input--textarea"
    :class="{
      'basic-input--loading': loading,
      'basic-input--disabled': disabled,
      'basic-input--invalid': !isValid,
      [`basic-input--${size}`]: size && size !== 'default',
      'basic-input--resize-none': resize === 'none',
      'basic-input--resize-horizontal': resize === 'horizontal',
      'basic-input--resize-vertical': resize === 'vertical'
    }"
    :title="displayValue"
  >
    <!-- 头部区域：标题 -->
    <div v-if="fieldConfig?.title || $slots.header" class="basic-input__header">
      <label v-if="fieldConfig?.title" class="basic-input__label">
        {{ fieldConfig.title }}
      </label>
      <slot name="header" />
    </div>

    <!-- 内容区域 -->
    <div class="basic-input__content">
      <div v-if="loading" class="basic-input__loading">
        <el-skeleton :rows="3" animated />
      </div>
      <el-input
        v-else
        type="textarea"
        :model-value="innerValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :clearable="clearable"
        :maxlength="maxLength"
        :show-word-limit="showWordLimit"
        :rows="rows"
        :resize="resize"
        :size="size"
        @update:model-value="handleInput"
        @focus.stop="handleFocus"
        @blur.stop="handleBlur"
      />
      <!-- 前缀/后缀/追加插槽 — el-input textarea 不支持这些原生插槽，在外部渲染 -->
      <span v-if="$slots.prefix" class="basic-input__slot-prefix">
        <slot name="prefix" />
      </span>
      <span v-if="$slots.suffix" class="basic-input__slot-suffix">
        <slot name="suffix" />
      </span>
      <span v-if="$slots.append" class="basic-input__slot-append">
        <slot name="append" />
      </span>
    </div>

    <!-- 底部区域：校验错误信息 -->
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
  ErpTextareaProps,
  ErpTextareaEmits,
  ErpTextareaExpose,
  ValidatorRule,
  ErpInputLinkageEvent
} from '@/types/basic-input'
import { useFormLinkage } from '@/composables/useFormLinkage'

const props = withDefaults(defineProps<ErpTextareaProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请输入',
  clearable: true,
  showWordLimit: false,
  rows: 3,
  resize: 'vertical',
  size: 'default'
})

const emit = defineEmits<ErpTextareaEmits>()

const innerValue = ref(props.modelValue)
const errorMessages = ref<string[]>([])
const lastEmittedValue = ref(props.modelValue)

const { evaluateCondition } = useFormLinkage()

watch(
  () => props.modelValue,
  (val) => {
    innerValue.value = val
    lastEmittedValue.value = val
  }
)

watch(
  () => props.fieldConfig,
  () => {
    errorMessages.value = []
  }
)

watch(innerValue, (newValue, oldValue) => {
  if (newValue === oldValue) return
  const linkages = props.fieldConfig?.linkages
  if (!linkages || linkages.length === 0) return

  const triggeredLinkages = linkages.filter(
    (linkage) =>
      linkage.triggerField === props.fieldConfig?.field && evaluateCondition(linkage, newValue)
  )

  if (triggeredLinkages.length > 0) {
    const event: ErpInputLinkageEvent = {
      field: props.fieldConfig?.field ?? '',
      value: newValue,
      linkages: triggeredLinkages
    }
    emit('linkage', event)
  }
})

const displayValue = computed(() => {
  if (innerValue.value === null || innerValue.value === undefined) return ''
  return String(innerValue.value)
})

const isValid = computed(() => errorMessages.value.length === 0)

const errorMsg = computed(() => errorMessages.value.join('; '))

function handleInput(value: unknown): void {
  innerValue.value = value
  errorMessages.value = []
  emit('update:modelValue', value)
}

function handleFocus(event: FocusEvent): void {
  emit('focus', event)
}

function handleBlur(event: FocusEvent): void {
  const currentValue = innerValue.value
  if (currentValue !== lastEmittedValue.value) {
    emit('change', currentValue)
    lastEmittedValue.value = currentValue
  }
  emit('blur', event)
}

function collectErrors(value: unknown, rules?: ValidatorRule[]): string[] {
  if (!rules || rules.length === 0) return []
  const errors: string[] = []
  for (const rule of rules) {
    if (rule.required && (value === undefined || value === null || value === '')) {
      errors.push(rule.message || '此项为必填')
      continue
    }
    if (rule.min !== undefined && typeof value === 'string' && value.length < rule.min) {
      errors.push(rule.message || `最少 ${rule.min} 个字符`)
      continue
    }
    if (rule.max !== undefined && typeof value === 'string' && value.length > rule.max) {
      errors.push(rule.message || `最多 ${rule.max} 个字符`)
      continue
    }
    if (rule.pattern && typeof value === 'string' && !rule.pattern.test(value)) {
      errors.push(rule.message || '格式不正确')
      continue
    }
  }
  return errors
}

async function runCustomValidators(value: unknown, rules?: ValidatorRule[]): Promise<string[]> {
  if (!rules || rules.length === 0) return []
  const errors: string[] = []
  for (const rule of rules) {
    if (rule.validator) {
      try {
        const result = await rule.validator(value)
        if (!result) {
          errors.push(rule.message || '校验不通过')
        }
      } catch {
        errors.push(rule.message || '校验异常')
      }
    }
  }
  return errors
}

async function validate(): Promise<boolean> {
  let errors: string[] = collectErrors(innerValue.value, props.rules)
  const customErrors = await runCustomValidators(innerValue.value, props.rules)
  errors = [...errors, ...customErrors]

  errorMessages.value = errors
  const isValid = errors.length === 0
  emit('validate', isValid)
  return isValid
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

defineExpose<ErpTextareaExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--textarea {
  :deep(.el-textarea__inner) {
    transition: resize 0.01s;
  }
}

.basic-input--resize-none {
  :deep(.el-textarea__inner) {
    resize: none;
  }
}

.basic-input--resize-horizontal {
  :deep(.el-textarea__inner) {
    resize: horizontal;
  }
}

.basic-input--resize-vertical {
  :deep(.el-textarea__inner) {
    resize: vertical;
  }
}
</style>
