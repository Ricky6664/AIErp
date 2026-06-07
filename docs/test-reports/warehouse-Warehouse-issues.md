# 仓库定义列表页 - 问题清单与修复方案

> 任务编号: P0-010-002-001-001-002
> 验证日期: 2026-06-08
> 验证人: W5
> 严重级别: P0-阻断 / P1-重要 / P2-建议

## 问题列表

### 问题 1: 后端Controller缺失 [P0-阻断]

- 严重级别: P0
- 描述: 前端页面调用5个仓库API接口(GET/POST/PUT/DELETE /api/warehouse/warehouse/*)，但后端不存在WarehouseController类。后端Service层(WarehouseServiceImpl)已实现，但Controller层缺失。
- 影响: 所有API调用将返回404，页面无法加载数据，CRUD操作全部失败。
- 根因: L4(Controller)任务未包含在P0-010模块的任务规划中，或Controller创建任务被遗漏。
- 修复方案: 创建WarehouseController.java，实现以下端点:
  - GET `/api/warehouse/warehouse` -> warehouseService.page(queryDTO, page)
  - GET `/api/warehouse/warehouse/{id}` -> warehouseService.getById(id)
  - POST `/api/warehouse/warehouse` -> warehouseService.save(dto)
  - PUT `/api/warehouse/warehouse/{id}` -> warehouseService.updateById(dto)
  - DELETE `/api/warehouse/warehouse/{id}` -> warehouseService.removeById(id, 关联校验)
- 修复文件: src/main/java/com/erp/module/warehouse/controller/WarehouseController.java (待创建)
- 修复人: 待认领

### 问题 2: 侧边栏菜单缺失仓库定义入口 [P1-重要]

- 严重级别: P1
- 描述: 侧边栏菜单配置(menuConfig.ts)中没有仓库定义页面条目。用户无法通过侧边栏导航到 `/warehouse/warehouse` 页面。
- 影响: 用户只能通过直接输入URL访问页面。
- 修复方案: 在menuConfig.ts中添加仓库管理菜单组，包含仓库定义子菜单项。
  ```typescript
  {
    title: '仓库管理',
    icon: 'Box',
    children: [
      { title: '仓库定义', path: '/warehouse/warehouse' }
    ]
  }
  ```
- 修复文件: erp-ai-web/src/layouts/components/Sidebar/menuConfig.ts
- 修复人: 待认领

### 问题 3: vite-plugin-compression 依赖缺失 [P2-建议]

- 严重级别: P2
- 描述: `pnpm build` 或 `npx vite build` 时报错 `Cannot find package 'vite-plugin-compression'`。该包在vite.config.ts中被引用但未安装。
- 影响: 前端生产构建失败，但不影响开发服务器(vite dev)和类型检查(vue-tsc)。
- 修复方案: 安装依赖 `pnpm add -D vite-plugin-compression` 或从vite.config.ts中移除该插件引用。
- 修复文件: package.json 或 vite.config.ts
- 备注: 此问题非本任务引入，属于项目基础设施问题。

## 验证通过的项

所有41项代码审查验证均通过：
- 前端代码结构完整、类型正确
- 路由注册正确
- 表单校验完备
- 异常处理覆盖所有API调用
- 边界场景(空数据/连续提交/未知类型)均有容错处理
- 删除操作有二次确认(el-popconfirm)
- 防抖搜索(300ms)减少API调用
- 提交按钮loading状态防止重复提交

## 总结

| 严重级别 | 数量 | 说明 |
|----------|:---:|------|
| P0-阻断 | 1 | Controller缺失导致API无法调用 |
| P1-重要 | 1 | 菜单缺失导致用户无法导航 |
| P2-建议 | 1 | 构建依赖缺失(非本任务引入) |

前端代码本身质量良好，问题集中在后端接口依赖和菜单配置上。
