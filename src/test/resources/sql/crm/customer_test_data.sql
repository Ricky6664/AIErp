-- ============================================================
-- CustomerService 单元测试数据脚本
-- 用于集成测试场景，初始化测试客户数据
-- ============================================================

-- 清理已有测试数据
DELETE FROM crm_customer WHERE customer_code LIKE 'TEST%';

-- 插入基础测试客户数据
INSERT INTO crm_customer (id, customer_code, customer_name, short_name, phone, email,
    class_id, business_rep_id, business_dept_id, company_id, source, status, is_active, remark,
    tenant_id, is_deleted, create_time, update_time, creator_id, updater_id, version)
VALUES
(1001, 'TEST001', '测试客户A', '测试A', '13800001001', 'testa@example.com',
    1, 10, 100, 1, '官网', 1, 1, '测试数据A',
    1, 0, NOW(), NOW(), 1, 1, 0),
(1002, 'TEST002', '测试客户B', '测试B', '13800001002', 'testb@example.com',
    2, 20, 200, 1, '地推', 2, 1, '测试数据B',
    1, 0, NOW(), NOW(), 1, 1, 0),
(1003, 'TEST003', '测试客户C', '测试C', '13800001003', 'testc@example.com',
    1, 10, 100, 2, '渠道', 1, 0, '测试数据C',
    1, 0, NOW(), NOW(), 1, 1, 0);
