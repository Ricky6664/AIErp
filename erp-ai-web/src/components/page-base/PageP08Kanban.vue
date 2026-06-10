<template>
  <div class="page-p08-kanban">
    <!-- 查询区 -->
    <div v-if="config.showQueryPanel !== false" class="query-area">
      <slot name="query-panel">
        <div class="area-placeholder">
          <el-icon :size="18"><Search /></el-icon>
          <span>查询区 — 可通过 query-panel 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 操作栏 -->
    <div v-if="config.showActionBar !== false" class="action-area">
      <slot name="action-bar">
        <div class="area-placeholder">
          <el-icon :size="18"><Operation /></el-icon>
          <span>操作栏 — 可通过 action-bar 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 看板主内容区 -->
    <div class="main-content-area">
      <div class="kanban-board" :style="boardStyle">
        <div
          v-for="column in config.columns"
          :key="column.id"
          class="kanban-column"
          :style="columnStyle"
        >
          <div class="column-header" :class="column.color ? `column-header--${column.color}` : ''">
            <div class="column-header-left">
              <span
                v-if="column.color"
                class="column-dot"
                :style="{ backgroundColor: `var(--el-color-${column.color})` }"
              />
              <span class="column-title">{{ column.label }}</span>
            </div>
            <el-tag size="small" round>{{ column.items?.length ?? 0 }}</el-tag>
          </div>
          <div class="column-body">
            <div
              v-for="item in column.items"
              :key="item.id"
              class="kanban-card"
              @click="handleCardClick(item)"
            >
              <div class="card-header">
                <span class="card-title">{{ item.title }}</span>
                <el-tag
                  v-if="item.priority"
                  :type="priorityTagType(item.priority)"
                  size="small"
                  effect="plain"
                >
                  {{ item.priority }}
                </el-tag>
              </div>
              <p v-if="item.description" class="card-description">{{ item.description }}</p>
              <div v-if="item.tags && item.tags.length > 0" class="card-tags">
                <el-tag v-for="tag in item.tags" :key="tag" size="small" class="card-tag">{{
                  tag
                }}</el-tag>
              </div>
              <div v-if="item.assignee" class="card-footer">
                <el-icon :size="14"><User /></el-icon>
                <span class="card-assignee">{{ item.assignee }}</span>
              </div>
            </div>
            <div v-if="!column.items || column.items.length === 0" class="column-empty">
              <span>暂无数据</span>
            </div>
          </div>
        </div>
        <div v-if="!config.columns || config.columns.length === 0" class="kanban-empty">
          <slot name="main-content">
            <div class="area-placeholder">
              <el-icon :size="48"><DataBoard /></el-icon>
              <span>看板区 — 可通过 main-content 插槽自定义</span>
            </div>
          </slot>
        </div>
      </div>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { Search, Operation, DataBoard, User } from '@element-plus/icons-vue'
import type {
  PageBaseProps,
  PageBaseEmits,
  KanbanPageConfig,
  KanbanItemConfig
} from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<KanbanPageConfig>(() => {
  return (props.config || {}) as KanbanPageConfig
})

const boardStyle = computed<Record<string, string>>(() => {
  return {}
})

const columnStyle = computed<Record<string, string>>(() => {
  const width = config.value.columnWidth ?? 280
  const w = typeof width === 'number' ? `${width}px` : width
  return {
    flex: `0 0 ${w}`,
    minWidth: w
  }
})

function priorityTagType(
  priority: KanbanItemConfig['priority']
): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    low: 'info',
    medium: 'warning',
    high: 'danger',
    urgent: ''
  }
  return priority ? (map[priority] ?? 'info') : 'info'
}

function handleCardClick(item: KanbanItemConfig) {
  emit('data-change', { source: 'kanban-card-click', data: item })
}

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p08-kanban {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.area-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  min-height: 80px;
}

// 查询区
.query-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  min-height: 56px;
  display: flex;
  align-items: center;
}

// 操作栏
.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  min-height: 48px;
  display: flex;
  align-items: center;
}

// 主内容区
.main-content-area {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.kanban-board {
  display: flex;
  gap: 16px;
  height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 4px;
}

// 看板列
.kanban-column {
  display: flex;
  flex-direction: column;
  background: var(--el-fill-color-light);
  border-radius: 12px;
  padding: 12px;
  min-height: 0;
}

.column-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 4px 12px 4px;
  border-bottom: 2px solid var(--el-border-color-lighter);
  margin-bottom: 8px;
  flex-shrink: 0;

  &--primary {
    border-bottom-color: var(--el-color-primary);
  }
  &--success {
    border-bottom-color: var(--el-color-success);
  }
  &--warning {
    border-bottom-color: var(--el-color-warning);
  }
  &--danger {
    border-bottom-color: var(--el-color-danger);
  }
  &--info {
    border-bottom-color: var(--el-color-info);
  }
}

.column-header-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.column-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.column-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

// 列内容
.column-body {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 0;
}

// 看板卡片
.kanban-card {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  border: 1px solid var(--el-border-color-lighter);
  transition:
    box-shadow 0.2s,
    border-color 0.2s;

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border-color: var(--el-color-primary-light-5);
  }
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 6px;
  margin-bottom: 6px;
}

.card-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  line-height: 1.4;
  word-break: break-word;
}

.card-description {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  margin: 0 0 8px 0;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 8px;
}

.card-tag {
  font-size: 11px;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--el-text-color-placeholder);
  font-size: 12px;
  margin-top: 4px;
}

.card-assignee {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 列空状态
.column-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder);
  font-size: 13px;
  min-height: 120px;
}

// 全空状态
.kanban-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
