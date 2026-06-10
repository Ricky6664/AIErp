import type { RouteRecordRaw } from 'vue-router'
import { REDIRECT_ROUTE } from './redirect'

// ============================================================
// 独立路由（不需要 AppLayout 包裹）
// ============================================================

// 登录页
export const LOGIN_ROUTE: RouteRecordRaw = {
  path: '/login',
  name: 'Login',
  component: () => import('@/views/login/index.vue'),
  meta: { title: '登录', titleI18n: 'login.title', hideMenu: true, hideTab: true }
}

// 404 / 403 / 无权限 页面
export const ERROR_404: RouteRecordRaw = {
  path: '/404',
  name: 'Error404',
  component: () => import('@/views/error/404.vue'),
  meta: { title: '404', hideMenu: true, hideTab: true }
}

export const ERROR_403: RouteRecordRaw = {
  path: '/403',
  name: 'Error403',
  component: () => import('@/views/error/403.vue'),
  meta: { title: '403', hideMenu: true, hideTab: true }
}

export const NO_PERMISSION: RouteRecordRaw = {
  path: '/no-permission',
  name: 'NoPermission',
  component: () => import('@/views/error/no-permission.vue'),
  meta: { title: '无权限', hideMenu: true, hideTab: true }
}

// 修改密码（密码过期强制跳转）
export const CHANGE_PASSWORD_ROUTE: RouteRecordRaw = {
  path: '/change-password',
  name: 'ChangePassword',
  component: () => import('@/views/login/ChangePassword.vue'),
  meta: { title: '修改密码', hideMenu: true, hideTab: true }
}

// ============================================================
// 首页 Layout 子路由
// ============================================================

export const HOME_PAGE: RouteRecordRaw = {
  path: 'home',
  name: 'HomePage',
  component: () => import('@/views/home/index.vue'),
  meta: { title: '首页', titleI18n: 'home.title', icon: 'HomeFilled', affix: true, keepAlive: true }
}

// ============================================================
// 业务页面路由（全部作为 AppLayout 的子路由）
//   path 以 '/' 开头表示绝对路径，不以 '/' 开头则拼接父路径
//   父路由 path='/' + 子路由 path='warehouse/warehouse' = /warehouse/warehouse
// ============================================================

export const DEV_VIRTUAL_SCROLL: RouteRecordRaw = {
  path: 'dev/virtual-scroll',
  name: 'DevVirtualScroll',
  component: () => import('@/views/dev/virtual-scroll-demo.vue'),
  meta: { title: '虚拟滚动验证', hideMenu: false, hideTab: false }
}

export const DEMO_LIST_TABLE: RouteRecordRaw = {
  path: 'demo/list-table',
  name: 'DemoListTable',
  component: () => import('@/views/demo/list-table/index.vue'),
  meta: { title: '列表表格演示', icon: 'List', keepAlive: true }
}

export const DEMO_EDIT_TABLE: RouteRecordRaw = {
  path: 'demo/edit-table',
  name: 'DemoEditTable',
  component: () => import('@/views/demo/edit-table/index.vue'),
  meta: { title: '录入表格演示', icon: 'Edit', keepAlive: true }
}

export const DEMO_EDIT_TABLE_READONLY: RouteRecordRaw = {
  path: 'demo/edit-table/readonly',
  name: 'DemoEditTableReadonly',
  component: () => import('@/views/demo/edit-table/readonly-demo.vue'),
  meta: { title: '只读/禁用态演示', icon: 'View', keepAlive: true }
}

// ============================================================
// P01-P15 页面类型演示（标准示范页面）
// ============================================================

export const DEMO_P01_DASHBOARD: RouteRecordRaw = {
  path: 'demo/pages/p01',
  name: 'DemoP01Dashboard',
  component: () => import('@/views/demo/pages/P01DashboardDemo.vue'),
  meta: { title: 'P01 首页仪表盘', icon: 'DataBoard', keepAlive: true }
}

export const DEMO_P02_WORKBENCH: RouteRecordRaw = {
  path: 'demo/pages/p02',
  name: 'DemoP02Workbench',
  component: () => import('@/views/demo/pages/P02WorkbenchDemo.vue'),
  meta: { title: 'P02 工作台', icon: 'DataBoard', keepAlive: true }
}

export const DEMO_P03_MASTER_LIST: RouteRecordRaw = {
  path: 'demo/pages/p03',
  name: 'DemoP03MasterList',
  component: () => import('@/views/demo/pages/P03MasterListDemo.vue'),
  meta: { title: 'P03 主从列表页', icon: 'List', keepAlive: true }
}

export const DEMO_P04_SIMPLE_LIST: RouteRecordRaw = {
  path: 'demo/pages/p04',
  name: 'DemoP04SimpleList',
  component: () => import('@/views/demo/pages/P04SimpleListDemo.vue'),
  meta: { title: 'P04 单一列表页', icon: 'List', keepAlive: true }
}

export const DEMO_P05_TREE_LIST: RouteRecordRaw = {
  path: 'demo/pages/p05',
  name: 'DemoP05TreeList',
  component: () => import('@/views/demo/pages/P05TreeListDemo.vue'),
  meta: { title: 'P05 树形列表页', icon: 'Grid', keepAlive: true }
}

export const DEMO_P06_MASTER_FORM: RouteRecordRaw = {
  path: 'demo/pages/p06',
  name: 'DemoP06MasterForm',
  component: () => import('@/views/demo/pages/P06MasterFormDemo.vue'),
  meta: { title: 'P06 主从表单页', icon: 'Edit', keepAlive: true }
}

export const DEMO_P07_SIMPLE_FORM: RouteRecordRaw = {
  path: 'demo/pages/p07',
  name: 'DemoP07SimpleForm',
  component: () => import('@/views/demo/pages/P07SimpleFormDemo.vue'),
  meta: { title: 'P07 单一表单页', icon: 'Edit', keepAlive: true }
}

export const DEMO_P08_KANBAN: RouteRecordRaw = {
  path: 'demo/pages/p08',
  name: 'DemoP08Kanban',
  component: () => import('@/views/demo/pages/P08KanbanDemo.vue'),
  meta: { title: 'P08 看板页', icon: 'Grid', keepAlive: true }
}

export const DEMO_P09_QUERY: RouteRecordRaw = {
  path: 'demo/pages/p09',
  name: 'DemoP09Query',
  component: () => import('@/views/demo/pages/P09QueryDemo.vue'),
  meta: { title: 'P09 查询页', icon: 'Search', keepAlive: true }
}

export const DEMO_P10_REPORT: RouteRecordRaw = {
  path: 'demo/pages/p10',
  name: 'DemoP10Report',
  component: () => import('@/views/demo/pages/P10ReportDemo.vue'),
  meta: { title: 'P10 报表页', icon: 'Document', keepAlive: true }
}

export const DEMO_P11_SCREEN: RouteRecordRaw = {
  path: 'demo/pages/p11',
  name: 'DemoP11Screen',
  component: () => import('@/views/demo/pages/P11ScreenDemo.vue'),
  meta: { title: 'P11 大屏页', icon: 'Monitor', keepAlive: true }
}

export const DEMO_P12_PROFILE: RouteRecordRaw = {
  path: 'demo/pages/p12',
  name: 'DemoP12Profile',
  component: () => import('@/views/demo/pages/P12ProfileDemo.vue'),
  meta: { title: 'P12 画像页', icon: 'User', keepAlive: true }
}

export const DEMO_P13_CONFIG: RouteRecordRaw = {
  path: 'demo/pages/p13',
  name: 'DemoP13Config',
  component: () => import('@/views/demo/pages/P13ConfigDemo.vue'),
  meta: { title: 'P13 配置页', icon: 'Setting', keepAlive: true }
}

export const DEMO_P14_AI_DIALOG: RouteRecordRaw = {
  path: 'demo/pages/p14',
  name: 'DemoP14AIDialog',
  component: () => import('@/views/demo/pages/P14AIDialogDemo.vue'),
  meta: { title: 'P14 AI对话页', icon: 'ChatDotRound', keepAlive: true }
}

export const DEMO_P15_DESIGNER: RouteRecordRaw = {
  path: 'demo/pages/p15',
  name: 'DemoP15Designer',
  component: () => import('@/views/demo/pages/P15DesignerDemo.vue'),
  meta: { title: 'P15 设计器页（旧）', icon: 'Brush', keepAlive: true }
}

// P15 设计器拆分为 3 个独立页面
export const DEMO_P15_PRINT_DESIGNER: RouteRecordRaw = {
  path: 'demo/pages/p15-print',
  name: 'DemoP15PrintDesigner',
  component: () => import('@/views/demo/pages/P15PrintDesignerDemo.vue'),
  meta: { title: 'P15 打印模板设计器', icon: 'Printer', keepAlive: true }
}

export const DEMO_P15_REPORT_DESIGNER: RouteRecordRaw = {
  path: 'demo/pages/p15-report',
  name: 'DemoP15ReportDesigner',
  component: () => import('@/views/demo/pages/P15ReportDesignerDemo.vue'),
  meta: { title: 'P15 数据报表设计器', icon: 'DataAnalysis', keepAlive: true }
}

export const DEMO_P15_APPROVAL_DESIGNER: RouteRecordRaw = {
  path: 'demo/pages/p15-approval',
  name: 'DemoP15ApprovalDesigner',
  component: () => import('@/views/demo/pages/P15ApprovalDesignerDemo.vue'),
  meta: { title: 'P15 审批流程设计器', icon: 'Stamp', keepAlive: true }
}

// 组件演示（独立页面）
export const DEMO_BASIC_INPUTS: RouteRecordRaw = {
  path: 'demo/components/basic-inputs',
  name: 'DemoBasicInputs',
  component: () => import('@/views/demo/components/BasicInputsDemo.vue'),
  meta: { title: '§8.1 基础录入组件', icon: 'Edit', keepAlive: true }
}

export const DEMO_DICT_COMPONENTS: RouteRecordRaw = {
  path: 'demo/components/dict',
  name: 'DemoDictComponents',
  component: () => import('@/views/demo/components/DictComponentsDemo.vue'),
  meta: { title: '§8.2 字典下拉组件', icon: 'Menu', keepAlive: true }
}

export const DEMO_LIST_TABLE_FEATURES: RouteRecordRaw = {
  path: 'demo/components/list-table',
  name: 'DemoListTableFeatures',
  component: () => import('@/views/demo/components/ListTableFeaturesDemo.vue'),
  meta: { title: '§6.2 列表表格功能', icon: 'List', keepAlive: true }
}

export const DEMO_EDIT_TABLE_FEATURES: RouteRecordRaw = {
  path: 'demo/components/edit-table',
  name: 'DemoEditTableFeatures',
  component: () => import('@/views/demo/components/EditTableFeaturesDemo.vue'),
  meta: { title: '§6.3 录入表格功能', icon: 'Edit', keepAlive: true }
}

// 用户管理
export const USER_WORKBENCH: RouteRecordRaw = {
  path: 'user/workbench',
  name: 'UserWorkbench',
  component: () => import('@/views/user/workbench/index.vue'),
  meta: { title: '用户管理工作台', icon: 'DataBoard', keepAlive: true }
}

// 权限配置
export const AUTH_CONFIG_WORKBENCH: RouteRecordRaw = {
  path: 'auth/config/workbench',
  name: 'AuthConfigWorkbench',
  component: () => import('@/views/auth/config/workbench/index.vue'),
  meta: { title: '权限配置工作台', icon: 'DataBoard', keepAlive: true }
}

export const LOGIN_LOG_PAGE: RouteRecordRaw = {
  path: 'auth/config/login-log',
  name: 'LoginLogList',
  component: () => import('@/views/auth/config/login-log/index.vue'),
  meta: { title: '登录日志', icon: 'Document', keepAlive: true }
}

export const ONLINE_DEVICE_PAGE: RouteRecordRaw = {
  path: 'auth/config/online-device',
  name: 'OnlineDeviceList',
  component: () => import('@/views/auth/config/online-device/index.vue'),
  meta: { title: '在线设备管理', icon: 'Monitor', keepAlive: true }
}

export const SSO_OAUTH2_CONFIG_PAGE: RouteRecordRaw = {
  path: 'auth/config/sso-oauth2',
  name: 'SsoOauth2Config',
  component: () => import('@/views/auth/config/sso-oauth2/index.vue'),
  meta: { title: 'SSO/OAuth2配置', icon: 'Setting', keepAlive: true }
}

// 财务
export const FINANCE_WORKBENCH: RouteRecordRaw = {
  path: 'finance/workbench',
  name: 'FinanceWorkbench',
  component: () => import('@/views/finance/financeworkbench/index.vue'),
  meta: { title: '财务工作台', icon: 'DataBoard', keepAlive: true }
}

export const FINANCE_CURRENCYRATE: RouteRecordRaw = {
  path: 'finance/currencyrate',
  name: 'FinanceCurrencyrate',
  component: () => import('@/views/finance/currencyrate/index.vue'),
  meta: { title: '币种汇率', icon: 'Money', keepAlive: true }
}

export const FINANCE_BANKACCOUNT: RouteRecordRaw = {
  path: 'finance/bankaccount',
  name: 'FinanceBankaccount',
  component: () => import('@/views/finance/bankaccount/index.vue'),
  meta: { title: '银行账户', icon: 'CreditCard', keepAlive: true }
}

export const FINANCE_ACCOUNT: RouteRecordRaw = {
  path: 'finance/account',
  name: 'FinanceAccount',
  component: () => import('@/views/finance/account/index.vue'),
  meta: { title: '会计科目', icon: 'List', keepAlive: true }
}

export const FINANCE_VOUCHERWORD: RouteRecordRaw = {
  path: 'finance/voucherword',
  name: 'FinanceVoucherword',
  component: () => import('@/views/finance/voucherword/index.vue'),
  meta: { title: '凭证字管理', icon: 'Document', keepAlive: true }
}

// 组织架构
export const ORG_WORKBENCH: RouteRecordRaw = {
  path: 'org/workbench',
  name: 'OrgWorkbench',
  component: () => import('@/views/org/OrgWorkbench.vue'),
  meta: { title: '组织架构工作台', icon: 'DataBoard', keepAlive: true }
}

// 仓库
export const WAREHOUSE_WORKBENCH: RouteRecordRaw = {
  path: 'warehouse/workbench',
  name: 'WarehouseWorkbench',
  component: () => import('@/views/warehouse/workbench/index.vue'),
  meta: { title: '仓库工作台', icon: 'DataBoard', keepAlive: true }
}

export const WAREHOUSE_LIST: RouteRecordRaw = {
  path: 'warehouse/warehouse',
  name: 'WarehouseList',
  component: () => import('@/views/warehouse/warehouse/index.vue'),
  meta: { title: '仓库定义', icon: 'Box', keepAlive: true }
}

export const WAREHOUSE_LOCATION: RouteRecordRaw = {
  path: 'warehouse/location',
  name: 'WarehouseLocation',
  component: () => import('@/views/warehouse/location/index.vue'),
  meta: { title: '库位管理', icon: 'Location', keepAlive: true }
}

// 系统管理
export const SYSTEM_USER: RouteRecordRaw = {
  path: 'system/user',
  name: 'SystemUser',
  component: () => import('@/views/system/user/index.vue'),
  meta: { title: '用户管理', icon: 'User', keepAlive: true }
}

export const SYSTEM_ROLE: RouteRecordRaw = {
  path: 'system/role',
  name: 'SystemRole',
  component: () => import('@/views/system/role/RoleList.vue'),
  meta: { title: '角色管理', icon: 'UserFilled', keepAlive: true }
}

export const SYSTEM_MENU: RouteRecordRaw = {
  path: 'system/menu',
  name: 'SystemMenu',
  component: () => import('@/views/system/menu/index.vue'),
  meta: { title: '菜单管理', icon: 'Menu', keepAlive: true }
}

export const SYSTEM_PARAMS: RouteRecordRaw = {
  path: 'system/params',
  name: 'SystemParams',
  component: () => import('@/views/system/params/index.vue'),
  meta: { title: '系统参数', icon: 'Setting', keepAlive: true }
}

export const SYSTEM_CACHE: RouteRecordRaw = {
  path: 'system/cache',
  name: 'SystemCache',
  component: () => import('@/views/system/cache/index.vue'),
  meta: { title: '缓存管理', icon: 'Monitor', keepAlive: true }
}

export const SYSTEM_ANNOUNCEMENT: RouteRecordRaw = {
  path: 'system/announcement',
  name: 'SystemAnnouncement',
  component: () => import('@/views/system/announcement/index.vue'),
  meta: { title: '公告管理', icon: 'Bell', keepAlive: true }
}

// HRM
export const HRM_WORKBENCH: RouteRecordRaw = {
  path: 'hrm/workbench',
  name: 'HrmWorkbench',
  component: () => import('@/views/hrm/hrmworkbench/index.vue'),
  meta: { title: 'HRM工作台', icon: 'DataAnalysis', keepAlive: true }
}

export const HRM_EMPLOYEECENTER: RouteRecordRaw = {
  path: 'hrm/employeecenter',
  name: 'HrmEmployeecenter',
  component: () => import('@/views/hrm/employeecenter/index.vue'),
  meta: { title: '员工中心', icon: 'User', keepAlive: true }
}

export const HRM_EMPLOYEEARCHIVE: RouteRecordRaw = {
  path: 'hrm/employeearchive',
  name: 'HrmEmployeearchive',
  component: () => import('@/views/hrm/employeearchive/index.vue'),
  meta: { title: '员工档案', icon: 'Document', keepAlive: true }
}

export const HRM_RECRUITMENT: RouteRecordRaw = {
  path: 'hrm/recruitment',
  name: 'HrmRecruitment',
  component: () => import('@/views/hrm/recruitment/index.vue'),
  meta: { title: '招聘管理', icon: 'UserFilled', keepAlive: true }
}

export const HRM_ATTENDANCE: RouteRecordRaw = {
  path: 'hrm/attendance',
  name: 'HrmAttendance',
  component: () => import('@/views/hrm/attendance/index.vue'),
  meta: { title: '考勤管理', icon: 'Calendar', keepAlive: true }
}

export const HRM_SALARY: RouteRecordRaw = {
  path: 'hrm/salary',
  name: 'HrmSalary',
  component: () => import('@/views/hrm/salary/index.vue'),
  meta: { title: '薪资管理', icon: 'Money', keepAlive: true }
}

// 审批
export const APPROVAL_WORKBENCH: RouteRecordRaw = {
  path: 'approval/workbench',
  name: 'ApprovalWorkbench',
  component: () => import('@/views/approval/workbench/index.vue'),
  meta: { title: '审批工作台', icon: 'DataBoard', keepAlive: true }
}

export const APPROVAL_DEFINITION: RouteRecordRaw = {
  path: 'approval/definition',
  name: 'ApprovalDefinition',
  component: () => import('@/views/approval/preset/index.vue'),
  meta: { title: '审批定义', icon: 'DocumentChecked', keepAlive: true }
}

export const APPROVAL_INSTANCE: RouteRecordRaw = {
  path: 'approval/instance',
  name: 'ApprovalInstance',
  component: () => import('@/views/approval/instance/index.vue'),
  meta: { title: '审批实例', icon: 'List', keepAlive: true }
}

export const APPROVAL_MY: RouteRecordRaw = {
  path: 'approval/my',
  name: 'ApprovalMy',
  component: () => import('@/views/approval/my/index.vue'),
  meta: { title: '我的审批', icon: 'Checked', keepAlive: true }
}

export const APPROVAL_STATISTICS: RouteRecordRaw = {
  path: 'approval/statistics',
  name: 'ApprovalStatistics',
  component: () => import('@/views/approval/statistics/index.vue'),
  meta: { title: '审批统计', icon: 'PieChart', keepAlive: true }
}

export const APPROVAL_LOG: RouteRecordRaw = {
  path: 'approval/log',
  name: 'ApprovalLog',
  component: () => import('@/views/approval/log/index.vue'),
  meta: { title: '审批日志', icon: 'Tickets', keepAlive: true }
}

// 消息
export const MSG_WORKBENCH: RouteRecordRaw = {
  path: 'msg/workbench',
  name: 'MsgWorkbench',
  component: () => import('@/views/msg/workbench/index.vue'),
  meta: { title: '消息工作台', icon: 'DataAnalysis', keepAlive: true }
}

export const MSG_MESSAGE_CENTER: RouteRecordRaw = {
  path: 'msg/message',
  name: 'MsgMessageCenter',
  component: () => import('@/views/msg/MessageCenterList.vue'),
  meta: { title: '消息中心', icon: 'Bell', keepAlive: true }
}

export const MSG_TEMPLATE: RouteRecordRaw = {
  path: 'msg/template',
  name: 'MsgTemplate',
  component: () => import('@/views/msg/MessageTemplateList.vue'),
  meta: { title: '消息模板', icon: 'Document', keepAlive: true }
}

export const MSG_TYPE: RouteRecordRaw = {
  path: 'msg/type',
  name: 'MsgType',
  component: () => import('@/views/msg/MessageTypeList.vue'),
  meta: { title: '消息类型', icon: 'Grid', keepAlive: true }
}

export const MSG_TODO: RouteRecordRaw = {
  path: 'msg/todo',
  name: 'MsgTodo',
  component: () => import('@/views/msg/TodoList.vue'),
  meta: { title: '单据待办', icon: 'List', keepAlive: true }
}

export const MSG_WARNING_DASHBOARD: RouteRecordRaw = {
  path: 'msg/warning-dashboard',
  name: 'MsgWarningDashboard',
  component: () => import('@/views/msg/WarningDashboard.vue'),
  meta: { title: '业务预警看板', icon: 'Warning', keepAlive: true }
}

// ============================================================
// 布局路由 — 所有业务页面通过此路由共享 AppLayout（侧边栏+导航栏+标签栏）
// ============================================================

const ALL_CHILDREN: RouteRecordRaw[] = [
  // 首页重定向
  { path: '', redirect: '/home' },
  // 首页
  HOME_PAGE,
  // 开发+演示
  DEV_VIRTUAL_SCROLL,
  DEMO_LIST_TABLE,
  DEMO_EDIT_TABLE,
  DEMO_EDIT_TABLE_READONLY,
  // P01-P15 页面类型演示
  DEMO_P01_DASHBOARD,
  DEMO_P02_WORKBENCH,
  DEMO_P03_MASTER_LIST,
  DEMO_P04_SIMPLE_LIST,
  DEMO_P05_TREE_LIST,
  DEMO_P06_MASTER_FORM,
  DEMO_P07_SIMPLE_FORM,
  DEMO_P08_KANBAN,
  DEMO_P09_QUERY,
  DEMO_P10_REPORT,
  DEMO_P11_SCREEN,
  DEMO_P12_PROFILE,
  DEMO_P13_CONFIG,
  DEMO_P14_AI_DIALOG,
  DEMO_P15_DESIGNER,
  DEMO_P15_PRINT_DESIGNER,
  DEMO_P15_REPORT_DESIGNER,
  DEMO_P15_APPROVAL_DESIGNER,
  // 组件演示（独立页面）
  DEMO_BASIC_INPUTS,
  DEMO_DICT_COMPONENTS,
  DEMO_LIST_TABLE_FEATURES,
  DEMO_EDIT_TABLE_FEATURES,
  // 用户+权限
  USER_WORKBENCH,
  AUTH_CONFIG_WORKBENCH,
  LOGIN_LOG_PAGE,
  ONLINE_DEVICE_PAGE,
  SSO_OAUTH2_CONFIG_PAGE,
  // 财务
  FINANCE_WORKBENCH,
  FINANCE_CURRENCYRATE,
  FINANCE_BANKACCOUNT,
  FINANCE_ACCOUNT,
  FINANCE_VOUCHERWORD,
  // 组织
  ORG_WORKBENCH,
  // 仓库
  WAREHOUSE_WORKBENCH,
  WAREHOUSE_LIST,
  WAREHOUSE_LOCATION,
  // 系统
  SYSTEM_USER,
  SYSTEM_ROLE,
  SYSTEM_MENU,
  SYSTEM_PARAMS,
  SYSTEM_CACHE,
  SYSTEM_ANNOUNCEMENT,
  // HRM
  HRM_WORKBENCH,
  HRM_EMPLOYEECENTER,
  HRM_EMPLOYEEARCHIVE,
  HRM_RECRUITMENT,
  HRM_ATTENDANCE,
  HRM_SALARY,
  // 审批
  APPROVAL_WORKBENCH,
  APPROVAL_DEFINITION,
  APPROVAL_INSTANCE,
  APPROVAL_MY,
  APPROVAL_STATISTICS,
  APPROVAL_LOG,
  // 消息
  MSG_WORKBENCH,
  MSG_MESSAGE_CENTER,
  MSG_TEMPLATE,
  MSG_TYPE,
  MSG_TODO,
  MSG_WARNING_DASHBOARD
]

/**
 * 主布局路由 — 所有业务页面通过此路由共享 AppLayout。
 * path='/' + children[{ path: 'warehouse/warehouse' }] → 最终路径 /warehouse/warehouse
 * 路由守卫在首次导航时将后端菜单树生成的动态路由通过 router.addRoute('Layout', ...) 追加到此处。
 */
export const LAYOUT_ROUTE: RouteRecordRaw = {
  path: '/',
  name: 'Layout',
  component: () => import('@/layouts/AppLayout.vue'),
  children: ALL_CHILDREN
}

// ============================================================
// 静态路由集合
// ============================================================

export const staticRoutes: RouteRecordRaw[] = [
  LOGIN_ROUTE,
  LAYOUT_ROUTE,
  ERROR_404,
  ERROR_403,
  NO_PERMISSION,
  CHANGE_PASSWORD_ROUTE,
  REDIRECT_ROUTE
]

// 用于路由守卫的白名单和首页判断
export { REDIRECT_ROUTE }
