<template>
  <span v-if="svgContent" class="svg-icon" v-html="svgContent" />
  <span v-else class="svg-icon svg-icon--fallback" />
</template>

<script setup lang="ts">
import { computed } from 'vue'

defineOptions({ name: 'SvgIcon' })

interface Props {
  name: string
}
const props = defineProps<Props>()

const modules = import.meta.glob('@/assets/svg/*.svg', {
  query: '?raw',
  import: 'default',
  eager: true
}) as Record<string, string>

const svgContent = computed(() => {
  const key = `/src/assets/svg/${props.name}.svg`
  const altKey = Object.keys(modules).find((k) => k.endsWith(`/${props.name}.svg`))
  return modules[key] || (altKey ? modules[altKey] : null)
})
</script>

<style lang="scss" scoped>
.svg-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1em;
  height: 1em;
  vertical-align: middle;

  :deep(svg) {
    width: 100%;
    height: 100%;
    fill: currentColor;
  }

  &--fallback {
    width: 20px;
    height: 20px;
    background: var(--el-color-info-light-5);
    border-radius: 2px;
  }
}
</style>
