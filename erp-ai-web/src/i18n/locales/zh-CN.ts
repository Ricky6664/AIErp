import common from './zh-CN/common'

export default {
  common,
  status: {
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
  },
  validation: {
    required: '{field}不能为空',
    maxLength: '{field}长度不能超过{max}个字符',
    minLength: '{field}长度不能少于{min}个字符',
    email: '请输入有效的邮箱地址',
    phone: '请输入有效的手机号码',
    url: '请输入有效的URL地址',
    number: '请输入有效的数字',
    integer: '请输入有效的整数',
    positive: '请输入正数',
    max: '{field}不能大于{max}',
    min: '{field}不能小于{min}',
    duplicate: '{field}已存在',
    formatError: '{field}格式不正确'
  }
}
