<template>
  <div class="navbar">
    <!-- 左侧区域：折叠按钮 + 面包屑 -->
    <div class="navbar__left">
      <el-icon class="navbar__collapse-btn" :size="20" @click="layoutStore.toggleCollapse()">
        <Fold v-if="!layoutStore.isCollapsed" />
        <Expand v-else />
      </el-icon>
      <el-breadcrumb separator="/" class="navbar__breadcrumb">
        <el-breadcrumb-item :to="{ path: '/home' }">
          <el-icon :size="14"><HomeFilled /></el-icon>
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="breadcrumbParent">{{ breadcrumbParent }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ breadcrumbCurrent }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 中间区域：全局搜索 -->
    <div class="navbar__center">
      <div class="navbar__search-wrapper">
        <el-icon :size="16" class="navbar__search-icon"><Search /></el-icon>
        <input
          ref="searchInputRef"
          v-model="searchText"
          class="navbar__search-input"
          placeholder="搜索菜单、功能、单据..."
          @focus="searchDialogRef?.open()"
          @keydown.enter="handleSearchEnter"
        />
        <span class="navbar__search-shortcut">Ctrl+K</span>
      </div>
    </div>

    <!-- 右侧区域：消息 + 主题 + 语言 + 全屏 + 用户（最右边） -->
    <div class="navbar__right">
      <el-popover
        placement="bottom-end"
        :width="340"
        trigger="click"
        @show="handleMessagePopoverShow"
      >
        <template #reference>
          <el-badge :value="unreadCount" :hidden="!unreadCount" class="navbar__badge">
            <el-icon class="navbar__action-btn" :size="20"><Bell /></el-icon>
          </el-badge>
        </template>
        <div class="navbar__message-list">
          <template v-if="messages.length > 0">
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="navbar__message-item is-unread"
              @click="handleMessageClick(msg)"
            >
              <div class="navbar__message-title">{{ msg.title }}</div>
              <div class="navbar__message-time">{{ msg.publishTime }}</div>
            </div>
          </template>
          <div v-else class="navbar__message-empty">
            <el-icon :size="32"><Bell /></el-icon>
            <span>暂无消息</span>
          </div>
        </div>
      </el-popover>

      <el-tooltip :content="isDark ? '切换浅色模式' : '切换深色模式'" placement="bottom">
        <el-icon class="navbar__action-btn" :size="20" @click="toggleDarkMode">
          <Sunny v-if="isDark" />
          <Moon v-else />
        </el-icon>
      </el-tooltip>

      <el-dropdown trigger="click" @command="handleLangSwitch">
        <span class="navbar__lang-btn">
          <el-icon :size="18"><Connection /></el-icon>
          <span class="navbar__lang-label">{{
            localeStore.currentLanguage === 'zh-CN' ? '中' : 'EN'
          }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="lang in localeStore.availableLanguages"
              :key="lang.code"
              :command="lang.code"
              :class="{ 'is-active': localeStore.currentLanguage === lang.code }"
            >
              {{ lang.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <el-tooltip content="全屏" placement="bottom">
        <el-icon class="navbar__action-btn" :size="20" @click="layoutStore.toggleFullscreen()">
          <FullScreen />
        </el-icon>
      </el-tooltip>

      <el-dropdown trigger="click" @command="handleUserCommand">
        <div class="navbar__user">
          <el-avatar :size="32" :src="userStore.avatar">
            {{ userStore.nickname?.charAt(0) }}
          </el-avatar>
          <span class="navbar__username">{{ userStore.nickname }}</span>
          <el-icon :size="14" class="navbar__user-arrow"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>个人中心
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon>修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <SearchDialog ref="searchDialogRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Fold,
  Expand,
  Search,
  Bell,
  FullScreen,
  Sunny,
  Moon,
  ArrowDown,
  User,
  Lock,
  SwitchButton,
  Connection,
  HomeFilled
} from '@element-plus/icons-vue'
import SearchDialog from './SearchDialog.vue'
import { useLayoutStore } from '@/stores/modules/layout'
import { useUserStore } from '@/stores/modules/user'
import { usePermissionStore } from '@/stores/modules/permission'
import { useLocaleStore } from '@/stores/modules/locale'
import { useAnnouncementStore } from '@/stores/modules/announcement'
import type { AnnouncementListItem } from '@/api/types/announcement'

defineOptions({ name: 'Navbar' })

const route = useRoute()
const router = useRouter()
const layoutStore = useLayoutStore()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const localeStore = useLocaleStore()
const announcementStore = useAnnouncementStore()

const searchDialogRef = ref<InstanceType<typeof SearchDialog>>()
const searchInputRef = ref<HTMLInputElement>()
const searchText = ref('')
const unreadCount = computed(() => announcementStore.unreadCount)

const DARK_KEY = 'erp_dark_mode'
const isDark = ref(localStorage.getItem(DARK_KEY) === 'true')

function toggleDarkMode() {
  isDark.value = !isDark.value
  localStorage.setItem(DARK_KEY, String(isDark.value))
  document.documentElement.classList.toggle('dark', isDark.value)
}

// 初始化时同步 dark class
if (isDark.value) {
  document.documentElement.classList.add('dark')
} else {
  document.documentElement.classList.remove('dark')
}

// 面包屑
const breadcrumbCurrent = computed(() => {
  const meta = route.meta as any
  return (meta?.title as string) || (route.name as string) || ''
})

const breadcrumbParent = computed(() => {
  const matched = route.matched
  if (matched.length >= 2) {
    const parentMeta = matched[matched.length - 2]?.meta as any
    const parentTitle = parentMeta?.title as string
    if (parentTitle && parentTitle !== 'Layout' && parentTitle !== breadcrumbCurrent.value) {
      return parentTitle
    }
  }
  return ''
})

const messages = ref<AnnouncementListItem[]>([])

async function handleMessagePopoverShow() {
  const list = await announcementStore.fetchUnreadList()
  messages.value = list
}

function handleMessageClick(_msg: AnnouncementListItem) {
  router.push('/system/announcement')
}

function handleSearchEnter() {
  if (searchText.value.trim()) {
    searchDialogRef.value?.open()
  }
}

async function handleUserCommand(command: string) {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    permissionStore.resetPermission()
    await userStore.logout()
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'password') {
    router.push('/change-password')
  }
}

async function handleLangSwitch(code: string) {
  await localeStore.setLanguage(code)
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 16px;
  background: #ffffff;

  &__left {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-shrink: 0;
  }

  &__collapse-btn {
    cursor: pointer;
    color: var(--el-text-color-regular);
    padding: 6px;
    border-radius: 6px;
    transition: all 0.2s;
    &:hover {
      color: var(--el-color-primary);
      background: var(--el-fill-color-light);
    }
  }

  &__breadcrumb {
    font-size: 13px;
    :deep(.el-breadcrumb__item) {
      display: flex;
      align-items: center;
    }
  }

  &__center {
    flex: 1;
    display: flex;
    justify-content: center;
    padding: 0 24px;
  }

  &__search-wrapper {
    position: relative;
    width: 100%;
    max-width: 480px;
    display: flex;
    align-items: center;
  }

  &__search-icon {
    position: absolute;
    left: 12px;
    color: var(--el-text-color-placeholder);
    pointer-events: none;
  }

  &__search-input {
    width: 100%;
    height: 36px;
    padding: 0 72px 0 36px;
    border: 1px solid var(--el-border-color);
    border-radius: 18px;
    background: var(--el-fill-color-light);
    font-size: 13px;
    color: var(--el-text-color-primary);
    outline: none;
    transition: all 0.2s;
    &::placeholder {
      color: var(--el-text-color-placeholder);
    }
    &:focus {
      border-color: var(--el-color-primary);
      background: var(--el-bg-color);
      box-shadow: 0 0 0 3px var(--el-color-primary-light-9);
    }
  }

  &__search-shortcut {
    position: absolute;
    right: 10px;
    font-size: 11px;
    color: var(--el-text-color-placeholder);
    background: var(--el-fill-color);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 4px;
    padding: 1px 6px;
    pointer-events: none;
  }

  &__right {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
  }

  &__action-btn {
    cursor: pointer;
    color: var(--el-text-color-regular);
    transition: all 0.2s;
    padding: 6px;
    border-radius: 6px;
    &:hover {
      color: var(--el-color-primary);
      background: var(--el-fill-color-light);
    }
  }

  &__badge {
    cursor: pointer;
  }

  &__lang-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    padding: 6px 8px;
    border-radius: 6px;
    color: var(--el-text-color-regular);
    transition: all 0.2s;
    &:hover {
      background: var(--el-fill-color-light);
    }
  }

  &__lang-label {
    font-size: 12px;
    font-weight: 600;
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 8px;
    transition: background 0.2s;
    &:hover {
      background: var(--el-fill-color-light);
    }
  }

  &__username {
    font-size: 14px;
    font-weight: 500;
    color: var(--el-text-color-primary);
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__user-arrow {
    color: var(--el-text-color-secondary);
  }

  &__message-list {
    max-height: 360px;
    overflow-y: auto;
  }

  &__message-item {
    padding: 10px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
    cursor: pointer;
    &:last-child {
      border-bottom: none;
    }
    &.is-unread .navbar__message-title {
      font-weight: 600;
    }
  }

  &__message-title {
    font-size: 14px;
    color: var(--el-text-color-primary);
  }

  &__message-time {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-top: 4px;
  }

  &__message-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 24px 0;
    color: var(--el-text-color-secondary);
    font-size: 14px;
  }
}
</style>
