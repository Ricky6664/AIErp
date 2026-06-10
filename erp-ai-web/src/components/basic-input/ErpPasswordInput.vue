<template>
  <div
    class="basic-input basic-input--password"
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
        <el-skeleton :rows="1" animated />
      </div>
      <el-input
        v-else
        type="password"
        :model-value="innerValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :maxlength="maxLength"
        :show-password="showPassword"
        :size="size"
        @update:model-value="handleInput"
        @focus.stop="handleFocus"
        @blur.stop="handleBlur"
      />
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
  ErpPasswordInputProps,
  ErpPasswordInputEmits,
  ErpPasswordInputExpose,
  ValidatorRule,
  ErpInputLinkageEvent
} from '@/types/basic-input'
import { useFormLinkage } from '@/composables/useFormLinkage'

const props = withDefaults(defineProps<ErpPasswordInputProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请输入密码',
  showPassword: true,
  size: 'default'
})

const emit = defineEmits<ErpPasswordInputEmits>()

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

defineExpose<ErpPasswordInputExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--password {
  width: 100%;
}
</style>
