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
        <el-icon class="navbar__action-btn" :size="18" @click="searchDialogRef?.toggle()">
          <Search />
        </el-icon>
      </el-tooltip>

      <!-- 消息铃铛 -->
      <el-popover
        placement="bottom-end"
        :width="320"
        trigger="click"
        @show="handleMessagePopoverShow"
      >
        <template #reference>
          <el-badge :value="unreadCount" :hidden="!unreadCount" class="navbar__badge">
            <el-icon class="navbar__action-btn" :size="18">
              <Bell />
            </el-icon>
          </el-badge>
        </template>
        <div class="navbar__message-list">
          <template v-if="messages.length > 0">
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="navbar__message-item"
              :class="{ 'is-unread': !msg.read }"
              @click="handleMessageClick(msg)"
            >
              <div class="navbar__message-title">{{ msg.title }}</div>
              <div class="navbar__message-time">{{ msg.time }}</div>
            </div>
          </template>
          <div v-else class="navbar__message-empty">暂无消息</div>
        </div>
      </el-popover>

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

    <!-- 全局搜索弹窗 -->
    <SearchDialog ref="searchDialogRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Fold, Expand, Search, Bell, FullScreen } from '@element-plus/icons-vue'
import Breadcrumb from './Breadcrumb.vue'
import SearchDialog from './SearchDialog.vue'
import { useLayoutStore } from '@/stores/modules/layout'
import { useUserStore } from '@/stores/modules/user'

defineOptions({ name: 'Navbar' })

const router = useRouter()
const layoutStore = useLayoutStore()
const userStore = useUserStore()

const searchDialogRef = ref<InstanceType<typeof SearchDialog>>()
const unreadCount = ref<number>(0)

interface MessageItem {
  id: number
  title: string
  time: string
  read: boolean
}

const messages = ref<MessageItem[]>([])

function handleMessagePopoverShow() {
  // 消息列表加载逻辑在后续消息模块接入API后完善
}

function handleMessageClick(msg: MessageItem) {
  msg.read = true
  unreadCount.value = messages.value.filter((m) => !m.read).length
}

async function handleUserCommand(command: string) {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.logout()
    await router.push('/login')
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

  &__badge {
    cursor: pointer;
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
  }

  &__message-list {
    max-height: 320px;
    overflow-y: auto;
  }

  &__message-item {
    padding: 10px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
    cursor: pointer;

    &:last-child {
      border-bottom: none;
    }

    &.is-unread {
      .navbar__message-title {
        font-weight: 600;
      }
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
    text-align: center;
    padding: 16px 0;
    color: var(--el-text-color-secondary);
    font-size: 14px;
  }
}
</style>
