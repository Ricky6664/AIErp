<template>
  <PageP08Kanban view-id="demo-p08" page-type="P08" :config="pageConfig" :permissions="[]">
    <template #action-bar>
      <div class="kanban-toolbar">
        <el-select v-model="filter" placeholder="筛选产线" style="width: 160px" size="default">
          <el-option label="全部产线" value="all" />
          <el-option label="产线A" value="A" />
          <el-option label="产线B" value="B" />
        </el-select>
        <el-button-group>
          <el-button
            :type="viewMode === 'kanban' ? 'primary' : 'default'"
            @click="viewMode = 'kanban'"
            >看板</el-button
          >
          <el-button :type="viewMode === 'list' ? 'primary' : 'default'" @click="viewMode = 'list'"
            >列表</el-button
          >
        </el-button-group>
        <el-button text>刷新</el-button>
      </div>
    </template>

    <template #main-content>
      <div class="kanban-board">
        <div v-for="col in columns" :key="col.id" class="kanban-column">
          <div class="kanban-column-header" :class="`kanban-column-header--${col.color}`">
            <span>{{ col.label }}</span>
            <el-tag size="small" round>{{ col.items.length }}</el-tag>
          </div>
          <div class="kanban-column-body">
            <div
              v-for="item in col.items"
              :key="item.id"
              class="kanban-card"
              :class="`kanban-card--${item.priority}`"
            >
              <div class="kanban-card-header">
                <span class="kanban-card-no">{{ item.no }}</span>
                <el-tag v-if="item.priority === 'urgent'" type="danger" size="small" effect="dark"
                  >紧急</el-tag
                >
              </div>
              <div class="kanban-card-title">{{ item.title }}</div>
              <div class="kanban-card-meta">
                <span>{{ item.product }}</span>
                <span>Qty: {{ item.qty }}</span>
              </div>
              <div class="kanban-card-footer">
                <span class="kanban-card-assignee">{{ item.assignee }}</span>
                <span class="kanban-card-date">{{ item.date }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </PageP08Kanban>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageP08Kanban from '@/components/page-base/PageP08Kanban.vue'

const filter = ref('all')
const viewMode = ref('kanban')

const columns = [
  {
    id: 'col-1',
    label: '待排产',
    color: 'info',
    items: [
      {
        id: 'k1',
        no: 'MO-001',
        title: '传感器A型生产任务',
        product: '传感器A型',
        qty: 500,
        assignee: '李工',
        date: '06-15',
        priority: 'medium'
      },
      {
        id: 'k2',
        no: 'MO-002',
        title: '控制器模块生产',
        product: '控制器B型',
        qty: 200,
        assignee: '王工',
        date: '06-18',
        priority: 'low'
      }
    ]
  },
  {
    id: 'col-2',
    label: '生产中',
    color: 'primary',
    items: [
      {
        id: 'k3',
        no: 'MO-003',
        title: '连接线缆批量生产',
        product: '线缆C型',
        qty: 1000,
        assignee: '赵工',
        date: '06-12',
        priority: 'high'
      },
      {
        id: 'k4',
        no: 'MO-004',
        title: '传感器B型紧急订单',
        product: '传感器B型',
        qty: 300,
        assignee: '钱工',
        date: '06-11',
        priority: 'urgent'
      }
    ]
  },
  {
    id: 'col-3',
    label: '已完工',
    color: 'success',
    items: [
      {
        id: 'k5',
        no: 'MO-005',
        title: '外壳注塑生产',
        product: '外壳D型',
        qty: 800,
        assignee: '孙工',
        date: '06-08',
        priority: 'medium'
      }
    ]
  },
  {
    id: 'col-4',
    label: '已检验',
    color: 'warning',
    items: [
      {
        id: 'k6',
        no: 'MO-006',
        title: 'PCB板焊接',
        product: 'PCB-E型',
        qty: 600,
        assignee: '周工',
        date: '06-05',
        priority: 'low'
      },
      {
        id: 'k7',
        no: 'MO-007',
        title: '成品组装',
        product: '成品F型',
        qty: 150,
        assignee: '吴工',
        date: '06-03',
        priority: 'low'
      }
    ]
  }
]
</script>

<style scoped lang="scss">
.kanban-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.kanban-board {
  display: flex;
  gap: 12px;
  height: 100%;
  overflow-x: auto;
}
.kanban-column {
  flex: 1;
  min-width: 280px;
  max-width: 360px;
  display: flex;
  flex-direction: column;
  background: var(--el-fill-color-lighter);
  border-radius: 12px;
  overflow: hidden;
}
.kanban-column-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  font-weight: 600;
  font-size: 15px;
  &--info {
    background: var(--el-color-info-light-9);
    color: var(--el-color-info);
  }
  &--primary {
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }
  &--success {
    background: var(--el-color-success-light-9);
    color: var(--el-color-success);
  }
  &--warning {
    background: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
  }
  &--danger {
    background: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }
}
.kanban-column-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.kanban-card {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 12px;
  border-left: 3px solid var(--el-color-primary);
  &--urgent {
    border-left-color: var(--el-color-danger);
  }
  &--high {
    border-left-color: var(--el-color-warning);
  }
  .kanban-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }
  .kanban-card-no {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    font-weight: 500;
  }
  .kanban-card-title {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  .kanban-card-meta {
    display: flex;
    gap: 16px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-bottom: 8px;
  }
  .kanban-card-footer {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: var(--el-text-color-placeholder);
  }
}
</style>
