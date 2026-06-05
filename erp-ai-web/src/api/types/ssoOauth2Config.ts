/** SSO配置项 */
export interface SsoConfigItem {
  id?: number
  ssoName: string
  type: string
  idpUrl: string
  spEntityId: string
  ssoLoginUrl: string
  ssoLogoutUrl: string
  certificate: string
  enabled: boolean
  version?: number
  createTime?: string
  updateTime?: string
}

/** OAuth2配置项 */
export interface Oauth2ConfigItem {
  id?: number
  supplierName: string
  type: string
  clientId: string
  clientSecret: string
  authUrl: string
  tokenUrl: string
  userInfoUrl: string
  scope: string
  enabled: boolean
  version?: number
  createTime?: string
  updateTime?: string
}

/** SSO配置分页查询参数 */
export interface SsoConfigQuery {
  pageNum: number
  pageSize: number
  ssoName?: string
  type?: string
  enabled?: boolean
}

/** OAuth2配置分页查询参数 */
export interface Oauth2ConfigQuery {
  pageNum: number
  pageSize: number
  supplierName?: string
  type?: string
  enabled?: boolean
}

/** 分页结果 */
export interface ConfigPageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

/** OAuth2连接测试结果 */
export interface Oauth2TestResult {
  success: boolean
  statusCode: number
  responseTime: number
  message: string
  testUrl: string
}
