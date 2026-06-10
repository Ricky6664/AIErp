<template>
  <PageP03MasterList view-id="demo-p03" page-type="P03" :config="pageConfig" :permissions="[]">
    <!-- §5.3.1 查询区：上部多字段模糊 + 下部常用字段 -->
    <template #query-panel>
      <div class="query-wrapper">
        <div class="query-fuzzy">
          <el-input
            v-model="search.keyword"
            placeholder="名称/编码/型号 多字段模糊搜索"
            clearable
            style="width: 360px"
            :prefix-icon="SearchIcon"
          />
        </div>
        <div class="query-fields">
          <el-input v-model="search.name" placeholder="名称" clearable style="width: 160px" />
          <el-input v-model="search.code" placeholder="编码" clearable style="width: 140px" />
          <el-select v-model="search.status" placeholder="状态" clearable style="width: 120px">
            <el-option label="正常" value="normal" />
            <el-option label="禁用" value="disabled" />
          </el-select>
          <el-select v-model="search.category" placeholder="分类" clearable style="width: 120px">
            <el-option label="企业客户" value="corp" />
            <el-option label="个人客户" value="person" />
          </el-select>
          <el-button type="primary" :icon="SearchIcon">查询</el-button>
          <el-button :icon="RefreshIcon">重置</el-button>
        </div>
      </div>
    </template>

    <!-- §5.3.2 操作栏左侧 -->
    <template #action-left>
      <el-button type="primary" :icon="PlusIcon">新增</el-button>
      <el-button :icon="DownloadIcon">导出</el-button>
      <el-button :icon="TopRightIcon" disabled>下推</el-button>
      <el-dropdown>
        <el-button :icon="MoreIcon">更多</el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>沟通</el-dropdown-item>
            <el-dropdown-item>分享</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </template>

    <!-- §5.3.2 操作栏右侧 -->
    <template #action-right>
      <el-tooltip content="格式设置"><el-button :icon="SettingIcon" text /></el-tooltip>
      <el-tooltip content="刷新数据"><el-button :icon="RefreshIcon" text /></el-tooltip>
      <el-tooltip content="行高调整"><el-button :icon="SortIcon" text /></el-tooltip>
      <el-tooltip content="铺满"><el-button :icon="FullScreenIcon" text /></el-tooltip>
    </template>

    <!-- §5.3.2 数据表格 -->
    <template #main-content>
      <el-table
        :data="masterData"
        stripe
        highlight-current-row
        style="width: 100%"
        height="100%"
        @selection-change="onSelectChange"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column
          prop="code"
          label="编码"
          width="140"
          sortable="custom"
          show-overflow-tooltip
        />
        <el-table-column
          prop="name"
          label="客户名称"
          min-width="160"
          sortable="custom"
          show-overflow-tooltip
          :filters="[
            { text: '科技', value: '科技' },
            { text: '制造', value: '制造' },
            { text: '商贸', value: '商贸' }
          ]"
          :filter-method="(v: any, r: any) => r.name.includes(v)"
        />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'info'" size="small">{{
              row.status
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createDate" label="创建日期" width="120" sortable="custom" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default>
            <el-button type="primary" link size="small">编辑</el-button>
            <el-button type="danger" link size="small">删除</el-button>
            <el-button link size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-row">
        <span class="total-text">共 {{ masterData.length }} 条</span>
        <el-pagination
          :total="masterData.length"
          :page-sizes="[10, 20, 50, 100]"
          layout="sizes, prev, pager, next"
          small
        />
      </div>
    </template>

    <!-- §5.3.3 关联信息区标签页 -->
    <template #relation-tabs>
      <el-tabs v-model="activeTab" type="border-card" class="demo-tabs">
        <el-tab-pane label="客户联系人" name="contacts" />
        <el-tab-pane label="客户地址" name="addresses" />
        <el-tab-pane label="客户标签" name="tags" />
        <el-tab-pane label="客户附件" name="attachments" />
      </el-tabs>
    </template>

    <template #relation-toolbar>
      <el-tooltip content="新增联系人"><el-button :icon="PlusIcon" text size="small" /></el-tooltip>
      <el-tooltip content="放大查看"
        ><el-button :icon="FullScreenIcon" text size="small"
      /></el-tooltip>
    </template>

    <template #relation-content>
      <!-- 联系人 -->
      <div v-if="activeTab === 'contacts'">
        <el-table :data="contactData" size="small" stripe>
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="title" label="职务" width="120" />
          <el-table-column prop="phone" label="电话" width="140" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="isPrimary" label="首要" width="60">
            <template #default="{ row }"
              ><el-tag v-if="row.isPrimary" type="success" size="small">是</el-tag></template
            >
          </el-table-column>
        </el-table>
      </div>
      <!-- 地址 -->
      <div v-else-if="activeTab === 'addresses'">
        <el-table :data="addressData" size="small" stripe>
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column prop="address" label="详细地址" min-width="280" />
          <el-table-column prop="isDefault" label="默认" width="80" />
        </el-table>
      </div>
      <!-- 标签 -->
      <div v-else-if="activeTab === 'tags'">
        <div class="tag-cloud">
          <el-tag v-for="t in tags" :key="t" style="margin: 4px" size="large">{{ t }}</el-tag>
        </div>
      </div>
      <!-- 附件 -->
      <div v-else-if="activeTab === 'attachments'">
        <el-table :data="attachData" size="small" stripe>
          <el-table-column prop="name" label="文件名" min-width="200" />
          <el-table-column prop="size" label="大小" width="100" />
          <el-table-column prop="date" label="上传日期" width="140" />
        </el-table>
      </div>
    </template>
  </PageP03MasterList>
</template>

<script setup lang="ts">
import { ref, shallowRef } from 'vue'
import {
  Search,
  Refresh,
  Plus,
  Download,
  TopRight,
  More,
  Setting,
  Sort,
  FullScreen
} from '@element-plus/icons-vue'
import PageP03MasterList from '@/components/page-base/PageP03MasterList.vue'

const SearchIcon = shallowRef(Search)
const RefreshIcon = shallowRef(Refresh)
const PlusIcon = shallowRef(Plus)
const DownloadIcon = shallowRef(Download)
const TopRightIcon = shallowRef(TopRight)
const MoreIcon = shallowRef(More)
const SettingIcon = shallowRef(Setting)
const SortIcon = shallowRef(Sort)
const FullScreenIcon = shallowRef(FullScreen)

const search = ref({ keyword: '', name: '', code: '', status: '', category: '' })
const activeTab = ref('contacts')
const selectedRows = ref<any[]>([])

function onSelectChange(rows: any[]) {
  selectedRows.value = rows
}

const pageConfig = {
  title: '客户中心（P03 主从列表页）',
  listHeightPercent: 3,
  relationHeightPercent: 2
}

const masterData = [
  {
    code: 'CUST-001',
    name: '深圳科技有限公司',
    category: '企业客户',
    contactPerson: '张三',
    phone: '0755-88880001',
    email: 'zhangsan@example.com',
    status: '正常',
    createDate: '2025-03-15'
  },
  {
    code: 'CUST-002',
    name: '广州制造集团',
    category: '企业客户',
    contactPerson: '李四',
    phone: '020-88880002',
    email: 'lisi@example.com',
    status: '正常',
    createDate: '2025-04-20'
  },
  {
    code: 'CUST-003',
    name: '珠海商贸公司',
    category: '个人客户',
    contactPerson: '王五',
    phone: '0756-88880003',
    email: 'wangwu@example.com',
    status: '禁用',
    createDate: '2025-01-10'
  },
  {
    code: 'CUST-004',
    name: '东莞电子科技',
    category: '企业客户',
    contactPerson: '赵六',
    phone: '0769-88880004',
    email: 'zhaoliu@example.com',
    status: '正常',
    createDate: '2025-06-01'
  },
  {
    code: 'CUST-005',
    name: '佛山物流公司',
    category: '企业客户',
    contactPerson: '钱七',
    phone: '0757-88880005',
    email: 'qianqi@example.com',
    status: '正常',
    createDate: '2025-02-28'
  }
]

const contactData = [
  {
    name: '张三',
    title: '采购经理',
    phone: '13800001111',
    email: 'zhangsan@example.com',
    isPrimary: true
  },
  {
    name: '李四',
    title: '技术总监',
    phone: '13800002222',
    email: 'lisi@example.com',
    isPrimary: false
  }
]
const addressData = [
  { type: '办公地址', address: '深圳市南山区科技园路100号', isDefault: '是' },
  { type: '收货地址', address: '深圳市宝安区XX工业园A区', isDefault: '否' }
]
const tags = ['VIP客户', '长期合作', '制造业', 'A级信用', '战略伙伴', '500强']
const attachData = [
  { name: '采购合同_2026Q2.pdf', size: '2.3MB', date: '2026-06-01' },
  { name: '企业资质证明.pdf', size: '1.1MB', date: '2026-05-15' },
  { name: '产品检测报告.xlsx', size: '0.8MB', date: '2026-04-20' }
]
</script>

<style scoped lang="scss">
.query-wrapper {
  width: 100%;
  .query-fuzzy {
    margin-bottom: 10px;
  }
  .query-fields {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: center;
  }
}
.pagination-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  .total-text {
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }
}
.demo-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }
}
.tag-cloud {
  padding: 4px;
}
</style>
