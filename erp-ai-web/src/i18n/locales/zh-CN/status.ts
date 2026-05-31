const status = {
  audit: {
    pending: '待审核',
    approved: '已审核',
    rejected: '已驳回'
  },
  enable: {
    enabled: '启用',
    disabled: '禁用'
  },
  order: {
    draft: '草稿',
    submitted: '已提交',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  },
  payment: {
    unpaid: '未付款',
    paid: '已付款',
    refunded: '已退款'
  }
}

export default status

export type StatusLocale = typeof status
