<template>
  <PageP04SimpleList view-id="system-announcement-list" page-type="P04" :config="pageConfig">
    <template #query-panel>
      <el-input
        v-model="searchTitle"
        placeholder="公告标题"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      >
        <template #prepend>标题</template>
      </el-input>
      <el-select
        v-model="searchType"
        placeholder="公告类型"
        clearable
        style="width: 160px"
        @change="handleSearch"
      >
        <el-option label="系统公告" value="system" />
        <el-option label="业务公告" value="business" />
        <el-option label="活动通知" value="event" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button @click="handleReset">
        <el-icon><Refresh /></el-icon>
        重置
      </el-button>
    </template>

    <template #action-bar>
      <div class="action-left">
        <span class="record-count">共 {{ total }} 条记录</span>
      </div>
      <div class="action-right">
        <el-button v-permission="'system:announcement:manage'" type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增公告
        </el-button>
      </div>
    </template>

    <template #main-content>
      <el-table ref="tableRef" v-loading="loading" :data="list" border stripe style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column label="内容" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ truncateContent(row.content) }}
          </template>
        </el-table-column>
        <el-table-column prop="announcementType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.announcementType)" size="small">
              {{ typeLabel(row.announcementType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatTime(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isTop ? 'danger' : 'info'" size="small">
              {{ row.isTop ? '置顶' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建人" width="120" prop="creatorName" show-overflow-tooltip />
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'system:announcement:manage'"
              type="warning"
              link
              size="small"
              @click="handleToggleTop(row)"
            >
              <el-icon><Top /></el-icon>
              {{ row.isTop ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button
              v-permission="'system:announcement:manage'"
              type="primary"
              link
              size="small"
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-permission="'system:announcement:manage'"
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </template>
  </PageP04SimpleList>

  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    @closed="handleDialogClosed"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
      <el-form-item label="公告标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入公告标题"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="公告类型" prop="announcementType">
        <el-select
          v-model="formData.announcementType"
          placeholder="请选择公告类型"
          style="width: 100%"
        >
          <el-option label="系统公告" value="system" />
          <el-option label="业务公告" value="business" />
          <el-option label="活动通知" value="event" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker
          v-model="formData.publishTime"
          type="datetime"
          placeholder="请选择发布时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="是否置顶" prop="isTop">
        <el-switch v-model="formData.isTop" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="2">已发布</el-radio>
          <el-radio :value="1">草稿</el-radio>
          <el-radio :value="3">已撤回</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="公告内容" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="6"
          placeholder="请输入公告内容（支持HTML富文本）"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, Top } from '@element-plus/icons-vue'
import PageP04SimpleList from '@/components/page-base/PageP04SimpleList.vue'
import type { SimpleListPageConfig } from '@/types/page-base.d.ts'
import type { AnnouncementListItem, AnnouncementQuery } from '@/api/types/announcement'
import {
  getAnnouncementPageList,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement
} from '@/api/modules/announcement'

const pageConfig: SimpleListPageConfig = {
  title: '公告管理',
  showQueryPanel: true,
  showActionBar: true
}

const loading = ref(false)
const list = ref<AnnouncementListItem[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const searchTitle = ref('')
const searchType = ref('')

const typeMap: Record<string, string> = {
  system: '系统公告',
  business: '业务公告',
  event: '活动通知'
}

const typeTagMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
  system: 'primary',
  business: 'success',
  event: 'warning'
}

const statusMap: Record<number, string> = { 1: '草稿', 2: '已发布', 3: '已撤回' }
const statusTagMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
  1: 'info',
  2: 'success',
  3: 'warning'
}

function typeLabel(type: string): string {
  return typeMap[type] || type || '-'
}

function typeTagType(type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  return typeTagMap[type] ?? 'info'
}

function statusLabel(status: number): string {
  return statusMap[status] || String(status)
}

function statusTagType(status: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  return statusTagMap[status] ?? 'info'
}

function formatTime(time: string | null): string {
  if (!time) return '-'
  const d = new Date(time)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function truncateContent(content: string | null): string {
  if (!content) return '-'
  const plain = content.replace(/<[^>]+>/g, '')
  return plain.length > 100 ? plain.substring(0, 100) + '...' : plain
}

async function fetchList(): Promise<void> {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchTitle.value) params.title = searchTitle.value
    if (searchType.value) params.announcementType = searchType.value
    const result = await getAnnouncementPageList(params as unknown as AnnouncementQuery)
    list.value = result.records ?? []
    total.value = result.total ?? 0
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  pageNum.value = 1
  fetchList()
}

function handleReset(): void {
  searchTitle.value = ''
  searchType.value = ''
  pageNum.value = 1
  fetchList()
}

function handlePageChange(): void {
  fetchList()
}

function handleSizeChange(): void {
  pageNum.value = 1
  fetchList()
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增公告')
const submitLoading = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance | null>(null)

const initialFormData = {
  title: '',
  content: '',
  announcementType: '',
  publishTime: '',
  isTop: false,
  status: 2
}

const formData = reactive({ ...initialFormData })

const formRules: FormRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { max: 200, message: '公告标题最长200个字符', trigger: 'blur' }
  ],
  announcementType: [{ max: 50, message: '公告类型最长50个字符', trigger: 'blur' }]
}

function handleAdd(): void {
  isEdit.value = false
  editId.value = null
  dialogTitle.value = '新增公告'
  Object.assign(formData, { ...initialFormData })
  dialogVisible.value = true
}

function handleEdit(row: AnnouncementListItem): void {
  isEdit.value = true
  editId.value = row.id
  dialogTitle.value = '编辑公告'
  Object.assign(formData, {
    title: row.title,
    content: row.content || '',
    announcementType: row.announcementType || '',
    publishTime: row.publishTime || '',
    isTop: row.isTop ?? false,
    status: row.status ?? 2
  })
  dialogVisible.value = true
}

function handleDialogClosed(): void {
  formRef.value?.resetFields()
}

async function handleSubmit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateAnnouncement(editId.value, { ...formData, id: editId.value })
      ElMessage.success('编辑成功')
    } else {
      await createAnnouncement({ ...formData })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await fetchList()
  } catch {
    // error handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

function handleToggleTop(row: AnnouncementListItem): void {
  const newTop = !row.isTop
  ElMessageBox.confirm(`确定${newTop ? '置顶' : '取消置顶'}公告「${row.title}」？`, '操作确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await updateAnnouncement(row.id, {
        id: row.id,
        title: row.title,
        content: row.content,
        announcementType: row.announcementType,
        publishTime: row.publishTime,
        isTop: newTop,
        status: row.status
      })
      ElMessage.success(newTop ? '已置顶' : '已取消置顶')
      await fetchList()
    })
    .catch(() => {
      // user cancelled
    })
}

function handleDelete(row: AnnouncementListItem): void {
  ElMessageBox.confirm(`确定删除公告「${row.title}」？删除后不可恢复。`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await deleteAnnouncement(row.id)
      ElMessage.success('删除成功')
      await fetchList()
    })
    .catch(() => {
      // user cancelled
    })
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
.action-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.record-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
