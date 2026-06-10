<template>
  <div
    class="basic-input basic-input--dict-multi"
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
      <el-select
        v-else
        :model-value="innerValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :clearable="clearable"
        :multiple="true"
        :max-collapse-tags="3"
        :size="size"
        :loading="dictLoading"
        collapse-tags
        collapse-tags-tooltip
        @update:model-value="handleInput"
        @change.stop="handleChange"
      >
        <el-option
          v-for="item in dictItems"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
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
  ErpDictMultiSelectProps,
  ErpDictMultiSelectEmits,
  ErpDictMultiSelectExpose,
  SelectOption,
  ValidatorRule
} from '@/types/basic-input'

const props = withDefaults(defineProps<ErpDictMultiSelectProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请选择',
  clearable: true,
  size: 'default'
})

const emit = defineEmits<ErpDictMultiSelectEmits>()

const innerValue = ref(props.modelValue)
const errorMessages = ref<string[]>([])
const dictItems = ref<SelectOption[]>([])
const dictLoading = ref(false)

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

watch(
  () => props.dictCode,
  () => {
    fetchDictItems()
  }
)

const isValid = computed(() => errorMessages.value.length === 0)

const errorMsg = computed(() => errorMessages.value.join('; '))

async function fetchDictItems(): Promise<void> {
  if (!props.dictCode) {
    dictItems.value = []
    return
  }
  dictLoading.value = true
  try {
    const { getDictDataApi } = await import('@/api/modules/system')
    const data = await getDictDataApi(props.dictCode)
    dictItems.value = (data || []).map((item: Record<string, unknown>) => ({
      label: (item.dictLabel || item.label || '') as string,
      value: (item.dictValue || item.value || '') as string | number
    }))
  } catch {
    dictItems.value = []
  } finally {
    dictLoading.value = false
  }
}

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
    const isEmpty =
      value === undefined || value === null || (Array.isArray(value) && value.length === 0)
    if (rule.required && isEmpty) {
      errors.push(rule.message || '请至少选择一项')
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
  innerValue.value = []
  emit('update:modelValue', [])
}

onMounted(() => {
  innerValue.value = props.modelValue
  errorMessages.value = []
  fetchDictItems()
})

onBeforeUnmount(() => {
  errorMessages.value = []
})

defineExpose<ErpDictMultiSelectExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--dict-multi {
  width: 100%;
}
</style>
