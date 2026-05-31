const status = {
  audit: {
    pending: 'Pending',
    approved: 'Approved',
    rejected: 'Rejected'
  },
  enabled: 'Enabled',
  disabled: 'Disabled',
  active: 'Active',
  inactive: 'Inactive',
  locked: 'Locked',
  deleted: 'Deleted',
  pending: 'Pending',
  processing: 'Processing',
  completed: 'Completed',
  failed: 'Failed',
  online: 'Online',
  offline: 'Offline'
}

export default status

export type StatusLocale = typeof status
