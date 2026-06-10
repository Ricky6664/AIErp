<template>
  <div class="sidebar">
    <!-- Logo区域 -->
    <div class="sidebar__logo" @click="router.push(SIDEBAR_LOGO.link)">
      <img
        :src="layoutStore.isCollapsed ? SIDEBAR_LOGO.collapsed : SIDEBAR_LOGO.expanded"
        :alt="SIDEBAR_LOGO.title"
        class="sidebar__logo-img"
      />
      <span v-show="!layoutStore.isCollapsed" class="sidebar__logo-title">
        {{ SIDEBAR_LOGO.title }}
      </span>
    </div>

    <!-- 菜单区域 -->
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="layoutStore.isCollapsed"
        :unique-opened="true"
        :collapse-transition="true"
        background-color="transparent"
        @select="handleMenuSelect"
      >
        <SidebarItem v-for="menu in filteredMenus" :key="menu.path" :item="menu" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SidebarItem from './SidebarItem.vue'
import { useLayoutStore } from '@/stores/modules/layout'
import { usePermissionStore } from '@/stores/modules/permission'
import { filterRoutesByPermission } from '@/utils/permission'
import { routeToMenuItem } from './types'
import { SIDEBAR_LOGO } from './menuConfig'

defineOptions({ name: 'Sidebar' })

const route = useRoute()
const router = useRouter()
const layoutStore = useLayoutStore()
const permissionStore = usePermissionStore()

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta?.activeMenu) return meta.activeMenu as string
  return path
})

const filteredMenus = computed(() => {
  const filtered = filterRoutesByPermission(permissionStore.routes, permissionStore.permissions)
  return filtered.map(routeToMenuItem)
})

function handleMenuSelect(index: string) {
  if (/^(https?:\/\/|\/\/)/.test(index)) {
    window.open(index, '_blank')
    return
  }
  // 检查该路径是否对应一个实际可渲染的路由（不是仅有子菜单的父节点）
  const resolved = router.resolve(index)
  if (!resolved || resolved.name === 'Error404' || resolved.name === 'NotFound') {
    // 父节点路径没有对应页面，不导航，仅展开/收起子菜单
    return
  }
  router.push(index)
}
</script>

<style lang="scss" scoped>
.sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--sidebar-bg, #304156);
  color: var(--sidebar-text, #bfcbd9);
  transition: width 0.3s;
}

.sidebar__logo {
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 16px;
  cursor: pointer;
  overflow: hidden;
  flex-shrink: 0;
}

.sidebar__logo-img {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.sidebar__logo-title {
  margin-left: 12px;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  color: #fff;
}

:deep(.el-scrollbar) {
  flex: 1;
  overflow: hidden;

  .el-scrollbar__view {
    height: 100%;
  }
}

:deep(.el-menu) {
  border-right: none;

  .el-menu-item,
  .el-sub-menu__title {
    color: var(--sidebar-text, #bfcbd9);

    &:hover {
      background-color: rgba(255, 255, 255, 0.08);
      color: #fff;
    }
  }

  .el-menu-item.is-active {
    color: #fff;
    background-color: var(--el-color-primary);
  }
}
</style>
