<template>
  <Teleport to="body">
    <el-dialog
      v-model="visible"
      title="未读公告"
      width="480px"
      :close-on-click-modal="false"
      :destroy-on-close="false"
      class="announcement-popup"
      @close="handleClose"
    >
      <div v-if="unreadList.length > 0" class="announcement-popup__list">
        <div
          v-for="item in unreadList"
          :key="item.id"
          class="announcement-popup__item"
          @click="handleItemClick(item)"
        >
          <div class="announcement-popup__item-title">
            <el-tag
              v-if="item.isTop"
              size="small"
              type="danger"
              class="announcement-popup__item-top"
              >置顶</el-tag
            >
            <span>{{ item.title }}</span>
          </div>
          <div class="announcement-popup__item-time">{{ item.publishTime }}</div>
        </div>
      </div>
      <div v-else class="announcement-popup__empty">
        <el-empty description="暂无未读公告" :image-size="80" />
      </div>
    </el-dialog>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAnnouncementStore } from '@/stores/modules/announcement'
import { markAsRead } from '@/api/modules/announcement'
import type { AnnouncementListItem } from '@/api/types/announcement'

const DISMISSED_KEY = 'ANNOUNCEMENT_DISMISSED_IDS'
const CHECKED_KEY = 'ANNOUNCEMENT_CHECKED'

defineOptions({ name: 'AnnouncementPopup' })

const router = useRouter()
const announcementStore = useAnnouncementStore()

const visible = ref<boolean>(false)
const unreadList = ref<AnnouncementListItem[]>([])

onMounted(async () => {
  // 本次会话从未检查过才弹窗，避免路由切换导致重复弹窗
  if (sessionStorage.getItem(CHECKED_KEY)) return
  sessionStorage.setItem(CHECKED_KEY, '1')

  const list = await announcementStore.fetchUnreadList()
  if (list.length === 0) return

  const dismissedRaw = sessionStorage.getItem(DISMISSED_KEY)
  const dismissedIds: number[] = dismissedRaw ? JSON.parse(dismissedRaw) : []
  const currentIds = list.map((item) => item.id)
  const newIds = currentIds.filter((id) => !dismissedIds.includes(id))

  if (newIds.length > 0) {
    unreadList.value = list
    visible.value = true
  }
})

function handleClose(): void {
  sessionStorage.setItem(DISMISSED_KEY, JSON.stringify(unreadList.value.map((item) => item.id)))
}

async function handleItemClick(item: AnnouncementListItem): Promise<void> {
  try {
    await markAsRead(item.id)
    announcementStore.decrementUnreadCount()
    unreadList.value = unreadList.value.filter((u) => u.id !== item.id)
    if (unreadList.value.length === 0) {
      visible.value = false
      sessionStorage.setItem(DISMISSED_KEY, JSON.stringify([]))
    }
    router.push('/system/announcement')
  } catch {
    // 即使标记已读失败，也执行跳转
    router.push('/system/announcement')
  }
}
</script>

<style lang="scss" scoped>
.announcement-popup {
  &__list {
    max-height: 360px;
    overflow-y: auto;
  }

  &__item {
    padding: 12px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
    cursor: pointer;
    transition: background-color 0.2s;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background-color: var(--el-fill-color-light);
      margin: 0 -20px;
      padding-left: 20px;
      padding-right: 20px;
    }
  }

  &__item-title {
    font-size: 14px;
    color: var(--el-text-color-primary);
    font-weight: 500;
  }

  &__item-top {
    margin-right: 4px;
  }

  &__item-time {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-top: 4px;
  }

  &__empty {
    padding: 20px 0;
  }
}
</style>
