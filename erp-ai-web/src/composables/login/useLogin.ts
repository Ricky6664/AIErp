import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/modules/user'
import { usePermissionStore } from '@/stores/modules/permission'
import { getCaptchaApi } from '@/api/modules/auth'
import type { LoginDTO } from '@/api/types/auth'
import type { FormInstance, FormRules } from 'element-plus'

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

  const form = reactive<LoginFormData>({
    username: '',
    password: '',
    captchaCode: '',
    captchaKey: '',
    rememberMe: false
  })

  const rules: FormRules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ],
    captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
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

      // 动态路由生成：根据菜单树添加路由
      if (userStore.menuTree.length > 0) {
        permissionStore.generateRoutes(userStore.menuTree)
        const dynamicRoutes = permissionStore.routes
        for (const r of dynamicRoutes) {
          router.addRoute(r)
        }
      }

      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/home'
      router.replace(redirect)
    } catch (error: unknown) {
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

  return {
    formRef,
    form,
    rules,
    loading,
    captchaImage,
    captchaKey,
    loadCaptcha,
    handleLogin,
    handleCaptchaRefresh
  }
}
