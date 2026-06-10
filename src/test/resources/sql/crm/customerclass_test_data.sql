-- ============================================================
-- CustomerClassService 测试数据脚本
-- 用于 CustomerClassServiceTest 单元测试
-- ============================================================

-- 插入根分类
INSERT INTO crm_customer_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (1, 'VIP客户', NULL, 1, 1, 1, NOW(), NOW(), 1, 1, 0, 0);

-- 插入子分类
INSERT INTO crm_customer_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (2, '黄金VIP', 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, 0);

-- 插入另一子分类
INSERT INTO crm_customer_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (3, '钻石VIP', 1, 2, 1, 1, NOW(), NOW(), 1, 1, 0, 0);

-- 插入停用分类
INSERT INTO crm_customer_class (id, class_name, parent_id, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (4, '已停用分类', NULL, 99, 0, 1, NOW(), NOW(), 1, 1, 0, 0);
