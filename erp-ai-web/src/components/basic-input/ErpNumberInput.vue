<template>
  <div
    class="basic-input basic-input--number"
    :class="{
      'basic-input--loading': loading,
      'basic-input--disabled': disabled,
      'basic-input--invalid': !isValid,
      [`basic-input--${size}`]: size && size !== 'default'
    }"
    :title="String(innerValue ?? '')"
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
      <!-- 加载态 -->
      <div v-if="loading" class="basic-input__loading">
        <el-skeleton :rows="1" animated />
      </div>

      <!-- 正常态 / 空态（el-input-number 原生支持空值展示） -->
      <el-input-number
        v-else
        :model-value="innerValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :min="min"
        :max="max"
        :step="step"
        :precision="precision"
        :controls="controls"
        :controls-position="controlsPosition"
        :size="size"
        @update:model-value="handleInput"
        @focus.stop="handleFocus"
        @blur.stop="handleBlur"
      >
        <template v-if="$slots.prefix" #prefix>
          <slot name="prefix" />
        </template>
        <template v-if="$slots.suffix" #suffix>
          <slot name="suffix" />
        </template>
        <template v-if="$slots.append" #append>
          <slot name="append" />
        </template>
      </el-input-number>
    </div>

    <!-- 底部区域：校验错误信息 + 辅助文本 -->
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
  ErpNumberInputProps,
  ErpNumberInputEmits,
  ErpNumberInputExpose,
  ValidatorRule,
  ErpInputLinkageEvent
} from '@/types/basic-input'
import { useFormLinkage } from '@/composables/useFormLinkage'

const props = withDefaults(defineProps<ErpNumberInputProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请输入',
  min: undefined,
  max: undefined,
  step: 1,
  precision: 0,
  controls: true,
  controlsPosition: '',
  size: 'default'
})

const emit = defineEmits<ErpNumberInputEmits>()

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
  const numValue = value !== null && value !== undefined && value !== '' ? Number(value) : NaN
  for (const rule of rules) {
    if (
      rule.required &&
      (value === null || value === undefined || value === '' || isNaN(numValue))
    ) {
      errors.push(rule.message || '此项为必填')
      continue
    }
    if (rule.min !== undefined && !isNaN(numValue) && numValue < rule.min) {
      errors.push(rule.message || `最小值为 ${rule.min}`)
      continue
    }
    if (rule.max !== undefined && !isNaN(numValue) && numValue > rule.max) {
      errors.push(rule.message || `最大值为 ${rule.max}`)
      continue
    }
    if (rule.pattern && typeof rule.pattern === 'object' && 'test' in rule.pattern) {
      const strValue = value !== null && value !== undefined ? String(value) : ''
      if (!rule.pattern.test(strValue)) {
        errors.push(rule.message || '格式不正确')
        continue
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
  innerValue.value = null
  emit('update:modelValue', null)
}

onMounted(() => {
  innerValue.value = props.modelValue
  errorMessages.value = []
})

onBeforeUnmount(() => {
  errorMessages.value = []
})

defineExpose<ErpNumberInputExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--number {
  --bi-label-color: var(--el-text-color-primary, #303133);
  --bi-error-color: var(--el-color-danger, #f56c6c);
  --bi-loading-min-height: 32px;
  --bi-disabled-bg: var(--el-fill-color-light, #f5f7fa);
  --bi-border-color: var(--el-border-color-base, #dcdfe6);

  width: 100%;

  // 加载态
  &--loading {
    .basic-input__content {
      min-height: var(--bi-loading-min-height);
    }
  }

  // 禁用态
  &--disabled {
    opacity: 1;
    cursor: not-allowed;
  }

  // 校验失败态
  &--invalid {
    :deep(.el-input-number) {
      .el-input__wrapper {
        box-shadow: 0 0 0 1px var(--bi-error-color) inset;
      }
    }
  }

  // 尺寸变体
  &--small {
    font-size: var(--el-font-size-small, 12px);
  }

  &--large {
    font-size: var(--el-font-size-large, 16px);
  }

  // 头部区域
  &__header {
    margin-bottom: 4px;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__label {
    font-size: 14px;
    color: var(--bi-label-color);
    line-height: 1.4;
    user-select: none;
  }

  // 内容区域
  &__content {
    position: relative;
    width: 100%;
  }

  // 加载骨架屏
  &__loading {
    min-height: var(--bi-loading-min-height);
    display: flex;
    align-items: center;
  }

  // 空态提示
  &__empty {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    min-height: var(--bi-loading-min-height);
    padding: 0 12px;
    font-size: 14px;
    color: var(--el-text-color-placeholder, #c0c4cc);
    background-color: var(--bi-disabled-bg);
    border: 1px solid var(--bi-border-color);
    border-radius: var(--el-border-radius-base, 4px);
    line-height: 1;
    height: 32px;
    box-sizing: border-box;
  }

  // 底部区域
  &__footer {
    margin-top: 4px;
    min-height: 0;
  }

  // 错误信息列表
  &__errors {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  &__error-item {
    font-size: 12px;
    color: var(--bi-error-color);
    line-height: 1.5;
  }

  // 辅助信息文本
  &__helper-text {
    font-size: 12px;
    color: var(--el-text-color-secondary, #909399);
    line-height: 1.5;
  }
}
</style>
