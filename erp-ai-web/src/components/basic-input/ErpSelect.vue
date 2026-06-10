<template>
  <div
    class="basic-input basic-input--select"
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
        :multiple="multiple"
        :filterable="filterable"
        :clearable="clearable"
        :remote="remote"
        :remote-method="remote ? handleRemoteSearch : undefined"
        :size="size"
        @update:model-value="handleInput"
        @change.stop="handleChange"
        @focus.stop="handleFocus"
        @blur.stop="handleBlur"
      >
        <el-option
          v-for="opt in resolvedOptions"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
          :disabled="opt.disabled"
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
  ErpSelectProps,
  ErpSelectEmits,
  ErpSelectExpose,
  SelectOption,
  ValidatorRule,
  ErpInputLinkageEvent
} from '@/types/basic-input'
import { useFormLinkage } from '@/composables/useFormLinkage'

const props = withDefaults(defineProps<ErpSelectProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请选择',
  options: () => [],
  multiple: false,
  filterable: false,
  clearable: true,
  remote: false,
  size: 'default'
})

const emit = defineEmits<ErpSelectEmits>()

const innerValue = ref(props.modelValue)
const errorMessages = ref<string[]>([])
const lastEmittedValue = ref(props.modelValue)
const remoteOptions = ref<SelectOption[]>([])

const { evaluateCondition } = useFormLinkage()

const resolvedOptions = computed(() => {
  if (props.remote) return remoteOptions.value
  return props.options || []
})

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

async function handleRemoteSearch(query: string): Promise<void> {
  if (props.remoteMethod) {
    remoteOptions.value = await props.remoteMethod(query)
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
    if (rule.required) {
      const isEmpty =
        value === undefined ||
        value === null ||
        value === '' ||
        (Array.isArray(value) && value.length === 0)
      if (isEmpty) {
        errors.push(rule.message || '此项为必填')
      }
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

defineExpose<ErpSelectExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--select {
  width: 100%;
}
</style>
