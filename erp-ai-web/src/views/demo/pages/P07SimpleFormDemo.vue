<template>
  <PageP07SimpleForm view-id="demo-p07" page-type="P07" :config="pageConfig" :permissions="[]">
    <template #action-bar>
      <div class="action-left">
        <el-button type="primary">新增</el-button>
        <el-button type="success">保存</el-button>
        <el-button type="danger">删除</el-button>
        <el-button>重置</el-button>
      </div>
      <div class="action-right">
        <el-button type="warning">审核</el-button>
        <el-button>反审</el-button>
        <el-divider direction="vertical" />
        <el-button>打印</el-button>
        <el-button>导出</el-button>
      </div>
    </template>

    <template #main-content>
      <div class="form-container">
        <h3 class="section-label">公司信息</h3>
        <el-form :model="form" label-width="120px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="公司编码" required
                ><el-input v-model="form.code"
              /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="公司名称" required
                ><el-input v-model="form.name"
              /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="简称"><el-input v-model="form.shortName" /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="法人代表"><el-input v-model="form.legalPerson" /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-switch v-model="form.status" active-text="启用" inactive-text="禁用" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="地址"
                ><el-input v-model="form.address" type="textarea" :rows="2"
              /></el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="备注"
                ><el-input v-model="form.remark" type="textarea" :rows="3"
              /></el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </template>
  </PageP07SimpleForm>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageP07SimpleForm from '@/components/page-base/PageP07SimpleForm.vue'

const pageConfig = { title: '公司管理（P07）', showActionBar: true, formMaxWidth: 960 }

const form = ref({
  code: 'COMP-001',
  name: 'XX科技有限公司',
  shortName: 'XX科技',
  legalPerson: '张三',
  phone: '010-12345678',
  status: true,
  address: '北京市朝阳区XX路100号',
  remark: ''
})
</script>

<style scoped lang="scss">
.action-left {
  display: flex;
  gap: 8px;
  align-items: center;
}
.action-right {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-left: auto;
}
.form-container {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px 32px;
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
  .section-label {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }
}
</style>
