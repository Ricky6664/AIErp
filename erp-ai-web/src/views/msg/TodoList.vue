<template>
  <div class="todo-list">
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="全部待办" name="all" />
      <el-tab-pane name="pending_approval">
        <template #label>
          待审核<el-badge
            :value="countMap.pending_approval"
            :hidden="!countMap.pending_approval"
            class="tab-badge"
          />
        </template>
      </el-tab-pane>
      <el-tab-pane name="pending_handle">
        <template #label>
          待审批<el-badge
            :value="countMap.pending_handle"
            :hidden="!countMap.pending_handle"
            class="tab-badge"
          />
        </template>
      </el-tab-pane>
      <el-tab-pane name="pending_confirm">
        <template #label>
          待确认<el-badge
            :value="countMap.pending_confirm"
            :hidden="!countMap.pending_confirm"
            class="tab-badge"
          />
        </template>
      </el-tab-pane>
    </el-tabs>

    <div class="batch-bar">
      <el-button type="primary" :disabled="selectedIds.length === 0" @click="batchApprove">
        批量审批（{{ selectedIds.length }}）
      </el-button>
    </div>

    <el-table :data="tableData" border @selection-change="onSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="businessType" label="来源模块" width="120">
        <template #default="{ row }">
          <el-tag>{{ businessTypeMap[row.businessType] || row.businessType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="businessNo" label="单据编号" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="goToDoc(row)">{{ row.businessNo }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="待办标题" min-width="200" />
      <el-table-column prop="todoType" label="待办类型" width="100">
        <template #default="{ row }">
          <el-tag :type="todoTypeColor[row.todoType]">{{ todoTypeLabel[row.todoType] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column prop="dueTime" label="截止时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-popover :ref="(el: any) => setPopoverRef(el, row.id)" trigger="click">
            <template #reference>
              <el-button link type="primary">审批</el-button>
            </template>
            <el-input
              v-model="opinionMap[row.id]"
              type="textarea"
              placeholder="审批意见"
              :rows="3"
            />
            <div style="text-align: right; margin-top: 8px">
              <el-button size="small" type="primary" @click="doApprove(row)">确认审批</el-button>
            </div>
          </el-popover>
          <el-button link type="danger" @click="doReject(row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      :total="total"
      layout="total, prev, pager, next"
      @change="loadData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTodoPage, approveTodo, rejectTodo, batchApproveTodo } from '@/api/msg/todo'
import type { TodoListVO } from '@/types/msg'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('all')
const tableData = ref<TodoListVO[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 20
const selectedIds = ref<number[]>([])
const opinionMap = reactive<Record<number, string>>({})
const countMap = reactive({ pending_approval: 0, pending_handle: 0, pending_confirm: 0 })
const popoverRefMap: Record<number, any> = {}

const businessTypeMap: Record<string, string> = {
  sale_order: '销售订单',
  purchase_order: '采购订单',
  payment: '付款单',
  receipt: '收款单'
}
const todoTypeLabel: Record<string, string> = {
  pending_approval: '待审核',
  pending_handle: '待审批',
  pending_confirm: '待确认'
}
const todoTypeColor: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'info'> = {
  pending_approval: 'warning',
  pending_handle: 'primary',
  pending_confirm: 'info'
}

const loadData = async () => {
  const res = await getTodoPage({
    todoType: activeTab.value === 'all' ? '' : activeTab.value,
    isCompleted: false,
    pageNum: pageNum.value,
    pageSize
  })
  tableData.value = res.records
  total.value = res.total
}

const onTabChange = () => {
  pageNum.value = 1
  loadData()
}

const onSelectionChange = (rows: any[]) => {
  selectedIds.value = rows.map((r: TodoListVO) => r.id)
}

const setPopoverRef = (el: any, id: number) => {
  if (el) {
    popoverRefMap[id] = el
  }
}

const doApprove = async (row: any) => {
  const todo = row as TodoListVO
  await approveTodo({ todoId: todo.id, opinion: opinionMap[todo.id] || '' })
  ElMessage.success('审批成功')
  popoverRefMap[todo.id]?.hide()
  loadData()
}

const doReject = async (row: any) => {
  const todo = row as TodoListVO
  const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputValidator: (val: string) => {
      if (!val || !val.trim()) {
        return '驳回原因不能为空'
      }
      return true
    }
  })
  await rejectTodo({ todoId: todo.id, opinion: value || '' })
  ElMessage.success('驳回成功')
  loadData()
}

const batchApprove = async () => {
  await ElMessageBox.confirm(`确认批量审批 ${selectedIds.value.length} 条待办？`, '提示', {
    type: 'warning'
  })
  await batchApproveTodo({ todoIds: selectedIds.value, opinion: '批量审批通过' })
  ElMessage.success('批量审批成功')
  loadData()
}

const goToDoc = (row: any) => {
  router.push({ path: `/${(row as TodoListVO).businessType}/${(row as TodoListVO).businessId}` })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.todo-list {
  padding: 16px;
}
.batch-bar {
  margin-bottom: 12px;
}
.tab-badge {
  margin-left: 6px;
}
</style>
