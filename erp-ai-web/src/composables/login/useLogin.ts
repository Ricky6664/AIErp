import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/modules/user'
import { usePermissionStore } from '@/stores/modules/permission'
import { getCaptchaApi, getLockStatusApi } from '@/api/modules/auth'
import type { LoginDTO } from '@/api/types/auth'
import type { LockStatusResponse } from '@/api/types/auth'
import type { FormInstance, FormRules } from 'element-plus'

const REMEMBERED_USERNAME_KEY = 'remembered_username'

export interface LoginFormData {
  username: string
  password: string
  captchaCode: string
  captchaKey: string
  rememberMe: boolean
}

const ERROR_CODE_MAP: Record<string, string> = {
  CAPTCHA_EXPIRED: '验证码已过期',
  ACCOUNT_LOCKED: '账户已锁定，请稍后再试',
  PASSWORD_ERROR: '用户名或密码错误',
  ACCOUNT_DISABLED: '账户已禁用',
  CAPTCHA_ERROR: '验证码错误'
}

function resolveErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    const msg = error.message || ''
    for (const [code, text] of Object.entries(ERROR_CODE_MAP)) {
      if (msg.toUpperCase().includes(code.toUpperCase())) {
        return text
      }
    }
    return msg || '登录失败，请检查用户名和密码'
  }
  return '登录失败，请检查用户名和密码'
}

export function useLogin() {
  const router = useRouter()
  const route = useRoute()
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  const formRef = ref<FormInstance>()
  const loading = ref(false)
  const captchaImage = ref('')
  const captchaKey = ref('')
  const lockStatus = ref<LockStatusResponse>({
    locked: false,
    remainingSeconds: 0,
    remainingMinutes: 0
  })
  let lockTimer: ReturnType<typeof setInterval> | null = null
  let debounceTimer: ReturnType<typeof setTimeout> | null = null

  const form = reactive<LoginFormData>({
    username: '',
    password: '',
    captchaCode: '',
    captchaKey: '',
    rememberMe: false
  })

  const rules: FormRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 8, max: 32, message: '密码长度至少8位', trigger: 'blur' }
    ],
    captchaCode: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { len: 4, message: '验证码为4位', trigger: 'blur' }
    ]
  }

  async function checkLockStatus(username: string) {
    if (!username || username.trim().length === 0) {
      lockStatus.value = { locked: false, remainingSeconds: 0, remainingMinutes: 0 }
      stopLockTimer()
      return
    }
    try {
      const status = await getLockStatusApi(username.trim())
      lockStatus.value = status
      if (status.locked) {
        startLockTimer()
      } else {
        stopLockTimer()
      }
    } catch {
      lockStatus.value = { locked: false, remainingSeconds: 0, remainingMinutes: 0 }
      stopLockTimer()
    }
  }

  function startLockTimer() {
    stopLockTimer()
    lockTimer = setInterval(() => {
      if (lockStatus.value.remainingSeconds > 0) {
        lockStatus.value.remainingSeconds--
        lockStatus.value.remainingMinutes = Math.ceil(lockStatus.value.remainingSeconds / 60)
      } else {
        lockStatus.value = { locked: false, remainingSeconds: 0, remainingMinutes: 0 }
        stopLockTimer()
      }
    }, 1000)
  }

  function stopLockTimer() {
    if (lockTimer !== null) {
      clearInterval(lockTimer)
      lockTimer = null
    }
  }

  function onUsernameInput(username: string) {
    if (debounceTimer !== null) {
      clearTimeout(debounceTimer)
    }
    if (!username || username.trim().length === 0) {
      lockStatus.value = { locked: false, remainingSeconds: 0, remainingMinutes: 0 }
      stopLockTimer()
      return
    }
    debounceTimer = setTimeout(() => {
      checkLockStatus(username)
    }, 500)
  }

  function onUsernameBlur(username: string) {
    if (debounceTimer !== null) {
      clearTimeout(debounceTimer)
    }
    checkLockStatus(username)
  }

  async function loadCaptcha() {
    try {
      const data = await getCaptchaApi()
      captchaImage.value = data.captchaImage
      captchaKey.value = data.captchaKey
      form.captchaKey = data.captchaKey
    } catch {
      captchaImage.value = ''
      captchaKey.value = ''
    }
  }

  async function handleLogin() {
    if (!formRef.value) return
    try {
      await formRef.value.validate()
    } catch {
      return
    }

    loading.value = true
    try {
      const loginData: LoginDTO = {
        username: form.username,
        password: form.password,
        captchaCode: form.captchaCode,
        captchaKey: form.captchaKey,
        rememberMe: form.rememberMe
      }
      await userStore.login(loginData)

      // 记住我：存储加密后的用户名
      if (form.rememberMe) {
        const encoded = btoa(encodeURIComponent(form.username))
        localStorage.setItem(REMEMBERED_USERNAME_KEY, encoded)
      } else {
        localStorage.removeItem(REMEMBERED_USERNAME_KEY)
      }

      // 密码过期强制跳转修改密码页
      if (userStore.passwordExpired) {
        ElMessage.warning('您的密码已过期，请修改密码')
        router.replace('/change-password')
        return
      }

      // 动态路由生成：根据菜单树添加路由（作为 Layout 子路由，跳过路径已存在的静态路由）
      if (userStore.menuTree.length > 0) {
        permissionStore.generateRoutes(userStore.menuTree)
        const existingPaths = new Set(
          router.getRoutes().map((r) => {
            const p = r.path || ''
            return p.startsWith('/') ? p : `/${p}`
          })
        )
        for (const r of permissionStore.routes) {
          const fullPath = (r.path || '').startsWith('/') ? r.path : `/${r.path}`
          if (r.name && !router.hasRoute(r.name) && !existingPaths.has(fullPath)) {
            router.addRoute('Layout', r)
          }
        }
      }

      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/home'
      router.replace(redirect)
    } catch (error: unknown) {
      console.error('[Login] Error during login:', error)
      const errMsg = resolveErrorMessage(error)
      ElMessage.error(errMsg)
      form.captchaCode = ''
      loadCaptcha()
    } finally {
      loading.value = false
    }
  }

  function handleCaptchaRefresh() {
    form.captchaCode = ''
    loadCaptcha()
  }

  function loadRememberedUsername() {
    const encoded = localStorage.getItem(REMEMBERED_USERNAME_KEY)
    if (encoded) {
      try {
        form.username = decodeURIComponent(atob(encoded))
        form.rememberMe = true
      } catch {
        localStorage.removeItem(REMEMBERED_USERNAME_KEY)
      }
    }
  }

  return {
    formRef,
    form,
    rules,
    loading,
    captchaImage,
    captchaKey,
    lockStatus,
    loadCaptcha,
    loadRememberedUsername,
    checkLockStatus,
    onUsernameInput,
    onUsernameBlur,
    handleLogin,
    handleCaptchaRefresh
  }
}
