<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="login-bg-shape shape-1" />
      <div class="login-bg-shape shape-2" />
      <div class="login-bg-shape shape-3" />
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="48" height="48" rx="12" fill="var(--el-color-primary)" />
            <path d="M14 16h20v4H14zM14 24h20v4H14zM14 32h12v4H14z" fill="#fff" />
          </svg>
        </div>
        <h1 class="login-title">{{ $t('login.title') }}</h1>
        <p class="login-desc">{{ $t('login.desc') }}</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            :placeholder="$t('login.username')"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            :placeholder="$t('login.password')"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="captchaCode">
          <div class="captcha-row">
            <el-input
              v-model="form.captchaCode"
              :placeholder="$t('login.captcha')"
              :prefix-icon="Key"
              class="captcha-input"
            />
            <CaptchaImage :image="captchaImage" @refresh="handleCaptchaRefresh" />
          </div>
        </el-form-item>

        <el-form-item>
          <div class="form-options">
            <el-checkbox v-model="form.rememberMe">{{ $t('login.rememberMe') }}</el-checkbox>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            {{ loading ? '' : $t('login.submit') }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { User, Lock, Key } from '@element-plus/icons-vue'
import CaptchaImage from './components/CaptchaImage.vue'
import { useLogin } from '@/composables/login/useLogin'

defineOptions({ name: 'LoginPage' })

const {
  formRef,
  form,
  rules,
  loading,
  captchaImage,
  loadCaptcha,
  handleLogin,
  handleCaptchaRefresh
} = useLogin()

defineExpose({ formRef })

onMounted(() => {
  loadCaptcha()
})
</script>

<style scoped>
.login-page {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.login-bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  background: #fff;
}

.shape-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -100px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -50px;
}

.shape-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 15%;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  padding: 40px 36px 32px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
}

.login-logo svg {
  width: 100%;
  height: 100%;
}

.login-title {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.login-desc {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.login-form {
  width: 100%;
}

.captcha-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
}

@media (max-width: 480px) {
  .login-card {
    width: calc(100% - 32px);
    margin: 0 16px;
    padding: 32px 24px 24px;
  }
}
</style>
