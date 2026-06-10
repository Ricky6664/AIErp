<template>
  <PageP15Designer view-id="demo-p15" page-type="P15" :config="pageConfig" :permissions="[]">
    <template #main-content>
      <div class="designer-container">
        <!-- 左侧组件面板 -->
        <div class="designer-palette">
          <h4 class="palette-title">组件面板</h4>
          <div v-for="cat in componentCategories" :key="cat.name" class="palette-category">
            <div class="palette-cat-name">{{ cat.name }}</div>
            <div v-for="comp in cat.items" :key="comp.type" class="palette-item" draggable="true">
              <el-icon :size="16"><component :is="comp.icon" /></el-icon>
              <span>{{ comp.label }}</span>
            </div>
          </div>
        </div>

        <!-- 中部画布 -->
        <div class="designer-canvas">
          <div class="canvas-header">
            <span class="canvas-title">销售订单 — 模板设计</span>
            <div class="canvas-toolbar">
              <el-button size="small">撤销</el-button>
              <el-button size="small">重做</el-button>
              <el-divider direction="vertical" />
              <el-button size="small" type="primary">保存</el-button>
              <el-button size="small">预览</el-button>
            </div>
          </div>
          <div class="canvas-body">
            <!-- 模拟画布中的布局区域 -->
            <div class="canvas-zone canvas-zone--query">
              <span class="zone-label">查询区</span>
              <div class="zone-placeholder">拖拽查询组件到此区域</div>
            </div>
            <div class="canvas-zone canvas-zone--action">
              <span class="zone-label">操作栏</span>
              <div class="zone-placeholder">拖拽操作按钮到此区域</div>
            </div>
            <div class="canvas-zone canvas-zone--main">
              <span class="zone-label">主内容区</span>
              <div class="canvas-placed-item">
                <el-icon :size="16"><List /></el-icon>
                <span>列表数据表格</span>
                <el-tag size="small">已放置</el-tag>
              </div>
              <div class="canvas-placed-item">
                <el-icon :size="16"><Edit /></el-icon>
                <span>主表单录入区</span>
                <el-tag size="small" type="success">已放置</el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧属性面板 -->
        <div class="designer-properties">
          <h4 class="properties-title">属性面板</h4>
          <el-empty description="请选中画布中的组件" :image-size="80" />
          <div v-if="false" class="properties-form">
            <el-form label-width="80px" size="small">
              <el-form-item label="标题"><el-input value="列表数据表格" /></el-form-item>
              <el-form-item label="数据源"><el-select value="view_001" /></el-form-item>
              <el-form-item label="显示边框"><el-switch :model-value="true" /></el-form-item>
              <el-form-item label="斑马纹"><el-switch :model-value="true" /></el-form-item>
              <el-form-item label="分页大小"><el-input-number :value="20" /></el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </template>
  </PageP15Designer>
</template>

<script setup lang="ts">
import { List, Edit } from '@element-plus/icons-vue'
import PageP15Designer from '@/components/page-base/PageP15Designer.vue'
import type { DesignerAvailableComponentConfig } from '@/types/page-base.d.ts'

const pageConfig = {
  title: '模板设计器（P15）',
  showPalette: true,
  showProperties: true
}

const componentCategories: { name: string; items: DesignerAvailableComponentConfig[] }[] = [
  {
    name: '布局组件',
    items: [
      { type: 'query-panel', label: '查询面板', icon: SearchIconName, category: 'layout' },
      { type: 'action-bar', label: '操作栏', icon: OperationIconName, category: 'layout' }
    ]
  },
  {
    name: '数据组件',
    items: [
      { type: 'list-table', label: '列表表格', icon: ListIconName, category: 'data' },
      { type: 'master-form', label: '主表单', icon: EditIconName, category: 'form' },
      { type: 'simple-form', label: '简单表单', icon: EditIconName, category: 'form' }
    ]
  },
  {
    name: '图表组件',
    items: [
      { type: 'chart', label: '图表', icon: ChartIconName, category: 'chart' },
      { type: 'kanban', label: '看板', icon: KanbanIconName, category: 'other' }
    ]
  }
]

// Workaround: dynamic icons in config data need symbol names resolved
const SearchIconName = 'Search' as DesignerAvailableComponentConfig['type']
const OperationIconName = 'query-panel' as DesignerAvailableComponentConfig['type']
const ListIconName = 'list-table' as DesignerAvailableComponentConfig['type']
const EditIconName = 'master-form' as DesignerAvailableComponentConfig['type']
const ChartIconName = 'chart' as DesignerAvailableComponentConfig['type']
const KanbanIconName = 'kanban' as DesignerAvailableComponentConfig['type']
</script>

<style scoped lang="scss">
.designer-container {
  display: flex;
  height: 100%;
  gap: 0;
  min-height: 0;
}
.designer-palette {
  width: 220px;
  flex-shrink: 0;
  background: var(--el-bg-color);
  border-radius: 12px 0 0 12px;
  overflow-y: auto;
  padding: 16px;
  border-right: 1px solid var(--el-border-color-lighter);
  .palette-title {
    margin: 0 0 12px;
    font-size: 15px;
  }
}
.palette-category {
  margin-bottom: 16px;
  .palette-cat-name {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-bottom: 6px;
    text-transform: uppercase;
    font-weight: 600;
  }
}
.palette-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: grab;
  margin-bottom: 4px;
  font-size: 13px;
  border: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-lighter);
  &:hover {
    background: var(--el-fill-color-light);
    border-color: var(--el-color-primary);
  }
}
.designer-canvas {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--el-fill-color);
  min-width: 0;
  .canvas-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    background: var(--el-bg-color);
    border-bottom: 1px solid var(--el-border-color-lighter);
    .canvas-title {
      font-weight: 600;
      font-size: 15px;
    }
    .canvas-toolbar {
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }
  .canvas-body {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
}
.canvas-zone {
  border: 2px dashed var(--el-border-color);
  border-radius: 8px;
  padding: 16px;
  position: relative;
  min-height: 80px;
  .zone-label {
    position: absolute;
    top: -10px;
    left: 12px;
    background: var(--el-fill-color);
    padding: 0 8px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
    font-weight: 600;
  }
  .zone-placeholder {
    color: var(--el-text-color-placeholder);
    font-size: 13px;
    text-align: center;
    padding: 20px;
  }
  &--main {
    flex: 1;
  }
}
.canvas-placed-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: var(--el-bg-color);
  border-radius: 6px;
  margin-bottom: 8px;
  border: 1px solid var(--el-border-color-light);
  font-size: 14px;
}
.designer-properties {
  width: 260px;
  flex-shrink: 0;
  background: var(--el-bg-color);
  border-radius: 0 12px 12px 0;
  overflow-y: auto;
  padding: 16px;
  border-left: 1px solid var(--el-border-color-lighter);
  .properties-title {
    margin: 0 0 16px;
    font-size: 15px;
  }
  .properties-form {
    padding: 8px 0;
  }
}
</style>
