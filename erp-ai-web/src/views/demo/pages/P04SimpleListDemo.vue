<template>
  <PageP04SimpleList view-id="demo-p04" page-type="P04" :config="pageConfig" :permissions="[]">
    <template #query-panel>
      <el-form :inline="true">
        <el-form-item label="关键字">
          <el-input
            v-model="search.keyword"
            placeholder="名称/编码"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="search.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" value="enabled" />
            <el-option label="禁用" value="disabled" />
          </el-select>
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
        <el-button type="danger" disabled>批量删除</el-button>
        <el-button>导出</el-button>
      </div>
      <div class="action-right">
        <el-button text>格式</el-button>
        <el-button text>刷新</el-button>
        <span class="record-count">共 89 条</span>
      </div>
    </template>

    <template #main-content>
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="编码" width="140" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'" size="small">{{
              row.status
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default>
            <el-button type="primary" link size="small">编辑</el-button>
            <el-button type="danger" link size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="20"
          :total="89"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </template>
  </PageP04SimpleList>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'

const search = ref({ keyword: '', status: '' })
const pageNum = ref(1)

const pageConfig = { title: '数据字典（P04）', showQueryPanel: true, showActionBar: true }

const tableData = [
  {
    id: 1,
    code: 'DICT-001',
    name: '客户等级',
    type: '系统字典',
    status: '启用',
    updatedAt: '2026-06-10 10:30'
  },
  {
    id: 2,
    code: 'DICT-002',
    name: '供应商等级',
    type: '系统字典',
    status: '启用',
    updatedAt: '2026-06-09 14:20'
  },
  {
    id: 3,
    code: 'DICT-003',
    name: '商品单位',
    type: '业务字典',
    status: '启用',
    updatedAt: '2026-06-08 09:15'
  },
  {
    id: 4,
    code: 'DICT-004',
    name: '结算方式',
    type: '业务字典',
    status: '启用',
    updatedAt: '2026-06-07 16:45'
  },
  {
    id: 5,
    code: 'DICT-005',
    name: '发票类型',
    type: '业务字典',
    status: '禁用',
    updatedAt: '2026-06-06 11:00'
  }
]
</script>

<style scoped lang="scss">
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
  white-space: nowrap;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0 0;
}
</style>
