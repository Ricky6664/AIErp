import request from '@/utils/request'
import type { AuthConfigWorkbenchData } from '@/api/types/authConfig'

export interface WorkbenchQueryParams {
  tenantId?: number
  startTime?: string
  endTime?: string
}

export function getAuthConfigWorkbenchApi(
  params?: WorkbenchQueryParams
): Promise<AuthConfigWorkbenchData> {
  return request.get('/api/system/auth-config/workbench', { params })
}
