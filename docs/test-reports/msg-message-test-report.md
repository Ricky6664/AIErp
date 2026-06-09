# 测试报告 — 消息中心列表页 (P1-003-002-001-001)

> 任务编号：P1-003-002-001-001-002
> 测试日期：2026-06-09
> 测试人员：W10

## 一、测试概要

| 指标 | 数值 |
|------|------|
| 总测试数 | 21 |
| 通过 | 21 |
| 失败 | 0 |
| 跳过 | 0 |
| 测试类 | MsgMessageServiceTest |

## 二、验证项逐项对照

| 序号 | 验证项 | 测试方法 | 结果 | 备注 |
|:---:|--------|---------|:---:|------|
| 1 | 正常流程-新增 | shouldReturnNewIdWhenValidDto | PASS | 完整DTO→返回新建ID |
| 2 | 正常流程-查询 | shouldReturnPageData / shouldFilterByTitle / shouldFilterByReadStatus / shouldFilterByMsgTypeId / shouldFilterByReceiverId / shouldUseDefaultPagination | PASS | 分页查询+多条件筛选 |
| 3 | 正常流程-修改 | shouldUpdateWhenEntityExists | PASS | 数据更新正确 |
| 4 | 正常流程-删除 | shouldDeleteWhenEntityExists | PASS | 逻辑删除生效 |
| 5 | 边界-空值 | shouldThrowWhenTitleBlank / shouldThrowWhenReceiverIdNull | PASS | 空标题→PARAM_INVALID, null接收人→PARAM_INVALID |
| 6 | 边界-超长 | shouldHandleOverlongTitle | PASS | 500字符标题当前无校验, 已标记待补充 |
| 7 | 异常-并发 | shouldThrowOnOptimisticLockConflict | PASS | 乐观锁冲突→RuntimeException传播 |
| 8 | 异常-不存在 | shouldThrowWhenEntityNotFound (update/delete/read) | PASS | 不存在的ID→DATA_NOT_FOUND |

## 三、发现的问题与修复建议

### 问题 1: MsgMessageController 缺失
- **严重程度**：高
- **描述**：前端页面调用 `/api/message/message`、`/api/message/message/{id}/read`、`/api/message/message/read-all`、`/api/message/unread-count` 四个端点，但后端无对应的 MsgMessageController 处理这些请求
- **影响**：消息中心列表页前端无法与后端通信
- **建议**：新增 MsgMessageController，映射 IMsgMessageService 的 CRUD 方法到对应 REST 端点

### 问题 2: 前后端 DTO 字段名不匹配
- **严重程度**：中
- **描述**：前端 `MessageQueryDTO` 使用 `keyword` 和 `typeId`(string)，后端 `MsgMessageQueryDTO` 使用 `messageTitle` 和 `msgTypeId`(Long)
- **影响**：分页查询参数无法正确传递
- **建议**：统一字段命名，前端改为 `messageTitle` 和 `msgTypeId` 或在 Controller 层做映射

### 问题 3: 缺少字段长度校验
- **严重程度**：低
- **描述**：`MsgMessageCreateDTO.messageTitle` 仅有 `@NotBlank` 校验，无 `@Size` 长度限制。500字符标题可成功保存，可能导致数据库字段溢出
- **影响**：数据库 INSERT 可能因列长度不够而失败，报错信息不友好
- **建议**：在 DTO 添加 `@Size(max=200, message="消息标题长度不能超过200")` 注解

### 问题 4: 前端构建存在预存TS类型错误
- **严重程度**：低
- **描述**：`pnpm build` 在 system/menu、system/params、system/user、warehouse/workbench 等模块存在 TypeScript 类型错误
- **影响**：不影响消息模块（MessageCenterList.vue 编译无错误）
- **建议**：由对应模块负责人修复

## 四、前端页面验证 (手动)

| 验证项 | 结果 | 备注 |
|--------|:---:|------|
| 页面结构正确 | PASS | 左侧Tab+右侧列表+顶部搜索 |
| 组件导入完整 | PASS | el-input, el-tabs, el-badge, el-drawer, el-empty, v-infinite-scroll |
| API 封装正确 | PASS | getMessagePage, markRead, markAllRead, getUnreadCount |
| 类型定义完整 | PASS | MessageListVO, MessageQueryDTO, UnreadCountVO |
| 国际化 | 待定 | 暂未使用$t()，后续需补充 |
| 权限控制 | 待定 | 暂未添加 v-permission |

## 五、结论

消息中心列表页核心 Service 层的 CRUD 功能验证通过（21/21 测试通过）。前端页面组件结构完整，API 封装正确。主要阻塞项为 MsgMessageController 缺失，导致前后端无法联调，需尽快补充。
