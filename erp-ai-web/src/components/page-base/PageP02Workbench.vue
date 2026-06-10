<template>
  <div class="page-p02-workbench">
    <!-- 顶部标题栏与统计卡片 -->
    <div class="workbench-header">
      <div class="header-left">
        <h2 class="page-title">{{ config.title || '工作台' }}</h2>
      </div>
      <div class="header-right">
        <slot name="header-extra" />
      </div>
    </div>

    <!-- 统计卡片行 -->
    <div v-if="config.statCards && config.statCards.length > 0" class="stat-cards-row">
      <div
        v-for="card in config.statCards"
        :key="card.id"
        class="stat-card-item"
        :class="`stat-card-${card.color || 'blue'}`"
        @click="card.to && handleNavigate(card.to)"
      >
        <div class="stat-card-icon">
          <el-icon :size="28">
            <component :is="card.icon" />
          </el-icon>
        </div>
        <div class="stat-card-info">
          <span class="stat-card-value">{{ card.value }}</span>
          <span class="stat-card-label">{{ card.label }}</span>
        </div>
      </div>
    </div>

    <!-- 查询区 -->
    <div v-if="config.showQueryPanel !== false" class="query-area">
      <slot name="query-panel">
        <div class="query-placeholder">
          <el-icon :size="18"><Search /></el-icon>
          <span>查询区 — 可通过 query-panel 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 操作栏 -->
    <div v-if="config.showActionBar !== false" class="action-area">
      <slot name="action-bar">
        <div class="action-placeholder">
          <el-icon :size="18"><Operation /></el-icon>
          <span>操作栏 — 可通过 action-bar 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 主内容区 -->
    <div class="main-area">
      <slot name="main-content">
        <div class="main-placeholder">
          <el-icon :size="48"><Document /></el-icon>
          <span>主内容区 — 可通过 main-content 插槽自定义</span>
        </div>
      </slot>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { Search, Operation, Document } from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, WorkbenchPageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<WorkbenchPageConfig>(() => {
  return (props.config || {}) as WorkbenchPageConfig
})

function handleNavigate(to: string): void {
  emit('navigate', { to })
}

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p02-workbench {
  padding: 16px;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

// 顶部标题栏
.workbench-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: var(--el-bg-color);
  border-radius: 12px;

  .header-left {
    .page-title {
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin: 0;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

// 统计卡片行
.stat-cards-row {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;

  .stat-card-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px 20px;
    background: var(--el-bg-color);
    border-radius: 12px;
    border-left: 4px solid var(--el-color-primary);
    cursor: default;
    transition:
      box-shadow 0.2s,
      transform 0.2s;

    &:hover {
      box-shadow: var(--el-box-shadow-light);
    }

    &.stat-card-blue {
      border-left-color: var(--el-color-primary);
    }
    &.stat-card-green {
      border-left-color: var(--el-color-success);
    }
    &.stat-card-orange {
      border-left-color: var(--el-color-warning);
    }
    &.stat-card-purple {
      border-left-color: #a855f7;
    }
    &.stat-card-red {
      border-left-color: var(--el-color-danger);
    }

    .stat-card-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 10px;
      background: var(--el-fill-color-light);
      color: var(--el-color-primary);

      .stat-card-blue & {
        color: var(--el-color-primary);
      }
      .stat-card-green & {
        color: var(--el-color-success);
      }
      .stat-card-orange & {
        color: var(--el-color-warning);
      }
      .stat-card-purple & {
        color: #a855f7;
      }
      .stat-card-red & {
        color: var(--el-color-danger);
      }
    }

    .stat-card-info {
      display: flex;
      flex-direction: column;
      gap: 2px;

      .stat-card-value {
        font-size: 24px;
        font-weight: 700;
        color: var(--el-text-color-primary);
        line-height: 1.2;
      }

      .stat-card-label {
        font-size: 13px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}

// 查询区
.query-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  min-height: 56px;
  display: flex;
  align-items: center;

  .query-placeholder {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--el-text-color-placeholder);
    font-size: 14px;
  }
}

// 操作栏
.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  min-height: 48px;
  display: flex;
  align-items: center;

  .action-placeholder {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--el-text-color-placeholder);
    font-size: 14px;
  }
}

// 主内容区
.main-area {
  flex: 1;
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
  min-height: 300px;
  display: flex;
  flex-direction: column;

  .main-placeholder {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12px;
    color: var(--el-text-color-placeholder);
    font-size: 14px;
  }
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
