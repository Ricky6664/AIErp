<template>
  <div class="tab-nav">
    <!-- TabNav component stub -->
  </div>
</template>

<script setup lang="ts">
import type { TagView } from '@/stores/modules/tagsView'

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

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const emit = defineEmits<{
  (e: 'select', path: string): void
  (e: 'close', tag: TagView): void
  (e: 'refresh', path: string): void
  (e: 'contextmenu', event: MouseEvent, tag: TagView): void
}>()
</script>
