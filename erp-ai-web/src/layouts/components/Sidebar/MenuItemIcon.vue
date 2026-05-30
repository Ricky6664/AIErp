<template>
  <el-icon v-if="iconType === 'element' && elementIcon" class="menu-icon">
    <component :is="elementIcon" />
  </el-icon>

  <SvgIcon v-else-if="iconType === 'svg' && icon" :name="icon" class="menu-icon" />

  <img
    v-else-if="iconType === 'external' && icon"
    :src="icon"
    class="menu-icon menu-icon--external"
    alt=""
  />

  <span v-else class="menu-icon menu-icon--placeholder" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import * as ElIcons from '@element-plus/icons-vue'
import SvgIcon from '@/components/SvgIcon/index.vue'

defineOptions({ name: 'MenuItemIcon' })

interface Props {
  icon?: string
  iconType?: 'element' | 'svg' | 'external'
}
const props = withDefaults(defineProps<Props>(), {
  iconType: 'element'
})

const elementIcon = computed(() => {
  if (!props.icon) return null
  return (ElIcons as Record<string, any>)[props.icon] || ElIcons.Menu
})
</script>

<style lang="scss" scoped>
.menu-icon {
  width: 20px;
  height: 20px;
  font-size: 18px;
  margin-right: 8px;
  flex-shrink: 0;

  &--external {
    object-fit: contain;
  }

  &--placeholder {
    display: inline-block;
  }
}
</style>
