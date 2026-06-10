<template>
  <template v-if="!item.hideMenu">
    <el-sub-menu v-if="hasVisibleChildren" :index="item.path">
      <template #title>
        <MenuItemIcon :icon="item.icon" :icon-type="item.iconType" />
        <span>{{ item.title }}</span>
      </template>
      <SidebarItem v-for="child in visibleChildren" :key="child.path" :item="child" />
    </el-sub-menu>

    <a
      v-else-if="isExternal"
      :href="item.path"
      target="_blank"
      rel="noopener noreferrer"
      class="el-menu-item"
    >
      <MenuItemIcon :icon="item.icon" :icon-type="item.iconType" />
      <span>{{ item.title }}</span>
    </a>

    <el-menu-item v-else :index="item.path">
      <MenuItemIcon :icon="item.icon" :icon-type="item.iconType" />
      <template #title>
        <span>{{ item.title }}</span>
      </template>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MenuItemData } from './types'
import MenuItemIcon from './MenuItemIcon.vue'

defineOptions({ name: 'SidebarItem' })

interface Props {
  item: MenuItemData
}
const props = defineProps<Props>()

const visibleChildren = computed(() =>
  (props.item.children || []).filter((child) => !child.hideMenu)
)

const hasVisibleChildren = computed(() => visibleChildren.value.length > 0)

const isExternal = computed(() => /^(https?:\/\/|\/\/)/.test(props.item.path))
</script>
