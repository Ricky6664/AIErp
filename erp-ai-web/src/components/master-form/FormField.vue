<template>
  <el-form-item
    :label="fieldConfig.label ?? fieldConfig.field"
    :prop="fieldConfig.field"
    :required="fieldConfig.required"
    :error="firstError"
    :label-width="fieldConfig.labelWidth ?? layoutConfig?.labelWidth"
  >
    <!-- 文本输入 -->
    <el-input
      v-if="fieldConfig.fieldType === 'text'"
      :model-value="modelValue as string"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      :clearable="true"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    >
      <template v-if="$slots.prefix" #prefix><slot name="prefix" /></template>
      <template v-if="$slots.suffix" #suffix><slot name="suffix" /></template>
    </el-input>

    <!-- 数字输入 -->
    <el-input-number
      v-else-if="fieldConfig.fieldType === 'number'"
      :model-value="modelValue as number"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 多行文本 -->
    <el-input
      v-else-if="fieldConfig.fieldType === 'textarea'"
      :model-value="modelValue as string"
      type="textarea"
      :rows="3"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 密码输入 -->
    <el-input
      v-else-if="fieldConfig.fieldType === 'password'"
      :model-value="modelValue as string"
      type="password"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      show-password
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 日期选择 -->
    <el-date-picker
      v-else-if="fieldConfig.fieldType === 'date'"
      :model-value="modelValue as string"
      type="date"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 日期时间选择 -->
    <el-date-picker
      v-else-if="fieldConfig.fieldType === 'datetime'"
      :model-value="modelValue as string"
      type="datetime"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 时间选择 -->
    <el-time-picker
      v-else-if="fieldConfig.fieldType === 'time'"
      :model-value="modelValue as string"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 下拉选择 -->
    <el-select
      v-else-if="fieldConfig.fieldType === 'select'"
      :model-value="modelValue"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      :clearable="true"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    >
      <el-option
        v-for="opt in resolvedOptions"
        :key="String(opt.value)"
        :label="opt.label"
        :value="opt.value"
        :disabled="opt.disabled"
      />
    </el-select>

    <!-- 多选下拉 -->
    <el-select
      v-else-if="fieldConfig.fieldType === 'multi-select'"
      :model-value="(modelValue as unknown[]) ?? []"
      multiple
      :disabled="isDisabled"
      :placeholder="placeholderText"
      :clearable="true"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    >
      <el-option
        v-for="opt in resolvedOptions"
        :key="String(opt.value)"
        :label="opt.label"
        :value="opt.value"
        :disabled="opt.disabled"
      />
    </el-select>

    <!-- 单选按钮组 -->
    <el-radio-group
      v-else-if="fieldConfig.fieldType === 'radio'"
      :model-value="modelValue"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    >
      <el-radio
        v-for="opt in resolvedOptions"
        :key="String(opt.value)"
        :value="opt.value"
        :disabled="opt.disabled"
      >
        {{ opt.label }}
      </el-radio>
    </el-radio-group>

    <!-- 多选复选框组 -->
    <el-checkbox-group
      v-else-if="fieldConfig.fieldType === 'checkbox'"
      :model-value="(modelValue as unknown[]) ?? []"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    >
      <el-checkbox
        v-for="opt in resolvedOptions"
        :key="String(opt.value)"
        :value="opt.value"
        :disabled="opt.disabled"
      >
        {{ opt.label }}
      </el-checkbox>
    </el-checkbox-group>

    <!-- 开关 -->
    <el-switch
      v-else-if="fieldConfig.fieldType === 'switch'"
      :model-value="modelValue as boolean"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 树形选择 -->
    <el-tree-select
      v-else-if="fieldConfig.fieldType === 'tree-select'"
      :model-value="modelValue"
      :data="resolvedOptions as unknown as Record<string, unknown>[]"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      :clearable="true"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 级联选择 -->
    <el-cascader
      v-else-if="fieldConfig.fieldType === 'cascader'"
      :model-value="modelValue as unknown[]"
      :options="resolvedOptions"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      :clearable="true"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 文件上传 -->
    <el-upload
      v-else-if="fieldConfig.fieldType === 'upload'"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
    >
      <el-button :disabled="isDisabled">点击上传</el-button>
    </el-upload>

    <!-- 图片上传 -->
    <el-upload
      v-else-if="fieldConfig.fieldType === 'image'"
      list-type="picture-card"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>

    <!-- 颜色选择 -->
    <el-color-picker
      v-else-if="fieldConfig.fieldType === 'color'"
      :model-value="modelValue as string"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 评分 -->
    <el-rate
      v-else-if="fieldConfig.fieldType === 'rate'"
      :model-value="modelValue as number"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 滑块 -->
    <el-slider
      v-else-if="fieldConfig.fieldType === 'slider'"
      :model-value="modelValue as number"
      :disabled="isDisabled"
      v-bind="fieldConfig.componentProps"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 默认：文本输入兜底 -->
    <el-input
      v-else
      :model-value="modelValue as string"
      :disabled="isDisabled"
      :placeholder="placeholderText"
      :clearable="true"
      @update:model-value="handleUpdate"
      @change="handleChange"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <!-- 自定义label插槽 -->
    <template v-if="$slots.label" #label>
      <slot name="label" />
    </template>

    <!-- 自定义error插槽 -->
    <template v-if="$slots.error && firstError" #error>
      <slot name="error" :error="firstError" />
    </template>
  </el-form-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { FormFieldConfig, FormLayoutConfig, FormFieldOption } from '@/types/master-form'

const props = withDefaults(
  defineProps<{
    modelValue: unknown
    fieldConfig: FormFieldConfig
    disabled?: boolean
    placeholder?: string
    layoutConfig?: FormLayoutConfig
  }>(),
  { disabled: false, placeholder: undefined, layoutConfig: undefined }
)

const emit = defineEmits<{
  'update:modelValue': [value: unknown]
  change: [value: unknown]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
}>()

const isDisabled = computed(() => props.disabled || props.fieldConfig.readonly || false)
const placeholderText = computed(
  () => props.placeholder || props.fieldConfig.placeholder || '请输入'
)

const resolvedOptions = computed<FormFieldOption[]>(() => {
  return props.fieldConfig.options || []
})

const firstError = computed(() => {
  const rules = props.fieldConfig.rules
  if (!rules || rules.length === 0) return ''
  return ''
})

function handleUpdate(value: unknown) {
  emit('update:modelValue', value)
}

function handleChange(value: unknown) {
  emit('change', value)
}

function handleFocus(event: FocusEvent) {
  emit('focus', event)
}

function handleBlur(event: FocusEvent) {
  emit('blur', event)
}

defineExpose({
  focus: () => {},
  blur: () => {},
  validate: async (): Promise<boolean> => true,
  clearValidate: () => {},
  resetField: () => {
    emit('update:modelValue', props.fieldConfig.defaultValue ?? undefined)
  }
})
</script>
