import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadList } from '@/api/modules/announcement'
import type { AnnouncementListItem } from '@/api/types/announcement'

export const useAnnouncementStore = defineStore('announcement', () => {
  const unreadCount = ref<number>(0)

  async function fetchUnreadList(): Promise<AnnouncementListItem[]> {
    try {
      const list = await getUnreadList()
      unreadCount.value = list.length
      return list
    } catch {
      unreadCount.value = 0
      return []
    }
  }

  function decrementUnreadCount(): void {
    if (unreadCount.value > 0) {
      unreadCount.value--
    }
  }

  return { unreadCount, fetchUnreadList, decrementUnreadCount }
})
