import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import LoginPage from '@/views/login/index.vue'

function createWrapper() {
  return mount(LoginPage, {
    global: {
      stubs: {
        CaptchaImage: { template: '<div class="captcha-stub"></div>' },
        PasswordStrength: { template: '<div class="password-strength-stub"></div>' },
        'el-input': {
          template: '<input :placeholder="$attrs.placeholder" />',
          inheritAttrs: false
        },
        'el-button': { template: '<button><slot /></button>' },
        'el-form': { template: '<form><slot /></form>' },
        'el-form-item': { template: '<div><slot /></div>' },
        'el-checkbox': { template: '<input type="checkbox" />' },
        ChangePassword: { template: '<div class="change-password-stub"></div>' },
        RouterLink: { template: '<a><slot /></a>' }
      },
      mocks: {
        $t: (key: string) => key
      }
    }
  })
}

describe('LoginPage', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('渲染登录表单: 组件正常挂载', () => {
    const wrapper = createWrapper()
    expect(wrapper.exists()).toBe(true)
  })
})
