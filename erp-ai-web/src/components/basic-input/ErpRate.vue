<template>
  <div
    class="basic-input basic-input--rate"
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
      <div v-else class="basic-input__rate-row">
        <el-rate
          :model-value="innerValue"
          :disabled="disabled"
          :max="maxStars"
          :show-score="showScore"
          :allow-half="allowHalf"
          :texts="texts"
          :size="size"
          @update:model-value="handleInput"
          @change.stop="handleChange"
        />
        <span v-if="showScore && innerValue" class="basic-input__rate-score"
          >{{ innerValue }} 分</span
        >
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
import type { ErpRateProps, ErpRateEmits, ErpRateExpose, ValidatorRule } from '@/types/basic-input'

const props = withDefaults(defineProps<ErpRateProps>(), {
  disabled: false,
  loading: false,
  maxStars: 5,
  showScore: false,
  allowHalf: false,
  size: 'default'
})

const emit = defineEmits<ErpRateEmits>()

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

function handleChange(value: unknown): void {
  emit('change', value)
}

function collectErrors(value: unknown, rules?: ValidatorRule[]): string[] {
  if (!rules || rules.length === 0) return []
  const errors: string[] = []
  for (const rule of rules) {
    if (rule.required && (value === undefined || value === null || value === 0)) {
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
  innerValue.value = 0
  emit('update:modelValue', 0)
}

onMounted(() => {
  innerValue.value = props.modelValue
  errorMessages.value = []
})

onBeforeUnmount(() => {
  errorMessages.value = []
})

defineExpose<ErpRateExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--rate {
  width: 100%;

  &__rate-row {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__rate-score {
    font-size: 14px;
    color: var(--el-color-warning, #e6a23c);
    white-space: nowrap;
  }
}
</style>
