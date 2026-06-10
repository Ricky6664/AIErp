-- ============================================================
-- ProductControlService 测试数据脚本
-- 用于 ProductControlServiceTest 集成测试
-- ============================================================

-- 插入商品100的控制策略（批次管理，未启用序列号）
INSERT INTO prod_product_control (id, product_id, default_purchase_unit_id, default_sale_unit_id, inventory_manage_flag, location_manage_flag, batch_manage_flag, serial_manage_flag, shelf_life_manage_flag, min_package_qty, min_order_qty, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (1, 100, 10, 20, true, false, true, false, true, 1.00, 10.00, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入商品200的控制策略（序列号管理，未启用批次）
INSERT INTO prod_product_control (id, product_id, default_purchase_unit_id, default_sale_unit_id, inventory_manage_flag, location_manage_flag, batch_manage_flag, serial_manage_flag, shelf_life_manage_flag, min_package_qty, min_order_qty, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (2, 200, 30, 30, true, true, false, true, false, 5.00, 50.00, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入商品300的控制策略（库位管理，无批次无序列号）
INSERT INTO prod_product_control (id, product_id, default_purchase_unit_id, default_sale_unit_id, inventory_manage_flag, location_manage_flag, batch_manage_flag, serial_manage_flag, shelf_life_manage_flag, min_package_qty, min_order_qty, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (3, 300, 15, 25, false, true, false, false, false, 0.00, 0.00, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入已删除的控制策略（用于测试逻辑删除后的查询）
INSERT INTO prod_product_control (id, product_id, default_purchase_unit_id, default_sale_unit_id, inventory_manage_flag, location_manage_flag, batch_manage_flag, serial_manage_flag, shelf_life_manage_flag, min_package_qty, min_order_qty, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (4, 400, 40, 40, true, false, false, false, false, 10.00, 100.00, 1, NOW(), NOW(), 1, 1, true, 0);

-- 插入商品500的控制策略（全功能开启，批次与序列号互斥测试不应同时为true，此处仅设批次）
INSERT INTO prod_product_control (id, product_id, default_purchase_unit_id, default_sale_unit_id, inventory_manage_flag, location_manage_flag, batch_manage_flag, serial_manage_flag, shelf_life_manage_flag, min_package_qty, min_order_qty, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (5, 500, 1, 2, true, true, true, false, true, 2.00, 20.00, 1, NOW(), NOW(), 1, 1, false, 0);
