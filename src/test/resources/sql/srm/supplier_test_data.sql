-- 供应商测试数据
-- 用于 SupplierServiceTest 单元测试

-- 基础供应商数据
INSERT INTO srm_supplier (id, tenant_id, supplier_code, supplier_name, short_name, phone, email, class_id, buyer_id, buyer_dept_id, company_id, source, audit_status, is_active, remark, create_time, update_time, creator_id, updater_id, is_deleted, owner_dept_id, owner_id, version) VALUES
(1, 1, 'SUP-001', '华为技术有限公司', '华为', '0755-28780808', 'huawei@huawei.com', 10, 100, 200, 1, 'SELF_REGISTER', 'DRAFT', true, '测试供应商', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 1, 1, false, 1, 1, 0),
(2, 1, 'SUP-002', '中兴通讯', '中兴', '0755-26770000', 'zte@zte.com.cn', 10, 101, 200, 1, 'SELF_REGISTER', 'DRAFT', true, '通讯设备供应商', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 1, 1, false, 1, 1, 0),
(3, 1, 'SUP-003', '已审核供应商', '已审核', '021-12345678', 'approved@test.com', 10, 102, 200, 1, 'SELF_REGISTER', 'APPROVED', true, '已通过审核', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 1, 1, false, 1, 1, 0),
(4, 2, 'SUP-004', '华为技术有限公司', '华为', '0755-28780808', 'huawei@huawei.com', 10, 100, 201, 2, 'SELF_REGISTER', 'DRAFT', true, '不同公司主体的同名供应商', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 1, 1, false, 2, 1, 0),
(5, 1, 'SUP-005', '禁用供应商', '禁用', '010-87654321', 'disabled@test.com', 11, 103, 200, 1, 'SELF_REGISTER', 'DRAFT', false, '已禁用的供应商', '2026-01-01 00:00:00', '2026-01-01 00:00:00', 1, 1, false, 1, 1, 0);

-- 清理数据（测试结束后执行）
-- DELETE FROM srm_supplier WHERE id BETWEEN 1 AND 5;
