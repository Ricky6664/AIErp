<template>
  <div class="designer-demo">
    <div class="demo-header">
      <h1>单据打印/导出模板设计器（P15-打印）</h1>
      <p class="demo-desc">
        可视化设计单据打印模板与数据导出模板。左侧组件面板提供打印元素，中间画布拖拽布局，右侧属性面板配置样式。
      </p>
    </div>

    <div class="designer-layout">
      <!-- 顶部工具栏 -->
      <div class="toolbar">
        <div class="toolbar__left">
          <el-button type="primary" :icon="Plus" size="small">新建模板</el-button>
          <el-button :icon="FolderOpened" size="small">打开</el-button>
          <el-button :icon="Check" size="small" type="success">保存</el-button>
          <el-divider direction="vertical" />
          <el-button :icon="RefreshLeft" size="small" @click="undo">撤销</el-button>
          <el-button :icon="RefreshRight" size="small" @click="redo">重做</el-button>
          <el-divider direction="vertical" />
          <el-button :icon="View" size="small" @click="previewVisible = true">预览</el-button>
        </div>
        <div class="toolbar__right">
          <el-select v-model="paperSize" size="small" style="width: 120px" placeholder="纸张">
            <el-option v-for="s in paperSizes" :key="s" :label="s" :value="s" />
          </el-select>
          <el-select v-model="orientation" size="small" style="width: 100px">
            <el-option label="纵向" value="portrait" /><el-option label="横向" value="landscape" />
          </el-select>
        </div>
      </div>

      <!-- 主体三栏 -->
      <div class="main-panels">
        <!-- 左侧组件面板 -->
        <div class="panel panel--left">
          <div class="panel__title">打印元素</div>
          <el-input
            v-model="paletteSearch"
            size="small"
            placeholder="搜索元素..."
            clearable
            style="margin-bottom: 8px"
          />
          <div class="component-list">
            <div v-for="group in componentGroups" :key="group.name" class="comp-group">
              <div class="comp-group__name">{{ group.name }}</div>
              <div
                v-for="item in filteredItems(group.items)"
                :key="item.type"
                class="comp-item"
                draggable="true"
                @dragstart="onDragStart($event, item)"
              >
                <el-icon :size="16"><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 中间画布 -->
        <div class="panel panel--center">
          <div class="panel__title">画布</div>
          <div
            class="canvas"
            :style="{ width: canvasW + 'px', minHeight: canvasH + 'px' }"
            @drop.prevent="onDrop"
            @dragover.prevent
          >
            <div class="canvas__page">
              <div class="canvas__header">单据标题区</div>
              <div
                v-for="zone in canvasZones"
                :key="zone.id"
                class="canvas-zone"
                :class="{ 'is-active': activeZoneId === zone.id }"
                @click="selectZone(zone.id)"
              >
                <div class="zone-label">{{ zone.name }}</div>
                <div
                  v-for="(el, ei) in zone.elements"
                  :key="el.id"
                  class="canvas-element"
                  @click.stop="selectElement(el.id)"
                >
                  <el-icon :size="14"><component :is="el.icon" /></el-icon>
                  <span>{{ el.label }}</span>
                  <el-icon class="el-remove" :size="12" @click.stop="removeElement(zone.id, ei)"
                    ><Close
                  /></el-icon>
                </div>
              </div>
              <div class="canvas__footer">单据尾部区</div>
            </div>
          </div>
        </div>

        <!-- 右侧属性面板 -->
        <div class="panel panel--right">
          <div class="panel__title">属性配置</div>
          <div v-if="!selectedElement" class="panel__empty">请选择画布中的元素</div>
          <el-form v-else label-width="80px" size="small">
            <el-form-item label="元素名称"
              ><el-input v-model="selectedElement.label"
            /></el-form-item>
            <el-form-item label="X坐标"
              ><el-input-number v-model="selectedElement.x" :min="0" controls-position="right"
            /></el-form-item>
            <el-form-item label="Y坐标"
              ><el-input-number v-model="selectedElement.y" :min="0" controls-position="right"
            /></el-form-item>
            <el-form-item label="宽度"
              ><el-input-number v-model="selectedElement.w" :min="20" controls-position="right"
            /></el-form-item>
            <el-form-item label="高度"
              ><el-input-number v-model="selectedElement.h" :min="10" controls-position="right"
            /></el-form-item>
            <el-form-item label="字体大小"
              ><el-input-number v-model="selectedElement.fontSize" :min="8" :max="48"
            /></el-form-item>
            <el-form-item label="字体颜色"
              ><el-color-picker v-model="selectedElement.color" size="small"
            /></el-form-item>
            <el-form-item label="加粗"
              ><el-switch v-model="selectedElement.bold" size="small"
            /></el-form-item>
            <el-form-item label="对齐"
              ><el-select v-model="selectedElement.align" size="small"
                ><el-option v-for="a in aligns" :key="a" :label="a" :value="a" /></el-select
            ></el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="模板预览" width="800px" top="5vh">
      <div class="preview-page">
        <div class="preview-header">单据打印预览</div>
        <table class="preview-table">
          <thead>
            <tr>
              <th v-for="h in previewHeaders" :key="h">{{ h }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(r, i) in previewRows" :key="i">
              <td v-for="(c, ci) in r" :key="ci">{{ c }}</td>
            </tr>
          </tbody>
        </table>
        <div class="preview-footer">制单人: ____ | 审核人: ____ | 日期: ____</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Plus,
  FolderOpened,
  Check,
  RefreshLeft,
  RefreshRight,
  View,
  Close
} from '@element-plus/icons-vue'

defineOptions({ name: 'PrintTemplateDesignerDemo' })

const paperSizes = ['A4 210×297mm', 'A5 148×210mm', 'Letter 216×279mm', '80mm热敏', '自定义']
const paperSize = ref('A4 210×297mm')
const orientation = ref('portrait')
const previewVisible = ref(false)
const paletteSearch = ref('')

const canvasW = computed(() => (orientation.value === 'portrait' ? 595 : 842))
const canvasH = computed(() => (orientation.value === 'portrait' ? 842 : 595))

const componentGroups = [
  {
    name: '数据字段',
    items: [
      { type: 'text', label: '文本字段', icon: 'Document' },
      { type: 'number', label: '数值字段', icon: 'DataBoard' },
      { type: 'date', label: '日期字段', icon: 'Edit' },
      { type: 'barcode', label: '条形码', icon: 'Grid' },
      { type: 'qrcode', label: '二维码', icon: 'Picture' }
    ]
  },
  {
    name: '图形元素',
    items: [
      { type: 'line', label: '横线', icon: 'Tickets' },
      { type: 'rect', label: '矩形', icon: 'Grid' },
      { type: 'image', label: '图片/Logo', icon: 'Picture' },
      { type: 'table', label: '表格', icon: 'DataBoard' }
    ]
  },
  {
    name: '打印元素',
    items: [
      { type: 'pageNo', label: '页码', icon: 'Tickets' },
      { type: 'printDate', label: '打印日期', icon: 'Edit' }
    ]
  }
]

function filteredItems(items: any[]) {
  if (!paletteSearch.value) return items
  return items.filter((i: any) => i.label.includes(paletteSearch.value))
}

const canvasZones = ref([
  {
    id: 1,
    name: '单据信息区',
    elements: [
      {
        id: 101,
        label: '单据编号: [order_no]',
        icon: 'Document',
        x: 10,
        y: 10,
        w: 200,
        h: 20,
        fontSize: 12,
        color: '#333',
        bold: false,
        align: 'left'
      },
      {
        id: 102,
        label: '单据日期: [order_date]',
        icon: 'Edit',
        x: 350,
        y: 10,
        w: 200,
        h: 20,
        fontSize: 12,
        color: '#333',
        bold: false,
        align: 'left'
      }
    ]
  },
  {
    id: 2,
    name: '客户信息区',
    elements: [
      {
        id: 201,
        label: '客户名称: [customer_name]',
        icon: 'Document',
        x: 10,
        y: 10,
        w: 250,
        h: 20,
        fontSize: 12,
        color: '#333',
        bold: false,
        align: 'left'
      }
    ]
  },
  {
    id: 3,
    name: '明细表格区',
    elements: [
      {
        id: 301,
        label: '商品明细表',
        icon: 'DataBoard',
        x: 10,
        y: 10,
        w: 575,
        h: 300,
        fontSize: 12,
        color: '#333',
        bold: false,
        align: 'left'
      }
    ]
  }
])

const activeZoneId = ref<number | null>(null)
const selectedElement = ref<any | null>(null)
const aligns = ['left', 'center', 'right']

function selectZone(id: number) {
  activeZoneId.value = id
  selectedElement.value = null
}
function selectElement(id: number) {
  for (const z of canvasZones.value) {
    const el = z.elements.find((e) => e.id === id)
    if (el) {
      selectedElement.value = el
      activeZoneId.value = z.id
      return
    }
  }
}
function removeElement(zoneId: number, ei: number) {
  const z = canvasZones.value.find((z) => z.id === zoneId)
  if (z) z.elements.splice(ei, 1)
}
function onDragStart(e: DragEvent, item: any) {
  e.dataTransfer!.setData('text/plain', JSON.stringify(item))
}
function onDrop(e: DragEvent) {
  const data = JSON.parse(e.dataTransfer!.getData('text/plain'))
  if (activeZoneId.value) {
    const z = canvasZones.value.find((z) => z.id === activeZoneId.value)
    if (z)
      z.elements.push({
        id: Date.now(),
        label: data.label,
        icon: data.icon,
        x: 10,
        y: 10,
        w: 100,
        h: 20,
        fontSize: 12,
        color: '#333',
        bold: false,
        align: 'left'
      })
  }
}
function undo() {}
function redo() {}

const previewHeaders = [
  '序号',
  '商品编码',
  '商品名称',
  '规格',
  '单位',
  '数量',
  '单价',
  '金额',
  '备注'
]
const previewRows = [
  ['1', 'P001', '商品A', '10cm', '个', '100', '25.00', '2,500.00', ''],
  ['2', 'P002', '商品B', '5kg', 'kg', '50', '80.00', '4,000.00', ''],
  ['3', 'P003', '商品C', '-', '套', '20', '350.00', '7,000.00', '']
]
</script>

<style lang="scss" scoped>
.designer-demo {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #e6e6fa;

  .demo-header {
    padding: 16px 24px 0;
    h1 {
      font-size: 20px;
      font-weight: 700;
      margin: 0 0 4px;
      color: var(--el-text-color-primary);
    }
    .demo-desc {
      font-size: 13px;
      color: var(--el-text-color-secondary);
      margin: 0;
    }
  }

  .designer-layout {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    padding: 16px;
    gap: 8px;
  }

  .toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: #fff;
    border-radius: 12px;
    padding: 8px 16px;
    flex-shrink: 0;
    &__left {
      display: flex;
      align-items: center;
      gap: 6px;
    }
    &__right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .main-panels {
    flex: 1;
    display: flex;
    gap: 8px;
    overflow: hidden;
  }

  .panel {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    &__title {
      padding: 10px 12px;
      font-weight: 600;
      font-size: 13px;
      border-bottom: 1px solid var(--el-border-color-lighter);
    }
    &__empty {
      padding: 24px;
      text-align: center;
      color: var(--el-text-color-secondary);
      font-size: 13px;
    }

    &--left {
      width: 200px;
      flex-shrink: 0;
      padding: 0 8px 8px;
    }
    &--center {
      flex: 1;
      overflow: auto;
      display: flex;
      justify-content: center;
      padding: 16px;
    }
    &--right {
      width: 240px;
      flex-shrink: 0;
      overflow-y: auto;
      padding: 8px 12px;
    }
  }

  .component-list {
    overflow-y: auto;
    height: calc(100% - 70px);
  }
  .comp-group {
    margin-bottom: 8px;
    &__name {
      font-size: 11px;
      font-weight: 600;
      color: var(--el-text-color-secondary);
      padding: 4px 0;
      text-transform: uppercase;
    }
  }
  .comp-item {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 6px 8px;
    border-radius: 6px;
    cursor: grab;
    font-size: 12px;
    transition: background 0.15s;
    &:hover {
      background: var(--el-fill-color-light);
    }
  }

  .canvas {
    background: var(--el-fill-color-light);
    border-radius: 12px;
    padding: 16px;
    display: flex;
    justify-content: center;
    &__page {
      background: #fff;
      width: 100%;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      padding: 24px;
    }
    &__header,
    &__footer {
      text-align: center;
      font-size: 12px;
      color: var(--el-text-color-placeholder);
      padding: 12px;
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      margin-bottom: 12px;
    }
  }

  .canvas-zone {
    border: 2px dashed var(--el-border-color);
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 12px;
    min-height: 60px;
    cursor: pointer;
    &.is-active {
      border-color: var(--el-color-primary);
    }
    .zone-label {
      font-size: 11px;
      color: var(--el-text-color-placeholder);
      margin-bottom: 8px;
      font-weight: 600;
    }
  }
  .canvas-element {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    background: var(--el-fill-color-light);
    padding: 4px 8px;
    border-radius: 4px;
    margin: 2px;
    font-size: 12px;
    cursor: pointer;
    .el-remove {
      color: var(--el-text-color-placeholder);
      cursor: pointer;
      &:hover {
        color: var(--el-color-danger);
      }
    }
  }

  .preview-page {
    background: #fff;
    border: 1px solid var(--el-border-color);
    padding: 24px;
  }
  .preview-header {
    text-align: center;
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 16px;
  }
  .preview-table {
    width: 100%;
    border-collapse: collapse;
    th,
    td {
      border: 1px solid #ccc;
      padding: 6px 8px;
      font-size: 12px;
      text-align: center;
    }
    th {
      background: var(--el-fill-color-light);
    }
  }
  .preview-footer {
    margin-top: 16px;
    display: flex;
    justify-content: space-between;
    font-size: 12px;
  }
}
</style>
