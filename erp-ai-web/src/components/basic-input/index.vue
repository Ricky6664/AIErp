<template>
  <div class="basic-input">
    <el-input
      v-model="localValue"
      :placeholder="props.placeholder || '请输入'"
      :disabled="props.disabled"
      :clearable="!props.disabled"
      @focus="handleFocus"
      @blur="handleBlur"
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
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ErpInputProps, ErpInputEmits, ErpInputExpose } from '@/types/basic-input'

const props = withDefaults(defineProps<ErpInputProps>(), {
  disabled: false,
  placeholder: '请输入'
})

const emit = defineEmits<ErpInputEmits>()

const localValue = computed({
  get: () => props.modelValue,
  set: (val) => {
    emit('update:modelValue', val)
  }
})

function handleFocus(event: FocusEvent): void {
  emit('focus', event)
}

function handleBlur(event: FocusEvent): void {
  emit('blur', event)
}

async function validate(): Promise<boolean> {
  if (props.rules && props.rules.length > 0) {
    for (const rule of props.rules) {
      if (
        rule.required &&
        (props.modelValue === undefined || props.modelValue === null || props.modelValue === '')
      ) {
        emit('validate', false)
        return false
      }
      if (rule.validator) {
        try {
          const result = await rule.validator(props.modelValue)
          if (!result) {
            emit('validate', false)
            return false
          }
        } catch {
          emit('validate', false)
          return false
        }
      }
    }
  }
  emit('validate', true)
  return true
}

function reset(): void {
  emit('update:modelValue', '')
}

defineExpose<ErpInputExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input {
  width: 100%;
}
</style>
