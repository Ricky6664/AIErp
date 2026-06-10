<template>
  <div class="approval-instance-page">
    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="业务类型">
          <el-input
            v-model="searchForm.businessType"
            placeholder="请输入业务类型"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option label="待审批" value="待审批" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
            <el-option label="已撤回" value="已撤回" />
          </el-select>
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="definitionName" label="审批定义" min-width="140" />
        <el-table-column prop="businessType" label="业务类型" width="120" />
        <el-table-column prop="businessId" label="业务单据ID" width="120" />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentNodeName" label="当前节点" width="120" />
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              v-if="row.status === '待审批'"
              type="warning"
              link
              size="small"
              @click="handleWithdraw(row)"
            >
              <el-icon><Back /></el-icon>
              撤回
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
    <el-dialog v-model="detailVisible" title="审批实例详情" width="560px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="实例ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="statusTagType(detailData.status)" size="small">
            {{ detailData.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批定义">{{
          detailData.definitionName
        }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ detailData.businessType }}</el-descriptions-item>
        <el-descriptions-item label="业务单据ID">{{ detailData.businessId }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailData.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="当前节点">{{
          detailData.currentNodeName
        }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Back } from '@element-plus/icons-vue'
import { getInstancePage, getInstanceDetail, withdrawInstance } from '@/api/modules/approval'
import type { InstanceVO } from '@/api/types/approval'

const loading = ref(false)
const tableData = ref<InstanceVO[]>([])
const detailVisible = ref(false)
const detailData = reactive<InstanceVO>({
  id: 0,
  definitionId: 0,
  definitionName: '',
  businessType: '',
  businessId: 0,
  applicantId: 0,
  applicantName: '',
  currentNodeId: 0,
  currentNodeName: '',
  status: '',
  createTime: ''
})

const searchForm = reactive({
  businessType: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

let searchTimer: ReturnType<typeof setTimeout> | null = null

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
    const res = await getInstancePage({
      businessType: searchForm.businessType || undefined,
      status: searchForm.status || undefined,
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
  searchForm.businessType = ''
  searchForm.status = ''
  handleSearch()
}

async function handleDetail(row: InstanceVO) {
  try {
    const data = await getInstanceDetail(row.id)
    Object.assign(detailData, data)
    detailVisible.value = true
  } catch {
    // error handled by interceptor
  }
}

async function handleWithdraw(row: InstanceVO) {
  await ElMessageBox.confirm(`确定要撤回该审批实例吗？`, '撤回确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await withdrawInstance(row.id)
  ElMessage.success('撤回成功')
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-instance-page {
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
