<template>
  <div class="demo-edit-table-readonly-page">
    <div class="page-header">
      <h2>录入数据表格只读/禁用态演示页</h2>
      <p class="page-desc">
        演示 ErpEditTable 组件的只读/禁用态功能：通过
        <code>disabled</code> 属性控制表格编辑状态，<br />
        支持表单模式切换（编辑态/详情查看态），当切换到查看态时所有可编辑单元格自动转为禁用状态
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
        <el-button type="primary" :disabled="isDisabled" @click="addRow">
          <el-icon><Plus /></el-icon>
          新增行
        </el-button>
        <el-button
          type="danger"
          :disabled="isDisabled || tableData.length === 0"
          @click="deleteRow(tableData.length - 1)"
        >
          <el-icon><Delete /></el-icon>
          删除末行
        </el-button>
        <el-button :disabled="isDisabled" @click="handleSave">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
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
        <el-tag :type="isDisabled ? 'warning' : 'success'" size="large" effect="dark">
          表单模式：{{ isDisabled ? '查看态（只读）' : '编辑态' }}
        </el-tag>
        <el-button :type="isDisabled ? 'success' : 'warning'" @click="toggleFormMode">
          <el-icon>
            <View v-if="isDisabled" />
            <Edit v-else />
          </el-icon>
          {{ isDisabled ? '切换到编辑态' : '切换到查看态' }}
        </el-button>
      </div>
    </div>

    <div class="mode-indicator">
      <el-alert
        :title="
          isDisabled
            ? '当前为【查看态】— 表格所有可编辑单元格已自动禁用，新增/删除/保存按钮不可用'
            : '当前为【编辑态】— 表格单元格可正常编辑，所有操作按钮可用'
        "
        :type="isDisabled ? 'info' : 'success'"
        :closable="false"
        show-icon
      />
    </div>

    <div v-loading="loading" class="table-wrapper">
      <ErpEditTable
        ref="tableRef"
        v-model="tableData"
        :columns="columns"
        :size="tableSize"
        :disabled="isDisabled"
        view-code="demo-edit-table-readonly"
        border="full"
        @change="handleCellChange"
      >
        <template #status="{ row }">
          <el-tag :type="(statusTagTypeMap[row.status as string] as any) || 'info'" size="small">
            {{ statusLabelMap[row.status as string] || row.status }}
          </el-tag>
        </template>
      </ErpEditTable>
    </div>

    <div class="feature-info">
      <el-alert title="只读/禁用态功能说明" type="info" :closable="false" show-icon>
        <template #default>
          <ul class="info-list">
            <li>
              <strong>disabled 属性</strong>：ErpEditTable 组件通过 <code>disabled</code> prop
              控制所有可编辑单元格的启用/禁用状态，设为 <code>true</code>
              时表格进入只读模式
            </li>
            <li>
              <strong>自动转换</strong>：当
              <code>formMode</code> 从编辑态（edit）切换为查看态（view）时， 自动将
              <code>disabled</code> 设置为 <code>true</code>， 无需手动逐列控制只读
            </li>
            <li>
              <strong>不可编辑列</strong>：本身就不可编辑的列（如
              <code>editable: false</code> 的行号、总金额列） 在任何模式下都不可编辑，不受
              <code>disabled</code> 影响
            </li>
            <li>
              <strong>工具栏联动</strong
              >：切换到查看态时新增/删除/保存等编辑操作按钮自动禁用，防止误操作
            </li>
            <li>
              <strong>详情查看场景</strong>：适用于审批详情查看、历史单据浏览等只读场景， 表单通过
              <code>disabled</code> 控制即可，无需额外开发只读页面
            </li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus, Delete, Check, Refresh, ArrowDown, View, Edit } from '@element-plus/icons-vue'
import ErpEditTable from '@/components/edit-table/index.vue'
import { useDemoEditTableReadonly } from '@/composables/useDemoEditTableReadonly'

const {
  loading,
  error,
  tableData,
  isDisabled,
  tableSize,
  columns,
  statusLabelMap,
  statusTagTypeMap,
  rowSizeOptions,
  fetchData,
  handleCellChange,
  addRow,
  deleteRow,
  handleSave,
  handleReset,
  toggleFormMode
} = useDemoEditTableReadonly()

const tableRef = ref<InstanceType<typeof ErpEditTable> | null>(null)

const sizeLabelMap: Record<string, string> = {
  mini: '迷你',
  small: '小',
  medium: '中',
  large: '大'
}

const sizeLabel = computed(() => sizeLabelMap[tableSize.value] || tableSize.value)

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.demo-edit-table-readonly-page {
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
      line-height: 1.8;
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

  .mode-indicator {
    margin-bottom: 12px;
    flex-shrink: 0;
  }

  .table-wrapper {
    flex: 1;
    min-height: 0;
    border: 1px solid var(--el-border-color-light);
    border-radius: 4px;
    overflow: hidden;
  }

  .feature-info {
    margin-top: 16px;
    flex-shrink: 0;

    code {
      padding: 1px 5px;
      border-radius: 3px;
      background: rgba(0, 0, 0, 0.06);
      font-size: 12px;
      color: #dc3545;
    }

    .info-list {
      margin: 4px 0 0;
      padding-left: 20px;
      font-size: 13px;
      line-height: 1.8;

      li {
        margin-bottom: 4px;
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
