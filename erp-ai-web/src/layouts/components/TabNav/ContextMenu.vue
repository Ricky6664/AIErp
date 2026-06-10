<template>
  <Teleport to="body">
    <ul
      v-show="visible"
      class="tab-context-menu"
      :style="{ left: position.x + 'px', top: position.y + 'px' }"
    >
      <li @click="handleAction('refresh')">
        <el-icon><Refresh /></el-icon> 刷新当前页
      </li>
      <li :class="{ 'is-disabled': selectedTag?.affix }" @click="handleAction('close')">
        <el-icon><Close /></el-icon> 关闭当前
      </li>
      <li @click="handleAction('closeOther')">
        <el-icon><Switch /></el-icon> 关闭其他
      </li>
      <li @click="handleAction('closeLeft')">
        <el-icon><Back /></el-icon> 关闭左侧
      </li>
      <li @click="handleAction('closeRight')">
        <el-icon><Right /></el-icon> 关闭右侧
      </li>
      <li @click="handleAction('closeAll')">
        <el-icon><Minus /></el-icon> 关闭全部
      </li>
    </ul>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh, Close, Switch, Back, Right, Minus } from '@element-plus/icons-vue'
import { useTagsViewStore } from '@/stores/modules/tagsView'
import type { TagView } from '@/stores/modules/tagsView'

defineOptions({ name: 'TabContextMenu' })

interface Props {
  selectedTag?: TagView
}
const props = defineProps<Props>()

const router = useRouter()
const visible = ref(false)
const position = ref({ x: 0, y: 0 })
const tagsViewStore = useTagsViewStore()

function open(event: MouseEvent) {
  event.preventDefault()
  const menuWidth = 140
  const menuHeight = 216
  let x = event.clientX
  let y = event.clientY
  if (x + menuWidth > window.innerWidth) {
    x = window.innerWidth - menuWidth - 8
  }
  if (y + menuHeight > window.innerHeight) {
    y = window.innerHeight - menuHeight - 8
  }
  position.value = { x, y }
  visible.value = true
}

function close() {
  visible.value = false
}

function handleAction(action: string) {
  if (!props.selectedTag) return
  if (action === 'close' && props.selectedTag.affix) return

  switch (action) {
    case 'refresh':
      tagsViewStore.refreshSelectedPage(router.currentRoute.value)
      break
    case 'close':
      tagsViewStore.closeSelectedTag(props.selectedTag)
      break
    case 'closeOther':
      tagsViewStore.closeOtherTags(props.selectedTag)
      break
    case 'closeLeft':
      tagsViewStore.closeLeftTags(props.selectedTag)
      break
    case 'closeRight':
      tagsViewStore.closeRightTags(props.selectedTag)
      break
    case 'closeAll':
      tagsViewStore.closeAllTags()
      break
  }
  close()
}

function handleClickOutside() {
  close()
}

defineExpose({ open, close })

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.tab-context-menu {
  position: fixed;
  z-index: 9999;
  min-width: 140px;
  margin: 0;
  padding: 4px 0;
  list-style: none;
  background: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color-light);
  border-radius: var(--el-border-radius-base);
  box-shadow: var(--el-box-shadow-light);
}

.tab-context-menu li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  font-size: 13px;
  color: var(--el-text-color-regular);
  cursor: pointer;
  white-space: nowrap;
}

.tab-context-menu li:hover {
  background: var(--el-fill-color-light);
  color: var(--el-color-primary);
}

.tab-context-menu li.is-disabled {
  color: var(--el-text-color-placeholder);
  cursor: not-allowed;
  pointer-events: none;
}

.tab-context-menu li .el-icon {
  font-size: 14px;
}
</style>
