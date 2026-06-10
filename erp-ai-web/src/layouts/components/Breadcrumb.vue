<template>
  <el-breadcrumb class="breadcrumb" separator="/">
    <el-breadcrumb-item v-for="item in items" :key="item.path" :to="item.path">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

defineOptions({ name: 'Breadcrumb' })

const route = useRoute()

interface BreadcrumbItem {
  title: string
  path: string
}

const items = computed<BreadcrumbItem[]>(() => {
  const matched = route.matched.filter((r) => r.meta?.title)
  return matched.map((r) => {
    const title = (r.meta?.title as string) || r.name?.toString() || r.path
    return { title, path: r.path }
  })
})
</script>

<style lang="scss" scoped>
.breadcrumb {
  font-size: 14px;
  user-select: none;
}
</style>
