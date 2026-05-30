<template>
  <el-icon v-if="icon">
    <component :is="icon" />
  </el-icon>
</template>

<script setup lang="ts">
defineOptions({ name: 'MenuItemIcon' })

interface Props {
  icon?: string
}
defineProps<Props>()
</script>
