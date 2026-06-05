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
                :disabled="props.disabled"
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
            :disabled="props.disabled"
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
import type {
  FormFieldConfig,
  FormLayoutConfig,
  FormGroupConfig,
  FieldLinkageRule,
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

const fieldVisibility = reactive<Record<string, boolean>>({})

const fieldConfigsMutable = reactive<FormFieldConfig[]>([...props.fieldConfigs])

watch(
  () => props.fieldConfigs,
  (val) => {
    fieldConfigsMutable.splice(0, fieldConfigsMutable.length, ...val)
  },
  { deep: true }
)

const allLinkages = computed<FieldLinkageRule[]>(() =>
  fieldConfigsMutable.flatMap((f) => f.linkages ?? [])
)

const visibleFieldConfigs = computed(() =>
  fieldConfigsMutable.filter((f) => {
    if (f.visible === false) return false
    if (fieldVisibility[f.field] === false) return false
    return true
  })
)

function processLinkages(changedField: string, value: unknown) {
  if (!props.enableLinkage) return
  for (const linkage of allLinkages.value) {
    if (linkage.triggerField !== changedField) continue
    if (linkage.condition && !linkage.condition(value)) continue

    switch (linkage.action) {
      case 'show':
        fieldVisibility[linkage.targetField] = true
        break
      case 'hide':
        fieldVisibility[linkage.targetField] = false
        break
      case 'setValue': {
        const paramValue = linkage.params?.value
        localData[linkage.targetField] = paramValue
        emit('update:modelValue', { ...localData })
        break
      }
      case 'setOptions': {
        const idx = fieldConfigsMutable.findIndex((f) => f.field === linkage.targetField)
        if (idx !== -1) {
          const newOptions = linkage.params?.options as FormFieldConfig['options']
          fieldConfigsMutable[idx] = { ...fieldConfigsMutable[idx], options: newOptions }
        }
        break
      }
      case 'enable':
      case 'disable':
        break
    }
  }
}

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
  processLinkages(field, value)
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
}
.master-form__prefix,
.master-form__suffix {
  margin-bottom: 12px;
}
.master-form__group {
  margin-bottom: 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 12px;
}
.master-form__group-header {
  margin-bottom: 12px;
}
.master-form__group-title {
  font-weight: 600;
  font-size: 14px;
}
.master-form__actions {
  margin-top: 16px;
  text-align: right;
}
</style>
