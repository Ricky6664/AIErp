# MsgDiscussionService 测试报告

> **任务编号**: P1-003-001-009-001-002
> **测试日期**: 2026-06-09
> **测试人**: W10
> **测试框架**: JUnit5 + Mockito
> **测试类**: MsgDiscussionServiceTest

---

## 测试执行摘要

| 指标 | 数值 |
|------|------|
| 测试总数 | 13 |
| 通过 | 13 |
| 失败 | 0 |
| 错误 | 0 |
| 跳过 | 0 |

---

## 测试用例详情

### 1. pageByDoc - 按单据分页查询沟通记录

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldReturnPagedResults | 正常流程-查询，传入完整DTO，返回正确分页数据 | ✅ |
| shouldReturnEmptyWhenNoRecords | 查询无记录，返回空列表 | ✅ |
| shouldUseDefaultPageParamsWhenNull | pageNum/pageSize为null，使用默认值1和20 | ✅ |

### 2. create - 新增留言/回复

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldCreateSimpleComment | 正常流程-新增，返回新建ID，数据库记录正确 | ✅ |
| shouldCreateCommentWithMentions | @提及用户，解析@提及，发送消息通知+WebSocket推送 | ✅ |
| shouldNotNotifyWhenMentioningSelf | @提及自己，不给自己发通知 | ✅ |
| shouldCreateReplyToParent | 回复父留言，通知父留言作者+WebSocket推送 | ✅ |
| shouldNotNotifyWhenReplyingToSelf | 回复自己的留言，不给自己发回复通知 | ✅ |
| shouldNotDuplicateNotificationForMentionAndReply | @提及用户与回复同一人，去重仅通知一次 | ✅ |
| shouldCreateEvenWhenParentNotFound | 父留言不存在，正常创建(不触发回复通知) | ✅ |
| shouldDeduplicateMentions | 批量@提及去重，相同用户仅通知一次 | ✅ |

### 3. getReplies - 查询回复列表

| 用例 | 验证项 | 结果 |
|------|--------|:--:|
| shouldReturnReplies | 正常流程-查询，返回回复列表 | ✅ |
| shouldReturnEmptyWhenNoReplies | 无回复，返回空列表 | ✅ |

---

## 验证清单对照（任务文档 Section 5.1）

| 序号 | 验证项 | 对应测试 | 结果 |
|:---:|--------|---------|:--:|
| 1 | 正常流程-新增 | shouldCreateSimpleComment | ✅ |
| 2 | 正常流程-查询 | shouldReturnPagedResults | ✅ |
| 3 | 正常流程-修改 | 无修改API（消息沟通只支持新增/查询） | ⏭️ N/A |
| 4 | 正常流程-删除 | 无删除API（消息沟通不支持删除） | ⏭️ N/A |
| 5 | 边界-空值 | Controller层@Valid校验，Service层不重复校验 | ⏭️ N/A |
| 6 | 边界-超长 | Controller层@Valid校验，Service层不重复校验 | ⏭️ N/A |
| 7 | 异常-并发 | 乐观锁由BaseEntity.version字段+MyBatis-Plus @Version自动处理 | ⏭️ 框架层 |
| 8 | 异常-不存在 | shouldCreateEvenWhenParentNotFound(父留言不存在不中断) | ✅ |

> 注：本项目消息沟通（单据沟通）的CRUD接口仅支持新增留言和查询，不支持修改和删除操作。边界校验由Controller层@Valid注解处理，并发控制由BaseEntity内置的乐观锁(@Version)处理，均不在Service层重复验证。

---

## 测试覆盖的入口条件

- 参数正确 → 正常执行业务逻辑
- @提及用户 → 解析并通知被提及用户
- @提及自己 → 跳过自身通知
- 父留言存在且作者非自己 → 通知父留言作者
- 父留言存在且作者为自己 → 跳过通知
- @提及用户与父留言作者为同一人 → 去重，仅通知一次
- 父留言不存在 → 正常创建留言，跳过回复通知
- 无查询结果 → 返回空列表

---

## 发现的问题

无。

## 遗留风险

无。所有核心业务逻辑已通过单元测试验证。
