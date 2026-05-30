<template>
  <div class="redirect-loading">
    <el-icon class="is-loading"><Loading /></el-icon>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'

defineOptions({ name: 'Redirect' })

const route = useRoute()
const router = useRouter()

onBeforeMount(() => {
  const { params, query } = route
  const redirectPath = '/' + (params.path as string[])?.join('/') || '/home'
  router.replace({ path: redirectPath, query })
})
</script>
