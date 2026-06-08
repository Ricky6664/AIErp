<template>
  <div class="approval-my-page">
    <!-- Tab 切换 -->
    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待审批" name="pending" />
        <el-tab-pane label="已审批" name="reviewed" />
        <el-tab-pane label="我的申请" name="submitted" />
      </el-tabs>
    </el-card>

    <!-- 搜索表单 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" :inline="true" @submit.prevent>
        <el-form-item label="审批定义">
          <el-input
            v-model="searchForm.definitionName"
            placeholder="请输入审批定义名称"
            clearable
            @input="handleSearchDebounced"
          />
        </el-form-item>
        <el-form-item v-if="activeTab === 'submitted'" label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option label="待审批" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
            <el-option label="已撤回" value="WITHDRAWN" />
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
        <el-table-column prop="instanceId" label="实例ID" width="80" />
        <el-table-column
          prop="definitionName"
          label="审批定义"
          min-width="140"
          show-overflow-tooltip
        />
        <el-table-column prop="businessType" label="业务类型" width="120" />
        <el-table-column prop="businessId" label="业务单据ID" width="120" />
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column
          prop="currentNodeName"
          label="当前节点"
          width="120"
          show-overflow-tooltip
        />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          v-if="activeTab === 'reviewed'"
          label="我的操作"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="actionTagType(row.myAction)" size="small">
              {{ actionLabel(row.myAction) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          v-if="activeTab === 'reviewed'"
          prop="myComment"
          label="审批意见"
          min-width="140"
          show-overflow-tooltip
        />
        <el-table-column label="时间" width="180">
          <template #default="{ row }">
            {{ activeTab === 'reviewed' ? row.myOperateTime : row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="activeTab === 'pending'">
              <el-button
                type="success"
                link
                size="small"
                @click="handleApprove(row as MyApprovalVO)"
              >
                <el-icon><Select /></el-icon>
                通过
              </el-button>
              <el-button type="danger" link size="small" @click="handleReject(row as MyApprovalVO)">
                <el-icon><CloseBold /></el-icon>
                驳回
              </el-button>
            </template>
            <template v-else-if="activeTab === 'submitted'">
              <el-button
                v-if="row.status === 'PENDING'"
                type="warning"
                link
                size="small"
                @click="handleWithdraw(row as MyApprovalVO)"
              >
                <el-icon><Back /></el-icon>
                撤回
              </el-button>
              <el-button
                type="primary"
                link
                size="small"
                @click="handleDetail(row as MyApprovalVO)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
            </template>
            <template v-else>
              <el-button
                type="primary"
                link
                size="small"
                @click="handleDetail(row as MyApprovalVO)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
            </template>
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

    <!-- 审批操作对话框 -->
    <el-dialog
      v-model="actionVisible"
      :title="actionType === 'APPROVED' ? '审批通过' : '审批驳回'"
      width="480px"
      @close="actionForm.comment = ''"
    >
      <el-form :model="actionForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-input
            v-model="actionForm.comment"
            type="textarea"
            :rows="3"
            :placeholder="actionType === 'APPROVED' ? '请输入通过意见（选填）' : '请输入驳回原因'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="actionVisible = false">取消</el-button>
        <el-button :type="actionType === 'APPROVED' ? 'success' : 'danger'" @click="submitAction">
          {{ actionType === 'APPROVED' ? '通过' : '驳回' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="审批详情" width="560px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="实例ID">{{ detailData.instanceId }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="statusTagType(detailData.status || '')" size="small">
            {{ statusLabel(detailData.status || '') }}
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
import { Search, Refresh, Select, CloseBold, View, Back } from '@element-plus/icons-vue'
import { getMyApprovalPage, recordAction, withdrawInstance } from '@/api/modules/approval'
import type { MyApprovalVO } from '@/api/types/approval'

const loading = ref(false)
const tableData = ref<MyApprovalVO[]>([])
const activeTab = ref('pending')

// 操作对话框
const actionVisible = ref(false)
const actionType = ref('')
const actionTarget = ref<MyApprovalVO | null>(null)
const actionForm = reactive({ comment: '' })

// 详情对话框
const detailVisible = ref(false)
const detailData = reactive<Partial<MyApprovalVO>>({})

const searchForm = reactive({
  definitionName: '',
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
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'WITHDRAWN':
      return 'warning'
    default:
      return 'info'
  }
}

function statusLabel(status: string): string {
  switch (status) {
    case 'PENDING':
      return '待审批'
    case 'APPROVED':
      return '已通过'
    case 'REJECTED':
      return '已驳回'
    case 'WITHDRAWN':
      return '已撤回'
    default:
      return status || '-'
  }
}

function actionTagType(action: string): 'success' | 'danger' | 'warning' | 'info' {
  switch (action) {
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'TRANSFER':
      return 'warning'
    default:
      return 'info'
  }
}

function actionLabel(action: string): string {
  switch (action) {
    case 'APPROVED':
      return '通过'
    case 'REJECTED':
      return '驳回'
    case 'TRANSFER':
      return '转办'
    case 'COUNTERSIGN':
      return '加签'
    default:
      return action || '-'
  }
}

function handleSearchDebounced() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => handleSearch(), 300)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyApprovalPage({
      tab: activeTab.value,
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
  searchForm.definitionName = ''
  searchForm.status = ''
  handleSearch()
}

function handleTabChange() {
  handleReset()
}

function handleApprove(row: MyApprovalVO) {
  actionType.value = 'APPROVED'
  actionTarget.value = row
  actionForm.comment = ''
  actionVisible.value = true
}

function handleReject(row: MyApprovalVO) {
  actionType.value = 'REJECTED'
  actionTarget.value = row
  actionForm.comment = ''
  actionVisible.value = true
}

async function submitAction() {
  if (!actionTarget.value) return
  try {
    await recordAction({
      instanceId: actionTarget.value.instanceId,
      action: actionType.value,
      comment: actionForm.comment || undefined
    })
    ElMessage.success(actionType.value === 'APPROVED' ? '审批通过' : '已驳回')
    actionVisible.value = false
    fetchData()
  } catch {
    // error handled by interceptor
  }
}

async function handleWithdraw(row: MyApprovalVO) {
  await ElMessageBox.confirm('确定要撤回该审批申请吗？', '撤回确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await withdrawInstance(row.instanceId)
  ElMessage.success('撤回成功')
  fetchData()
}

function handleDetail(row: MyApprovalVO) {
  Object.assign(detailData, row)
  detailVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-my-page {
  padding: 16px;
}

.tab-card {
  margin-bottom: 16px;
}

.tab-card :deep(.el-card__body) {
  padding-bottom: 0;
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
