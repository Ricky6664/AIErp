<template>
  <div
    class="basic-input basic-input--tree-select"
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
      <el-tree-select
        v-else
        :model-value="innerValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :data="treeData"
        :multiple="multiple"
        :filterable="filterable"
        :clearable="clearable"
        :check-strictly="checkStrictly"
        :show-checkbox="showCheckbox"
        :size="size"
        default-expand-all
        check-on-click-node
        @update:model-value="handleInput"
        @change.stop="handleChange"
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
  ErpTreeSelectProps,
  ErpTreeSelectEmits,
  ErpTreeSelectExpose,
  ValidatorRule
} from '@/types/basic-input'

const props = withDefaults(defineProps<ErpTreeSelectProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请选择',
  treeData: () => [],
  multiple: false,
  filterable: true,
  clearable: true,
  checkStrictly: false,
  showCheckbox: false,
  size: 'default'
})

const emit = defineEmits<ErpTreeSelectEmits>()

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
    const isEmpty =
      value === undefined ||
      value === null ||
      value === '' ||
      (Array.isArray(value) && value.length === 0)
    if (rule.required && isEmpty) {
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
  innerValue.value = props.multiple ? [] : ''
  emit('update:modelValue', props.multiple ? [] : '')
}

onMounted(() => {
  innerValue.value = props.modelValue
  errorMessages.value = []
})

onBeforeUnmount(() => {
  errorMessages.value = []
})

defineExpose<ErpTreeSelectExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--tree-select {
  width: 100%;
}
</style>
