<template>
  <div class="approval-log-page">
    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="审批定义ID">
          <el-input
            v-model="searchForm.definitionId"
            placeholder="请输入定义ID"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-input
            v-model="searchForm.businessType"
            placeholder="请输入业务类型"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select
            v-model="searchForm.action"
            placeholder="请选择操作"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="提交" value="提交" />
            <el-option label="通过" value="通过" />
            <el-option label="驳回" value="驳回" />
            <el-option label="转办" value="转办" />
            <el-option label="加签" value="加签" />
            <el-option label="撤回" value="撤回" />
            <el-option label="催办" value="催办" />
          </el-select>
        </el-form-item>
        <el-form-item label="实例状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="待审批" value="待审批" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
            <el-option label="已撤回" value="已撤回" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
            @change="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="recordId" label="记录ID" width="80" />
        <el-table-column
          prop="definitionName"
          label="审批定义"
          min-width="140"
          show-overflow-tooltip
        />
        <el-table-column prop="instanceId" label="实例ID" width="100" />
        <el-table-column prop="businessType" label="业务类型" width="120" />
        <el-table-column prop="nodeName" label="审批节点" width="120" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="actionTagType(row.action)" size="small">
              {{ row.action }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="审批意见" min-width="160" show-overflow-tooltip />
        <el-table-column label="实例状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.instanceStatus)" size="small">
              {{ row.instanceStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="审批记录详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录ID">{{ detailData.recordId }}</el-descriptions-item>
        <el-descriptions-item label="实例ID">{{ detailData.instanceId }}</el-descriptions-item>
        <el-descriptions-item label="审批定义">{{
          detailData.definitionName
        }}</el-descriptions-item>
        <el-descriptions-item label="定义编码">{{
          detailData.definitionCode
        }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ detailData.businessType }}</el-descriptions-item>
        <el-descriptions-item label="业务单据ID">{{ detailData.businessId }}</el-descriptions-item>
        <el-descriptions-item label="审批节点">{{ detailData.nodeName }}</el-descriptions-item>
        <el-descriptions-item label="操作">
          <el-tag :type="actionTagType(detailData.action)" size="small">
            {{ detailData.action }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="实例状态">
          <el-tag :type="statusTagType(detailData.instanceStatus)" size="small">
            {{ detailData.instanceStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailData.operateTime }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{
          detailData.comment || '无'
        }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import { getRecordLogPage } from '@/api/modules/approval'
import type { RecordLogVO } from '@/api/types/approval'

const loading = ref(false)
const tableData = ref<RecordLogVO[]>([])
const detailVisible = ref(false)
const detailData = reactive<RecordLogVO>({
  recordId: 0,
  instanceId: 0,
  nodeName: '',
  approverId: 0,
  action: '',
  comment: '',
  operateTime: '',
  recordCreateTime: '',
  definitionId: 0,
  businessType: '',
  businessId: 0,
  applicantId: 0,
  instanceStatus: '',
  currentNodeId: 0,
  definitionName: '',
  definitionCode: ''
})

const searchForm = reactive({
  definitionId: '',
  businessType: '',
  action: '',
  status: '',
  dateRange: null as [string, string] | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

let searchTimer: ReturnType<typeof setTimeout> | null = null

function actionTagType(action: string): 'success' | 'danger' | 'warning' | 'info' | '' {
  switch (action) {
    case '通过':
      return 'success'
    case '驳回':
      return 'danger'
    case '撤回':
      return 'warning'
    case '提交':
      return 'info'
    case '转办':
      return ''
    case '加签':
      return ''
    case '催办':
      return 'warning'
    default:
      return 'info'
  }
}

function statusTagType(status: string): 'success' | 'danger' | 'warning' | 'info' {
  switch (status) {
    case '已通过':
      return 'success'
    case '已驳回':
      return 'danger'
    case '已撤回':
      return 'warning'
    default:
      return 'info'
  }
}

function handleSearchDebounced() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => handleSearch(), 300)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getRecordLogPage({
      definitionId: searchForm.definitionId ? Number(searchForm.definitionId) : undefined,
      businessType: searchForm.businessType || undefined,
      action: searchForm.action || undefined,
      status: searchForm.status || undefined,
      startTime: searchForm.dateRange?.[0] || undefined,
      endTime: searchForm.dateRange?.[1] || undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchData()
}

function handleReset() {
  searchForm.definitionId = ''
  searchForm.businessType = ''
  searchForm.action = ''
  searchForm.status = ''
  searchForm.dateRange = null
  handleSearch()
}

function handleDetail(row: RecordLogVO) {
  Object.assign(detailData, row)
  detailVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-log-page {
  padding: 16px;
}

.search-card,
.table-card {
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
