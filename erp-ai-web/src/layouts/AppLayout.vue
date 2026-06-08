<template>
  <el-container class="app-layout" :class="{ 'is-mobile': isMobile }">
    <!-- 移动端遮罩层 -->
    <div
      v-if="isMobile && !isCollapsed"
      class="app-layout__overlay"
      @click="layoutStore.toggleCollapse()"
    />

    <!-- 左侧边栏 -->
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="app-layout__aside">
      <Sidebar :is-collapsed="isCollapsed" />
    </el-aside>

    <!-- 右侧主区域 -->
    <el-container class="app-layout__main-container">
      <!-- 顶部导航栏 -->
      <el-header class="app-layout__header" height="56px">
        <Navbar />
      </el-header>

      <!-- 标签页导航 -->
      <div class="app-layout__tabs">
        <TabNav />
      </div>

      <!-- 内容区域 -->
      <el-main class="app-layout__content">
        <router-view v-slot="{ Component, route }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive :include="cachedViews" :max="MAX_CACHED_VIEWS">
              <component :is="Component" :key="route.fullPath" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 公告通知弹窗 -->
    <AnnouncementPopup />
  </el-container>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import Sidebar from './components/Sidebar/index.vue'
import Navbar from './components/Navbar.vue'
import TabNav from './components/TabNav/index.vue'
import AnnouncementPopup from '@/components/AnnouncementPopup.vue'
import { useLayoutStore } from '@/stores/modules/layout'
import { useTagsViewStore } from '@/stores/modules/tagsView'
import { MAX_CACHED_VIEWS } from '@/stores/modules/tagsView'
import { useResponsive } from '@/composables/useResponsive'

defineOptions({ name: 'AppLayout' })

const layoutStore = useLayoutStore()
const tagsViewStore = useTagsViewStore()

const { isMobile } = useResponsive()

const isCollapsed = computed(() => layoutStore.isCollapsed)
const cachedViews = computed(() => tagsViewStore.cachedViews)

// 移动端自动折叠侧边栏
watch(
  isMobile,
  (mobile) => {
    if (mobile && !layoutStore.isCollapsed) {
      layoutStore.toggleCollapse()
    }
  },
  { immediate: true }
)
</script>

<style lang="scss">
@use './styles/app-layout.scss';
@use './styles/navbar-responsive.scss';
</style>
