<template>
  <div class="sso-oauth2-config-page">
    <div class="page-header">
      <h2>SSO/OAuth2配置管理</h2>
      <p class="page-desc">
        管理SSO单点登录和OAuth2第三方登录的配置信息，支持新增、修改、删除和连接测试
      </p>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- ==================== SSO配置 Tab ==================== -->
      <el-tab-pane label="SSO配置" name="sso">
        <div class="toolbar">
          <div class="toolbar-left">
            <el-input
              v-model="ssoSearch.ssoName"
              placeholder="配置名称"
              clearable
              style="width: 180px"
              @keyup.enter="handleSsoSearch"
            />
            <el-select
              v-model="ssoSearch.type"
              placeholder="认证类型"
              clearable
              style="width: 160px"
              @change="handleSsoSearch"
            >
              <el-option label="全部" value="" />
              <el-option label="SAML 2.0" value="saml2" />
              <el-option label="CAS" value="cas" />
              <el-option label="OIDC" value="oidc" />
              <el-option label="LDAP" value="ldap" />
            </el-select>
            <el-select
              v-model="ssoSearch.enabled"
              placeholder="启用状态"
              clearable
              style="width: 140px"
              @change="handleSsoSearch"
            >
              <el-option label="全部" value="" />
              <el-option label="已启用" value="true" />
              <el-option label="已禁用" value="false" />
            </el-select>
            <el-button type="primary" @click="handleSsoSearch">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleSsoReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-button
              v-permission="'system:sso-config:create'"
              type="primary"
              @click="handleSsoAdd"
            >
              <el-icon><Plus /></el-icon>
              新增SSO配置
            </el-button>
          </div>
        </div>

        <el-table v-loading="ssoLoading" :data="ssoList" border stripe style="width: 100%">
          <el-table-column prop="ssoName" label="配置名称" width="150" />
          <el-table-column label="认证类型" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="ssoTypeTagType(row.type)" size="small">
                {{ ssoTypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="idpUrl" label="IDP地址" min-width="200" show-overflow-tooltip />
          <el-table-column prop="spEntityId" label="SP实体ID" width="180" show-overflow-tooltip />
          <el-table-column label="启用状态" width="90" align="center">
            <template #default="{ row }">
              <el-switch
                :model-value="row.enabled"
                disabled
                style="--el-switch-on-color: var(--el-color-success)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170" />
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button
                v-permission="'system:sso-config:update'"
                type="primary"
                size="small"
                link
                @click="handleSsoEdit(row as SsoConfigItem)"
              >
                编辑
              </el-button>
              <el-popconfirm
                title="确定删除该SSO配置？"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleSsoDelete(row as SsoConfigItem)"
              >
                <template #reference>
                  <el-button
                    v-permission="'system:sso-config:delete'"
                    type="danger"
                    size="small"
                    link
                  >
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="ssoPageNum"
            v-model:page-size="ssoPageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="ssoTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSsoSizeChange"
            @current-change="handleSsoPageChange"
          />
        </div>
      </el-tab-pane>

      <!-- ==================== OAuth2配置 Tab ==================== -->
      <el-tab-pane label="OAuth2配置" name="oauth2">
        <div class="toolbar">
          <div class="toolbar-left">
            <el-input
              v-model="oauth2Search.supplierName"
              placeholder="供应商名称"
              clearable
              style="width: 180px"
              @keyup.enter="handleOauth2Search"
            />
            <el-select
              v-model="oauth2Search.type"
              placeholder="认证类型"
              clearable
              style="width: 160px"
              @change="handleOauth2Search"
            >
              <el-option label="全部" value="" />
              <el-option label="微信" value="wechat" />
              <el-option label="钉钉" value="dingtalk" />
              <el-option label="飞书" value="feishu" />
              <el-option label="企业微信" value="wecom" />
              <el-option label="GitHub" value="github" />
              <el-option label="Google" value="google" />
              <el-option label="Microsoft" value="microsoft" />
            </el-select>
            <el-select
              v-model="oauth2Search.enabled"
              placeholder="启用状态"
              clearable
              style="width: 140px"
              @change="handleOauth2Search"
            >
              <el-option label="全部" value="" />
              <el-option label="已启用" value="true" />
              <el-option label="已禁用" value="false" />
            </el-select>
            <el-button type="primary" @click="handleOauth2Search">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleOauth2Reset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
          <div class="toolbar-right">
            <el-button
              v-permission="'system:oauth2-config:create'"
              type="primary"
              @click="handleOauth2Add"
            >
              <el-icon><Plus /></el-icon>
              新增OAuth2配置
            </el-button>
          </div>
        </div>

        <el-table v-loading="oauth2Loading" :data="oauth2List" border stripe style="width: 100%">
          <el-table-column prop="supplierName" label="供应商名称" width="140" />
          <el-table-column label="认证类型" width="110" align="center">
            <template #default="{ row }">
              <el-tag :type="oauth2TypeTagType(row.type)" size="small">
                {{ oauth2TypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="clientId" label="Client ID" width="220" show-overflow-tooltip />
          <el-table-column prop="authUrl" label="授权URL" min-width="200" show-overflow-tooltip />
          <el-table-column prop="scope" label="Scope" width="120" show-overflow-tooltip />
          <el-table-column label="启用状态" width="90" align="center">
            <template #default="{ row }">
              <el-switch
                :model-value="row.enabled"
                disabled
                style="--el-switch-on-color: var(--el-color-success)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="170" />
          <el-table-column label="操作" width="220" align="center" fixed="right">
            <template #default="{ row }">
              <el-button
                v-permission="'system:oauth2-config:update'"
                type="primary"
                size="small"
                link
                @click="handleOauth2Edit(row as Oauth2ConfigItem)"
              >
                编辑
              </el-button>
              <el-button
                v-permission="'system:oauth2-config:query'"
                type="warning"
                size="small"
                link
                :loading="testingId === row.id"
                @click="handleTestConnection(row as Oauth2ConfigItem)"
              >
                测试连接
              </el-button>
              <el-popconfirm
                title="确定删除该OAuth2配置？"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleOauth2Delete(row as Oauth2ConfigItem)"
              >
                <template #reference>
                  <el-button
                    v-permission="'system:oauth2-config:delete'"
                    type="danger"
                    size="small"
                    link
                  >
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="oauth2PageNum"
            v-model:page-size="oauth2PageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="oauth2Total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleOauth2SizeChange"
            @current-change="handleOauth2PageChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- ==================== SSO配置表单对话框 ==================== -->
    <el-dialog
      v-model="ssoDialogVisible"
      :title="ssoFormMode === 'add' ? '新增SSO配置' : '编辑SSO配置'"
      width="680px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="ssoFormRef"
        :model="ssoForm"
        :rules="ssoFormRules"
        label-width="130px"
        label-position="right"
      >
        <el-form-item label="配置名称" prop="ssoName">
          <el-input v-model="ssoForm.ssoName" placeholder="请输入SSO配置名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="认证类型" prop="type">
          <el-select v-model="ssoForm.type" placeholder="请选择认证类型" style="width: 100%">
            <el-option label="SAML 2.0" value="saml2" />
            <el-option label="CAS" value="cas" />
            <el-option label="OIDC" value="oidc" />
            <el-option label="LDAP" value="ldap" />
          </el-select>
        </el-form-item>
        <el-form-item label="IDP地址" prop="idpUrl">
          <el-input v-model="ssoForm.idpUrl" placeholder="https://idp.example.com/auth" />
        </el-form-item>
        <el-form-item label="SP实体ID" prop="spEntityId">
          <el-input v-model="ssoForm.spEntityId" placeholder="urn:example:sp" />
        </el-form-item>
        <el-form-item label="SSO登录URL" prop="ssoLoginUrl">
          <el-input v-model="ssoForm.ssoLoginUrl" placeholder="https://idp.example.com/sso/login" />
        </el-form-item>
        <el-form-item label="SSO登出URL" prop="ssoLogoutUrl">
          <el-input
            v-model="ssoForm.ssoLogoutUrl"
            placeholder="https://idp.example.com/sso/logout（可选）"
          />
        </el-form-item>
        <el-form-item label="X.509证书" prop="certificate">
          <el-input
            v-model="ssoForm.certificate"
            type="textarea"
            :rows="4"
            placeholder="-----BEGIN CERTIFICATE-----
（可选，粘贴X.509 PEM格式证书内容）
-----END CERTIFICATE-----"
          />
        </el-form-item>
        <el-form-item label="启用状态" prop="enabled">
          <el-switch
            v-model="ssoForm.enabled"
            active-text="启用"
            inactive-text="禁用"
            style="--el-switch-on-color: var(--el-color-success)"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ssoDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="ssoSubmitting" @click="handleSsoSubmit">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- ==================== OAuth2配置表单对话框 ==================== -->
    <el-dialog
      v-model="oauth2DialogVisible"
      :title="oauth2FormMode === 'add' ? '新增OAuth2配置' : '编辑OAuth2配置'"
      width="680px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="oauth2FormRef"
        :model="oauth2Form"
        :rules="oauth2FormRules"
        label-width="130px"
        label-position="right"
      >
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input
            v-model="oauth2Form.supplierName"
            placeholder="请输入供应商/平台名称"
            maxlength="100"
          />
        </el-form-item>
        <el-form-item label="认证类型" prop="type">
          <el-select v-model="oauth2Form.type" placeholder="请选择认证类型" style="width: 100%">
            <el-option label="微信" value="wechat" />
            <el-option label="钉钉" value="dingtalk" />
            <el-option label="飞书" value="feishu" />
            <el-option label="企业微信" value="wecom" />
            <el-option label="GitHub" value="github" />
            <el-option label="Google" value="google" />
            <el-option label="Microsoft" value="microsoft" />
          </el-select>
        </el-form-item>
        <el-form-item label="Client ID" prop="clientId">
          <el-input v-model="oauth2Form.clientId" placeholder="第三方平台分配的Client ID" />
        </el-form-item>
        <el-form-item label="Client Secret" prop="clientSecret">
          <el-input
            v-model="oauth2Form.clientSecret"
            type="password"
            show-password
            placeholder="第三方平台分配的Client Secret"
          />
        </el-form-item>
        <el-form-item label="授权URL" prop="authUrl">
          <el-input
            v-model="oauth2Form.authUrl"
            placeholder="https://provider.example.com/oauth2/authorize"
          />
        </el-form-item>
        <el-form-item label="Token URL" prop="tokenUrl">
          <el-input
            v-model="oauth2Form.tokenUrl"
            placeholder="https://provider.example.com/oauth2/token"
          />
        </el-form-item>
        <el-form-item label="用户信息URL" prop="userInfoUrl">
          <el-input
            v-model="oauth2Form.userInfoUrl"
            placeholder="https://provider.example.com/oauth2/userinfo（可选）"
          />
        </el-form-item>
        <el-form-item label="Scope" prop="scope">
          <el-input v-model="oauth2Form.scope" placeholder="openid profile email（可选）" />
        </el-form-item>
        <CallbackUrlInput :provider-type="oauth2Form.type" />
        <el-form-item label="启用状态" prop="enabled">
          <el-switch
            v-model="oauth2Form.enabled"
            active-text="启用"
            inactive-text="禁用"
            style="--el-switch-on-color: var(--el-color-success)"
          />
        </el-form-item>
      </el-form>

      <div v-if="oauth2TestResult" class="test-result-area">
        <el-divider />
        <el-alert
          :title="oauth2TestResult.success ? '连接成功' : '连接失败'"
          :type="oauth2TestResult.success ? 'success' : 'error'"
          :closable="false"
          show-icon
        >
          <template #default>
            <div class="test-result-detail">
              <p><span class="label">测试URL：</span>{{ oauth2TestResult.testUrl }}</p>
              <p><span class="label">HTTP状态码：</span>{{ oauth2TestResult.statusCode || '-' }}</p>
              <p><span class="label">响应时间：</span>{{ oauth2TestResult.responseTime }}ms</p>
              <p><span class="label">结果：</span>{{ oauth2TestResult.message }}</p>
            </div>
          </template>
        </el-alert>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <div class="footer-left">
            <el-button
              v-if="oauth2FormMode === 'edit'"
              type="warning"
              :loading="oauth2Testing"
              @click="handleOauth2TestInDialog"
            >
              测试连接
            </el-button>
          </div>
          <div class="footer-right">
            <el-button :disabled="oauth2Testing" @click="oauth2DialogVisible = false"
              >取消</el-button
            >
            <el-button
              type="primary"
              :loading="oauth2Submitting"
              :disabled="oauth2Testing"
              @click="handleOauth2Submit"
            >
              保存
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import type { SsoConfigItem, Oauth2ConfigItem, Oauth2TestResult } from '@/api/types/ssoOauth2Config'
import CallbackUrlInput from './components/CallbackUrlInput.vue'
import {
  getSsoConfigPageApi,
  createSsoConfigApi,
  updateSsoConfigApi,
  deleteSsoConfigApi,
  getOauth2ConfigPageApi,
  createOauth2ConfigApi,
  updateOauth2ConfigApi,
  deleteOauth2ConfigApi,
  testOauth2ConnectionApi
} from '@/api/modules/ssoOauth2Config'

// ==================== Tab状态 ====================
const activeTab = ref('sso')

// ==================== SSO配置状态 ====================
const ssoLoading = ref(false)
const ssoSubmitting = ref(false)
const ssoList = ref<SsoConfigItem[]>([])
const ssoTotal = ref(0)
const ssoPageNum = ref(1)
const ssoPageSize = ref(20)
const ssoSearch = reactive({ ssoName: '', type: '', enabled: '' })

const ssoDialogVisible = ref(false)
const ssoFormMode = ref<'add' | 'edit'>('add')
const ssoFormRef = ref<FormInstance | null>(null)
const ssoForm = reactive<SsoConfigItem>({
  ssoName: '',
  type: '',
  idpUrl: '',
  spEntityId: '',
  ssoLoginUrl: '',
  ssoLogoutUrl: '',
  certificate: '',
  enabled: true
})
const editingSsoId = ref<number | null>(null)
let ssoInitialValues: SsoConfigItem | null = null

const URL_REGEX = /^https?:\/\/.+\..+/

function urlValidator(_rule: any, value: string, callback: (error?: Error) => void): void {
  if (!value) {
    callback()
    return
  }
  if (!URL_REGEX.test(value)) {
    callback(new Error('请输入有效的URL地址'))
  } else {
    callback()
  }
}

const ssoFormRules: FormRules = {
  ssoName: [
    { required: true, message: '请输入配置名称', trigger: 'blur' },
    { max: 100, message: '配置名称不超过100个字符', trigger: 'blur' }
  ],
  type: [{ required: true, message: '请选择认证类型', trigger: 'change' }],
  idpUrl: [
    { required: true, message: '请输入IDP地址', trigger: 'blur' },
    { validator: urlValidator, trigger: 'blur' }
  ],
  spEntityId: [{ required: true, message: '请输入SP实体ID', trigger: 'blur' }],
  ssoLoginUrl: [
    { required: true, message: '请输入SSO登录URL', trigger: 'blur' },
    { validator: urlValidator, trigger: 'blur' }
  ]
}

const ssoTypeMap: Record<string, string> = {
  saml2: 'SAML 2.0',
  cas: 'CAS',
  oidc: 'OIDC',
  ldap: 'LDAP'
}
const ssoTypeColorMap: Record<string, string> = {
  saml2: 'primary',
  cas: 'success',
  oidc: 'warning',
  ldap: 'info'
}

type TagType = 'success' | 'warning' | 'primary' | 'info' | 'danger' | undefined

function ssoTypeLabel(type: string): string {
  return ssoTypeMap[type] || type
}
function ssoTypeTagType(type: string): TagType {
  return (ssoTypeColorMap[type] as TagType) || 'info'
}

// ==================== OAuth2配置状态 ====================
const oauth2Loading = ref(false)
const oauth2Submitting = ref(false)
const oauth2List = ref<Oauth2ConfigItem[]>([])
const oauth2Total = ref(0)
const oauth2PageNum = ref(1)
const oauth2PageSize = ref(20)
const oauth2Search = reactive({
  supplierName: '',
  type: '',
  enabled: ''
})

const oauth2DialogVisible = ref(false)
const oauth2FormMode = ref<'add' | 'edit'>('add')
const oauth2FormRef = ref<FormInstance | null>(null)
const oauth2Form = reactive<Oauth2ConfigItem>({
  supplierName: '',
  type: '',
  clientId: '',
  clientSecret: '',
  authUrl: '',
  tokenUrl: '',
  userInfoUrl: '',
  scope: '',
  enabled: true
})
const editingOauth2Id = ref<number | null>(null)
let oauth2InitialValues: Oauth2ConfigItem | null = null

const oauth2FormRules: FormRules = {
  supplierName: [
    { required: true, message: '请输入供应商名称', trigger: 'blur' },
    { max: 100, message: '供应商名称不超过100个字符', trigger: 'blur' }
  ],
  type: [{ required: true, message: '请选择认证类型', trigger: 'change' }],
  clientId: [{ required: true, message: '请输入Client ID', trigger: 'blur' }],
  clientSecret: [
    { required: true, message: '请输入Client Secret', trigger: 'blur' },
    { min: 8, message: 'Client Secret长度不能少于8位', trigger: 'blur' }
  ],
  authUrl: [
    { required: true, message: '请输入授权URL', trigger: 'blur' },
    { validator: urlValidator, trigger: 'blur' }
  ],
  tokenUrl: [
    { required: true, message: '请输入Token URL', trigger: 'blur' },
    { validator: urlValidator, trigger: 'blur' }
  ]
}

const testingId = ref<number | null>(null)
const oauth2Testing = ref(false)
const oauth2TestResult = ref<Oauth2TestResult | null>(null)

const oauth2TypeMap: Record<string, string> = {
  wechat: '微信',
  dingtalk: '钉钉',
  feishu: '飞书',
  wecom: '企业微信',
  github: 'GitHub',
  google: 'Google',
  microsoft: 'Microsoft'
}
const oauth2TypeColorMap: Record<string, string> = {
  wechat: 'success',
  dingtalk: '',
  feishu: 'primary',
  wecom: 'success',
  github: '',
  google: 'danger',
  microsoft: 'primary'
}

function oauth2TypeLabel(type: string): string {
  return oauth2TypeMap[type] || type
}
function oauth2TypeTagType(type: string): TagType {
  return (oauth2TypeColorMap[type] as TagType) || 'info'
}

// ==================== Tab切换 ====================
function handleTabChange(): void {
  if (activeTab.value === 'sso') {
    fetchSsoList()
  } else {
    fetchOauth2List()
  }
}

// ==================== SSO列表 ====================
function buildSsoQueryParams() {
  const params: Record<string, unknown> = { pageNum: ssoPageNum.value, pageSize: ssoPageSize.value }
  if (ssoSearch.ssoName) params.ssoName = ssoSearch.ssoName
  if (ssoSearch.type) params.type = ssoSearch.type
  if (ssoSearch.enabled !== '') params.enabled = ssoSearch.enabled === 'true'
  return params
}

async function fetchSsoList(): Promise<void> {
  ssoLoading.value = true
  try {
    const result = await getSsoConfigPageApi(buildSsoQueryParams() as any)
    ssoList.value = result.records ?? []
    ssoTotal.value = result.total ?? 0
  } catch {
    ssoList.value = []
    ssoTotal.value = 0
  } finally {
    ssoLoading.value = false
  }
}

function handleSsoSearch(): void {
  ssoPageNum.value = 1
  fetchSsoList()
}
function handleSsoReset(): void {
  ssoSearch.ssoName = ''
  ssoSearch.type = ''
  ssoSearch.enabled = ''
  ssoPageNum.value = 1
  fetchSsoList()
}
function handleSsoPageChange(): void {
  fetchSsoList()
}
function handleSsoSizeChange(): void {
  ssoPageNum.value = 1
  fetchSsoList()
}

// ==================== SSO增/改 ====================
function resetSsoForm(): void {
  Object.assign(ssoForm, {
    ssoName: '',
    type: '',
    idpUrl: '',
    spEntityId: '',
    ssoLoginUrl: '',
    ssoLogoutUrl: '',
    certificate: '',
    enabled: true
  })
  editingSsoId.value = null
  ssoInitialValues = null
}

function handleSsoAdd(): void {
  ssoFormMode.value = 'add'
  resetSsoForm()
  ssoDialogVisible.value = true
}

function handleSsoEdit(row: SsoConfigItem): void {
  ssoFormMode.value = 'edit'
  editingSsoId.value = row.id ?? null
  ssoInitialValues = { ...row }
  Object.assign(ssoForm, {
    ssoName: row.ssoName,
    type: row.type,
    idpUrl: row.idpUrl,
    spEntityId: row.spEntityId,
    ssoLoginUrl: row.ssoLoginUrl,
    ssoLogoutUrl: row.ssoLogoutUrl || '',
    certificate: row.certificate || '',
    enabled: row.enabled,
    version: row.version
  })
  ssoDialogVisible.value = true
}

const ssoFieldLabels: Record<string, string> = {
  ssoName: '配置名称',
  type: '认证类型',
  idpUrl: 'IDP地址',
  spEntityId: 'SP实体ID',
  ssoLoginUrl: 'SSO登录URL',
  ssoLogoutUrl: 'SSO登出URL',
  certificate: '证书',
  enabled: '启用状态'
}

function getSsoChanges(): string[] {
  if (!ssoInitialValues) return []
  const changes: string[] = []
  for (const key of Object.keys(ssoFieldLabels)) {
    const oldVal = (ssoInitialValues as any)[key]
    const newVal = (ssoForm as any)[key]
    if (oldVal !== newVal) {
      changes.push(ssoFieldLabels[key] || key)
    }
  }
  return changes
}

async function handleSsoSubmit(): Promise<void> {
  if (!ssoFormRef.value) return
  try {
    await ssoFormRef.value.validate()
  } catch {
    return
  }

  if (ssoFormMode.value === 'edit' && ssoInitialValues) {
    const changes = getSsoChanges()
    if (changes.length > 0) {
      try {
        await ElMessageBox.confirm(
          `<div>修改了以下字段：</div><ul>${changes.map((c) => `<li>${c}</li>`).join('')}</ul><div style="margin-top:8px">确认保存？</div>`,
          '变更确认',
          {
            confirmButtonText: '确认保存',
            cancelButtonText: '取消',
            dangerouslyUseHTMLString: true,
            type: 'warning'
          }
        )
      } catch {
        return
      }
    }
  }

  ssoSubmitting.value = true
  try {
    if (ssoFormMode.value === 'add') {
      await createSsoConfigApi(ssoForm)
      ElMessage.success('SSO配置已新增')
    } else {
      const payload = { ...ssoForm, version: ssoInitialValues?.version }
      await updateSsoConfigApi(editingSsoId.value!, payload)
      ElMessage.success('SSO配置已修改')
    }
    ssoDialogVisible.value = false
    ElMessage.info('配置已保存，新配置将在下次用户登录时生效')
    await fetchSsoList()
  } catch (e: any) {
    const errMsg = e?.message || ''
    if (errMsg && errMsg !== 'success') {
      ElMessage.error(errMsg)
    } else {
      ElMessage.error(ssoFormMode.value === 'add' ? '新增失败' : '修改失败')
    }
  } finally {
    ssoSubmitting.value = false
  }
}

async function handleSsoDelete(row: SsoConfigItem): Promise<void> {
  try {
    await deleteSsoConfigApi(row.id!)
    ElMessage.success('SSO配置已删除')
    await fetchSsoList()
  } catch {
    ElMessage.error('删除失败')
  }
}

// ==================== OAuth2列表 ====================
function buildOauth2QueryParams() {
  const params: Record<string, unknown> = {
    pageNum: oauth2PageNum.value,
    pageSize: oauth2PageSize.value
  }
  if (oauth2Search.supplierName) params.supplierName = oauth2Search.supplierName
  if (oauth2Search.type) params.type = oauth2Search.type
  if (oauth2Search.enabled !== '') params.enabled = oauth2Search.enabled === 'true'
  return params
}

async function fetchOauth2List(): Promise<void> {
  oauth2Loading.value = true
  try {
    const result = await getOauth2ConfigPageApi(buildOauth2QueryParams() as any)
    oauth2List.value = result.records ?? []
    oauth2Total.value = result.total ?? 0
  } catch {
    oauth2List.value = []
    oauth2Total.value = 0
  } finally {
    oauth2Loading.value = false
  }
}

function handleOauth2Search(): void {
  oauth2PageNum.value = 1
  fetchOauth2List()
}
function handleOauth2Reset(): void {
  oauth2Search.supplierName = ''
  oauth2Search.type = ''
  oauth2Search.enabled = ''
  oauth2PageNum.value = 1
  fetchOauth2List()
}
function handleOauth2PageChange(): void {
  fetchOauth2List()
}
function handleOauth2SizeChange(): void {
  oauth2PageNum.value = 1
  fetchOauth2List()
}

// ==================== OAuth2增/改 ====================
const oauth2FieldLabels: Record<string, string> = {
  supplierName: '供应商名称',
  type: '认证类型',
  clientId: 'Client ID',
  clientSecret: 'Client Secret',
  authUrl: '授权URL',
  tokenUrl: 'Token URL',
  userInfoUrl: '用户信息URL',
  scope: 'Scope',
  enabled: '启用状态'
}

function getOauth2Changes(): string[] {
  if (!oauth2InitialValues) return []
  const changes: string[] = []
  for (const key of Object.keys(oauth2FieldLabels)) {
    const oldVal = (oauth2InitialValues as any)[key]
    const newVal = (oauth2Form as any)[key]
    if (oldVal !== newVal) {
      if (key === 'clientSecret') {
        changes.push('密钥已更新')
      } else {
        changes.push(oauth2FieldLabels[key] || key)
      }
    }
  }
  return changes
}

function resetOauth2Form(): void {
  Object.assign(oauth2Form, {
    supplierName: '',
    type: '',
    clientId: '',
    clientSecret: '',
    authUrl: '',
    tokenUrl: '',
    userInfoUrl: '',
    scope: '',
    enabled: true
  })
  editingOauth2Id.value = null
  oauth2TestResult.value = null
  oauth2InitialValues = null
}

function handleOauth2Add(): void {
  oauth2FormMode.value = 'add'
  resetOauth2Form()
  oauth2DialogVisible.value = true
}

function handleOauth2Edit(row: Oauth2ConfigItem): void {
  oauth2FormMode.value = 'edit'
  editingOauth2Id.value = row.id ?? null
  oauth2TestResult.value = null
  oauth2InitialValues = { ...row }
  Object.assign(oauth2Form, {
    supplierName: row.supplierName,
    type: row.type,
    clientId: row.clientId,
    clientSecret: row.clientSecret || '',
    authUrl: row.authUrl,
    tokenUrl: row.tokenUrl,
    userInfoUrl: row.userInfoUrl || '',
    scope: row.scope || '',
    enabled: row.enabled,
    version: row.version
  })
  oauth2DialogVisible.value = true
}

async function handleOauth2Submit(): Promise<void> {
  if (!oauth2FormRef.value) return
  try {
    await oauth2FormRef.value.validate()
  } catch {
    return
  }

  if (oauth2FormMode.value === 'edit' && oauth2InitialValues) {
    const changes = getOauth2Changes()
    if (changes.length > 0) {
      try {
        await ElMessageBox.confirm(
          `<div>修改了以下字段：</div><ul>${changes.map((c) => `<li>${c}</li>`).join('')}</ul><div style="margin-top:8px">确认保存？</div>`,
          '变更确认',
          {
            confirmButtonText: '确认保存',
            cancelButtonText: '取消',
            dangerouslyUseHTMLString: true,
            type: 'warning'
          }
        )
      } catch {
        return
      }
    }
  }

  oauth2Submitting.value = true
  try {
    if (oauth2FormMode.value === 'add') {
      await createOauth2ConfigApi(oauth2Form)
      ElMessage.success('OAuth2配置已新增')
    } else {
      const payload = { ...oauth2Form, version: oauth2InitialValues?.version }
      await updateOauth2ConfigApi(editingOauth2Id.value!, payload)
      ElMessage.success('OAuth2配置已修改')
    }
    oauth2DialogVisible.value = false
    oauth2TestResult.value = null
    if (oauth2Form.enabled !== oauth2InitialValues?.enabled) {
      ElMessage.warning('启用状态已变更，此操作将立即影响登录页面的认证方式显示')
    }
    ElMessage.info('配置已保存，新配置将在下次用户登录时生效')
    await fetchOauth2List()
  } catch (e: any) {
    const errMsg = e?.message || ''
    if (errMsg && errMsg !== 'success') {
      ElMessage.error(errMsg)
    } else {
      ElMessage.error(oauth2FormMode.value === 'add' ? '新增失败' : '修改失败')
    }
  } finally {
    oauth2Submitting.value = false
  }
}

async function handleOauth2Delete(row: Oauth2ConfigItem): Promise<void> {
  try {
    await deleteOauth2ConfigApi(row.id!)
    ElMessage.success('OAuth2配置已删除')
    await fetchOauth2List()
  } catch {
    ElMessage.error('删除失败')
  }
}

// ==================== OAuth2连接测试 ====================
async function handleTestConnection(row: Oauth2ConfigItem): Promise<void> {
  testingId.value = row.id ?? null
  try {
    const result = await testOauth2ConnectionApi(row.id!)
    ElMessage.success(result.message || '连接成功')
  } catch (e: any) {
    ElMessage.error(e?.message || '连接测试失败')
  } finally {
    testingId.value = null
  }
}

async function handleOauth2TestInDialog(): Promise<void> {
  if (!editingOauth2Id.value) return
  oauth2Testing.value = true
  oauth2TestResult.value = null
  try {
    const result = await testOauth2ConnectionApi(editingOauth2Id.value)
    oauth2TestResult.value = result
    if (result.success) {
      ElMessage.success('连接成功：授权URL可访问')
    } else {
      ElMessage.error(result.message || '连接测试失败')
    }
  } catch (e: any) {
    const errMsg = e?.message || '连接测试失败'
    ElMessage.error(errMsg)
    oauth2TestResult.value = {
      success: false,
      statusCode: 0,
      responseTime: 0,
      message: errMsg,
      testUrl: oauth2Form.authUrl || ''
    }
  } finally {
    oauth2Testing.value = false
  }
}

onMounted(() => {
  fetchSsoList()
})
</script>

<style scoped lang="scss">
.sso-oauth2-config-page {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0 0 8px;
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .page-desc {
      margin: 0;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    flex-wrap: wrap;
    gap: 12px;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
    }

    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .pagination-wrapper {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  .footer-left {
    flex: 0 0 auto;
  }

  .footer-right {
    display: flex;
    gap: 8px;
    flex: 0 0 auto;
  }
}

.test-result-area {
  margin-top: 8px;

  .test-result-detail {
    p {
      margin: 4px 0;
      font-size: 13px;
      line-height: 1.6;

      .label {
        font-weight: 500;
        color: var(--el-text-color-secondary);
        margin-right: 4px;
      }
    }
  }
}
</style>
