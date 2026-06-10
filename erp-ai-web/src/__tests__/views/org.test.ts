import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import CompanyForm from '@/views/org/CompanyForm.vue'
import OrgWorkbench from '@/views/org/OrgWorkbench.vue'

const globalStubs = {
  MasterForm: { template: '<div class="master-form-stub"><slot /></div>' },
  FormField: { template: '<div class="form-field-stub"><slot /></div>' },
  KpiCard: { template: '<div class="kpi-card-stub"></div>' },
  'el-dialog': { template: '<div v-if="modelValue"><slot /></div>', props: ['modelValue'] },
  'el-button': { template: '<button><slot /></button>' },
  'el-input': { template: '<input />' },
  'el-table': { template: '<table><slot /></table>' },
  'el-table-column': { template: '<td><slot /></td>' },
  'el-tag': { template: '<span><slot /></span>' },
  'el-pagination': { template: '<div class="pagination-stub"></div>' },
  RouterLink: { template: '<a><slot /></a>' }
}

const globalMocks = {
  $t: (key: string) => key
}

const globalDirectives = {
  permission: () => {}
}

describe('CompanyForm', () => {
  it('渲染时不崩溃: 组件正常挂载', () => {
    const wrapper = mount(CompanyForm, {
      props: {
        visible: true
      },
      global: {
        stubs: globalStubs,
        mocks: globalMocks,
        directives: globalDirectives
      }
    })
    expect(wrapper.exists()).toBe(true)
  })
})

describe('OrgWorkbench', () => {
  it('渲染时不崩溃: 组件正常挂载', () => {
    const wrapper = mount(OrgWorkbench, {
      global: {
        stubs: globalStubs,
        mocks: {
          ...globalMocks,
          $router: { push: () => {}, replace: () => {} },
          $route: { path: '/org/workbench', params: {}, query: {} }
        },
        directives: globalDirectives,
        provide: {
          router: { push: () => {}, replace: () => {} }
        }
      }
    })
    expect(wrapper.exists()).toBe(true)
  })
})
