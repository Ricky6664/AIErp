-- ============================================================
-- AuditEngine 测试数据脚本
-- 用于 AuditEngineServiceTest / AuditEngineUnconfirmTest
-- ============================================================

-- 插入草稿状态单据（供submit测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (1, 'sale_order', 1001, 0, 1, NOW(), NOW());

-- 插入已提交状态单据（供approve测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (2, 'sale_order', 1002, 1, 1, NOW(), NOW());

-- 插入已审核状态单据（供unconfirm测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3, 'sale_order', 1003, 2, 1, NOW(), NOW());

-- 插入已驳回状态单据（供异常状态测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4, 'sale_order', 1004, 3, 1, NOW(), NOW());

-- 插入采购订单草稿（跨单据类型测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (5, 'purchase_order', 2001, 0, 1, NOW(), NOW());

-- 插入采购订单已审核（反审下游检查测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (6, 'purchase_order', 2002, 2, 1, NOW(), NOW());
