<template>
  <div class="master-form">
    <div v-if="$slots.prefix" class="master-form__prefix">
      <slot name="prefix" />
    </div>

    <el-form
      ref="formRef"
      :model="localData"
      :disabled="props.disabled"
      :label-width="layout?.labelWidth ?? '100px'"
      :label-position="layout?.labelPosition ?? 'right'"
      :size="layout?.size ?? 'default'"
      :inline="layout?.mode === 'inline'"
    >
      <template v-if="sortedGroups.length > 0">
        <div v-for="group in sortedGroups" :key="group.key" class="master-form__group">
          <div class="master-form__group-header">
            <span class="master-form__group-title">{{ group.title }}</span>
          </div>
          <el-row :gutter="16">
            <el-col
              v-for="fieldConfig in getGroupFields(group.key)"
              :key="fieldConfig.field"
              :span="fieldColSpan(fieldConfig)"
            >
              <FormField
                :model-value="localData[fieldConfig.field]"
                :field-config="fieldConfig"
                :disabled="props.disabled || linkage.isFieldDisabled(fieldConfig)"
                :layout-config="layout"
                @update:model-value="handleFieldUpdate(fieldConfig.field, $event)"
                @change="handleFieldChange(fieldConfig.field, $event)"
                @focus="handleFieldFocus(fieldConfig.field, $event)"
                @blur="handleFieldBlur(fieldConfig.field, $event)"
              >
                <template v-if="$slots[`field-${fieldConfig.field}`]" #default>
                  <slot :name="`field-${fieldConfig.field}`" />
                </template>
              </FormField>
            </el-col>
          </el-row>
        </div>
      </template>

      <el-row v-else :gutter="16">
        <el-col
          v-for="fieldConfig in visibleFieldConfigs"
          :key="fieldConfig.field"
          :span="fieldColSpan(fieldConfig)"
        >
          <FormField
            :model-value="localData[fieldConfig.field]"
            :field-config="fieldConfig"
            :disabled="props.disabled || linkage.isFieldDisabled(fieldConfig)"
            :layout-config="layout"
            @update:model-value="handleFieldUpdate(fieldConfig.field, $event)"
            @change="handleFieldChange(fieldConfig.field, $event)"
            @focus="handleFieldFocus(fieldConfig.field, $event)"
            @blur="handleFieldBlur(fieldConfig.field, $event)"
          >
            <template v-if="$slots[`field-${fieldConfig.field}`]" #default>
              <slot :name="`field-${fieldConfig.field}`" />
            </template>
          </FormField>
        </el-col>
      </el-row>

      <div v-if="$slots['form-actions']" class="master-form__actions">
        <slot name="form-actions" />
      </div>
    </el-form>

    <div v-if="$slots.suffix" class="master-form__suffix">
      <slot name="suffix" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import type { FormInstance } from 'element-plus'
import FormField from './FormField.vue'
import { useFormValidation } from '@/composables/useFormValidation'
import { useFormLinkage } from '@/composables/useFormLinkage'
import type {
  FormFieldConfig,
  FormLayoutConfig,
  FormGroupConfig,
  MasterFormProps,
  MasterFormEmits
} from '@/types/master-form'

const props = withDefaults(defineProps<MasterFormProps>(), {
  disabled: false,
  showValidation: true,
  viewCode: undefined,
  enableLinkage: true,
  enableValidation: true,
  layoutConfig: undefined
})

const emit = defineEmits<MasterFormEmits>()

const { errors, validateForm, clearValidate } = useFormValidation()
const linkage = useFormLinkage()

const formRef = ref<FormInstance>()
const localData = reactive<Record<string, unknown>>({ ...props.modelValue })

watch(
  () => props.modelValue,
  (val) => {
    Object.keys(localData).forEach((k) => delete localData[k])
    Object.assign(localData, val)
  },
  { deep: true }
)

const layout = computed<FormLayoutConfig | undefined>(() => props.layoutConfig)

const sortedGroups = computed<FormGroupConfig[]>(() => {
  if (!layout.value?.groups) return []
  return [...layout.value.groups].sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
})

const fieldConfigsMutable = reactive<FormFieldConfig[]>([...props.fieldConfigs])

watch(
  () => props.fieldConfigs,
  (val) => {
    fieldConfigsMutable.splice(0, fieldConfigsMutable.length, ...val)
  },
  { deep: true }
)

const visibleFieldConfigs = computed(() =>
  fieldConfigsMutable.filter((f) => linkage.isFieldVisible(f))
)

function getGroupFields(groupKey: string): FormFieldConfig[] {
  return visibleFieldConfigs.value.filter((f) => f.group === groupKey)
}

function fieldColSpan(config: FormFieldConfig): number {
  if (config.fullRow) return 24
  return config.colSpan ?? 24
}

function handleFieldUpdate(field: string, value: unknown) {
  localData[field] = value
  emit('update:modelValue', { ...localData })
  if (props.enableLinkage) {
    linkage.executeLinkages(field, value, fieldConfigsMutable, localData)
    emit('update:modelValue', { ...localData })
  }
}

function handleFieldChange(field: string, value: unknown) {
  emit('change', field, value)
}

function handleFieldFocus(field: string, event: FocusEvent) {
  emit('field-focus', field, event)
}

function handleFieldBlur(field: string, event: FocusEvent) {
  emit('field-blur', field, event)
}

async function validate(): Promise<boolean> {
  if (!props.enableValidation) return true

  if (formRef.value) {
    try {
      await formRef.value.validate()
    } catch (elErrors: unknown) {
      const errObj = elErrors as Record<string, unknown>
      const firstErrorField = Object.keys(errObj)[0]
      if (firstErrorField) scrollToField(firstErrorField)
      emit('validate', false, errObj as Record<string, string[]>)
      return false
    }
  }

  const result = validateForm(localData, visibleFieldConfigs.value)
  if (!result.valid) {
    const firstErrorField = Object.keys(result.errors)[0]
    if (firstErrorField) scrollToField(firstErrorField)
    emit('validate', false, result.errors)
    return false
  }

  emit('validate', true, {})
  return true
}

function clearFieldValidate(field?: string) {
  clearValidate(field)
  if (formRef.value) {
    if (field) {
      formRef.value.clearValidate([field])
    } else {
      formRef.value.clearValidate()
    }
  }
}

function resetFields() {
  Object.keys(localData).forEach((k) => delete localData[k])
  for (const config of fieldConfigsMutable) {
    localData[config.field] = config.defaultValue ?? undefined
  }
  linkage.reset()
  clearFieldValidate()
}

function getFormData(): Record<string, unknown> {
  return { ...localData }
}

function setFormData(data: Record<string, unknown>) {
  Object.assign(localData, data)
}

function getFieldValue(field: string): unknown {
  return localData[field]
}

function setFieldValue(field: string, value: unknown) {
  localData[field] = value
}

function scrollToField(field: string) {
  const el = document.querySelector(`[prop="${field}"]`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

function getErrors(): Record<string, string[]> {
  return { ...errors.value }
}

defineExpose({
  validate,
  clearValidate: clearFieldValidate,
  resetFields,
  getFormData,
  setFormData,
  getFieldValue,
  setFieldValue,
  scrollToField,
  getErrors
})
</script>

<style scoped>
.master-form {
  width: 100%;
  background: var(--el-bg-color, #fff);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter, #ebeef5);
  padding: 20px 24px;
}
.master-form__prefix,
.master-form__suffix {
  margin-bottom: 16px;
}
.master-form__group {
  margin-bottom: 20px;
  border: 1px solid var(--el-border-color-lighter, #ebeef5);
  border-radius: 8px;
  padding: 16px;
  background: var(--el-fill-color-lighter, #fafafa);
}
.master-form__group-header {
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--el-border-color-light, #e4e7ed);
}
.master-form__group-title {
  font-weight: 600;
  font-size: 15px;
  color: var(--el-text-color-primary, #303133);
}
.master-form__actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter, #ebeef5);
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
