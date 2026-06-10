<template>
  <div
    class="basic-input basic-input--dict-cascade"
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
      <div v-else class="basic-input__cascade-row">
        <el-select
          v-for="(level, levelIndex) in cascadeLevels"
          :key="levelIndex"
          :model-value="innerValue[levelIndex]"
          :placeholder="`请选择${level.title || ''}`"
          :disabled="disabled || (levelIndex > 0 && !innerValue[levelIndex - 1])"
          :clearable="true"
          :size="size"
          :loading="level.loading"
          @update:model-value="(val: unknown) => handleLevelChange(levelIndex, val)"
        >
          <el-option
            v-for="item in level.items"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
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
import { computed, reactive, watch, onMounted, onBeforeUnmount } from 'vue'
import type {
  ErpDictCascadeProps,
  ErpDictCascadeEmits,
  ErpDictCascadeExpose,
  SelectOption,
  ValidatorRule
} from '@/types/basic-input'

interface CascadeLevel {
  title: string
  items: SelectOption[]
  loading: boolean
}

const props = withDefaults(defineProps<ErpDictCascadeProps>(), {
  disabled: false,
  loading: false,
  placeholder: '请选择',
  dictCodeList: () => [],
  size: 'default'
})

const emit = defineEmits<ErpDictCascadeEmits>()

const innerValue = reactive<(string | number | null)[]>([])
const errorMessages = reactive<string[]>([])
const cascadeLevels = reactive<CascadeLevel[]>([])

watch(
  () => props.modelValue,
  (val) => {
    if (Array.isArray(val)) {
      val.forEach((v, i) => {
        innerValue[i] = v as string | number | null
      })
    } else {
      innerValue.length = 0
    }
  }
)

watch(
  () => props.fieldConfig,
  () => {
    errorMessages.length = 0
  }
)

watch(
  () => props.dictCodeList,
  () => {
    initLevels()
  },
  { deep: true }
)

const isValid = computed(() => errorMessages.length === 0)

const errorMsg = computed(() => errorMessages.join('; '))

function initLevels(): void {
  cascadeLevels.length = 0
  const codes = props.dictCodeList || []
  for (const code of codes) {
    cascadeLevels.push({
      title: code,
      items: [],
      loading: false
    })
  }
  if (codes.length > 0) {
    loadLevel(0)
  }
}

async function loadLevel(levelIndex: number): Promise<void> {
  const codes = props.dictCodeList || []
  if (levelIndex >= codes.length || levelIndex >= cascadeLevels.length) return
  const level = cascadeLevels[levelIndex]
  if (!level) return
  level.loading = true
  try {
    const { getDictDataApi } = await import('@/api/modules/system')
    const data = await getDictDataApi(codes[levelIndex])
    level.items = (data || []).map((item: Record<string, unknown>) => ({
      label: (item.dictLabel || item.label || '') as string,
      value: (item.dictValue || item.value || '') as string | number
    }))
  } catch {
    level.items = []
  } finally {
    level.loading = false
  }
}

function handleLevelChange(levelIndex: number, value: unknown): void {
  innerValue[levelIndex] = value as string | number | null
  errorMessages.length = 0

  // 清除后续级别的值
  for (let i = levelIndex + 1; i < cascadeLevels.length; i++) {
    innerValue[i] = null
    cascadeLevels[i].items = []
  }

  // 加载下一级
  if (value !== null && value !== undefined && value !== '') {
    loadLevel(levelIndex + 1)
  }

  emit('update:modelValue', [...innerValue])
  emit('change', [...innerValue])
}

function collectErrors(value: unknown, rules?: ValidatorRule[]): string[] {
  if (!rules || rules.length === 0) return []
  const errors: string[] = []
  for (const rule of rules) {
    const arr = Array.isArray(value) ? value : []
    const isEmpty = arr.length === 0 || arr.every((v) => v === null || v === '')
    if (rule.required && isEmpty) {
      errors.push(rule.message || '此项为必填')
    }
  }
  return errors
}

async function validate(): Promise<boolean> {
  const errors = collectErrors([...innerValue], props.rules)
  errorMessages.length = 0
  errorMessages.push(...errors)
  const valid = errors.length === 0
  emit('validate', valid)
  return valid
}

function reset(): void {
  errorMessages.length = 0
  for (let i = 0; i < innerValue.length; i++) {
    innerValue[i] = null
  }
  emit('update:modelValue', innerValue.length > 0 ? [...innerValue] : [])
}

onMounted(() => {
  if (Array.isArray(props.modelValue)) {
    props.modelValue.forEach((v, i) => {
      innerValue[i] = v as string | number | null
    })
  }
  errorMessages.length = 0
  initLevels()
})

onBeforeUnmount(() => {
  errorMessages.length = 0
})

defineExpose<ErpDictCascadeExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--dict-cascade {
  width: 100%;

  &__cascade-row {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;

    > * {
      flex: 1;
      min-width: 140px;
    }
  }
}
</style>
