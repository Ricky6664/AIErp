<template>
  <div class="global-notification">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="bell-badge">
      <el-icon :size="18" @click="goToMessageCenter"><Bell /></el-icon>
    </el-badge>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElNotification } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { getUnreadCount } from '@/api/msg/message'
import { useWebSocket } from '@/composables/useWebSocket'

defineOptions({ name: 'GlobalNotification' })

const router = useRouter()
const unreadCount = ref(0)
let pollTimer: ReturnType<typeof setInterval> | null = null

interface WsMessage {
  title: string
  content: string
  priority: string
  type: string
}

const { connect, disconnect, onMessage } = useWebSocket('/api/msg/ws/connect')

onMessage((msg: WsMessage) => {
  unreadCount.value++
  const notifyType =
    msg.priority === 'urgent' ? 'error' : msg.priority === 'important' ? 'warning' : 'info'
  const duration = msg.priority === 'urgent' ? 0 : 5000
  ElNotification({
    title: msg.title,
    message: msg.content,
    type: notifyType as 'error' | 'warning' | 'info',
    duration,
    position: 'bottom-right',
    onClick: () => router.push('/msg/center')
  })
})

const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.all
  } catch {
    // 静默失败
  }
}

const goToMessageCenter = () => {
  router.push('/msg/center')
}

onMounted(() => {
  connect()
  fetchUnreadCount()
  pollTimer = setInterval(fetchUnreadCount, 30000)
})

onUnmounted(() => {
  disconnect()
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
})
</script>

<style lang="scss" scoped>
.global-notification {
  position: fixed;
  bottom: 60px;
  right: 24px;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--el-bg-color-overlay);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  }

  .bell-badge {
    cursor: pointer;
  }
}
</style>
