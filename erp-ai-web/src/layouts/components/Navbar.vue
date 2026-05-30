<template>
  <div class="navbar">
    <!-- 左侧区域 -->
    <div class="navbar__left">
      <!-- 折叠/展开按钮 -->
      <el-icon class="navbar__collapse-btn" :size="20" @click="layoutStore.toggleCollapse()">
        <Fold v-if="!layoutStore.isCollapsed" />
        <Expand v-else />
      </el-icon>
      <!-- 面包屑导航 -->
      <Breadcrumb />
    </div>

    <!-- 右侧区域 -->
    <div class="navbar__right">
      <!-- 全局搜索 -->
      <el-tooltip content="搜索" placement="bottom">
        <el-icon class="navbar__action-btn" :size="18">
          <Search />
        </el-icon>
      </el-tooltip>

      <!-- 消息铃铛 -->
      <el-badge :value="unreadCount" :hidden="!unreadCount" class="navbar__badge">
        <el-icon class="navbar__action-btn" :size="18">
          <Bell />
        </el-icon>
      </el-badge>

      <!-- 用户头像+下拉菜单 -->
      <el-dropdown trigger="click" @command="handleUserCommand">
        <div class="navbar__user">
          <el-avatar :size="32" :src="userStore.avatar">
            {{ userStore.nickname?.charAt(0) }}
          </el-avatar>
          <span class="navbar__username">{{ userStore.nickname }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <!-- 全屏切换 -->
      <el-tooltip content="全屏" placement="bottom">
        <el-icon class="navbar__action-btn" :size="18" @click="layoutStore.toggleFullscreen()">
          <FullScreen />
        </el-icon>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Fold, Expand, Search, Bell, FullScreen } from '@element-plus/icons-vue'
import Breadcrumb from './Breadcrumb.vue'
import { useLayoutStore } from '@/stores/modules/layout'
import { useUserStore } from '@/stores/modules/user'

defineOptions({ name: 'Navbar' })

const router = useRouter()
const layoutStore = useLayoutStore()
const userStore = useUserStore()

const unreadCount = ref<number>(0)

function handleUserCommand(command: string) {
  if (command === 'logout') {
    userStore.logout()
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;

  &__left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &__collapse-btn {
    cursor: pointer;
    transition: color 0.2s;
    &:hover {
      color: var(--el-color-primary);
    }
  }

  &__action-btn {
    cursor: pointer;
    transition: color 0.2s;
    &:hover {
      color: var(--el-color-primary);
    }
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
  }
}
</style>
