import request from '@/utils/request'
import type {
  SsoConfigItem,
  Oauth2ConfigItem,
  SsoConfigQuery,
  Oauth2ConfigQuery,
  ConfigPageResult
} from '@/api/types/ssoOauth2Config'

// ========== SSO配置 API ==========

/** 分页查询SSO配置列表 */
export function getSsoConfigPageApi(
  params: SsoConfigQuery
): Promise<ConfigPageResult<SsoConfigItem>> {
  return request.get('/api/system/sso-config/page', { params })
}

/** 查询SSO配置详情 */
export function getSsoConfigDetailApi(id: number): Promise<SsoConfigItem> {
  return request.get(`/api/system/sso-config/${id}`)
}

/** 新增SSO配置 */
export function createSsoConfigApi(data: SsoConfigItem): Promise<SsoConfigItem> {
  return request.post('/api/system/sso-config', data)
}

/** 修改SSO配置 */
export function updateSsoConfigApi(id: number, data: SsoConfigItem): Promise<SsoConfigItem> {
  return request.put(`/api/system/sso-config/${id}`, data)
}

/** 删除SSO配置 */
export function deleteSsoConfigApi(id: number): Promise<boolean> {
  return request.delete(`/api/system/sso-config/${id}`)
}

// ========== OAuth2配置 API ==========

/** 分页查询OAuth2配置列表 */
export function getOauth2ConfigPageApi(
  params: Oauth2ConfigQuery
): Promise<ConfigPageResult<Oauth2ConfigItem>> {
  return request.get('/api/system/oauth2-config/page', { params })
}

/** 查询OAuth2配置详情 */
export function getOauth2ConfigDetailApi(id: number): Promise<Oauth2ConfigItem> {
  return request.get(`/api/system/oauth2-config/${id}`)
}

/** 新增OAuth2配置 */
export function createOauth2ConfigApi(data: Oauth2ConfigItem): Promise<Oauth2ConfigItem> {
  return request.post('/api/system/oauth2-config', data)
}

/** 修改OAuth2配置 */
export function updateOauth2ConfigApi(
  id: number,
  data: Oauth2ConfigItem
): Promise<Oauth2ConfigItem> {
  return request.put(`/api/system/oauth2-config/${id}`, data)
}

/** 删除OAuth2配置 */
export function deleteOauth2ConfigApi(id: number): Promise<boolean> {
  return request.delete(`/api/system/oauth2-config/${id}`)
}

/** 测试OAuth2连接 */
export function testOauth2ConnectionApi(id: number): Promise<string> {
  return request.post(`/api/system/oauth2-config/${id}/test`)
}
