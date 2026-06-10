<template>
  <div class="page-p15-designer">
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

    <!-- 设计器主体：三栏布局 -->
    <div class="designer-main">
      <!-- 左侧：组件面板 -->
      <transition name="palette-slide">
        <div v-if="showPalette" class="designer-palette">
          <div class="palette-header">
            <el-icon :size="16"><Grid /></el-icon>
            <span>组件面板</span>
            <el-button
              v-if="config.showPalette !== false"
              text
              :icon="Close"
              size="small"
              @click="showPalette = false"
            />
          </div>

          <!-- 分类筛选 -->
          <div v-if="categorizedComponents.length > 0" class="palette-categories">
            <el-radio-group v-model="paletteCategory" size="small" @change="handleCategoryChange">
              <el-radio-button value="all">全部</el-radio-button>
              <el-radio-button v-for="cat in paletteCategories" :key="cat.key" :value="cat.key">
                {{ cat.label }}
              </el-radio-button>
            </el-radio-group>
          </div>

          <!-- 组件列表 -->
          <div class="palette-list">
            <div
              v-for="comp in filteredPaletteComponents"
              :key="comp.type"
              class="palette-item"
              draggable="true"
              @dragstart="handleDragStart($event, comp)"
            >
              <el-icon :size="16">
                <component :is="comp.icon" />
              </el-icon>
              <span>{{ comp.label }}</span>
            </div>
            <div v-if="filteredPaletteComponents.length === 0" class="palette-empty">
              暂无可用组件
            </div>
          </div>
        </div>
      </transition>

      <!-- 中间：设计画布 -->
      <div
        class="designer-canvas"
        :class="{ 'canvas-expanded': !showPalette || !showProperties }"
        @dragover.prevent="handleDragOver"
        @dragleave="handleDragLeave"
        @drop.prevent="handleDrop"
      >
        <!-- 画布工具栏 -->
        <div class="canvas-toolbar">
          <el-button
            v-if="config.showPalette !== false && !showPalette"
            text
            :icon="Grid"
            size="small"
            @click="showPalette = true"
          >
            组件面板
          </el-button>
          <el-button
            v-if="config.showProperties !== false && !showProperties"
            text
            :icon="Setting"
            size="small"
            @click="showProperties = true"
          >
            属性面板
          </el-button>
          <div class="canvas-toolbar-spacer" />
          <el-button
            v-if="placedComponents.length > 0"
            text
            :icon="Delete"
            size="small"
            @click="handleClearAll"
          >
            清空画布
          </el-button>
        </div>

        <!-- 画布内容 -->
        <div ref="canvasRef" class="canvas-body" :class="{ 'is-dragover': isDragOver }">
          <!-- 空状态 -->
          <div v-if="placedComponents.length === 0" class="canvas-placeholder">
            <el-icon :size="48"><Plus /></el-icon>
            <span class="canvas-placeholder-title">拖拽组件到此处构建页面</span>
            <span class="canvas-placeholder-desc">
              从左侧组件面板选择组件拖入画布，自由组合页面布局
            </span>
          </div>

          <!-- 已放置组件列表 -->
          <div
            v-for="(comp, idx) in placedComponents"
            :key="comp.id"
            class="canvas-component"
            :class="{
              'is-selected': selectedIndex === idx,
              'is-hovering': hoveringIndex === idx
            }"
            :style="{ gridColumn: `span ${comp.span || 24}` }"
            @click.stop="selectComponent(idx)"
            @mouseenter="hoveringIndex = idx"
            @mouseleave="hoveringIndex = null"
          >
            <!-- 组件头部 -->
            <div class="canvas-comp-header">
              <div class="comp-header-left">
                <el-icon :size="14">
                  <component :is="getCompIcon(comp.type)" />
                </el-icon>
                <span>{{ comp.label }}</span>
                <el-tag size="small" effect="plain" type="info">{{
                  getRegionLabel(comp.region)
                }}</el-tag>
              </div>
              <div class="comp-header-actions">
                <el-button
                  text
                  :icon="Top"
                  size="small"
                  :disabled="idx === 0"
                  @click.stop="moveComponent(idx, -1)"
                />
                <el-button
                  text
                  :icon="Bottom"
                  size="small"
                  :disabled="idx === placedComponents.length - 1"
                  @click.stop="moveComponent(idx, 1)"
                />
                <el-button text :icon="Close" size="small" @click.stop="removeComponent(idx)" />
              </div>
            </div>
            <!-- 组件内容区 -->
            <div class="canvas-comp-body">
              <slot :name="`comp-${comp.id}`" :comp="comp" :index="idx">
                <div class="comp-placeholder">
                  <el-icon :size="24"><component :is="getCompIcon(comp.type)" /></el-icon>
                  <span>{{ getCompPlaceholder(comp.type) }}</span>
                </div>
              </slot>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：属性面板 -->
      <transition name="properties-slide">
        <div v-if="showProperties" class="designer-properties">
          <div class="properties-header">
            <el-icon :size="16"><Setting /></el-icon>
            <span>属性配置</span>
            <el-button
              v-if="config.showProperties !== false"
              text
              :icon="Close"
              size="small"
              @click="showProperties = false"
            />
          </div>
          <div class="properties-body">
            <template v-if="selectedComponent">
              <div class="prop-group">
                <div class="prop-group-title">基本信息</div>
                <div class="prop-item">
                  <label>组件ID</label>
                  <el-input
                    v-model="selectedComponent.id"
                    size="small"
                    @change="handlePropChange"
                  />
                </div>
                <div class="prop-item">
                  <label>组件标签</label>
                  <el-input
                    v-model="selectedComponent.label"
                    size="small"
                    @change="handlePropChange"
                  />
                </div>
                <div class="prop-item">
                  <label>组件类型</label>
                  <el-select
                    v-model="selectedComponent.type"
                    size="small"
                    @change="handlePropChange"
                  >
                    <el-option
                      v-for="ac in config.availableComponents || defaultAvailableComponents"
                      :key="ac.type"
                      :label="ac.label"
                      :value="ac.type"
                    />
                  </el-select>
                </div>
              </div>
              <div class="prop-group">
                <div class="prop-group-title">布局设置</div>
                <div class="prop-item">
                  <label>放置区域</label>
                  <el-select
                    v-model="selectedComponent.region"
                    size="small"
                    @change="handlePropChange"
                  >
                    <el-option label="查询区" value="query" />
                    <el-option label="操作区" value="action" />
                    <el-option label="主内容区" value="main" />
                    <el-option label="额外区域" value="extra" />
                  </el-select>
                </div>
                <div class="prop-item">
                  <label>栅格跨度</label>
                  <el-input-number
                    v-model="selectedComponent.span"
                    :min="1"
                    :max="24"
                    size="small"
                    @change="handlePropChange"
                  />
                </div>
              </div>
              <div class="prop-group-actions">
                <el-button
                  type="danger"
                  :icon="Delete"
                  size="small"
                  plain
                  @click="removeComponent(selectedIndex!)"
                >
                  删除组件
                </el-button>
              </div>
            </template>
            <div v-else class="properties-empty">
              <el-icon :size="36"><InfoFilled /></el-icon>
              <span>请选择画布中的组件进行配置</span>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- 额外区域 -->
    <slot name="extra-area" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  Search,
  Operation,
  Plus,
  Close,
  Grid,
  Setting,
  Delete,
  Top,
  Bottom,
  InfoFilled
} from '@element-plus/icons-vue'
import type {
  PageBaseProps,
  PageBaseEmits,
  DesignerPageConfig,
  DesignerComponentItemConfig,
  DesignerAvailableComponentConfig
} from '@/types/page-base.d.ts'

// ---------- Props / Emits ----------
const props = defineProps<PageBaseProps>()

const emit = defineEmits<PageBaseEmits>()

// ---------- Computed config ----------
const config = computed<DesignerPageConfig>(() => {
  return (props.config || {}) as DesignerPageConfig
})

// ---------- Default available components ----------
const defaultAvailableComponents: DesignerAvailableComponentConfig[] = [
  { type: 'list-table', label: '数据表格', icon: 'List', category: 'data' },
  { type: 'master-form', label: '主从表单', icon: 'Document', category: 'form' },
  { type: 'simple-form', label: '简单表单', icon: 'Edit', category: 'form' },
  { type: 'query-panel', label: '查询面板', icon: 'Search', category: 'layout' },
  { type: 'action-bar', label: '操作栏', icon: 'Operation', category: 'layout' },
  { type: 'kanban', label: '看板', icon: 'DataBoard', category: 'data' },
  { type: 'chart', label: '图表', icon: 'DataAnalysis', category: 'chart' },
  { type: 'custom', label: '自定义', icon: 'More', category: 'other' }
]

const availableComps = computed<DesignerAvailableComponentConfig[]>(() => {
  return config.value.availableComponents && config.value.availableComponents.length > 0
    ? config.value.availableComponents
    : defaultAvailableComponents
})

// ---------- Palette state ----------
const showPalette = ref(config.value.showPalette !== false)
const showProperties = ref(config.value.showProperties !== false)
const paletteCategory = ref<string>('all')

const paletteCategories = computed<{ key: string; label: string }[]>(() => {
  const cats = new Map<string, string>()
  cats.set('layout', '布局')
  cats.set('data', '数据')
  cats.set('form', '表单')
  cats.set('chart', '图表')
  cats.set('other', '其他')
  const seen = new Set<string>()
  const result: { key: string; label: string }[] = []
  for (const comp of availableComps.value) {
    const cat = comp.category || 'other'
    if (!seen.has(cat)) {
      seen.add(cat)
      result.push({ key: cat, label: cats.get(cat) || cat })
    }
  }
  return result
})

const categorizedComponents = computed(() => {
  const result: { category: string; items: DesignerAvailableComponentConfig[] }[] = []
  const cats = new Map<string, DesignerAvailableComponentConfig[]>()
  for (const comp of availableComps.value) {
    const cat = comp.category || 'other'
    if (!cats.has(cat)) cats.set(cat, [])
    cats.get(cat)!.push(comp)
  }
  cats.forEach((items, category) => {
    result.push({ category, items })
  })
  return result
})

const filteredPaletteComponents = computed<DesignerAvailableComponentConfig[]>(() => {
  if (paletteCategory.value === 'all') return availableComps.value
  return availableComps.value.filter((c) => (c.category || 'other') === paletteCategory.value)
})

function handleCategoryChange() {
  // palette filtered via computed
}

// ---------- Canvas state ----------
const canvasRef = ref<HTMLElement | null>(null)
const isDragOver = ref(false)
const hoveringIndex = ref<number | null>(null)
const selectedIndex = ref<number | null>(null)
let dragComp: DesignerAvailableComponentConfig | null = null

const placedComponents = ref<DesignerComponentItemConfig[]>(
  config.value.components ? [...config.value.components] : []
)

const selectedComponent = computed<DesignerComponentItemConfig | null>(() => {
  if (selectedIndex.value === null || selectedIndex.value < 0) return null
  return placedComponents.value[selectedIndex.value] ?? null
})

// ---------- Drag & Drop ----------
function handleDragStart(e: DragEvent, comp: DesignerAvailableComponentConfig) {
  dragComp = comp
  if (e.dataTransfer) {
    e.dataTransfer.effectAllowed = 'copy'
    e.dataTransfer.setData('text/plain', comp.type)
  }
}

function handleDragOver() {
  isDragOver.value = true
}

function handleDragLeave() {
  isDragOver.value = false
}

function handleDrop() {
  isDragOver.value = false
  if (!dragComp) return
  const newComp: DesignerComponentItemConfig = {
    id: `${dragComp.type}-${Date.now()}`,
    type: dragComp.type as DesignerComponentItemConfig['type'],
    label: dragComp.label,
    icon: dragComp.icon,
    region: 'main',
    span: 24,
    props: {}
  }
  placedComponents.value.push(newComp)
  selectedIndex.value = placedComponents.value.length - 1
  emitDataChange('component-add', { component: newComp })
  dragComp = null
}

// ---------- Component operations ----------
function selectComponent(idx: number) {
  selectedIndex.value = idx
}

function removeComponent(idx: number) {
  const removed = placedComponents.value[idx]
  placedComponents.value.splice(idx, 1)
  if (selectedIndex.value === idx) {
    selectedIndex.value = null
  } else if (selectedIndex.value !== null && selectedIndex.value > idx) {
    selectedIndex.value--
  }
  if (removed) {
    emitDataChange('component-remove', { componentId: removed.id })
  }
}

function moveComponent(idx: number, direction: number) {
  const targetIdx = idx + direction
  if (targetIdx < 0 || targetIdx >= placedComponents.value.length) return
  const tmp = placedComponents.value[idx]
  placedComponents.value[idx] = placedComponents.value[targetIdx]
  placedComponents.value[targetIdx] = tmp
  selectedIndex.value = targetIdx
  emitDataChange('component-move', { from: idx, to: targetIdx })
}

function handleClearAll() {
  placedComponents.value = []
  selectedIndex.value = null
  emitDataChange('canvas-clear', {})
}

function handlePropChange() {
  if (selectedComponent.value) {
    emitDataChange('component-prop-change', { component: selectedComponent.value })
  }
}

// ---------- Helpers ----------
function getCompIcon(type: string): string {
  const map: Record<string, string> = {
    'query-panel': 'Search',
    'action-bar': 'Operation',
    'list-table': 'List',
    'master-form': 'Document',
    'simple-form': 'Edit',
    kanban: 'DataBoard',
    chart: 'DataAnalysis',
    custom: 'More'
  }
  return map[type] || 'More'
}

function getRegionLabel(region: string): string {
  const map: Record<string, string> = {
    query: '查询区',
    action: '操作区',
    main: '主内容区',
    extra: '额外区'
  }
  return map[region] || region
}

function getCompPlaceholder(type: string): string {
  const map: Record<string, string> = {
    'query-panel': '查询面板内容区域',
    'action-bar': '操作按钮区域',
    'list-table': '数据表格内容区域',
    'master-form': '主从表单内容区域',
    'simple-form': '简单表单内容区域',
    kanban: '看板内容区域',
    chart: '图表展示区域',
    custom: '自定义内容区域'
  }
  return map[type] || '组件内容区域'
}

function emitDataChange(source: string, data: unknown) {
  emit('data-change', { source: `designer:${source}`, data })
}

// ---------- Lifecycle ----------
onMounted(() => {
  emit('page-ready', { viewId: props.viewId, pageType: props.pageType })
})
</script>

<style scoped lang="scss">
.page-p15-designer {
  padding: 12px 16px;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.area-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
  min-height: 44px;
}

.query-area,
.action-area {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 12px 20px;
  flex-shrink: 0;
}

// 主区域：三栏布局
.designer-main {
  flex: 1;
  display: flex;
  gap: 12px;
  min-height: 0;
}

// ---- 左侧：组件面板 ----
.designer-palette {
  width: 220px;
  flex-shrink: 0;
  background: var(--el-bg-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.palette-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);

  .el-button {
    margin-left: auto;
  }
}

.palette-categories {
  padding: 10px 12px;
  flex-shrink: 0;
  overflow-x: auto;

  .el-radio-group {
    flex-wrap: wrap;
    gap: 4px;
  }
}

.palette-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.palette-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color);
  cursor: grab;
  transition: all 0.15s;
  font-size: 13px;
  color: var(--el-text-color-regular);
  user-select: none;

  &:hover {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
    background: var(--el-color-primary-light-9);
  }

  &:active {
    cursor: grabbing;
  }
}

.palette-empty {
  display: flex;
  justify-content: center;
  padding: 24px;
  color: var(--el-text-color-placeholder);
  font-size: 13px;
}

// ---- 中间：设计画布 ----
.designer-canvas {
  flex: 1;
  background: var(--el-bg-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 400px;
  transition: flex 0.25s ease;

  &.canvas-expanded {
    flex: 2;
  }
}

.canvas-toolbar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}

.canvas-toolbar-spacer {
  flex: 1;
}

.canvas-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(24, 1fr);
  gap: 12px;
  align-content: start;
  transition: background 0.2s;

  &.is-dragover {
    background: var(--el-color-primary-light-9);
  }
}

.canvas-placeholder {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 64px 16px;
  color: var(--el-text-color-placeholder);
  text-align: center;
}

.canvas-placeholder-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-secondary);
}

.canvas-placeholder-desc {
  font-size: 13px;
  line-height: 1.7;
  color: var(--el-text-color-placeholder);
}

// ---- 画布中的组件卡片 ----
.canvas-component {
  border: 2px solid var(--el-border-color);
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.15s;
  background: var(--el-fill-color-lighter);
  display: flex;
  flex-direction: column;

  &.is-selected {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 3px var(--el-color-primary-light-8);
  }

  &.is-hovering:not(.is-selected) {
    border-color: var(--el-color-primary-light-5);
  }
}

.canvas-comp-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}

.comp-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.comp-header-actions {
  display: flex;
  gap: 2px;
}

.canvas-comp-body {
  padding: 16px;
  min-height: 80px;
}

.comp-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  color: var(--el-text-color-placeholder);
  font-size: 13px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
}

// ---- 右侧：属性面板 ----
.designer-properties {
  width: 260px;
  flex-shrink: 0;
  background: var(--el-bg-color);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.properties-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);

  .el-button {
    margin-left: auto;
  }
}

.properties-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.properties-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 48px 16px;
  color: var(--el-text-color-placeholder);
  font-size: 13px;
  text-align: center;
}

.prop-group {
  margin-bottom: 16px;
}

.prop-group-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--el-text-color-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.prop-item {
  margin-bottom: 10px;

  label {
    display: block;
    font-size: 12px;
    color: var(--el-text-color-regular);
    margin-bottom: 4px;
  }

  .el-input,
  .el-select,
  .el-input-number {
    width: 100%;
  }
}

.prop-group-actions {
  padding-top: 8px;
  border-top: 1px solid var(--el-border-color-lighter);

  .el-button {
    width: 100%;
  }
}

// ---- 过渡动画 ----
.palette-slide-enter-active,
.palette-slide-leave-active,
.properties-slide-enter-active,
.properties-slide-leave-active {
  transition:
    width 0.25s ease,
    opacity 0.2s ease;
}

.palette-slide-enter-from,
.palette-slide-leave-to,
.properties-slide-enter-from,
.properties-slide-leave-to {
  width: 0;
  opacity: 0;
}
</style>
