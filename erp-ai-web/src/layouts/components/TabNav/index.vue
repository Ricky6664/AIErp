<template>
  <div class="tab-nav">
    <div ref="scrollRef" class="tab-nav__scroll">
      <div class="tab-nav__list">
        <div
          v-for="tag in views"
          :key="tag.fullPath"
          class="tab-nav__item"
          :class="{
            'tab-nav__item--active': tag.fullPath === activePath,
            'tab-nav__item--affix': tag.affix
          }"
          @click="emit('select', tag.fullPath)"
          @contextmenu="emit('contextmenu', $event, tag)"
        >
          <span class="tab-nav__title">{{ tag.title }}</span>

          <el-icon v-if="!tag.affix" class="tab-nav__close" @click.stop="emit('close', tag)">
            <Close />
          </el-icon>
        </div>
      </div>
    </div>

    <ContextMenu ref="contextMenuRef" :selected-tag="selectedTag" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Close } from '@element-plus/icons-vue'
import type { TagView } from '@/stores/modules/tagsView'
import ContextMenu from './ContextMenu.vue'

defineOptions({ name: 'TabNav' })

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = withDefaults(
  defineProps<{
    activePath: string
    views: readonly TagView[]
  }>(),
  {
    activePath: '',
    views: () => []
  }
)

const emit = defineEmits<{
  (e: 'select', path: string): void
  (e: 'close', tag: TagView): void
  (e: 'refresh', path: string): void
  (e: 'contextmenu', event: MouseEvent, tag: TagView): void
}>()

const scrollRef = ref<HTMLElement>()
const contextMenuRef = ref<InstanceType<typeof ContextMenu>>()
const selectedTag = ref<TagView>()

void scrollRef.value
void contextMenuRef.value
</script>

<style lang="scss" scoped>
.tab-nav {
  height: 34px;
  display: flex;
  align-items: center;
  padding: 0 8px;
  background: var(--el-bg-color);

  &__scroll {
    flex: 1;
    overflow-x: auto;
    overflow-y: hidden;
    &::-webkit-scrollbar {
      display: none;
    }
    scrollbar-width: none;
  }

  &__list {
    display: flex;
    gap: 4px;
    white-space: nowrap;
  }

  &__item {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    min-width: 80px;
    max-width: 160px;
    height: 26px;
    padding: 0 10px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 3px;
    cursor: pointer;
    font-size: 12px;
    color: var(--el-text-color-regular);
    background: var(--el-bg-color);
    transition: all 0.2s;
    user-select: none;

    &:hover {
      color: var(--el-color-primary);
    }

    &--active {
      color: var(--el-color-primary);
      background: var(--el-color-primary-light-9);
      border-color: var(--el-color-primary-light-5);
    }
  }

  &__title {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__close {
    flex-shrink: 0;
    font-size: 12px;
    border-radius: 50%;
    &:hover {
      background: var(--el-color-danger-light-5);
      color: #fff;
    }
  }
}
</style>
