import type { RouteRecordRaw } from 'vue-router'
import { REDIRECT_ROUTE } from './redirect'

// 登录页 - 无需鉴权，独立布局
export const LOGIN_ROUTE: RouteRecordRaw = {
  path: '/login',
  name: 'Login',
  component: () => import('@/views/login/index.vue'),
  meta: { title: '登录', titleI18n: 'login.title', hideMenu: true, hideTab: true }
}

// 根路由 - 重定向到首页
export const ROOT_ROUTE: RouteRecordRaw = {
  path: '/',
  name: 'Root',
  redirect: '/home',
  meta: { title: '根路径', hideMenu: true, hideTab: true }
}

// 首页 - 使用AdminLayout布局
export const HOME_ROUTE: RouteRecordRaw = {
  path: '/home',
  name: 'Home',
  component: () => import('@/layouts/AppLayout.vue'),
  children: [
    {
      path: '',
      name: 'HomePage',
      component: () => import('@/views/home/index.vue'),
      meta: {
        title: '首页',
        titleI18n: 'home.title',
        icon: 'HomeFilled',
        affix: true,
        keepAlive: true
      }
    }
  ]
}

// 404页面
export const ERROR_404: RouteRecordRaw = {
  path: '/404',
  name: 'Error404',
  component: () => import('@/views/error/404.vue'),
  meta: { title: '404', hideMenu: true, hideTab: true }
}

// 403页面
export const ERROR_403: RouteRecordRaw = {
  path: '/403',
  name: 'Error403',
  component: () => import('@/views/error/403.vue'),
  meta: { title: '403', hideMenu: true, hideTab: true }
}

// 无权限页面
export const NO_PERMISSION: RouteRecordRaw = {
  path: '/no-permission',
  name: 'NoPermission',
  component: () => import('@/views/error/no-permission.vue'),
  meta: { title: '无权限', hideMenu: true, hideTab: true }
}

// 开发调试页面
export const DEV_VIRTUAL_SCROLL: RouteRecordRaw = {
  path: '/dev/virtual-scroll',
  name: 'DevVirtualScroll',
  component: () => import('@/views/dev/virtual-scroll-demo.vue'),
  meta: { title: '虚拟滚动验证', hideMenu: false, hideTab: false }
}

// 用户管理工作台
export const USER_WORKBENCH: RouteRecordRaw = {
  path: '/user/workbench',
  name: 'UserWorkbench',
  component: () => import('@/views/user/workbench/index.vue'),
  meta: { title: '用户管理工作台', icon: 'DataBoard', keepAlive: true }
}

// 权限配置工作台
export const AUTH_CONFIG_WORKBENCH: RouteRecordRaw = {
  path: '/auth/config/workbench',
  name: 'AuthConfigWorkbench',
  component: () => import('@/views/auth/config/workbench/index.vue'),
  meta: { title: '权限配置工作台', icon: 'DataBoard', keepAlive: true }
}

// 登录日志页
export const LOGIN_LOG_PAGE: RouteRecordRaw = {
  path: '/auth/config/login-log',
  name: 'LoginLogList',
  component: () => import('@/views/auth/config/login-log/index.vue'),
  meta: { title: '登录日志', icon: 'Document', keepAlive: true }
}

// 在线设备管理页
export const ONLINE_DEVICE_PAGE: RouteRecordRaw = {
  path: '/auth/config/online-device',
  name: 'OnlineDeviceList',
  component: () => import('@/views/auth/config/online-device/index.vue'),
  meta: { title: '在线设备管理', icon: 'Monitor', keepAlive: true }
}

// SSO/OAuth2配置管理页
export const SSO_OAUTH2_CONFIG_PAGE: RouteRecordRaw = {
  path: '/auth/config/sso-oauth2',
  name: 'SsoOauth2Config',
  component: () => import('@/views/auth/config/sso-oauth2/index.vue'),
  meta: { title: 'SSO/OAuth2配置', icon: 'Setting', keepAlive: true }
}

// 修改密码页 - 密码过期强制跳转
export const CHANGE_PASSWORD_ROUTE: RouteRecordRaw = {
  path: '/change-password',
  name: 'ChangePassword',
  component: () => import('@/views/login/ChangePassword.vue'),
  meta: { title: '修改密码', hideMenu: true, hideTab: true }
}

// 列表表格基础标配功能演示页
export const DEMO_LIST_TABLE: RouteRecordRaw = {
  path: '/demo/list-table',
  name: 'DemoListTable',
  component: () => import('@/views/demo/list-table/index.vue'),
  meta: { title: '列表表格演示', icon: 'List', keepAlive: true }
}

// 录入数据表格合计列演示页
export const DEMO_EDIT_TABLE: RouteRecordRaw = {
  path: '/demo/edit-table',
  name: 'DemoEditTable',
  component: () => import('@/views/demo/edit-table/index.vue'),
  meta: { title: '录入表格演示', icon: 'Edit', keepAlive: true }
}

// 录入数据表格只读/禁用态演示页
export const DEMO_EDIT_TABLE_READONLY: RouteRecordRaw = {
  path: '/demo/edit-table/readonly',
  name: 'DemoEditTableReadonly',
  component: () => import('@/views/demo/edit-table/readonly-demo.vue'),
  meta: { title: '只读/禁用态演示', icon: 'View', keepAlive: true }
}

// 财务基础设置工作台
export const FINANCE_WORKBENCH: RouteRecordRaw = {
  path: '/finance/workbench',
  name: 'FinanceWorkbench',
  component: () => import('@/views/finance/financeworkbench/index.vue'),
  meta: { title: '财务工作台', icon: 'DataBoard', keepAlive: true }
}

// 币种汇率列表页
export const FINANCE_CURRENCYRATE: RouteRecordRaw = {
  path: '/finance/currencyrate',
  name: 'FinanceCurrencyrate',
  component: () => import('@/views/finance/currencyrate/index.vue'),
  meta: { title: '币种汇率', icon: 'Money', keepAlive: true }
}

// 银行账户列表页
export const FINANCE_BANKACCOUNT: RouteRecordRaw = {
  path: '/finance/bankaccount',
  name: 'FinanceBankaccount',
  component: () => import('@/views/finance/bankaccount/index.vue'),
  meta: { title: '银行账户', icon: 'CreditCard', keepAlive: true }
}

// 会计科目列表页
export const FINANCE_ACCOUNT: RouteRecordRaw = {
  path: '/finance/account',
  name: 'FinanceAccount',
  component: () => import('@/views/finance/account/index.vue'),
  meta: { title: '会计科目', icon: 'List', keepAlive: true }
}

// 组织架构工作台
export const ORG_WORKBENCH: RouteRecordRaw = {
  path: '/org/workbench',
  name: 'OrgWorkbench',
  component: () => import('@/views/org/OrgWorkbench.vue'),
  meta: { title: '组织架构工作台', icon: 'DataBoard', keepAlive: true }
}

// 凭证字列表页
export const FINANCE_VOUCHERWORD: RouteRecordRaw = {
  path: '/finance/voucherword',
  name: 'FinanceVoucherword',
  component: () => import('@/views/finance/voucherword/index.vue'),
  meta: { title: '凭证字管理', icon: 'Document', keepAlive: true }
}

// 仓库定义列表页
export const WAREHOUSE_LIST: RouteRecordRaw = {
  path: '/warehouse/warehouse',
  name: 'WarehouseList',
  component: () => import('@/views/warehouse/warehouse/index.vue'),
  meta: { title: '仓库定义', icon: 'Box', keepAlive: true }
}

// 缓存管理页
export const SYSTEM_CACHE: RouteRecordRaw = {
  path: '/system/cache',
  name: 'SystemCache',
  component: () => import('@/views/system/cache/index.vue'),
  meta: { title: '缓存管理', icon: 'Monitor', keepAlive: true }
}

// 库位管理列表页
export const WAREHOUSE_LOCATION: RouteRecordRaw = {
  path: '/warehouse/location',
  name: 'WarehouseLocation',
  component: () => import('@/views/warehouse/location/index.vue'),
  meta: { title: '库位管理', icon: 'Location', keepAlive: true }
}

// HRM工作台
export const HRM_WORKBENCH: RouteRecordRaw = {
  path: '/hrm/workbench',
  name: 'HrmWorkbench',
  component: () => import('@/views/hrm/hrmworkbench/index.vue'),
  meta: { title: 'HRM工作台', icon: 'DataAnalysis', keepAlive: true }
}

// 员工档案列表页
export const HRM_EMPLOYEEARCHIVE: RouteRecordRaw = {
  path: '/hrm/employeearchive',
  name: 'HrmEmployeearchive',
  component: () => import('@/views/hrm/employeearchive/index.vue'),
  meta: { title: '员工档案', icon: 'Document', keepAlive: true }
}

// 员工中心主从列表页
export const HRM_EMPLOYEECENTER: RouteRecordRaw = {
  path: '/hrm/employeecenter',
  name: 'HrmEmployeecenter',
  component: () => import('@/views/hrm/employeecenter/index.vue'),
  meta: { title: '员工中心', icon: 'User', keepAlive: true }
}

// 招聘管理列表页
export const HRM_RECRUITMENT: RouteRecordRaw = {
  path: '/hrm/recruitment',
  name: 'HrmRecruitment',
  component: () => import('@/views/hrm/recruitment/index.vue'),
  meta: { title: '招聘管理', icon: 'UserFilled', keepAlive: true }
}

// 考勤管理列表页
export const HRM_ATTENDANCE: RouteRecordRaw = {
  path: '/hrm/attendance',
  name: 'HrmAttendance',
  component: () => import('@/views/hrm/attendance/index.vue'),
  meta: { title: '考勤管理', icon: 'Calendar', keepAlive: true }
}

// 薪资管理主从列表页
export const HRM_SALARY: RouteRecordRaw = {
  path: '/hrm/salary',
  name: 'HrmSalary',
  component: () => import('@/views/hrm/salary/index.vue'),
  meta: { title: '薪资管理', icon: 'Money', keepAlive: true }
}

// 公告管理页
export const SYSTEM_ANNOUNCEMENT: RouteRecordRaw = {
  path: '/system/announcement',
  name: 'SystemAnnouncement',
  component: () => import('@/views/system/announcement/index.vue'),
  meta: { title: '公告管理', icon: 'Bell', keepAlive: true }
}

// 审批定义管理页
export const APPROVAL_DEFINITION: RouteRecordRaw = {
  path: '/approval/definition',
  name: 'ApprovalDefinition',
  component: () => import('@/views/approval/preset/index.vue'),
  meta: { title: '审批定义', icon: 'DocumentChecked', keepAlive: true }
}

// 静态路由集合 - 导出供router/index.ts使用
export const staticRoutes: RouteRecordRaw[] = [
  LOGIN_ROUTE,
  ROOT_ROUTE,
  HOME_ROUTE,
  DEV_VIRTUAL_SCROLL,
  DEMO_LIST_TABLE,
  DEMO_EDIT_TABLE,
  DEMO_EDIT_TABLE_READONLY,
  USER_WORKBENCH,
  AUTH_CONFIG_WORKBENCH,
  FINANCE_WORKBENCH,
  FINANCE_CURRENCYRATE,
  FINANCE_BANKACCOUNT,
  FINANCE_ACCOUNT,
  FINANCE_VOUCHERWORD,
  ORG_WORKBENCH,
  WAREHOUSE_LIST,
  WAREHOUSE_LOCATION,
  SYSTEM_CACHE,
  SYSTEM_ANNOUNCEMENT,
  APPROVAL_DEFINITION,
  LOGIN_LOG_PAGE,
  ONLINE_DEVICE_PAGE,
  SSO_OAUTH2_CONFIG_PAGE,
  HRM_WORKBENCH,
  HRM_EMPLOYEECENTER,
  HRM_EMPLOYEEARCHIVE,
  HRM_RECRUITMENT,
  HRM_ATTENDANCE,
  HRM_SALARY,
  CHANGE_PASSWORD_ROUTE,
  REDIRECT_ROUTE,
  ERROR_404,
  ERROR_403,
  NO_PERMISSION
]
