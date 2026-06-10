-- ============================================================
-- 反审功能测试数据脚本
-- 用于 AuditEngineUnconfirmTest / AuditEngineUnconfirmConcurrentTest
-- ============================================================

-- 已审核状态单据, 无下游关联(供正常反审测试, docId=2001)
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (101, 'sale_order', 2001, 2, 1, NOW(), NOW());

-- 已审核状态单据, 有关联下游发货单DN-20260001(供下游拦截测试, docId=2002)
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (102, 'sale_order', 2002, 2, 1, NOW(), NOW());

-- 草稿状态单据(供反审拒绝测试, docId=2003)
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (103, 'sale_order', 2003, 0, 1, NOW(), NOW());

-- 已提交状态单据(供反审拒绝测试, docId=2004)
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (104, 'sale_order', 2004, 1, 1, NOW(), NOW());

-- 已驳回状态单据(供反审拒绝测试, docId=2005)
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (105, 'sale_order', 2005, 3, 1, NOW(), NOW());

-- 已作废状态单据(供反审拒绝测试, docId=2006)
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (106, 'sale_order', 2006, 4, 1, NOW(), NOW());

-- 已审核状态单据(供并发反审测试, docId=2007)
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (107, 'sale_order', 2007, 2, 1, NOW(), NOW());
