const common = {
  // 操作类 - 基础按钮文本
  confirm: '确认',
  cancel: '取消',
  save: '保存',
  delete: '删除',
  edit: '编辑',
  add: '新增',
  search: '搜索',
  reset: '重置',
  submit: '提交',
  back: '返回',
  close: '关闭',
  refresh: '刷新',
  // 操作类 - 批量与详情
  batchDelete: '批量删除',
  detail: '详情',
  copy: '复制',
  copied: '已复制',
  more: '更多',
  // 确认与提示
  ok: '确定',
  tip: '提示',
  warning: '警告',
  info: '信息',
  loading: '加载中...',
  confirmDelete: '确认删除选中数据？',
  saveSuccess: '保存成功',
  deleteSuccess: '删除成功',
  operateSuccess: '操作成功',
  operateFailed: '操作失败',
  // 状态类
  yes: '是',
  no: '否',
  enable: '启用',
  disable: '禁用',
  success: '成功',
  failed: '失败',
  error: '失败',
  // 标签类
  export: '导出',
  import: '导入',
  upload: '上传',
  download: '下载',
  preview: '预览',
  // 表头与表单类
  operate: '操作',
  serialNo: '序号',
  status: '状态',
  createTime: '创建时间',
  updateTime: '更新时间',
  remark: '备注',
  desc: '描述',
  sort: '排序',
  // 占位与提示类
  noData: '暂无数据',
  noResult: '暂无结果',
  total: '共 {total} 条',
  pleaseSelect: '请选择',
  pleaseInput: '请输入',
  // 登录页
  login: {
    title: 'ERP AI 管理系统',
    desc: '智能企业资源管理系统',
    username: '用户名',
    password: '密码',
    captcha: '验证码',
    rememberMe: '记住我',
    submit: '登 录',
    captchaTip: '点击图片刷新验证码',
    loginSuccess: '登录成功',
    loginFailed: '登录失败',
    welcomeBack: '欢迎回来'
  }
}

export default common

export type CommonLocale = typeof common
