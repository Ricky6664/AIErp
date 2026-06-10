<template>
  <div
    class="basic-input basic-input--file"
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
      <el-upload
        v-else
        :file-list="fileList"
        :action="action || '#'"
        :accept="accept"
        :disabled="disabled"
        :multiple="multiple"
        :limit="maxCount"
        :http-request="customUpload"
        :on-remove="handleRemove"
        :on-exceed="handleExceed"
        :before-upload="handleBeforeUpload"
      >
        <el-button :disabled="disabled" :size="size" type="primary"> 选择文件 </el-button>
        <template #tip>
          <div v-if="accept || maxSize" class="basic-input__upload-tip">
            <span v-if="accept">支持格式：{{ accept }}</span>
            <span v-if="accept && maxSize"> | </span>
            <span v-if="maxSize">单个文件不超过 {{ formatSize(maxSize) }}</span>
          </div>
        </template>
      </el-upload>
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
  ErpFileUploadProps,
  ErpFileUploadEmits,
  ErpFileUploadExpose,
  UploadFile,
  ValidatorRule
} from '@/types/basic-input'
import { ElMessage } from 'element-plus'
import type { UploadRawFile } from 'element-plus'

const props = withDefaults(defineProps<ErpFileUploadProps>(), {
  disabled: false,
  loading: false,
  multiple: false,
  size: 'default'
})

const emit = defineEmits<ErpFileUploadEmits>()

const fileList = ref<UploadFile[]>([])
const errorMessages = ref<string[]>([])

watch(
  () => props.modelValue,
  (val) => {
    if (Array.isArray(val)) {
      fileList.value = val
    } else if (val === null || val === undefined) {
      fileList.value = []
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

function formatSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function handleBeforeUpload(rawFile: UploadRawFile): boolean {
  if (props.accept) {
    const ext = '.' + rawFile.name.split('.').pop()?.toLowerCase()
    const allowed = props.accept.split(',').map((s) => s.trim().toLowerCase())
    if (!allowed.some((a) => a === ext || a === rawFile.type)) {
      ElMessage.warning(`文件格式不支持，允许：${props.accept}`)
      return false
    }
  }
  if (props.maxSize && rawFile.size > props.maxSize * 1024 * 1024) {
    ElMessage.warning(`文件大小不能超过 ${formatSize(props.maxSize)}`)
    return false
  }
  return true
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function customUpload(options: any): void {
  const file = options.file as UploadRawFile
  const uploadFile: UploadFile = {
    name: file.name,
    size: file.size,
    status: 'success',
    uid: file.uid
  }
  if (!props.multiple) {
    fileList.value = [uploadFile]
  }
  fileList.value = fileList.value || []
  fileList.value.push(uploadFile)
  emit('update:modelValue', fileList.value)
  emit('change', fileList.value)
}

function handleRemove(file: UploadFile): void {
  const index = fileList.value.findIndex((f) => f.uid === file.uid || f.name === file.name)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
  emit('update:modelValue', fileList.value)
  emit('change', fileList.value)
}

function handleExceed(): void {
  ElMessage.warning(`最多只能上传 ${props.maxCount || 1} 个文件`)
}

function collectErrors(value: unknown, rules?: ValidatorRule[]): string[] {
  if (!rules || rules.length === 0) return []
  const errors: string[] = []
  for (const rule of rules) {
    const isEmpty =
      value === undefined || value === null || (Array.isArray(value) && value.length === 0)
    if (rule.required && isEmpty) {
      errors.push(rule.message || '请上传文件')
    }
  }
  return errors
}

async function validate(): Promise<boolean> {
  const errors = collectErrors(fileList.value, props.rules)
  errorMessages.value = errors
  const valid = errors.length === 0
  emit('validate', valid)
  return valid
}

function reset(): void {
  errorMessages.value = []
  fileList.value = []
  emit('update:modelValue', [])
}

onMounted(() => {
  if (Array.isArray(props.modelValue)) {
    fileList.value = props.modelValue
  }
  errorMessages.value = []
})

onBeforeUnmount(() => {
  errorMessages.value = []
})

defineExpose<ErpFileUploadExpose>({
  validate,
  reset
})
</script>

<style scoped lang="scss">
.basic-input--file {
  width: 100%;

  &__upload-tip {
    font-size: 12px;
    color: var(--el-text-color-secondary, #909399);
    margin-top: 4px;
    line-height: 1.5;
  }
}
</style>
