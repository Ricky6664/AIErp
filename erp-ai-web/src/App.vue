<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '@/stores/modules/app'

const appStore = useAppStore()

onMounted(async () => {
  await appStore.initAppConfig()
})
</script>

<template>
  <router-view />
</template>
