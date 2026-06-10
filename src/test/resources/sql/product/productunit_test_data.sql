-- ============================================================
-- ProductUnitService 测试数据脚本
-- 用于 ProductUnitServiceTest 集成测试
-- ============================================================

-- 插入基础单位（商品100的基础单位，转换比例为1）
INSERT INTO prod_product_unit (id, product_id, unit_id, is_base_unit, is_purchase_unit, is_sale_unit, is_production_unit, conversion_rate, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (1, 100, 10, true, true, true, false, 1.00, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入采购单位（非基础单位，与基础单位换算比例为100:1）
INSERT INTO prod_product_unit (id, product_id, unit_id, is_base_unit, is_purchase_unit, is_sale_unit, is_production_unit, conversion_rate, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (2, 100, 20, false, true, false, false, 100.00, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入销售单位（非基础单位，与基础单位换算比例为50:1）
INSERT INTO prod_product_unit (id, product_id, unit_id, is_base_unit, is_purchase_unit, is_sale_unit, is_production_unit, conversion_rate, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (3, 100, 30, false, false, true, false, 50.00, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入已删除单位（用于测试逻辑删除后的查询）
INSERT INTO prod_product_unit (id, product_id, unit_id, is_base_unit, is_purchase_unit, is_sale_unit, is_production_unit, conversion_rate, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (4, 100, 40, false, false, false, true, 10.00, 1, NOW(), NOW(), 1, 1, true, 0);

-- 插入另一商品的单位（商品200的基础单位）
INSERT INTO prod_product_unit (id, product_id, unit_id, is_base_unit, is_purchase_unit, is_sale_unit, is_production_unit, conversion_rate, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (5, 200, 10, true, true, true, true, 1.00, 1, NOW(), NOW(), 1, 1, false, 0);
