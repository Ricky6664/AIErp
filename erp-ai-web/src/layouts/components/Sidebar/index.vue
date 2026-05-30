<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapsed"
    :unique-opened="true"
    :collapse-transition="false"
    mode="vertical"
    class="sidebar-menu"
  >
    <SidebarItem v-for="item in filteredMenus" :key="item.path" :item="item" />
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import type { SidebarProps } from './types'
import { routeToMenuItem } from './types'
import SidebarItem from './SidebarItem.vue'
import { usePermissionStore } from '@/stores/modules/permission'
import { filterRoutesByPermission } from '@/utils/permission'

defineOptions({ name: 'Sidebar' })

defineProps<SidebarProps>()

const route = useRoute()
const permissionStore = usePermissionStore()

const filteredMenus = computed(() => {
  const filtered = filterRoutesByPermission(permissionStore.routes, permissionStore.permissions)
  return filtered.map(routeToMenuItem)
})

const activeMenu = computed(() => route.path)
</script>
