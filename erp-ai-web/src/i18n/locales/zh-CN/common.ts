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
  },
  // 财务工作台
  finance: {
    workbench: {
      title: '财务基础设置工作台',
      desc: '概览财务基础设置核心数据指标',
      kpiTitle: '数据概览',
      currencyRateCount: '币种汇率',
      bankAccountCount: '银行账户',
      accountCount: '会计科目',
      voucherWordCount: '凭证字',
      chartTitle: '数据分析',
      trendTitle: '月创建趋势',
      distTitle: '科目类型分布'
    },
    voucherword: {
      title: '凭证字管理',
      wordCode: '凭证字编码',
      wordName: '凭证字名称',
      sortOrder: '排序号',
      status: '状态',
      addTitle: '新增凭证字',
      editTitle: '编辑凭证字',
      confirmDelete: '确认删除该凭证字？'
    }
  },
  // 组织架构工作台
  org: {
    workbench: {
      title: '组织架构工作台',
      desc: '概览组织架构核心数据指标',
      kpiTitle: '数据概览',
      companyCount: '公司数量',
      departmentCount: '部门数量',
      positionCount: '岗位数量',
      employeeCount: '在职人数',
      chartTitle: '数据分析',
      deptTypeDist: '部门类型分布',
      companyDeptCompare: '各公司部门数量对比',
      quickActions: '快捷操作',
      addCompany: '新增公司',
      addDepartment: '新增部门',
      addPosition: '新增岗位',
      orgChart: '组织架构图',
      loadFailed: '数据加载失败',
      retry: '重试',
      recentRecords: '最近新增记录',
      recentCompany: '最近新增公司',
      recentDept: '最近新增部门',
      recentPosition: '最近新增岗位'
    },
    company: {
      addTitle: '新增公司',
      editTitle: '编辑公司',
      companyName: '公司名称',
      companyShortName: '公司简称',
      creditCode: '统一社会信用代码',
      creditCodePlaceholder: '请输入18位统一社会信用代码',
      legalPerson: '法定代表人',
      registeredCapital: '注册资本',
      address: '公司地址',
      phone: '联系电话',
      phonePlaceholder: '请输入联系电话（座机或手机）',
      registeredCapitalUnit: '万元',
      creditCodeInvalid: '请输入有效的统一社会信用代码',
      creditCodeDuplicate: '统一社会信用代码已存在',
      companyNameRequired: '公司名称不能为空',
      companyNameLength: '公司名称长度须在2-100个字符之间'
    }
  }
}

export default common

export type CommonLocale = typeof common
