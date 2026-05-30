<template>
  <el-container class="app-layout">
    <!-- 左侧边栏 -->
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="app-layout__aside">
      <Sidebar />
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
            <keep-alive :include="cachedViews">
              <component :is="Component" :key="route.fullPath" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Sidebar from './components/Sidebar/index.vue'
import Navbar from './components/Navbar.vue'
import TabNav from './components/TabNav/index.vue'
import { useLayoutStore } from '@/stores/modules/layout'
import { useTagsViewStore } from '@/stores/modules/tagsView'

defineOptions({ name: 'AppLayout' })

const layoutStore = useLayoutStore()
const tagsViewStore = useTagsViewStore()

const isCollapsed = computed(() => layoutStore.isCollapsed)
const cachedViews = computed(() => tagsViewStore.cachedViews)
</script>
