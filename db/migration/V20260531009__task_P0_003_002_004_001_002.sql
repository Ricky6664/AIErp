-- ============================================================
-- Flyway Migration Script
-- Version: V20260531009
-- Description: 系统管理表索引与约束
--   （sys_param/sys_dict_type/sys_dict_data/sys_code_rule/
--    sys_code_rule_segment/sys_operation_log/sys_data_view/
--    sys_data_view_field/sys_notice/sys_doc_config）
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- 1. sys_param 系统参数表 索引
-- ============================================================
-- 部分唯一索引：同一租户下参数编码唯一（排除已删除）
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_param_code
    ON sys_param(tenant_id, param_code) WHERE is_deleted = false;

-- 参数分组查询索引
CREATE INDEX IF NOT EXISTS idx_sys_param_group
    ON sys_param(tenant_id, param_group);

-- ============================================================
-- 2. sys_dict_type 字典类型表 索引
-- ============================================================
-- 部分唯一索引：同一租户下字典类型编码唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_dict_type_code
    ON sys_dict_type(tenant_id, dict_type_code) WHERE is_deleted = false;

-- ============================================================
-- 3. sys_dict_data 字典数据表 索引
-- ============================================================
-- 部分唯一索引：同一租户同一字典类型下键值唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_dict_data_value
    ON sys_dict_data(tenant_id, dict_type_id, dict_value) WHERE is_deleted = false;

-- 字典类型外键关联查询索引
CREATE INDEX IF NOT EXISTS idx_sys_dict_data_type
    ON sys_dict_data(tenant_id, dict_type_id);

-- ============================================================
-- 4. sys_code_rule 编码规则配置表 索引
-- ============================================================
-- 部分唯一索引：同一租户下规则编码唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_code_rule_code
    ON sys_code_rule(tenant_id, rule_code) WHERE is_deleted = false;

-- ============================================================
-- 5. sys_code_rule_segment 编码规则段配置表 索引
-- ============================================================
-- 部分唯一索引：同一租户同一规则下段顺序唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_code_rule_segment_seq
    ON sys_code_rule_segment(tenant_id, rule_id, seq) WHERE is_deleted = false;

-- 规则关联查询索引
CREATE INDEX IF NOT EXISTS idx_sys_code_rule_segment_rule
    ON sys_code_rule_segment(tenant_id, rule_id);

-- ============================================================
-- 6. sys_operation_log 操作日志表 索引
-- ============================================================
-- 操作时间范围查询索引
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_time
    ON sys_operation_log(tenant_id, oper_at);

-- 操作人查询索引
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_operator
    ON sys_operation_log(tenant_id, operator_id);

-- 操作模块查询索引
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_module
    ON sys_operation_log(tenant_id, module);

-- ============================================================
-- 7. sys_data_view 数据视图配置表 索引
-- ============================================================
-- 部分唯一索引：同一租户下视图编码唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_data_view_code
    ON sys_data_view(tenant_id, view_code) WHERE is_deleted = false;

-- ============================================================
-- 8. sys_data_view_field 数据视图字段配置表 索引
-- ============================================================
-- 部分唯一索引：同一租户同一视图下字段名唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_data_view_field_name
    ON sys_data_view_field(tenant_id, view_id, field_name) WHERE is_deleted = false;

-- 视图关联查询索引
CREATE INDEX IF NOT EXISTS idx_sys_data_view_field_view
    ON sys_data_view_field(tenant_id, view_id);

-- ============================================================
-- 9. sys_notice 系统公告表 索引
-- ============================================================
-- 公告状态查询索引
CREATE INDEX IF NOT EXISTS idx_sys_notice_status
    ON sys_notice(tenant_id, status);

-- 发布时间查询索引
CREATE INDEX IF NOT EXISTS idx_sys_notice_time
    ON sys_notice(tenant_id, publish_time);

-- 公告类型查询索引
CREATE INDEX IF NOT EXISTS idx_sys_notice_type
    ON sys_notice(tenant_id, type);

-- ============================================================
-- 10. sys_doc_config 单据配置表 索引
-- ============================================================
-- 部分唯一索引：同一租户下单据类型编码唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_doc_config_type
    ON sys_doc_config(tenant_id, doc_type) WHERE is_deleted = false;

-- ============================================================
-- COMMENT on indexes
-- ============================================================
COMMENT ON INDEX uk_sys_param_code IS '系统参数：tenant_id + param_code 部分唯一索引（排除已删除）';
COMMENT ON INDEX idx_sys_param_group IS '系统参数：tenant_id + param_group 分组查询索引';
COMMENT ON INDEX uk_sys_dict_type_code IS '字典类型：tenant_id + dict_type_code 部分唯一索引';
COMMENT ON INDEX uk_sys_dict_data_value IS '字典数据：tenant_id + dict_type_id + dict_value 部分唯一索引';
COMMENT ON INDEX idx_sys_dict_data_type IS '字典数据：tenant_id + dict_type_id 关联查询索引';
COMMENT ON INDEX uk_sys_code_rule_code IS '编码规则：tenant_id + rule_code 部分唯一索引';
COMMENT ON INDEX uk_sys_code_rule_segment_seq IS '编码规则段：tenant_id + rule_id + seq 部分唯一索引';
COMMENT ON INDEX idx_sys_code_rule_segment_rule IS '编码规则段：tenant_id + rule_id 关联查询索引';
COMMENT ON INDEX idx_sys_operation_log_time IS '操作日志：tenant_id + oper_at 时间范围查询索引';
COMMENT ON INDEX idx_sys_operation_log_operator IS '操作日志：tenant_id + operator_id 操作人查询索引';
COMMENT ON INDEX idx_sys_operation_log_module IS '操作日志：tenant_id + module 模块查询索引';
COMMENT ON INDEX uk_sys_data_view_code IS '数据视图：tenant_id + view_code 部分唯一索引';
COMMENT ON INDEX uk_sys_data_view_field_name IS '数据视图字段：tenant_id + view_id + field_name 部分唯一索引';
COMMENT ON INDEX idx_sys_data_view_field_view IS '数据视图字段：tenant_id + view_id 关联查询索引';
COMMENT ON INDEX idx_sys_notice_status IS '系统公告：tenant_id + status 状态查询索引';
COMMENT ON INDEX idx_sys_notice_time IS '系统公告：tenant_id + publish_time 时间查询索引';
COMMENT ON INDEX idx_sys_notice_type IS '系统公告：tenant_id + type 类型查询索引';
COMMENT ON INDEX uk_sys_doc_config_type IS '单据配置：tenant_id + doc_type 部分唯一索引';
