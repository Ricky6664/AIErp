<template>
  <div class="message-template-list">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" inline>
      <el-form-item label="模板编码">
        <el-input v-model="queryParams.templateCode" clearable />
      </el-form-item>
      <el-form-item label="模板名称">
        <el-input v-model="queryParams.keyword" clearable />
      </el-form-item>
      <el-form-item label="渠道">
        <el-select v-model="queryParams.channel" clearable placeholder="请选择">
          <el-option label="站内信" value="site" />
          <el-option label="邮件" value="email" />
          <el-option label="短信" value="sms" />
          <el-option label="微信" value="wechat" />
        </el-select>
      </el-form-item>
      <el-form-item label="启用状态">
        <el-switch v-model="queryParams.enableFlag" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openDialog()">新增</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table :data="tableData" border>
      <el-table-column prop="templateCode" label="模板编码" width="150" />
      <el-table-column prop="templateName" label="模板名称" min-width="180" />
      <el-table-column prop="channel" label="渠道" width="100">
        <template #default="{ row }">
          <el-tag>{{ channelMap[row.channel] || row.channel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="enableFlag" label="启用" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.enableFlag" @change="toggleEnable(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="queryParams.pageNum"
      v-model:page-size="queryParams.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @change="loadData"
    />

    <!-- 弹窗表单 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑模板' : '新增模板'" width="700px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="formData.templateCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" />
        </el-form-item>
        <el-form-item label="模板内容" prop="templateContent">
          <el-input
            v-model="formData.templateContent"
            type="textarea"
            :rows="6"
            placeholder="支持${变量名}格式，如：尊敬的${userName}，您的${docNo}已审批"
          />
          <div class="var-hint">
            可用变量：${userName}用户名、${docNo}单据编号、${docType}单据类型、${time}时间
          </div>
        </el-form-item>
        <el-form-item label="渠道" prop="channels">
          <el-checkbox-group v-model="formData.channels">
            <el-checkbox label="site">站内信</el-checkbox>
            <el-checkbox label="email">邮件</el-checkbox>
            <el-checkbox label="sms">短信</el-checkbox>
            <el-checkbox label="wechat">微信</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getTemplatePage, createTemplate, updateTemplate, deleteTemplate } from '@/api/msg/template'
import type { TemplateListVO, TemplateFormDTO } from '@/types/msg'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const channelMap: Record<string, string> = {
  site: '站内信',
  email: '邮件',
  sms: '短信',
  wechat: '微信'
}

const queryParams = reactive({
  templateCode: '',
  keyword: '',
  channel: '',
  enableFlag: undefined as boolean | undefined,
  pageNum: 1,
  pageSize: 20
})

const tableData = ref<TemplateListVO[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const defaultFormData: TemplateFormDTO = {
  id: undefined,
  templateCode: '',
  templateName: '',
  templateContent: '',
  channel: '',
  channels: ['site'],
  enableFlag: true
}

const formData = reactive<TemplateFormDTO>({ ...defaultFormData })

const rules: FormRules = {
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateContent: [{ required: true, message: '请输入模板内容', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getTemplatePage({
    templateCode: queryParams.templateCode || undefined,
    keyword: queryParams.keyword || undefined,
    channel: queryParams.channel || undefined,
    enableFlag: queryParams.enableFlag,
    pageNum: queryParams.pageNum,
    pageSize: queryParams.pageSize
  })
  tableData.value = res.records
  total.value = res.total
}

const resetQuery = () => {
  queryParams.templateCode = ''
  queryParams.keyword = ''
  queryParams.channel = ''
  queryParams.enableFlag = undefined
  queryParams.pageNum = 1
  loadData()
}

const openDialog = (row?: TemplateListVO) => {
  if (row) {
    isEdit.value = true
    Object.assign(formData, {
      id: row.id,
      templateCode: row.templateCode,
      templateName: row.templateName,
      templateContent: '',
      channel: row.channel,
      channels: row.channel ? row.channel.split(',') : [],
      enableFlag: row.enableFlag
    })
  } else {
    isEdit.value = false
    Object.assign(formData, defaultFormData)
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value?.validate()
  formData.channel = formData.channels.join(',')
  if (isEdit.value) {
    await updateTemplate(formData)
    ElMessage.success('修改成功')
  } else {
    await createTemplate(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确认删除该模板？', '提示', { type: 'warning' })
  await deleteTemplate(id)
  ElMessage.success('删除成功')
  loadData()
}

const toggleEnable = async (row: TemplateListVO) => {
  try {
    await updateTemplate({
      id: row.id,
      templateCode: row.templateCode,
      templateName: row.templateName,
      templateContent: '',
      channel: row.channel,
      channels: row.channel ? row.channel.split(',') : [],
      enableFlag: row.enableFlag
    })
    ElMessage.success('状态已更新')
  } catch {
    row.enableFlag = !row.enableFlag
    ElMessage.error('状态更新失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.message-template-list {
  padding: 16px;
}

.var-hint {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
}
</style>
