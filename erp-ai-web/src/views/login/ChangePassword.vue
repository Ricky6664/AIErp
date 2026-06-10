<template>
  <div class="change-password-container">
    <div class="change-password-card">
      <h2 class="title">修改密码</h2>
      <p class="tip">您的密码已过期，请设置新密码后继续使用系统</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            placeholder="请输入旧密码"
            show-password
          />
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="handleSubmit">
            确认修改
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { changeMyPasswordApi } from '@/api/modules/user'
import { useUserStore } from '@/stores/modules/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 32, message: '密码长度8-32位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  if (form.oldPassword === form.newPassword) {
    ElMessage.warning('新密码不能与旧密码相同')
    return
  }

  loading.value = true
  try {
    await changeMyPasswordApi(form.oldPassword, form.newPassword)
    ElMessage.success('密码修改成功')
    userStore.passwordExpired = false
    router.replace('/home')
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : '密码修改失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.change-password-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.change-password-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.tip {
  text-align: center;
  font-size: 14px;
  color: #909399;
  margin-bottom: 32px;
}

.submit-btn {
  width: 100%;
}
</style>
