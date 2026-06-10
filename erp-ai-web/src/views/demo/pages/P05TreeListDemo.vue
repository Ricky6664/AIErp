<template>
  <PageP05TreeList view-id="demo-p05" page-type="P05" :config="pageConfig" :permissions="[]">
    <template #tree-panel>
      <div class="tree-header">
        <el-input v-model="treeSearch" placeholder="搜索分类" clearable size="small" />
      </div>
      <el-tree
        :data="treeData"
        :props="{ label: 'label', children: 'children' }"
        node-key="id"
        default-expand-all
        highlight-current
        @node-click="handleTreeClick"
      />
    </template>

    <template #query-panel>
      <el-form :inline="true">
        <el-form-item label="关键字">
          <el-input
            v-model="search.keyword"
            placeholder="名称/编码"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>
    </template>

    <template #action-bar>
      <div class="action-left">
        <el-button type="primary">新增</el-button>
        <el-button>导出</el-button>
      </div>
      <div class="action-right">
        <span class="record-count">共 42 条</span>
      </div>
    </template>

    <template #main-content>
      <el-table :data="listData" stripe border style="width: 100%">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="code" label="编码" width="140" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="level" label="层级" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'" size="small">{{
              row.status
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default>
            <el-button type="primary" link size="small">编辑</el-button>
            <el-button type="success" link size="small">新增子级</el-button>
            <el-button type="danger" link size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          :current-page="1"
          :page-size="20"
          :total="42"
          layout="total, sizes, prev, pager, next"
        />
      </div>
    </template>
  </PageP05TreeList>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageP05TreeList from '@/components/page-base/PageP05TreeList.vue'

const treeSearch = ref('')
const search = ref({ keyword: '' })

const pageConfig = {
  title: '商品分类（P05）',
  showQueryPanel: true,
  showActionBar: true,
  treeWidth: 280
}

const treeData = [
  {
    id: 1,
    label: '电子产品',
    children: [
      { id: 11, label: '手机通讯' },
      { id: 12, label: '电脑办公' },
      { id: 13, label: '智能穿戴' }
    ]
  },
  {
    id: 2,
    label: '家居用品',
    children: [
      { id: 21, label: '厨房用品' },
      { id: 22, label: '家纺布艺' }
    ]
  },
  {
    id: 3,
    label: '服装鞋帽',
    children: [
      { id: 31, label: '男装' },
      { id: 32, label: '女装' }
    ]
  }
]

const listData = [
  { code: 'CAT-001', name: '手机通讯', level: 2, status: '启用' },
  { code: 'CAT-002', name: '电脑办公', level: 2, status: '启用' },
  { code: 'CAT-003', name: '智能穿戴', level: 2, status: '启用' },
  { code: 'CAT-004', name: '厨房用品', level: 2, status: '启用' },
  { code: 'CAT-005', name: '家纺布艺', level: 2, status: '禁用' }
]

function handleTreeClick(node: { label: string }): void {
  console.log('Selected tree node:', node.label)
}
</script>

<style scoped lang="scss">
.tree-header {
  padding: 0 0 12px 0;
}
.action-left {
  display: flex;
  gap: 8px;
}
.action-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}
.record-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0 0;
}
</style>
