<template>
  <div class="captcha-image" :title="$t('login.captchaTip')" @click="$emit('refresh')">
    <img v-if="image" :src="image" alt="captcha" class="captcha-img" />
    <div v-else class="captcha-placeholder">
      <el-icon><RefreshRight /></el-icon>
      <span>{{ $t('login.captchaTip') }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RefreshRight } from '@element-plus/icons-vue'

defineProps<{
  image: string
}>()

defineEmits<{
  refresh: []
}>()
</script>

<style scoped>
.captcha-image {
  flex-shrink: 0;
  width: 120px;
  height: 40px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid var(--el-border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-fill-color-light);
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  font-size: 10px;
  color: var(--el-text-color-secondary);
}
</style>
