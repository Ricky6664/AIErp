# P0-011-002-011-001-002 编写Service+Controller

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-011-002-011-001-002 |
| 任务名称 | 编写Service+Controller |
| 所属模块 | P0-011 |
| 优先级 | P0 |
| 任务类型 | Service服务层 |

## 二、任务目标

编写系统公告AnnouncementService与AnnouncementController，实现公告CRUD + 已读标记 + 未读查询 + 登录后弹窗获取未读公告列表，配置权限控制(CRUD需管理员权限，未读查询仅需登录)

## 三、前置依赖

### 3.1 前置任务

- P0-011-002-011-001 后端开发（父任务）
- P0-011-002-011-001-001 编写DDL+Entity/Mapper（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用
| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与编写要求 |
| 全局规范-AI开发执行手册 | AI开发执行流程与质量要求 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-API接口规范 | API接口设计与RESTful规范约束 |

## 五、详细开发规格
> **📦 本任务模块上下文**（来源：finance模块开发指南）
> - 本模块涉及数据表：fin_currency_rate, fin_bank_account, fin_account, fin_voucher_word, fin_accounting_period
> - 本模块涉及API：/api/finance/currency-rate, /api/finance/bank-account, /api/finance/account, /api/finance/voucher-word, /api/finance/accounting-period, /api/finance/workbench
> - 本模块业务规则：币种编码全局唯一; 汇率日期不得晚于当前日期+30天; 银行账号全局唯一; 会计科目编码按级次规则(4-2-2-2)全局唯一; 末级科目标记新增修改时自动计算; 会计期间不可重叠; 科目余额表凭证审核时自动更新
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Service接口
```java
public interface IAnnouncementService extends IServiceX<AnnouncementEntity> {
    AnnouncementVO create(AnnouncementCreateDTO dto);
    AnnouncementVO update(Long id, AnnouncementUpdateDTO dto);
    void delete(Long id);
    PageResult<AnnouncementVO> pageList(AnnouncementQueryDTO query);
    List<AnnouncementVO> getUnreadList();
    void markAsRead(Long announcementId);
}
```

### 5.2 Controller接口
| HTTP | 路径 | 功能 |
|------|------|------|
| GET | /api/system/announcement | 分页列表 |
| POST | /api/system/announcement | 新增 |
| PUT | /api/system/announcement/{id} | 修改 |
| DELETE | /api/system/announcement/{id} | 删除 |
| GET | /api/system/announcement/unread | 未读列表 |
| POST | /api/system/announcement/{id}/read | 标记已读 |

### 5.3 核心逻辑
- 未读查询：关联用户已读记录表
- 已读标记：插入用户已读记录(userId + announcementId)
- 置顶排序：is_top=true优先，publishTime倒序

### 5.4 权限控制
- CRUD需管理员权限
- 未读查询/已读标记仅需登录权限


## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | erp-system-module/src/main/java/com/erp/system/announcement/service/IAnnouncementService.java | Service接口 |
| 2 | erp-system-module/src/main/java/com/erp/system/announcement/service/impl/AnnouncementServiceImpl.java | Service实现 |
| 3 | erp-system-module/src/main/java/com/erp/system/announcement/controller/AnnouncementController.java | Controller |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|--------|
| 1 | 公告CRUD接口正常 | 接口测试 |
| 2 | 未读列表查询正确 | 接口测试 |
| 3 | 已读标记功能正常 | 接口测试 |
| 4 | 权限控制正确 | 权限测试 |
| 5 | 置顶排序正确 | 接口测试 |

## 八、易错警示

> ⚠️ 已读记录表要加唯一索引(userId + announcementId)，防止重复标记

> ⚠️ 未读查询SQL要排除用户已读的公告，使用LEFT JOIN + IS NULL或NOT EXISTS

> ⚠️ 公告内容(富文本)存储使用TEXT类型，注意XSS过滤

> ⚠️ 置顶功能要考虑并发场景，使用排序字段而非直接修改数据库顺序
