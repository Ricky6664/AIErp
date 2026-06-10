-- ============================================================
-- ProductClassService 测试数据脚本
-- 用于 ProductClassServiceTest 单元测试
-- ============================================================

-- 插入根分类
INSERT INTO prod_product_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (1, '电子产品', NULL, 1, true, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入子分类
INSERT INTO prod_product_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (2, '手机', 1, 1, true, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入另一子分类
INSERT INTO prod_product_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (3, '电脑', 1, 2, true, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入停用分类
INSERT INTO prod_product_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (4, '已停用分类', NULL, 99, false, 1, NOW(), NOW(), 1, 1, false, 0);
