import request from '@/utils/request'
import type { WorkbenchData } from '@/api/types/workbench'

export function getWorkbenchDataApi(): Promise<WorkbenchData> {
  return request.get('/api/system/user/workbench')
}
