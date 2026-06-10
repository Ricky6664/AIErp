import type { Ref } from 'vue'
import type { TimeRange } from '@/api/modules/warehouse-workbench'

export const WORKBENCH_CONTEXT_KEY = Symbol('warehouseWorkbench')

export interface WorkbenchContext {
  timeRange: Readonly<Ref<TimeRange>>
  refresh: () => Promise<void>
}
