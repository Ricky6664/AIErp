-- ============================================================
-- TagDefinitionService 测试数据脚本
-- 用于 TagDefinitionServiceTest 单元测试
-- ============================================================

-- 插入重要客户标签
INSERT INTO crm_tag_definition (id, tag_name, tag_group, tag_color, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (1, '重要客户', '客户等级', '#FF0000', 1, true, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入普通客户标签
INSERT INTO crm_tag_definition (id, tag_name, tag_group, tag_color, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (2, '普通客户', '客户等级', '#00FF00', 2, true, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入VIP客户标签
INSERT INTO crm_tag_definition (id, tag_name, tag_group, tag_color, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (3, 'VIP客户', 'VIP', '#0000FF', 1, true, 1, NOW(), NOW(), 1, 1, false, 0);

-- 插入停用标签
INSERT INTO crm_tag_definition (id, tag_name, tag_group, tag_color, sort_order, is_active, tenant_id, create_time, update_time, creator_id, updater_id, is_deleted, version)
VALUES (4, '已停用标签', '其他', '#CCCCCC', 99, false, 1, NOW(), NOW(), 1, 1, false, 0);
