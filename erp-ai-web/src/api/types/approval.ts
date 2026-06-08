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
