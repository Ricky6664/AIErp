<template>
  <Teleport to="body">
    <el-dialog
      v-model="visible"
      title="全局搜索"
      width="600px"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      class="search-dialog"
    >
      <el-input
        ref="inputRef"
        v-model="searchKeyword"
        placeholder="搜索菜单、功能..."
        :prefix-icon="Search"
        size="large"
        clearable
        @input="handleSearch"
      />
      <div v-if="results.length > 0" class="search-dialog__results">
        <div
          v-for="item in results"
          :key="item.path"
          class="search-dialog__item"
          @click="navigateTo(item.path)"
        >
          <el-icon :size="16"><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </div>
      </div>
      <div v-else-if="searchKeyword" class="search-dialog__empty">未找到匹配结果</div>
      <div v-else class="search-dialog__hint"><kbd>Ctrl</kbd> + <kbd>K</kbd> 快速打开搜索</div>
    </el-dialog>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

defineOptions({ name: 'SearchDialog' })

const router = useRouter()
const visible = ref(false)
const searchKeyword = ref('')
const inputRef = ref()

interface SearchResult {
  title: string
  path: string
  icon: string
}

const results = ref<SearchResult[]>([])

function handleKeydown(e: KeyboardEvent) {
  if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
    e.preventDefault()
    visible.value = !visible.value
    if (visible.value) {
      nextTick(() => {
        inputRef.value?.focus()
      })
    }
  }
}

function handleSearch() {
  // 搜索逻辑在后续消息模块接入API后完善
  results.value = []
}

function navigateTo(path: string) {
  visible.value = false
  searchKeyword.value = ''
  results.value = []
  router.push(path)
}

function toggle() {
  visible.value = !visible.value
  if (visible.value) {
    nextTick(() => {
      inputRef.value?.focus()
    })
  }
}

onMounted(() => document.addEventListener('keydown', handleKeydown))
onUnmounted(() => document.removeEventListener('keydown', handleKeydown))

defineExpose({ toggle })
</script>

<style lang="scss" scoped>
.search-dialog {
  &__results {
    margin-top: 16px;
    max-height: 300px;
    overflow-y: auto;
  }

  &__item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 12px;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background-color: var(--el-fill-color-light);
    }
  }

  &__empty {
    margin-top: 24px;
    text-align: center;
    color: var(--el-text-color-secondary);
    font-size: 14px;
  }

  &__hint {
    margin-top: 24px;
    text-align: center;
    color: var(--el-text-color-secondary);
    font-size: 13px;

    kbd {
      display: inline-block;
      padding: 2px 6px;
      font-size: 12px;
      font-family: inherit;
      background: var(--el-fill-color);
      border: 1px solid var(--el-border-color);
      border-radius: 4px;
    }
  }
}
</style>
