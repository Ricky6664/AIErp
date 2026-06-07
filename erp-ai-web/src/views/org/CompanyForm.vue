<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? $t('org.company.editTitle') : $t('org.company.addTitle')"
    width="720px"
    destroy-on-close
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('org.company.companyName')" prop="companyName">
            <el-input
              v-model="formData.companyName"
              :placeholder="$t('common.pleaseInput')"
              maxlength="200"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('org.company.companyShortName')" prop="companyShortName">
            <el-input
              v-model="formData.companyShortName"
              :placeholder="$t('common.pleaseInput')"
              maxlength="100"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('org.company.creditCode')" prop="creditCode">
            <el-input
              v-model="formData.creditCode"
              :placeholder="$t('org.company.creditCodePlaceholder')"
              maxlength="18"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('org.company.legalPerson')" prop="legalPerson">
            <el-input
              v-model="formData.legalPerson"
              :placeholder="$t('common.pleaseInput')"
              maxlength="50"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="$t('org.company.registeredCapital')" prop="registeredCapital">
            <el-input-number
              v-model="formData.registeredCapital"
              :min="0"
              :precision="0"
              controls-position="right"
              style="width: 100%"
            />
            <span class="input-unit">{{ $t('org.company.registeredCapitalUnit') }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('org.company.phone')" prop="phone">
            <el-input
              v-model="formData.phone"
              :placeholder="$t('org.company.phonePlaceholder')"
              maxlength="30"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item :label="$t('org.company.address')" prop="address">
        <el-input
          v-model="formData.address"
          type="textarea"
          :rows="3"
          :placeholder="$t('common.pleaseInput')"
          maxlength="300"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ $t('common.save') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getCompanyDetail, createCompany, updateCompany } from '@/api/modules/org'

const props = defineProps<{
  visible: boolean
  companyId?: number
}>()

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = ref(false)
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val
    if (val) initForm()
  }
)
watch(dialogVisible, (val) => emit('update:visible', val))

const isEdit = computed(() => !!props.companyId)

interface FormData {
  companyName: string
  companyShortName: string
  creditCode: string
  legalPerson: string
  registeredCapital: number | undefined
  address: string
  phone: string
}

const defaultForm = (): FormData => ({
  companyName: '',
  companyShortName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: undefined,
  address: '',
  phone: ''
})

const formData = ref<FormData>(defaultForm())
const submitting = ref(false)
const formRef = ref<FormInstance>()

const CREDIT_CODE_REGEX = /^[0-9A-HJ-NP-RTUW-Y]{2}\d{6}[0-9A-HJ-NP-RTUW-Y]{10}$/
const PHONE_REGEX = /^(0\d{2,3}-?\d{7,8})|(1[3-9]\d{9})$/

const formRules: FormRules = {
  companyName: [
    { required: true, message: '公司名称不能为空', trigger: 'blur' },
    { min: 2, max: 100, message: '公司名称长度须在2-100个字符之间', trigger: 'blur' }
  ],
  creditCode: [
    {
      validator: (_rule, value: string, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!CREDIT_CODE_REGEX.test(value)) {
          callback(new Error('请输入有效的统一社会信用代码'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    {
      validator: (_rule, value: string, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!PHONE_REGEX.test(value)) {
          callback(new Error('请输入正确的联系电话格式'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

async function initForm(): Promise<void> {
  formData.value = defaultForm()
  formRef.value?.clearValidate()

  if (props.companyId) {
    try {
      const detail = await getCompanyDetail(props.companyId)
      formData.value.companyName = detail.companyName ?? ''
      formData.value.companyShortName = detail.companyShortName ?? ''
      formData.value.creditCode = detail.creditCode ?? ''
      formData.value.legalPerson = detail.legalPerson ?? ''
      formData.value.registeredCapital =
        detail.registeredCapital != null ? Number(detail.registeredCapital) : undefined
      formData.value.address = detail.address ?? ''
      formData.value.phone = detail.phone ?? ''
    } catch {
      ElMessage.error('加载公司详情失败')
    }
  }
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      companyName: formData.value.companyName,
      companyShortName: formData.value.companyShortName || undefined,
      creditCode: formData.value.creditCode || undefined,
      legalPerson: formData.value.legalPerson || undefined,
      registeredCapital: formData.value.registeredCapital,
      address: formData.value.address || undefined,
      phone: formData.value.phone || undefined
    }

    if (isEdit.value && props.companyId) {
      await updateCompany({ id: props.companyId, ...payload })
    } else {
      await createCompany(payload as any)
    }

    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    emit('success')
    dialogVisible.value = false
  } catch {
    // error handled by request interceptor
  } finally {
    submitting.value = false
  }
}

function handleClosed(): void {
  formData.value = defaultForm()
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.input-unit {
  margin-left: 8px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}
</style>
