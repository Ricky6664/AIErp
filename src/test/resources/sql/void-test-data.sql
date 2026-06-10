-- ============================================================
-- AuditEngine void(作废)测试数据脚本
-- 用于 AuditEngineVoidTest / VoidResourceReleaseEventTest
-- ============================================================

-- 已审核状态单据（供正常作废测试：fromStatus=2→4）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3001, 'sale_order', 3001, 2, 1, NOW(), NOW());

-- 草稿状态单据（供草稿作废测试：fromStatus=0→4）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3002, 'sale_order', 3002, 0, 1, NOW(), NOW());

-- 已提交状态单据（供已提交作废测试：fromStatus=1→4）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3003, 'sale_order', 3003, 1, 1, NOW(), NOW());

-- 已驳回状态单据（供作废拦截测试：status=3不可作废）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3004, 'sale_order', 3004, 3, 1, NOW(), NOW());

-- 已作废状态单据（供重复作废拦截测试：status=4不可重复作废）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3005, 'sale_order', 3005, 4, 1, NOW(), NOW());

-- 已审核采购订单（供并发作废测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3006, 'purchase_order', 3006, 2, 1, NOW(), NOW());

-- 已审核销售订单（供资源释放事件-fromStatus=2测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3007, 'sale_order', 3007, 2, 1, NOW(), NOW());

-- 草稿采购订单（供资源释放事件-fromStatus=0测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3008, 'purchase_order', 3008, 0, 1, NOW(), NOW());
