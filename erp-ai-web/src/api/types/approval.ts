/** 审批定义查询参数 */
export interface DefinitionQueryDTO {
  definitionName?: string
  businessType?: string
  enableFlag?: boolean
  pageNum?: number
  pageSize?: number
}

/** 审批定义创建参数 */
export interface DefinitionCreateDTO {
  definitionName: string
  definitionCode: string
  businessType: string
  flowConfig?: string
  enableFlag?: boolean
  nodes?: NodeCreateDTO[]
}

/** 审批节点创建参数 */
export interface NodeCreateDTO {
  nodeName: string
  nodeOrder: number
  nodeType: string
  approverType?: string
  approverIds?: number[]
  conditionExpression?: string
  parallelMode?: string
  timeoutHours?: number
}

/** 审批定义更新参数 */
export interface DefinitionUpdateDTO extends Partial<DefinitionCreateDTO> {
  id: number
}

/** 审批定义列表项 */
export interface DefinitionListVO {
  id: number
  definitionName: string
  definitionCode: string
  businessType: string
  enableFlag: boolean
  createTime: string
  updateTime: string
}

/** 审批节点VO */
export interface NodeVO {
  id: number
  nodeName: string
  nodeOrder: number
  nodeType: string
  approverType?: string
  approverIds?: number[]
  conditionExpression?: string
  parallelMode?: string
  timeoutHours?: number
}

/** 审批定义详情 */
export interface DefinitionDetailVO extends DefinitionListVO {
  flowConfig: string
  version: number
  nodes: NodeVO[]
}

/** 审批实例查询参数 */
export interface InstanceQueryDTO {
  businessType?: string
  status?: string
  applicantId?: number
  pageNum?: number
  pageSize?: number
}

/** 审批实例创建参数 */
export interface InstanceCreateDTO {
  definitionId: number
  businessType: string
  businessId: number
}

/** 审批实例列表项 */
export interface InstanceVO {
  id: number
  definitionId: number
  definitionName: string
  businessType: string
  businessId: number
  applicantId: number
  applicantName: string
  currentNodeId: number
  currentNodeName: string
  status: string
  createTime: string
}

/** 我的审批查询参数 */
export interface MyApprovalQueryDTO {
  /** 标签: pending(待审) / reviewed(已审) / submitted(我的申请) */
  tab?: string
  pageNum?: number
  pageSize?: number
}

/** 审批统计VO */
export interface ApprovalStatisticsVO {
  totalInstances: number
  pendingCount: number
  approvedCount: number
  rejectedCount: number
  withdrawnCount: number
  myPendingCount: number
  myReviewedCount: number
  mySubmittedCount: number
  statusDistribution: Record<string, number>
  definitionCounts: Record<string, number>
}

/** 我的审批列表项 */
export interface MyApprovalVO {
  instanceId: number
  definitionId: number
  definitionName: string
  businessType: string
  businessId: number
  applicantId: number
  applicantName: string
  currentNodeName: string
  status: string
  myAction: string
  myComment: string
  myOperateTime: string
  createTime: string
}
