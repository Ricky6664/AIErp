const status = {
  audit: {
    pending: 'Pending Audit',
    approved: 'Approved',
    rejected: 'Rejected'
  },
  enable: {
    enabled: 'Enabled',
    disabled: 'Disabled'
  },
  order: {
    draft: 'Draft',
    submitted: 'Submitted',
    confirmed: 'Confirmed',
    completed: 'Completed',
    cancelled: 'Cancelled'
  },
  payment: {
    unpaid: 'Unpaid',
    paid: 'Paid',
    refunded: 'Refunded'
  }
}

export default status

export type StatusLocale = typeof status
