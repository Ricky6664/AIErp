<template>
  <div class="message-center-list">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索消息标题"
        clearable
        style="width: 320px"
        @clear="loadMessages"
        @keyup.enter="loadMessages"
      />
      <el-button type="primary" @click="markAllReadAction">全部标记已读</el-button>
    </div>
    <div class="main-content">
      <!-- 左侧类型Tab -->
      <div class="left-panel">
        <el-tabs v-model="activeTab" tab-position="left" @tab-click="onTabChange">
          <el-tab-pane v-for="tab in typeTabs" :key="tab.code" :name="tab.code">
            <template #label>
              <span>
                {{ tab.label }}
                <el-badge
                  :value="tab.unreadCount"
                  :hidden="!tab.unreadCount"
                  style="margin-left: 8px"
                />
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>
      <!-- 右侧消息列表 -->
      <div
        v-infinite-scroll="loadMore"
        class="right-panel"
        :infinite-scroll-disabled="messages.length >= total"
        :infinite-scroll-distance="50"
      >
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['msg-item', { unread: msg.readStatus === 0 }]"
          @click="handleRead(msg)"
        >
          <div class="msg-left">
            <span v-if="msg.readStatus === 0" class="unread-dot" />
            <span class="msg-title">{{ msg.messageTitle }}</span>
          </div>
          <span class="msg-time">{{ msg.createTime }}</span>
        </div>
        <el-empty v-if="messages.length === 0" description="暂无消息" />
      </div>
    </div>
    <!-- 消息详情抽屉 -->
    <el-drawer v-model="drawerVisible" :title="currentMsg?.messageTitle" size="500px">
      <div v-if="currentMsg" class="msg-detail">
        <div class="detail-meta">
          <span>时间：{{ currentMsg.createTime }}</span>
        </div>
        <el-divider />
        <div class="detail-content" v-html="currentMsg.messageContent || '暂无内容'" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMessagePage, markRead, markAllRead, getUnreadCount } from '@/api/msg/message'
import type { MessageListVO } from '@/types/msg'

const activeTab = ref('all')
const keyword = ref('')
const messages = ref<MessageListVO[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 20
const drawerVisible = ref(false)
const currentMsg = ref<MessageListVO | null>(null)

const typeTabs = ref([
  { code: 'all', label: '全部', unreadCount: 0 },
  { code: 'system', label: '系统消息', unreadCount: 0 },
  { code: 'business', label: '业务消息', unreadCount: 0 },
  { code: 'warning', label: '预警消息', unreadCount: 0 },
  { code: 'todo', label: '待办消息', unreadCount: 0 }
])

function tabTypeId(code: string): string | undefined {
  return code === 'all' ? undefined : code
}

const loadMessages = async () => {
  pageNum.value = 1
  const res = await getMessagePage({
    typeId: tabTypeId(activeTab.value),
    keyword: keyword.value || undefined,
    pageNum: 1,
    pageSize
  })
  messages.value = res.records
  total.value = res.total
}

const loadMore = async () => {
  if (messages.value.length >= total.value) return
  pageNum.value++
  const res = await getMessagePage({
    typeId: tabTypeId(activeTab.value),
    keyword: keyword.value || undefined,
    pageNum: pageNum.value,
    pageSize
  })
  messages.value.push(...res.records)
}

const handleRead = async (msg: MessageListVO) => {
  if (msg.readStatus === 0) {
    await markRead(msg.id)
    msg.readStatus = 1
    const tab = typeTabs.value.find((t) => t.code === activeTab.value)
    if (tab && tab.unreadCount > 0) tab.unreadCount--
  }
  currentMsg.value = msg
  drawerVisible.value = true
}

const markAllReadAction = async () => {
  await markAllRead()
  messages.value.forEach((m) => (m.readStatus = 1))
  typeTabs.value.forEach((t) => (t.unreadCount = 0))
}

const onTabChange = async () => {
  keyword.value = ''
  pageNum.value = 1
  await loadMessages()
}

const fetchUnreadCounts = async () => {
  try {
    const res = await getUnreadCount()
    typeTabs.value.forEach((tab) => {
      const key = tab.code as keyof typeof res
      if (key in res) {
        tab.unreadCount = res[key]
      }
    })
  } catch {
    // unread count fetch failure is non-critical
  }
}

onMounted(async () => {
  await Promise.all([loadMessages(), fetchUnreadCounts()])
})
</script>

<style scoped lang="scss">
.message-center-list {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.top-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: var(--el-bg-color);
  border-radius: 12px;
  margin-bottom: 12px;
}

.main-content {
  flex: 1;
  display: flex;
  min-height: 0;
  background: var(--el-bg-color);
  border-radius: 12px;
  overflow: hidden;
}

.left-panel {
  width: 200px;
  border-right: 1px solid var(--el-border-color-light);
  padding-top: 8px;
  flex-shrink: 0;
}

.left-panel :deep(.el-tabs) {
  height: 100%;
}

.left-panel :deep(.el-tabs__header) {
  width: 100%;
}

.right-panel {
  flex: 1;
  padding: 12px 16px;
  overflow-y: auto;
  min-height: 0;
}

.msg-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: var(--el-fill-color-light);
  }

  &.unread {
    background: var(--el-color-primary-light-9);

    .msg-title {
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }
}

.msg-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--el-color-primary);
  flex-shrink: 0;
}

.msg-title {
  font-size: 14px;
  color: var(--el-text-color-regular);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-time {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  flex-shrink: 0;
  margin-left: 16px;
}

.msg-detail {
  padding: 0 4px;
}

.detail-meta {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.detail-content {
  font-size: 14px;
  line-height: 1.8;
  color: var(--el-text-color-regular);
}
</style>
