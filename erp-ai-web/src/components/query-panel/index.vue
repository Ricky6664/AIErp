<template>
  <div class="query-panel">
    <el-form
      ref="formRef"
      :model="localModel"
      :disabled="props.disabled"
      label-width="auto"
      @keyup.enter="handleSearch"
    >
      <el-row :gutter="16">
        <el-col v-for="field in visibleFields" :key="field.field" :span="field.span ?? 6">
          <el-form-item
            :label="field.label"
            :prop="field.field"
            :rules="buildFormRules(field.rules)"
          >
            <!-- 文本输入 -->
            <el-input
              v-if="field.type === 'input'"
              v-model="localModel[field.field]"
              :placeholder="field.placeholder || '请输入'"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              @change="handleFieldChange(field, $event)"
              @focus="emit('focus', field.field)"
              @blur="emit('blur', field.field)"
            />

            <!-- 文本域 -->
            <el-input
              v-else-if="field.type === 'textarea'"
              v-model="localModel[field.field]"
              type="textarea"
              :rows="3"
              :placeholder="field.placeholder || '请输入'"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              @change="handleFieldChange(field, $event)"
            />

            <!-- 数字输入 -->
            <el-input-number
              v-else-if="field.type === 'number'"
              v-model="localModel[field.field]"
              :placeholder="field.placeholder || '请输入'"
              :disabled="field.disabled"
              controls-position="right"
              style="width: 100%"
              @change="handleFieldChange(field, $event)"
            />

            <!-- 下拉选择 -->
            <el-select
              v-else-if="field.type === 'select'"
              v-model="localModel[field.field]"
              :placeholder="field.placeholder || '请选择'"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              style="width: 100%"
              @change="handleFieldChange(field, $event)"
            >
              <el-option
                v-for="opt in field.options"
                :key="String(opt.value)"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>

            <!-- 日期选择 -->
            <el-date-picker
              v-else-if="field.type === 'date'"
              v-model="localModel[field.field]"
              type="date"
              :placeholder="field.placeholder || '选择日期'"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              style="width: 100%"
              @change="handleFieldChange(field, $event)"
            />

            <!-- 日期范围 -->
            <el-date-picker
              v-else-if="field.type === 'dateRange'"
              v-model="localModel[field.field]"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              style="width: 100%"
              @change="handleFieldChange(field, $event)"
            />

            <!-- 日期时间 -->
            <el-date-picker
              v-else-if="field.type === 'datetime'"
              v-model="localModel[field.field]"
              type="datetime"
              :placeholder="field.placeholder || '选择日期时间'"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              style="width: 100%"
              @change="handleFieldChange(field, $event)"
            />

            <!-- 月份选择 -->
            <el-date-picker
              v-else-if="field.type === 'dateMonth'"
              v-model="localModel[field.field]"
              type="month"
              :placeholder="field.placeholder || '选择月份'"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              style="width: 100%"
              @change="handleFieldChange(field, $event)"
            />

            <!-- Switch -->
            <el-switch
              v-else-if="field.type === 'switch'"
              v-model="localModel[field.field]"
              :disabled="field.disabled"
              @change="handleFieldChange(field, $event)"
            />

            <!-- Radio -->
            <el-radio-group
              v-else-if="field.type === 'radio'"
              v-model="localModel[field.field]"
              :disabled="field.disabled"
              @change="handleFieldChange(field, $event)"
            >
              <el-radio v-for="opt in field.options" :key="String(opt.value)" :value="opt.value">
                {{ opt.label }}
              </el-radio>
            </el-radio-group>

            <!-- Checkbox -->
            <el-checkbox-group
              v-else-if="field.type === 'checkbox'"
              v-model="localModel[field.field]"
              :disabled="field.disabled"
              @change="handleFieldChange(field, $event)"
            >
              <el-checkbox
                v-for="opt in field.options"
                :key="String(opt.value)"
                :value="opt.value"
                :label="opt.label"
              />
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div class="query-panel__actions">
      <el-button type="primary" :icon="Search" @click="handleSearch"> 查询 </el-button>
      <el-button :icon="RefreshRight" @click="handleReset"> 重置 </el-button>
      <el-button
        v-if="isCollapsible && totalFields > (props.collapseThreshold ?? 8)"
        type="default"
        text
        @click="collapsed = !collapsed"
      >
        {{ collapsed ? '展开' : '收起' }}
        <el-icon>
          <ArrowDown v-if="collapsed" />
          <ArrowUp v-else />
        </el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { Search, RefreshRight, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import type { FieldConfig, FieldValidationRule } from '@/types/query-panel'

const props = withDefaults(
  defineProps<{
    modelValue: Record<string, unknown>
    fieldConfig: FieldConfig[]
    disabled?: boolean
    collapsible?: boolean
    collapseThreshold?: number
  }>(),
  {
    disabled: false,
    collapsible: true,
    collapseThreshold: 8
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: Record<string, unknown>]
  search: []
  reset: []
  change: [field: string, value: unknown]
  focus: [field: string]
  blur: [field: string]
}>()

const formRef = ref<FormInstance>()
const collapsed = ref(true)

const totalFields = computed(() => props.fieldConfig.length)
const isCollapsible = computed(() => props.collapsible)

const collapseThreshold = computed(() => props.collapseThreshold ?? 8)

const visibleFields = computed(() => {
  if (!isCollapsible.value || totalFields.value <= collapseThreshold.value) {
    return props.fieldConfig
  }
  if (collapsed.value) {
    return props.fieldConfig.slice(0, 8)
  }
  return props.fieldConfig
})

const localModel = reactive<Record<string, unknown>>({})

function initModel(): void {
  Object.keys(localModel).forEach((key) => {
    delete localModel[key]
  })
  for (const field of props.fieldConfig) {
    localModel[field.field] = props.modelValue[field.field] ?? field.defaultValue ?? undefined
  }
}

function buildFormRules(fieldRules?: FieldValidationRule[]): Record<string, unknown>[] {
  if (!fieldRules || fieldRules.length === 0) return []
  return fieldRules.map((rule) => ({
    required: rule.required,
    min: rule.min,
    max: rule.max,
    pattern: rule.pattern,
    message: rule.message || '输入不合法',
    trigger: rule.trigger || 'blur'
  }))
}

function handleFieldChange(field: FieldConfig, value: unknown): void {
  emit('change', field.field, value)
  emit('update:modelValue', { ...localModel })
}

function handleSearch(): void {
  emit('update:modelValue', { ...localModel })
  emit('search')
}

function handleReset(): void {
  for (const field of props.fieldConfig) {
    localModel[field.field] = field.defaultValue ?? undefined
  }
  emit('update:modelValue', { ...localModel })
  emit('reset')
}

watch(
  () => props.modelValue,
  (val) => {
    for (const field of props.fieldConfig) {
      if (field.field in val) {
        localModel[field.field] = val[field.field]
      }
    }
  },
  { deep: true }
)

watch(
  () => props.fieldConfig,
  () => {
    initModel()
  },
  { immediate: true }
)

defineExpose({
  formRef
})
</script>

<style scoped lang="scss">
.query-panel {
  padding: 16px;
  background: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);

  &__actions {
    display: flex;
    align-items: center;
    gap: 8px;
    padding-top: 8px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}
</style>
