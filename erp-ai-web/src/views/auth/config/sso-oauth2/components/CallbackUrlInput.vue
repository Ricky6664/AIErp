<template>
  <div class="callback-url-section">
    <el-form-item label="回调URL">
      <div class="callback-url-row">
        <el-input :model-value="callbackUrl" readonly class="callback-url-field" />
        <el-button type="primary" plain @click="handleCopy">
          <el-icon><DocumentCopy /></el-icon>
          复制
        </el-button>
      </div>
    </el-form-item>
    <div class="callback-hint">
      <el-alert
        title="请将以下回调URL添加到OAuth2供应商后台配置中"
        type="info"
        :closable="false"
        show-icon
      />
    </div>
    <div v-if="isHttp" class="callback-https-warning">
      <el-alert
        title="当前站点使用HTTP协议，生产环境建议配置HTTPS"
        type="warning"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'

const props = withDefaults(
  defineProps<{
    providerType: string
    baseUrl?: string
  }>(),
  {
    baseUrl: () => window.location.origin
  }
)

const providerTypeUrlMap: Record<string, string> = {
  wecom: 'wechat_work'
}

const urlProviderType = computed<string>(() => {
  if (!props.providerType) return ''
  return providerTypeUrlMap[props.providerType] || props.providerType
})

const callbackUrl = computed<string>(() => {
  if (!urlProviderType.value) return ''
  return `${props.baseUrl}/api/auth/oauth2/callback/${urlProviderType.value}`
})

const isHttp = computed<boolean>(() => {
  return props.baseUrl.startsWith('http://')
})

async function handleCopy(): Promise<void> {
  if (!callbackUrl.value) return
  try {
    await navigator.clipboard.writeText(callbackUrl.value)
    ElMessage.success('回调URL已复制到剪贴板')
  } catch {
    try {
      const textarea = document.createElement('textarea')
      textarea.value = callbackUrl.value
      textarea.style.position = 'fixed'
      textarea.style.opacity = '0'
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      ElMessage.success('回调URL已复制到剪贴板')
    } catch {
      ElMessage.error('复制失败，请手动复制')
    }
  }
}
</script>

<style scoped lang="scss">
.callback-url-section {
  .callback-url-row {
    display: flex;
    gap: 8px;
    align-items: center;

    .callback-url-field {
      flex: 1;

      :deep(.el-input__inner) {
        font-family: monospace;
        color: var(--el-color-primary);
      }
    }
  }

  .callback-hint {
    margin-left: 130px;
    margin-bottom: 18px;
  }

  .callback-https-warning {
    margin-left: 130px;
    margin-bottom: 18px;
  }
}
</style>
