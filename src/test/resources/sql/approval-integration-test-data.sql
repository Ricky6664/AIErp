-- ============================================================
-- 审核引擎与审批流程集成测试数据
-- 任务: P1-001-001-005-001-002
-- ============================================================

-- 审核配置：启用审批流程
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, approval_enabled, auto_confirm, approval_flow_config, create_time, update_time)
VALUES (1, 'PURCHASE_ORDER', '采购订单', true, false, '{"flowDefinitionId":"FLOW_DEF_001","nodes":[{"nodeName":"部门经理审批","approverRole":"DEPT_MANAGER","nodeOrder":1,"required":true},{"nodeName":"总经理审批","approverRole":"GM","nodeOrder":2,"required":true}]}', NOW(), NOW());

-- 审核配置：未启用审批流程、不自动确认
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, approval_enabled, auto_confirm, approval_flow_config, create_time, update_time)
VALUES (2, 'SALE_ORDER', '销售订单', false, false, NULL, NOW(), NOW());

-- 审核配置：未启用审批流程、自动审核通过
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, approval_enabled, auto_confirm, approval_flow_config, create_time, update_time)
VALUES (3, 'STOCK_IN', '入库单', false, true, NULL, NOW(), NOW());

-- 审核配置：3节点审批流程
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, approval_enabled, auto_confirm, approval_flow_config, create_time, update_time)
VALUES (4, 'EXPENSE_REPORT', '费用报销单', true, false, '{"flowDefinitionId":"FLOW_DEF_002","nodes":[{"nodeName":"部门经理审批","approverRole":"DEPT_MANAGER","nodeOrder":1,"required":true},{"nodeName":"财务审批","approverRole":"FINANCE","nodeOrder":2,"required":true},{"nodeName":"总经理审批","approverRole":"GM","nodeOrder":3,"required":false}]}', NOW(), NOW());

-- 单据状态：草稿状态（用于提交测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (1, 'PURCHASE_ORDER', 1001, 0, 1, NOW(), NOW());

-- 单据状态：已提交状态（用于审核回调测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (2, 'PURCHASE_ORDER', 2001, 1, 1, NOW(), NOW());

-- 单据状态：已提交状态（用于驳回回调测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (3, 'PURCHASE_ORDER', 3001, 1, 1, NOW(), NOW());

-- 单据状态：已审核状态（用于幂等测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (4, 'PURCHASE_ORDER', 4001, 2, 1, NOW(), NOW());

-- 单据状态：已驳回状态（用于幂等测试）
INSERT INTO sys_document_status (id, doc_type, doc_id, status, tenant_id, create_time, update_time)
VALUES (5, 'PURCHASE_ORDER', 4002, 3, 1, NOW(), NOW());
