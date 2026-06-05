<template>
  <div class="demo-edit-table-page">
    <div class="page-header">
      <h2>录入数据表格合计列演示页</h2>
      <p class="page-desc">
        演示 ErpEditTable 组件的合计行功能：支持求和、平均值、计数、最小值、最大值、自定义合计方法，
        以及合计行前缀/后缀文本、数值格式化、行拖拽排序、单元格编辑等
      </p>
    </div>

    <el-alert
      v-if="error"
      :title="error"
      type="error"
      show-icon
      closable
      class="error-alert"
      @close="error = null"
    />

    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="addRow">
          <el-icon><Plus /></el-icon>
          新增行
        </el-button>
        <el-button
          type="danger"
          :disabled="tableData.length === 0"
          @click="deleteRow(tableData.length - 1)"
        >
          <el-icon><Delete /></el-icon>
          删除末行
        </el-button>
        <el-button @click="handleSave">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button type="danger" plain @click="handleResetAll">
          <el-icon><Delete /></el-icon>
          一键初始化
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-dropdown trigger="click" @command="(key: string) => (tableSize = key as any)">
          <el-button>
            行高：{{ sizeLabel }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="opt in rowSizeOptions"
                :key="opt.key"
                :command="opt.key"
                :class="{ 'is-active': tableSize === opt.key }"
              >
                {{ opt.label }}
                <el-icon v-if="tableSize === opt.key" class="check-icon"><Check /></el-icon>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-divider direction="vertical" />
        <el-tag :type="summaryEnabled ? 'success' : 'info'" size="large">
          合计行：{{ summaryEnabled ? '已启用' : '已禁用' }}
        </el-tag>
        <el-button :type="summaryEnabled ? 'warning' : 'success'" @click="toggleSummary">
          {{ summaryEnabled ? '禁用合计' : '启用合计' }}
        </el-button>
      </div>
    </div>

    <div class="summary-config-panel">
      <span class="config-label">合计方法说明：</span>
      <el-tag
        v-for="col in columns.filter((c) => c.summary)"
        :key="col.field"
        size="small"
        class="method-tag"
      >
        {{ col.title }}：{{ methodNameMap[col.summary!.method] || col.summary!.method }}
        <template v-if="col.summary!.prefix">（前缀："{{ col.summary!.prefix }}"）</template>
        <template v-if="col.summary!.suffix">（后缀："{{ col.summary!.suffix }}"）</template>
      </el-tag>
    </div>

    <div v-loading="loading" class="table-wrapper">
      <ErpEditTable
        ref="tableRef"
        v-model="tableData"
        :columns="columns"
        :size="tableSize"
        :summary-config="summaryConfig"
        :drag-config="{ enabled: true, trigger: 'icon', type: 'row', showTip: true }"
        :row-config="{ isCurrent: true, isHover: true, keyField: 'id' }"
        view-code="demo-edit-table-summary"
        border="full"
        @change="handleCellChange"
        @drag-sort="handleDragSort"
      >
        <template #status="{ row }">
          <el-tag :type="(statusTagTypeMap[row.status as string] as any) || 'info'" size="small">
            {{ statusLabelMap[row.status as string] || row.status }}
          </el-tag>
        </template>
      </ErpEditTable>
    </div>

    <div class="summary-info">
      <el-alert title="合计行功能说明" type="success" :closable="false" show-icon>
        <template #default>
          <ul class="info-list">
            <li><strong>求和 (sum)</strong>：对"数量"列和"总金额"列求和，合计行显示在表格底部</li>
            <li><strong>平均值 (avg)</strong>：对"单价"列计算平均值</li>
            <li><strong>前缀/后缀</strong>：合计值支持添加前缀（如 ¥）和后缀（如"件"）</li>
            <li><strong>自定义格式化</strong>：通过 formatter 函数格式化合计值显示</li>
            <li><strong>动态开关</strong>：通过 summaryConfig.enabled 控制合计行的显示/隐藏</li>
            <li><strong>响应式更新</strong>：编辑单元格后，合计行自动重新计算</li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus, Delete, Check, Refresh, ArrowDown } from '@element-plus/icons-vue'
import ErpEditTable from '@/components/edit-table/index.vue'
import { useDemoEditTable } from '@/composables/useDemoEditTable'

const {
  loading,
  error,
  tableData,
  summaryEnabled,
  tableSize,
  columns,
  summaryConfig,
  statusLabelMap,
  statusTagTypeMap,
  rowSizeOptions,
  fetchData,
  handleCellChange,
  handleDragSort,
  addRow,
  deleteRow,
  handleSave,
  handleReset,
  toggleSummary
} = useDemoEditTable()

const tableRef = ref<InstanceType<typeof ErpEditTable> | null>(null)

function handleResetAll(): void {
  handleReset()
  tableRef.value?.resetAll()
}

const sizeLabelMap: Record<string, string> = {
  mini: '迷你',
  small: '小',
  medium: '中',
  large: '大'
}

const sizeLabel = computed(() => sizeLabelMap[tableSize.value] || tableSize.value)

const methodNameMap: Record<string, string> = {
  sum: '求和',
  avg: '平均值',
  count: '计数',
  min: '最小值',
  max: '最大值',
  custom: '自定义',
  none: '无'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.demo-edit-table-page {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;

  .page-header {
    margin-bottom: 16px;
    flex-shrink: 0;

    h2 {
      margin: 0 0 6px;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-desc {
      margin: 0;
      font-size: 13px;
      color: var(--el-text-color-secondary);
      line-height: 1.6;
    }
  }

  .error-alert {
    margin-bottom: 12px;
    flex-shrink: 0;
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    flex-shrink: 0;
    flex-wrap: wrap;
    gap: 12px;

    .toolbar-left,
    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
    }
  }

  .summary-config-panel {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    flex-shrink: 0;
    flex-wrap: wrap;
    padding: 8px 12px;
    background: #f0f9eb;
    border-radius: 4px;
    border: 1px solid #e1f3d8;

    .config-label {
      font-size: 13px;
      font-weight: 600;
      color: #67c23a;
      white-space: nowrap;
    }

    .method-tag {
      font-size: 12px;
    }
  }

  .table-wrapper {
    flex: 1;
    min-height: 0;
    border: 1px solid var(--el-border-color-light);
    border-radius: 4px;
    overflow: hidden;
  }

  .summary-info {
    margin-top: 16px;
    flex-shrink: 0;

    .info-list {
      margin: 4px 0 0;
      padding-left: 20px;
      font-size: 13px;
      line-height: 1.8;

      li {
        margin-bottom: 2px;
      }
    }
  }

  .check-icon {
    margin-left: auto;
    color: var(--el-color-primary);
  }
}

.is-active {
  color: var(--el-color-primary);
  font-weight: 500;
}
</style>
