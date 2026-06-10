# MsgCollaborationService 测试报告

> **任务编号**: P1-003-001-010-001-002
> **测试日期**: 2026-06-09
> **测试人**: W10
> **测试框架**: JUnit5 + Mockito
> **测试类**: MsgCollaborationServiceTest

---

## 测试执行摘要

| 指标 | 数值 |
|------|------|
| 测试总数 | 23 |
| 通过 | 23 |
| 失败 | 0 |
| 错误 | 0 |
| 跳过 | 0 |

---

## 测试用例详情

### 1. create - 创建讨论主题

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldCreateDiscussionSuccessfully | 正常流程-新增，传入完整DTO，返回新建ID | ✅ |
| shouldAddCreatorToParticipants | 创建人自动加入参与人列表 | ✅ |
| shouldThrowWhenParticipantsEmpty | 边界-空值，参与人列表为空时抛出BusinessException | ✅ |
| shouldThrowWhenParticipantsNull | 边界-空值，参与人列表为null时抛出BusinessException | ✅ |
| shouldDefaultCategoryToGeneral | 默认category，不传category时默认为general | ✅ |

### 2. page - 分页查询讨论列表

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldReturnPagedResults | 正常流程-查询，分页查询返回正确数据 | ✅ |
| shouldReturnEmptyWhenNoRecords | 查询无记录，返回空列表 | ✅ |

### 3. getById - 查询讨论详情

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldReturnDetailWithReplies | 正常流程-查询，返回详情含回复树和参与人 | ✅ |
| shouldThrowWhenNotFound | 异常-不存在，查询不存在的ID抛出BusinessException | ✅ |

### 4. reply - 回复讨论

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldReplySuccessfully | 正常流程-回复，参与人回复成功，通知其他参与人 | ✅ |
| shouldThrowWhenDiscussionNotFound | 异常-不存在，回复不存在的讨论抛出BusinessException | ✅ |
| shouldThrowWhenDiscussionClosed | 异常-已关闭，回复已关闭的讨论抛出BusinessException | ✅ |
| shouldThrowWhenNotParticipant | 异常-权限，非参与人回复抛出BusinessException | ✅ |
| shouldSetParentReplyId | 楼中楼回复，parentReplyId正确传递给Entity | ✅ |
| shouldDeduplicateNotifications | @提及去重，同一用户既是参与人又被@时只通知一次 | ✅ |

### 5. closeDiscussion - 关闭讨论

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldCloseByCreator | 正常流程-修改，创建人关闭讨论成功 | ✅ |
| shouldCloseByAdmin | 正常流程-修改，管理员关闭他人创建的讨论 | ✅ |
| shouldThrowWhenNotFound | 异常-不存在，关闭不存在的讨论抛出BusinessException | ✅ |
| shouldThrowWhenNotAuthorized | 异常-权限，非创建人也非管理员关闭时抛出BusinessException | ✅ |

### 6. reopenDiscussion - 重开讨论

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldReopenByCreator | 正常流程-修改，创建人重开讨论成功 | ✅ |
| shouldReopenByAdmin | 正常流程-修改，管理员重开他人创建的讨论 | ✅ |
| shouldThrowWhenNotFound | 异常-不存在，重开不存在的讨论抛出BusinessException | ✅ |
| shouldThrowWhenNotAuthorized | 异常-权限，非创建人也非管理员重开时抛出BusinessException | ✅ |

---

## 验证清单对照（任务文档 Section 5.1）

| 序号 | 验证项 | 对应测试 | 结果 |
|:---:|--------|---------|:--:|
| 1 | 正常流程-新增 | shouldCreateDiscussionSuccessfully | ✅ |
| 2 | 正常流程-查询 | shouldReturnPagedResults / shouldReturnDetailWithReplies | ✅ |
| 3 | 正常流程-修改 | shouldCloseByCreator / shouldReopenByCreator | ✅ |
| 4 | 正常流程-删除 | 协作讨论无删除API | ⏭️ N/A |
| 5 | 边界-空值 | shouldThrowWhenParticipantsEmpty / shouldThrowWhenParticipantsNull | ✅ |
| 6 | 边界-超长 | Controller层@Valid校验，Service层不重复校验 | ⏭️ N/A |
| 7 | 异常-并发 | 乐观锁由BaseEntity.version字段+MyBatis-Plus @Version自动处理 | ⏭️ 框架层 |
| 8 | 异常-不存在 | shouldThrowWhenNotFound (getById/close/reopen/reply) | ✅ |

---

## 父任务验收标准对照（P1-003-001-010-001-001 Section 7）

| 序号 | 检查项 | 对应测试 | 结果 |
|:---:|--------|---------|:--:|
| 1 | 创建讨论正确保存主题并将创建人加入参与人列表 | shouldAddCreatorToParticipants | ✅ |
| 2 | 参与人非空校验：无参与人时抛出BusinessException | shouldThrowWhenParticipantsEmpty | ✅ |
| 3 | 回复时正确校验参与人权限，非参与人无法回复 | shouldThrowWhenNotParticipant | ✅ |
| 4 | 讨论已关闭时回复抛出"讨论已关闭"异常 | shouldThrowWhenDiscussionClosed | ✅ |
| 5 | 楼中楼回复（parentReplyId）正确构建树形结构 | shouldSetParentReplyId | ✅ |
| 6 | 新回复通知推送给所有参与人（排除回复人自己） | shouldReplySuccessfully | ✅ |
| 7 | 关闭/重开讨论仅创建人或管理员可操作 | shouldCloseByCreator/shouldThrowWhenNotAuthorized(close+reopen) | ✅ |

---

## 发现的问题

无。23个测试用例全部通过。

## 遗留风险

- 并发场景（乐观锁冲突）由MyBatis-Plus @Version在数据库层面处理，单元测试无法覆盖，需在集成测试中验证。
- WebSocket推送和消息通知的实际投递由MsgWebSocketService和IMsgMessageService负责，本测试仅验证调用次数和参数正确性。
