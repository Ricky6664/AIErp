<template>
  <div
    class="basic-input"
    :class="{
      'basic-input--loading': loading,
      'basic-input--disabled': disabled,
      [`basic-input--${size}`]: size && size !== 'default'
    }"
  >
    <!-- 头部区域：标题 + 操作按钮 -->
    <div v-if="fieldConfig?.title || $slots.header" class="basic-input__header">
      <label v-if="fieldConfig?.title" class="basic-input__label">
        {{ fieldConfig.title }}
      </label>
      <slot name="header" />
    </div>

    <!-- 内容区域：主要功能渲染区 -->
    <div class="basic-input__content">
      <!-- 加载态 -->
      <div v-if="loading" class="basic-input__loading">
        <el-skeleton :rows="1" animated />
      </div>
      <!-- 空态 / 正常态 -->
      <el-input
        v-else
        v-model="localValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :clearable="clearable"
        :maxlength="maxLength"
        :show-word-limit="showWordLimit"
        :size="size"
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
      </el-input>
    </div>

    <!-- 底部区域：校验错误信息 / 辅助信息 -->
    <div v-if="errorMessages.length > 0 || $slots.footer" class="basic-input__footer">
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
import { computed, ref } from 'vue'
import type {
  ErpInputProps,
  ErpInputEmits,
  ErpInputExpose,
  ValidatorRule
} from '@/types/basic-input'

const props = withDefaults(defineProps<ErpInputProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请输入',
  clearable: true,
  showWordLimit: false,
  size: 'default'
})

const emit = defineEmits<ErpInputEmits>()

const errorMessages = ref<string[]>([])

const localValue = computed({
  get: () => props.modelValue,
  set: (val) => {
    errorMessages.value = []
    emit('update:modelValue', val)
  }
})

function handleFocus(event: FocusEvent): void {
  emit('focus', event)
}

function handleBlur(event: FocusEvent): void {
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
  let errors: string[] = collectErrors(props.modelValue, props.rules)
  const customErrors = await runCustomValidators(props.modelValue, props.rules)
  errors = [...errors, ...customErrors]

  errorMessages.value = errors
  const isValid = errors.length === 0
  emit('validate', isValid)
  return isValid
}

function reset(): void {
  errorMessages.value = []
  emit('update:modelValue', '')
}

defineExpose<ErpInputExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input {
  --bi-label-color: var(--el-text-color-primary, #303133);
  --bi-error-color: var(--el-color-danger, #f56c6c);
  --bi-loading-min-height: 32px;

  width: 100%;

  &--loading {
    .basic-input__content {
      min-height: var(--bi-loading-min-height);
    }
  }

  &--disabled {
    opacity: 1;
  }

  &--small {
    font-size: var(--el-font-size-small, 12px);
  }

  &--large {
    font-size: var(--el-font-size-large, 16px);
  }

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
  }

  &__content {
    position: relative;
  }

  &__loading {
    min-height: var(--bi-loading-min-height);
    display: flex;
    align-items: center;
  }

  &__footer {
    margin-top: 4px;
  }

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
}
</style>
