<template>
  <div
    class="basic-input basic-input--tag"
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
      <div v-else class="basic-input__tag-wrapper">
        <el-tag
          v-for="(tag, index) in tagList"
          :key="index"
          :closable="!disabled"
          :disable-transitions="false"
          class="basic-input__tag-item"
          @close="handleRemoveTag(tag)"
        >
          {{ tag }}
        </el-tag>
        <el-input
          v-if="!disabled && allowNewTag"
          ref="tagInputRef"
          v-model="inputValue"
          :placeholder="tagList.length >= (maxTags || Infinity) ? '已达上限' : placeholder"
          :size="size"
          class="basic-input__tag-input"
          @keyup.enter="handleAddTag"
          @blur="handleAddTag"
        />
        <!-- 预设标签选择区 -->
        <div
          v-if="presetTags && presetTags.length > 0 && !disabled"
          class="basic-input__preset-area"
        >
          <span class="basic-input__preset-label">快捷选择：</span>
          <el-tag
            v-for="pt in availablePresetTags"
            :key="pt"
            class="basic-input__preset-tag"
            effect="plain"
            @click="handleAddPresetTag(pt)"
          >
            + {{ pt }}
          </el-tag>
        </div>
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
  ErpTagInputProps,
  ErpTagInputEmits,
  ErpTagInputExpose,
  ValidatorRule
} from '@/types/basic-input'

const props = withDefaults(defineProps<ErpTagInputProps>(), {
  disabled: false,
  loading: false,
  placeholder: '输入标签后按回车',
  presetTags: () => [],
  allowFreeInput: true,
  size: 'default'
})

const emit = defineEmits<ErpTagInputEmits>()

const tagList = ref<string[]>([])
const inputValue = ref('')
const errorMessages = ref<string[]>([])

const allowNewTag = computed(
  () => props.allowFreeInput && (!props.maxTags || tagList.value.length < props.maxTags)
)

const availablePresetTags = computed(() =>
  (props.presetTags || []).filter((pt) => !tagList.value.includes(pt))
)

watch(
  () => props.modelValue,
  (val) => {
    if (Array.isArray(val)) {
      tagList.value = val.filter((t: unknown) => typeof t === 'string') as string[]
    } else if (val === null || val === undefined) {
      tagList.value = []
    }
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

function emitChange(): void {
  emit('update:modelValue', [...tagList.value])
  emit('change', [...tagList.value])
}

function handleAddTag(): void {
  const value = inputValue.value.trim()
  if (!value) return
  if (tagList.value.includes(value)) {
    inputValue.value = ''
    return
  }
  if (props.maxTags && tagList.value.length >= props.maxTags) {
    inputValue.value = ''
    return
  }
  tagList.value.push(value)
  errorMessages.value = []
  emitChange()
  inputValue.value = ''
}

function handleAddPresetTag(tag: string): void {
  if (tagList.value.includes(tag)) return
  if (props.maxTags && tagList.value.length >= props.maxTags) return
  tagList.value.push(tag)
  errorMessages.value = []
  emitChange()
}

function handleRemoveTag(tag: string): void {
  const index = tagList.value.indexOf(tag)
  if (index > -1) {
    tagList.value.splice(index, 1)
    emitChange()
  }
}

function collectErrors(value: unknown, rules?: ValidatorRule[]): string[] {
  if (!rules || rules.length === 0) return []
  const errors: string[] = []
  for (const rule of rules) {
    const isEmpty =
      value === undefined || value === null || (Array.isArray(value) && value.length === 0)
    if (rule.required && isEmpty) {
      errors.push(rule.message || '请至少添加一个标签')
    }
  }
  return errors
}

async function validate(): Promise<boolean> {
  const errors = collectErrors(tagList.value, props.rules)
  errorMessages.value = errors
  const valid = errors.length === 0
  emit('validate', valid)
  return valid
}

function reset(): void {
  errorMessages.value = []
  tagList.value = []
  inputValue.value = ''
  emit('update:modelValue', [])
}

onMounted(() => {
  if (Array.isArray(props.modelValue)) {
    tagList.value = props.modelValue.filter((t: unknown) => typeof t === 'string') as string[]
  }
  errorMessages.value = []
})

onBeforeUnmount(() => {
  errorMessages.value = []
})

defineExpose<ErpTagInputExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--tag {
  width: 100%;

  &__tag-wrapper {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 6px;
    padding: 8px 12px;
    border: 1px solid var(--el-border-color, #dcdfe6);
    border-radius: var(--el-border-radius-base, 4px);
    min-height: 36px;
    background: var(--el-fill-color-blank, #fff);
  }

  &__tag-item {
    margin: 0;
  }

  &__tag-input {
    width: 140px;
    flex: none;

    :deep(.el-input__wrapper) {
      box-shadow: none;
      padding: 0;
      background: transparent;
    }
  }

  &__preset-area {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 4px;
    padding-top: 6px;
    border-top: 1px dashed var(--el-border-color-lighter, #e4e7ed);
  }

  &__preset-label {
    font-size: 12px;
    color: var(--el-text-color-secondary, #909399);
  }

  &__preset-tag {
    cursor: pointer;
    font-size: 12px;
  }
}
</style>
