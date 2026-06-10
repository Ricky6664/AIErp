<template>
  <div class="page-p12-profile">
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

    <!-- 个人中心头部 -->
    <div class="profile-header">
      <div class="profile-header-bg" />
      <div class="profile-header-content">
        <div class="profile-avatar-section">
          <el-avatar :size="80" :src="config.avatar" class="profile-avatar">
            <el-icon :size="36"><UserFilled /></el-icon>
          </el-avatar>
        </div>
        <div class="profile-info-section">
          <h2 v-if="config.userName" class="profile-name">{{ config.userName }}</h2>
          <p v-if="config.userRole" class="profile-role">
            <el-icon :size="14"><Avatar /></el-icon>
            {{ config.userRole }}
          </p>
          <p v-if="config.description" class="profile-desc">{{ config.description }}</p>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div v-if="config.stats && config.stats.length > 0" class="profile-stats">
        <div
          v-for="stat in config.stats"
          :key="stat.id"
          class="profile-stat-card"
          :class="stat.color ? `stat-card--${stat.color}` : ''"
        >
          <div class="stat-card-icon">
            <el-icon v-if="stat.icon" :size="22">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="stat-card-body">
            <div class="stat-card-value">{{ stat.value }}</div>
            <div class="stat-card-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 标签页导航 -->
    <div v-if="config.tabs && config.tabs.length > 0" class="profile-tabs-nav">
      <div
        v-for="tab in config.tabs"
        :key="tab.id"
        class="profile-tab-item"
        :class="{ 'is-active': activeTab === tab.id }"
        @click="handleTabClick(tab.id)"
      >
        <el-icon v-if="tab.icon" :size="16">
          <component :is="tab.icon" />
        </el-icon>
        <span>{{ tab.label }}</span>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content-area">
      <div class="form-wrapper" :style="formWrapperStyle">
        <slot name="main-content">
          <div class="area-placeholder">
            <el-icon :size="48"><User /></el-icon>
            <span>主内容区 — 可通过 main-content 插槽自定义</span>
          </div>
        </slot>
      </div>
    </div>

    <!-- 额外区域 -->
    <div v-if="$slots['extra-area']" class="extra-area">
      <slot name="extra-area" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { Search, Operation, User, UserFilled, Avatar } from '@element-plus/icons-vue'
import type { PageBaseProps, PageBaseEmits, ProfilePageConfig } from '@/types/page-base.d.ts'

const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

const config = computed<ProfilePageConfig>(() => {
  return (props.config || {}) as ProfilePageConfig
})

const activeTab = ref<string>(config.value.activeTab ?? config.value.tabs?.[0]?.id ?? '')

watch(activeTab, (val) => {
  emit('data-change', { source: 'profile-tab-change', data: { activeTab: val } })
})

watch(
  () => config.value.activeTab,
  (val) => {
    if (val !== undefined) {
      activeTab.value = val
    }
  }
)

const formWrapperStyle = computed<Record<string, string>>(() => {
  const maxWidth = config.value.formMaxWidth ?? 960
  const width = typeof maxWidth === 'number' ? `${maxWidth}px` : maxWidth
  return {
    maxWidth: width
  }
})

function handleTabClick(tabId: string) {
  activeTab.value = tabId
}

onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p12-profile {
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

// 个人中心头部
.profile-header {
  background: var(--el-bg-color);
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.profile-header-bg {
  height: 80px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 50%, #2c6fae 100%);
}

.profile-header-content {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  padding: 0 24px 16px;
  margin-top: -40px;
}

.profile-avatar-section {
  flex-shrink: 0;
}

.profile-avatar {
  border: 4px solid var(--el-bg-color);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  background: var(--el-color-primary-light-7);
  color: var(--el-color-primary);
}

.profile-info-section {
  flex: 1;
  min-width: 0;
  padding-top: 44px;
}

.profile-name {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.3;
}

.profile-role {
  margin: 0 0 4px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.profile-desc {
  margin: 0;
  font-size: 13px;
  color: var(--el-text-color-placeholder);
  line-height: 1.5;
}

// 统计卡片
.profile-stats {
  display: flex;
  gap: 1px;
  border-top: 1px solid var(--el-border-color-lighter);
  background: var(--el-border-color-lighter);
}

.profile-stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--el-bg-color);
  transition: background-color 0.2s;

  &:hover {
    background: var(--el-fill-color-light);
  }
}

.stat-card-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: var(--el-fill-color-light);
  color: var(--el-color-primary);
}

.stat-card--blue .stat-card-icon {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.stat-card--green .stat-card-icon {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.stat-card--orange .stat-card-icon {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-card--purple .stat-card-icon {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
}

.stat-card--red .stat-card-icon {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.stat-card-body {
  min-width: 0;
}

.stat-card-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.3;
}

.stat-card-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 2px;
}

// 标签页导航
.profile-tabs-nav {
  display: flex;
  gap: 0;
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 4px;
  flex-shrink: 0;
}

.profile-tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  color: var(--el-text-color-regular);
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;

  &:hover {
    color: var(--el-color-primary);
    background: var(--el-fill-color-light);
  }

  &.is-active {
    color: var(--el-color-primary);
    background: var(--el-color-primary-light-9);
    font-weight: 500;
  }
}

// 主内容区
.main-content-area {
  flex: 1;
  display: flex;
  justify-content: center;
  min-height: 0;
}

.form-wrapper {
  width: 100%;
  max-width: 960px;
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px 32px;
  overflow: auto;
}

// 额外区域
.extra-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px 20px;
}
</style>
