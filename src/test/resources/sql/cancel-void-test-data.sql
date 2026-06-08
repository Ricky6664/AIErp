-- ============================================================
-- AuditEngine cancelVoid(撤销作废)测试数据脚本
-- 用于 AuditEngineCancelVoidTest / CancelVoidResourceRestoreTest
-- ============================================================

-- 已审核→已作废单据（供撤销作废恢复至status=2测试：docId=4001）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4001, 'sale_order', 4001, 4, 1, NOW(), NOW());
INSERT INTO sys_audit_log (id, doc_type, doc_id, operation_type, from_status, to_status, operator_id, created_at, tenant_id)
VALUES (40011, 'sale_order', 4001, 'SUBMIT', 0, 1, 1, NOW() - INTERVAL '3 hours', 1);
INSERT INTO sys_audit_log (id, doc_type, doc_id, operation_type, from_status, to_status, operator_id, created_at, tenant_id)
VALUES (40012, 'sale_order', 4001, 'APPROVE', 1, 2, 2, NOW() - INTERVAL '2 hours', 1);
INSERT INTO sys_audit_log (id, doc_type, doc_id, operation_type, from_status, to_status, operator_id, created_at, tenant_id)
VALUES (40013, 'sale_order', 4001, 'VOID', 2, 4, 1, NOW() - INTERVAL '1 hour', 1);

-- 已提交→已作废单据（供撤销作废恢复至status=1测试：docId=4002）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4002, 'sale_order', 4002, 4, 1, NOW(), NOW());
INSERT INTO sys_audit_log (id, doc_type, doc_id, operation_type, from_status, to_status, operator_id, created_at, tenant_id)
VALUES (40021, 'sale_order', 4002, 'SUBMIT', 0, 1, 1, NOW() - INTERVAL '2 hours', 1);
INSERT INTO sys_audit_log (id, doc_type, doc_id, operation_type, from_status, to_status, operator_id, created_at, tenant_id)
VALUES (40022, 'sale_order', 4002, 'VOID', 1, 4, 1, NOW() - INTERVAL '1 hour', 1);

-- 草稿→已作废单据（供撤销作废恢复至status=0测试：docId=4003）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4003, 'sale_order', 4003, 4, 1, NOW(), NOW());
INSERT INTO sys_audit_log (id, doc_type, doc_id, operation_type, from_status, to_status, operator_id, created_at, tenant_id)
VALUES (40031, 'sale_order', 4003, 'VOID', 0, 4, 1, NOW() - INTERVAL '1 hour', 1);

-- 草稿状态单据（供撤销作废拦截测试：status=0不可撤销作废）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4004, 'sale_order', 4004, 0, 1, NOW(), NOW());

-- 已提交状态单据（供撤销作废拦截测试：status=1不可撤销作废）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4005, 'sale_order', 4005, 1, 1, NOW(), NOW());

-- 已审核状态单据（供撤销作废拦截测试：status=2不可撤销作废）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4006, 'sale_order', 4006, 2, 1, NOW(), NOW());

-- 已作废单据但无VOID日志（供数据异常测试：status=4但无VOID记录）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4007, 'sale_order', 4007, 4, 1, NOW(), NOW());
