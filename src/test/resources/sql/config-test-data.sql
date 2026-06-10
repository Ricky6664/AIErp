-- ============================================================
-- 审核配置测试数据脚本
-- 用于 AuditConfigServiceTest / AutoConfirmListenerTest
-- ============================================================

-- 销售订单配置：自动确认启用（auto_confirm=true, approval_enabled=false）
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, auto_confirm, approval_enabled, tenant_id, create_time, update_time, is_deleted, version)
VALUES (1, 'sale_order', '销售订单', true, false, 1, NOW(), NOW(), false, 0);

-- 销售订单配置：审批流程启用（auto_confirm=false, approval_enabled=true）
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, auto_confirm, approval_enabled, approval_flow_config, tenant_id, create_time, update_time, is_deleted, version)
VALUES (2, 'purchase_order', '采购订单', false, true, '{"flowDefinitionId":"FLOW_DEF_001","nodes":[{"nodeName":"部门经理审批","approverRole":"DEPT_MANAGER","nodeOrder":1,"required":true}]}', 1, NOW(), NOW(), false, 0);

-- 销售订单配置：两者均关闭（手动审核模式）
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, auto_confirm, approval_enabled, tenant_id, create_time, update_time, is_deleted, version)
VALUES (3, 'warehouse_order', '仓库订单', false, false, 1, NOW(), NOW(), false, 0);

-- 销售订单配置：两者均禁用（仅用于互斥校验测试——数据库层不阻止此状态但业务层会校验）
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, auto_confirm, approval_enabled, tenant_id, create_time, update_time, is_deleted, version)
VALUES (4, 'finance_order', '财务订单', false, false, 1, NOW(), NOW(), false, 0);

-- 已有已审核单据的配置（用于隔离测试：修改配置不影响已有单据）
INSERT INTO sys_audit_config (id, doc_type, doc_type_name, auto_confirm, approval_enabled, tenant_id, create_time, update_time, is_deleted, version)
VALUES (5, 'test_doc_type', '测试单据类型', false, true, 1, NOW(), NOW(), false, 0);
