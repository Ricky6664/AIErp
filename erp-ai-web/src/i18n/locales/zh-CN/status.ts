const status = {
  audit: {
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回'
  },
  enabled: '已启用',
  disabled: '已禁用',
  active: '活跃',
  inactive: '未激活',
  locked: '已锁定',
  deleted: '已删除',
  pending: '待处理',
  processing: '处理中',
  completed: '已完成',
  failed: '失败',
  online: '在线',
  offline: '离线'
}

export default status

export type StatusLocale = typeof status
