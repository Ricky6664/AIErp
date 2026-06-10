<template>
  <PageP13Config view-id="demo-p13" page-type="P13" :config="pageConfig" :permissions="[]">
    <template #config-content="{ navId }">
      <div v-if="navId === 'basic'" class="config-form-area">
        <h4 class="config-section-title">基础设置</h4>
        <el-form label-width="120px">
          <el-form-item label="系统名称"><el-input value="ERP智能管理系统" /></el-form-item>
          <el-form-item label="系统版本"><el-input value="V1.0.0" disabled /></el-form-item>
          <el-form-item label="公司名称"><el-input value="XX科技有限公司" /></el-form-item>
          <el-form-item label="默认语言">
            <el-select value="zh-CN" style="width: 200px">
              <el-option label="简体中文" value="zh-CN" />
              <el-option label="English" value="en-US" />
            </el-select>
          </el-form-item>
          <el-form-item label="时区">
            <el-select value="Asia/Shanghai" style="width: 200px">
              <el-option label="Asia/Shanghai (UTC+8)" value="Asia/Shanghai" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div v-else-if="navId === 'security'" class="config-form-area">
        <h4 class="config-section-title">安全配置</h4>
        <el-form label-width="120px">
          <el-form-item label="密码最小长度"
            ><el-input-number :min="6" :max="32" :value="8"
          /></el-form-item>
          <el-form-item label="密码复杂度"
            ><el-switch :model-value="true" active-text="启用"
          /></el-form-item>
          <el-form-item label="登录失败锁定"
            ><el-input-number :min="3" :max="10" :value="5" /> 次</el-form-item
          >
          <el-form-item label="会话超时"
            ><el-input-number :min="10" :max="1440" :value="30" /> 分钟</el-form-item
          >
        </el-form>
      </div>
      <div v-else-if="navId === 'display'" class="config-form-area">
        <h4 class="config-section-title">显示设置</h4>
        <el-form label-width="120px">
          <el-form-item label="每页默认条数">
            <el-select :model-value="20" style="width: 160px">
              <el-option :value="10" label="10条" /><el-option :value="20" label="20条" /><el-option
                :value="50"
                label="50条"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="侧边栏默认展开"><el-switch :model-value="true" /></el-form-item>
          <el-form-item label="标签页模式"
            ><el-switch :model-value="true" active-text="多标签" inactive-text="单页面"
          /></el-form-item>
          <el-form-item label="表格行高"
            ><el-select :model-value="'medium'" style="width: 160px">
              <el-option label="迷你" value="mini" /><el-option
                label="小"
                value="small"
              /><el-option label="正常" value="medium" /><el-option
                label="大"
                value="large"
              /> </el-select
          ></el-form-item>
        </el-form>
      </div>
    </template>
  </PageP13Config>
</template>

<script setup lang="ts">
import PageP13Config from '@/components/page-base/PageP13Config.vue'

const pageConfig = {
  title: '系统配置（P13）',
  configLayout: 'split' as const,
  showActionBar: true,
  showSaveBtn: true,
  showResetBtn: true,
  navItems: [
    {
      id: 'basic',
      label: '基础设置',
      children: [
        { id: 'basic-general', label: '通用设置' },
        { id: 'basic-company', label: '公司信息' }
      ]
    },
    {
      id: 'security',
      label: '安全配置',
      children: [
        { id: 'security-password', label: '密码策略' },
        { id: 'security-login', label: '登录策略' }
      ]
    },
    {
      id: 'display',
      label: '显示设置',
      children: [
        { id: 'display-table', label: '表格配置' },
        { id: 'display-layout', label: '布局配置' }
      ]
    }
  ]
}
</script>

<style scoped lang="scss">
.config-form-area {
  max-width: 720px;
}
.config-section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}
</style>
