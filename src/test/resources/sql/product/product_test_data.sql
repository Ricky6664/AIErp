-- ============================================================
-- ProductService 测试数据脚本
-- 用于 ProductServiceTest 集成测试
-- ============================================================

-- 插入商品分类（供外键关联）
INSERT INTO prod_product_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (1, '电子产品', NULL, 1, true, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入测试商品1（草稿状态）
INSERT INTO prod_product (id, product_code, product_name, model, spec, brand, base_unit_id, is_multi_unit, class_id, is_saleable, is_purchasable, is_producible, is_outsourceable, is_sub_part, audit_status, is_active, remark, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (1, 'PROD-001', '测试商品1', 'M001', 'S001', '测试品牌', 1, false, 1, true, true, false, false, false, '草稿', true, '测试备注1', 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入测试商品2（待审核状态）
INSERT INTO prod_product (id, product_code, product_name, model, spec, brand, base_unit_id, is_multi_unit, class_id, is_saleable, is_purchasable, is_producible, is_outsourceable, is_sub_part, audit_status, is_active, remark, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (2, 'PROD-002', '测试商品2', 'M002', 'S002', '测试品牌2', 1, true, 1, true, false, true, false, false, '待审核', true, '测试备注2', 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入测试商品3（已审核状态）
INSERT INTO prod_product (id, product_code, product_name, model, spec, brand, base_unit_id, is_multi_unit, class_id, is_saleable, is_purchasable, is_producible, is_outsourceable, is_sub_part, audit_status, is_active, remark, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (3, 'PROD-003', '测试商品3', 'M003', 'S003', '测试品牌3', 2, false, 1, false, true, false, true, false, '已审核', true, '测试备注3', 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入停用商品
INSERT INTO prod_product (id, product_code, product_name, model, spec, brand, base_unit_id, is_multi_unit, class_id, is_saleable, is_purchasable, is_producible, is_outsourceable, is_sub_part, audit_status, is_active, remark, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (4, 'PROD-004', '已停用商品', 'M004', 'S004', '测试品牌', 1, false, 1, false, false, false, false, false, '已驳回', false, '已停用', 1, NOW(), NOW(), 1, 1, false, 0);
