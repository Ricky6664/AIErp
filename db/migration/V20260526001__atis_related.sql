-- ============================================================
-- ERP AI 系统 - 多租户隔离DDL规范
-- 任务编号: P0-003-002-008-001-001
-- 文件名:   V20260526001__atis_related.sql
-- 说明:     定义多租户隔离DDL规范，包括10个通用必含字段规范、
--           部分唯一索引(WHERE is_deleted=false)规范、
--           联合索引以tenant_id为首列规范、数值精度decimal(18,8)规范、
--           以及多租户隔离约束元数据表和校验函数。
--           本脚本为多租户隔离规范定义脚本，执行后会创建
--           erp_base.tenant_isolation_constraint 约束元数据表，
--           用于存储多租户隔离的DDL约束规则，供后续代码生成和
--           DDL校验参考。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-06-01
-- ============================================================

-- ============================================================
-- 第1步：创建多租户隔离约束元数据表
-- 说明:     存储所有业务表的DDL约束规则元数据。
--           每条记录定义一个业务表在某项DDL规范上的合规要求：
--           - 是否必须包含10个通用字段
--           - 是否必须包含tenant_id
--           - 联合索引是否以tenant_id为首列
--           - 唯一索引是否包含WHERE is_deleted=false
--           - 数值字段是否使用decimal(18,8)
--           本表为DDL代码生成和合规校验提供权威的规则参考。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.tenant_isolation_constraint (
    id                      BIGSERIAL       PRIMARY KEY,
    schema_name             VARCHAR(64)     NOT NULL DEFAULT 'erp_base',
    table_name              VARCHAR(128)    NOT NULL,
    table_category          VARCHAR(30)     NOT NULL,
    require_common_fields   BOOLEAN         NOT NULL DEFAULT TRUE,
    require_tenant_id       BOOLEAN         NOT NULL DEFAULT TRUE,
    require_tenant_first_idx BOOLEAN        NOT NULL DEFAULT TRUE,
    require_partial_unique  BOOLEAN         NOT NULL DEFAULT TRUE,
    require_decimal_18_8    BOOLEAN         NOT NULL DEFAULT TRUE,
    require_comment         BOOLEAN         NOT NULL DEFAULT TRUE,
    forbid_foreign_key      BOOLEAN         NOT NULL DEFAULT TRUE,
    constraint_note         VARCHAR(500),
    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    sort_order              SMALLINT        NOT NULL DEFAULT 0,
    CONSTRAINT uk_tenant_isolation_rule UNIQUE (schema_name, table_name)
);

COMMENT ON TABLE erp_base.tenant_isolation_constraint IS '多租户隔离DDL约束元数据表 - 定义每个业务表的DDL合规要求';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.id IS '主键ID';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.schema_name IS 'Schema名称（如erp_base、erp_sale、erp_purchase等）';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.table_name IS '表名（snake_case）';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.table_category IS '表分类：system=系统表, biz_main=业务主表, biz_detail=业务明细, biz_config=业务配置, relation=关联表, snapshot=快照表, log=日志表';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.require_common_fields IS '是否必须包含10个通用字段（所有业务表必须为TRUE）';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.require_tenant_id IS '是否必须包含tenant_id且NOT NULL';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.require_tenant_first_idx IS '联合索引是否必须以tenant_id为首列';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.require_partial_unique IS '业务唯一索引是否必须包含WHERE is_deleted=false';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.require_decimal_18_8 IS '数值字段（金额/单价/数量/转换率）是否必须使用decimal(18,8)';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.require_comment IS '表和字段是否必须包含COMMENT注释';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.forbid_foreign_key IS '是否禁止使用数据库外键约束';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.constraint_note IS '约束补充说明';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.is_active IS '是否启用此规则';
COMMENT ON COLUMN erp_base.tenant_isolation_constraint.sort_order IS '排序号';

-- ============================================================
-- 第2步：插入多租户隔离约束规则数据（幂等性保证）
-- 说明:     按表分类预置DDL合规规则。
--           所有业务主表/明细表/配置表/关联表均需满足全部7项约束。
--           系统元数据表（如本表自身）部分约束可豁免。
-- ============================================================

INSERT INTO erp_base.tenant_isolation_constraint
    (schema_name, table_name, table_category, require_common_fields, require_tenant_id, require_tenant_first_idx, require_partial_unique, require_decimal_18_8, require_comment, forbid_foreign_key, constraint_note, sort_order)
VALUES
    -- ========== 系统核心表（erp_base schema） ==========
    ('erp_base', 'sys_user',              'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '用户表：username+tenant_id联合唯一索引WHERE is_deleted=false', 1),
    ('erp_base', 'sys_role',              'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '角色表：role_code+tenant_id联合唯一索引WHERE is_deleted=false', 2),
    ('erp_base', 'sys_menu',              'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '菜单表：permission_code+tenant_id联合唯一索引WHERE is_deleted=false', 3),
    ('erp_base', 'sys_user_role',         'relation',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '用户角色关联表：uk(user_id,role_id) WHERE is_deleted=false', 4),
    ('erp_base', 'sys_role_menu',         'relation',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '角色菜单关联表：uk(role_id,menu_id) WHERE is_deleted=false', 5),
    ('erp_base', 'sys_param',             'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '系统参数表：param_code+tenant_id联合唯一索引WHERE is_deleted=false', 6),
    ('erp_base', 'sys_dict_type',         'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '字典类型表：dict_type_code+tenant_id联合唯一索引WHERE is_deleted=false', 7),
    ('erp_base', 'sys_dict_data',         'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '字典数据表：uk(dict_type_id,dict_value) WHERE is_deleted=false', 8),
    ('erp_base', 'sys_code_rule',         'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '编码规则表：rule_code+tenant_id联合唯一索引WHERE is_deleted=false', 9),
    ('erp_base', 'sys_data_view',         'system',    TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '数据视图表：view_code+tenant_id联合唯一索引WHERE is_deleted=false', 10),

    -- ========== 组织架构表（erp_org schema） ==========
    ('erp_org',  'org_company',           'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '公司表：credit_code+tenant_id联合唯一索引WHERE is_deleted=false', 11),
    ('erp_org',  'org_department',        'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '部门表：dept_code+tenant_id联合唯一索引WHERE is_deleted=false', 12),
    ('erp_org',  'org_position',          'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '岗位表：position_code+tenant_id联合唯一索引WHERE is_deleted=false', 13),

    -- ========== 商品管理表（erp_product schema） ==========
    ('erp_product', 'prod_product',         'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '商品主表：product_code+tenant_id联合唯一索引WHERE is_deleted=false', 14),
    ('erp_product', 'prod_product_class',   'biz_config',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '商品分类表：class_code+tenant_id联合唯一索引WHERE is_deleted=false', 15),
    ('erp_product', 'prod_product_unit',    'biz_config',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '商品单位表：uk(product_id,unit_id) WHERE is_deleted=false', 16),

    -- ========== CRM客户管理表（erp_crm schema） ==========
    ('erp_crm', 'crm_customer',           'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '客户主表：customer_code+tenant_id联合唯一索引WHERE is_deleted=false', 17),
    ('erp_crm', 'crm_customer_contact',   'biz_detail',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '客户联系人表：uk(customer_id,contact_type,contact_value) WHERE is_deleted=false', 18),
    ('erp_crm', 'crm_customer_address',   'biz_detail',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '客户地址表', 19),

    -- ========== SRM供应商管理表（erp_srm schema） ==========
    ('erp_srm', 'srm_supplier',           'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '供应商主表：supplier_code+tenant_id联合唯一索引WHERE is_deleted=false', 20),
    ('erp_srm', 'srm_supplier_contact',   'biz_detail',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '供应商联系人表', 21),

    -- ========== 仓库库存表（erp_inventory schema） ==========
    ('erp_inventory', 'inv_warehouse',    'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '仓库表：warehouse_code+tenant_id联合唯一索引WHERE is_deleted=false', 22),
    ('erp_inventory', 'inv_location',     'biz_config',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '库位表：uk(warehouse_id,location_code) WHERE is_deleted=false', 23),

    -- ========== 财务基础表（erp_finance schema） ==========
    ('erp_finance', 'fin_account',        'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '会计科目表：account_code+tenant_id联合唯一索引WHERE is_deleted=false', 24),

    -- ========== HRM人力资源管理表（erp_hrm schema） ==========
    ('erp_hrm', 'hrm_employee',           'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '员工表：employee_no+tenant_id联合唯一索引WHERE is_deleted=false', 25),

    -- ========== 销售管理表（erp_sale schema） ==========
    ('erp_sale', 'sale_order',            'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '销售订单主表：order_no+tenant_id联合唯一索引WHERE is_deleted=false', 26),
    ('erp_sale', 'sale_order_detail',     'biz_detail',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '销售订单明细表：含商品快照字段，decimal(18,8)', 27),

    -- ========== 采购管理表（erp_purchase schema） ==========
    ('erp_purchase', 'pur_order',         'biz_main',  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '采购订单主表：order_no+tenant_id联合唯一索引WHERE is_deleted=false', 28),
    ('erp_purchase', 'pur_order_detail',  'biz_detail',TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  TRUE,  '采购订单明细表：含商品快照字段，decimal(18,8)', 29),

    -- ========== 元数据规范表自身（可豁免部分约束） ==========
    ('erp_base', 'tenant_isolation_constraint', 'system', FALSE, FALSE, FALSE, FALSE, FALSE, TRUE,  TRUE,  '本表为元数据规范表，不涉及多租户业务数据，豁免tenant_id等约束', 99)

ON CONFLICT (schema_name, table_name) DO UPDATE SET
    table_category          = EXCLUDED.table_category,
    require_common_fields   = EXCLUDED.require_common_fields,
    require_tenant_id       = EXCLUDED.require_tenant_id,
    require_tenant_first_idx= EXCLUDED.require_tenant_first_idx,
    require_partial_unique  = EXCLUDED.require_partial_unique,
    require_decimal_18_8    = EXCLUDED.require_decimal_18_8,
    require_comment         = EXCLUDED.require_comment,
    forbid_foreign_key      = EXCLUDED.forbid_foreign_key,
    constraint_note         = EXCLUDED.constraint_note,
    sort_order              = EXCLUDED.sort_order;

-- ============================================================
-- 第3步：多租户隔离DDL核心规范文档（注释文档）
-- 说明:     以注释形式系统阐述多租户隔离DDL规范的7条核心规则，
--           作为所有DDL开发人员的权威参考。
-- ============================================================

/*
 * ===================================================================
 * 多租户隔离DDL规范 — 七条核心规则
 * ===================================================================
 *
 * 规则一：通用字段强制包含（Common Fields Mandate）
 * ─────────────────────────────────────────────
 * 所有业务表必须包含以下10个通用字段，缺一不可：
 *
 *   id              BIGSERIAL       PRIMARY KEY     — 自增主键
 *   tenant_id       BIGINT          NOT NULL        — 租户ID，多租户隔离核心字段
 *   created_at      TIMESTAMP       NOT NULL DEFAULT NOW()  — 创建时间
 *   updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()  — 更新时间
 *   created_by      BIGINT                          — 创建人ID（审计字段，永不修改）
 *   updated_by      BIGINT                          — 修改人ID（每次更新自动刷新）
 *   is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE  — 软删除标记
 *   owner_dept_id   BIGINT                          — 所属部门ID（数据权限）
 *   owner_id        BIGINT                          — 数据负责人ID（数据权限）
 *   version         INT             NOT NULL DEFAULT 0      — 乐观锁版本号
 *
 * 豁免条件（仅限以下表类型）：
 *   - 元数据规范表（如本表 tenant_isolation_constraint）
 *   - Flyway迁移历史表（flyway_schema_history，由Flyway自动管理）
 *   - 辅助属性子表（doc_detail_location/doc_detail_batch/doc_detail_serial）
 *     — 仅需id/tenant_id/created_at/updated_at，不需要owner_dept_id/owner_id/version
 *
 * 违反后果：
 *   - MyBatis-Plus自动填充失败（created_at/updated_at/created_by/updated_by）
 *   - 多租户插件无法注入tenant_id条件
 *   - 乐观锁并发控制失效
 *   - 数据权限查询返回错误结果
 *
 *
 * 规则二：多租户隔离 — tenant_id（Multi-Tenant Isolation）
 * ────────────────────────────────────────────────────
 * 所有业务表的tenant_id字段：
 *   - 数据类型：BIGINT NOT NULL
 *   - 不可为空（NOT NULL约束）
 *   - 所有查询由MyBatis-Plus多租户插件自动注入 WHERE tenant_id = ?
 *   - 跨租户查询严格禁止
 *
 * 联合索引规则：
 *   - 所有联合索引必须以tenant_id为首列
 *   - 格式：CREATE INDEX idx_{table}_{col1}_{col2} ON {table}(tenant_id, col1, col2)
 *   - 原因：多租户插件注入的WHERE tenant_id = ?条件需要索引首列匹配才能高效利用索引
 *
 * 唯一约束规则：
 *   - 所有唯一约束必须包含tenant_id
 *   - 格式：UNIQUE(tenant_id, business_code)
 *   - 原因：不同租户可以有相同的业务编码（如不同租户各有自己的客户编码C001）
 *
 * 实现要点：
 *   - MyBatis-Plus TenantLineInnerInterceptor 自动注入tenant_id条件
 *   - INSERT时由MyBatis-Plus自动填充当前租户ID
 *   - SELECT/UPDATE/DELETE时由插件自动添加WHERE tenant_id = ?条件
 *   - 联表查询时插件自动为每个表注入tenant_id条件
 *
 *
 * 规则三：部分唯一索引（Partial Unique Index）
 * ──────────────────────────────────────────
 * 涉及业务唯一性校验的字段必须采用部分唯一索引方式：
 *
 *   正确写法：
 *     CREATE UNIQUE INDEX uk_{table}_{field}_active
 *         ON {table}(tenant_id, field_name)
 *         WHERE is_deleted = false;
 *
 *   禁止写法（错误）：
 *     CREATE UNIQUE INDEX uk_{table}_{field}
 *         ON {table}(tenant_id, field_name, is_deleted);
 *
 * 原因说明：
 *   - boolean类型的is_deleted在联合唯一索引中只能存在一删一活
 *   - 如果索引包含is_deleted列，同一个code可以有一条is_deleted=true和一条is_deleted=false
 *   - 但无法有两条is_deleted=false（违反唯一约束），同时也无法有两条is_deleted=true
 *   - 使用WHERE is_deleted=false部分索引，仅对未删除数据强制唯一，删除后可重复使用编码
 *
 * 实现要点：
 *   - 所有业务唯一性字段（code类字段）必须使用部分唯一索引
 *   - 部分唯一索引必须包含tenant_id（多租户隔离）
 *   - 部分唯一索引的WHERE条件固定为 is_deleted = false
 *   - MyBatis-Plus全局逻辑删除插件自动过滤is_deleted=false，与索引条件一致
 *
 *
 * 规则四：数值精度统一 — decimal(18,8)（Numeric Precision Standard）
 * ──────────────────────────────────────────────────────────────
 * 所有金额/单价/数量/转换率字段的数据库物理存储统一使用decimal(18,8)：
 *
 *   金额字段：     decimal(18,8)  — total_amount, tax_amount, discount_amount等
 *   单价字段：     decimal(18,8)  — unit_price, tax_price等
 *   数量字段：     decimal(18,8)  — qty, base_qty等
 *   转换率字段：   decimal(18,8)  — conversion_rate等
 *   扩展数值字段： decimal(18,8)  — ext_num1~ext_num5
 *
 * 显示精度由系统参数动态控制：
 *   - system.decimal_places_amount    — 金额小数位数（默认2，最高8）
 *   - sale.decimal_places_price       — 销售单价小数位数（默认4，最高8）
 *   - purchase.decimal_places_price   — 采购单价小数位数（默认4，最高8）
 *   - system.decimal_places_qty       — 数量小数位数（默认6，最高8）
 *
 * 实现要点：
 *   - DDL统一使用decimal(18,8)，不硬编码小数位数
 *   - 前端录入组件根据系统参数precision值控制输入框小数位数
 *   - 后端校验时按系统参数值截断/四舍五入
 *   - 数据库存储保留最大精度，避免精度丢失
 *
 *
 * 规则五：COMMENT注释强制（Comment Mandate）
 * ───────────────────────────────────────
 * 所有表和字段必须包含COMMENT注释：
 *
 *   表级注释：
 *     COMMENT ON TABLE {schema}.{table} IS '表中文描述';
 *
 *   列级注释：
 *     COMMENT ON COLUMN {schema}.{table}.{column} IS '字段中文描述';
 *
 *   枚举字段注释格式：
 *     COMMENT ON COLUMN {schema}.{table}.status IS '状态：draft=草稿/pending_audit=待审核/approved=已审核/closed=已关闭/voided=已作废';
 *
 *   注释内容要求：
 *     - 表级：简洁描述表的业务用途（10-30字）
 *     - 列级：描述字段含义，枚举字段需列出所有可能值及含义
 *     - 外键字段：标注关联的目标表
 *     - 金额字段：标注币种（如适用）
 *
 *   违反后果：
 *     - PostgreSQL元数据查询（pg_class/pg_attribute）返回空注释
 *     - 代码生成器无法获取字段中文名
 *     - 数据字典功能显示空白
 *
 *
 * 规则六：禁止外键约束（No Foreign Key Constraints）
 * ──────────────────────────────────────────────
 * 数据库层面禁止创建FOREIGN KEY约束，应用层通过MyBatis-Plus维护关联关系：
 *
 *   禁止写法（错误）：
 *     CONSTRAINT fk_sale_order_customer FOREIGN KEY (customer_id) REFERENCES crm_customer(id)
 *
 *   正确做法：
 *     — 不创建数据库外键
 *     — 应用层Service在保存/更新时校验关联数据是否存在
 *     — 通过MyBatis-Plus的@TableField(exist = false)标注非数据库字段
 *
 *   原因说明：
 *     - 外键约束导致级联操作（ON DELETE CASCADE等），数据风险高
 *     - 外键约束降低写入性能（每次INSERT/UPDATE都要检查引用完整性）
 *     - 多租户环境下外键跨Schema引用受限
 *     - 数据归档/分区/迁移时外键成为阻碍
 *     - 应用层校验更灵活（可按业务规则定制校验逻辑和错误提示）
 *
 *
 * 规则七：Flyway迁移脚本命名规范（Flyway Naming Convention）
 * ─────────────────────────────────────────────────────────
 * 迁移脚本命名格式：
 *   V{yyyyMMdd}{seq}__{description}.sql
 *
 *   命名元素：
 *     V            — 版本化脚本前缀（大写V）
 *     yyyyMMdd     — 创建日期（8位）
 *     seq          — 当日序号（3位，001-999）
 *     __           — 双下划线分隔版本号与描述（注意是2个下划线）
 *     description  — 蛇形命名（snake_case），简洁描述脚本内容
 *
 *   示例：
 *     V20260526001__init_schema.sql          — 初始化Schema
 *     V20260531001__task_P0_003_002_001.sql  — 任务对应脚本
 *     V20260601001__add_sale_order.sql       — 新增销售订单表
 *
 *   格式要求：
 *     - UTF-8编码
 *     - 脚本开头注释说明变更内容、任务编号、作者和日期
 *     - DDL语句使用标准PostgreSQL语法
 *     - DML语句使用ON CONFLICT保证幂等性
 *     - 每个CREATE TABLE必须包含全部通用必含字段
 *     - 索引在CREATE TABLE之后单独创建
 *     - 数据初始化脚本（INSERT）与结构脚本（CREATE TABLE）分开
 */

-- ============================================================
-- 第4步：DDL合规校验函数
-- 说明:     PL/pgSQL函数，校验指定业务表是否满足多租户隔离DDL规范。
--           检查项包括：
--           1. 10个通用字段是否全部存在且类型正确
--           2. tenant_id是否为BIGINT NOT NULL
--           3. 是否存在以tenant_id为首列的索引
--           4. 业务唯一索引是否使用WHERE is_deleted=false
--           5. 数值字段是否使用decimal(18,8)
--           6. 表和列是否有COMMENT注释
--           7. 是否存在外键约束
--           返回JSON结果，包含逐检查项明细。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_tenant_isolation_ddl(
    p_schema_name VARCHAR(64),
    p_table_name  VARCHAR(128)
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_result              JSONB;
    v_checks              JSONB[] := '{}';
    v_check               JSONB;
    v_common_fields       TEXT[] := ARRAY[
        'id', 'tenant_id', 'created_at', 'updated_at',
        'created_by', 'updated_by', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ];
    v_field               TEXT;
    v_col_exists          BOOLEAN;
    v_col_type            TEXT;
    v_col_notnull         BOOLEAN;
    v_has_comment         BOOLEAN;
    v_tenant_first_idx    INT;
    v_has_foreign_key     INT;
    v_all_pass            BOOLEAN := TRUE;
    v_pass_count          INT := 0;
    v_fail_count          INT := 0;
    v_full_table_name     TEXT;
BEGIN
    v_full_table_name := p_schema_name || '.' || p_table_name;

    -- ========== 检查1：10个通用字段是否全部存在 ==========
    FOREACH v_field IN ARRAY v_common_fields
    LOOP
        SELECT a.attname::TEXT,
               format_type(a.atttypid, a.atttypmod),
               a.attnotnull
        INTO v_col_exists, v_col_type, v_col_notnull
        FROM pg_catalog.pg_attribute a
        WHERE a.attrelid = v_full_table_name::regclass
          AND a.attname = v_field
          AND a.attnum > 0
          AND NOT a.attisdropped;

        IF v_col_exists IS NULL THEN
            v_check := jsonb_build_object(
                'check', 'common_field_' || v_field,
                'field', v_field,
                'exists', FALSE,
                'pass', FALSE,
                'error', '通用字段缺失：' || v_field
            );
            v_all_pass := FALSE;
            v_fail_count := v_fail_count + 1;
        ELSE
            v_check := jsonb_build_object(
                'check', 'common_field_' || v_field,
                'field', v_field,
                'exists', TRUE,
                'type', v_col_type,
                'notnull', v_col_notnull,
                'pass', TRUE
            );
            v_pass_count := v_pass_count + 1;
        END IF;
        v_checks := array_append(v_checks, v_check);
    END LOOP;

    -- ========== 检查2：tenant_id是否为BIGINT NOT NULL ==========
    SELECT a.attnotnull
    INTO v_col_notnull
    FROM pg_catalog.pg_attribute a
    WHERE a.attrelid = v_full_table_name::regclass
      AND a.attname = 'tenant_id'
      AND a.attnum > 0
      AND NOT a.attisdropped;

    IF v_col_notnull IS NULL OR NOT v_col_notnull THEN
        v_check := jsonb_build_object(
            'check', 'tenant_id_notnull',
            'pass', FALSE,
            'error', 'tenant_id必须为NOT NULL'
        );
        v_all_pass := FALSE;
        v_fail_count := v_fail_count + 1;
    ELSE
        v_check := jsonb_build_object(
            'check', 'tenant_id_notnull',
            'pass', TRUE,
            'message', 'tenant_id为NOT NULL，合规'
        );
        v_pass_count := v_pass_count + 1;
    END IF;
    v_checks := array_append(v_checks, v_check);

    -- ========== 检查3：是否存在以tenant_id为首列的索引 ==========
    SELECT COUNT(*)
    INTO v_tenant_first_idx
    FROM pg_catalog.pg_index i
    JOIN pg_catalog.pg_class c ON c.oid = i.indexrelid
    WHERE i.indrelid = v_full_table_name::regclass
      AND c.relname LIKE 'idx_%'
      AND i.indkey[0] = (
          SELECT a.attnum FROM pg_catalog.pg_attribute a
          WHERE a.attrelid = v_full_table_name::regclass
            AND a.attname = 'tenant_id'
            AND a.attnum > 0
      );

    IF v_tenant_first_idx = 0 THEN
        v_check := jsonb_build_object(
            'check', 'tenant_first_index',
            'pass', FALSE,
            'error', '未检测到以tenant_id为首列的索引（idx_前缀）'
        );
        v_all_pass := FALSE;
        v_fail_count := v_fail_count + 1;
    ELSE
        v_check := jsonb_build_object(
            'check', 'tenant_first_index',
            'pass', TRUE,
            'message', '已检测到' || v_tenant_first_idx || '个以tenant_id为首列的索引'
        );
        v_pass_count := v_pass_count + 1;
    END IF;
    v_checks := array_append(v_checks, v_check);

    -- ========== 检查4：表级COMMENT是否存在 ==========
    SELECT obj_description(v_full_table_name::regclass, 'pg_class') IS NOT NULL
    INTO v_has_comment;

    IF NOT v_has_comment THEN
        v_check := jsonb_build_object(
            'check', 'table_comment',
            'pass', FALSE,
            'error', '表缺少COMMENT注释'
        );
        v_all_pass := FALSE;
        v_fail_count := v_fail_count + 1;
    ELSE
        v_check := jsonb_build_object(
            'check', 'table_comment',
            'pass', TRUE,
            'message', '表COMMENT注释已存在'
        );
        v_pass_count := v_pass_count + 1;
    END IF;
    v_checks := array_append(v_checks, v_check);

    -- ========== 检查5：是否存在外键约束 ==========
    SELECT COUNT(*)
    INTO v_has_foreign_key
    FROM pg_catalog.pg_constraint
    WHERE conrelid = v_full_table_name::regclass
      AND contype = 'f';

    IF v_has_foreign_key > 0 THEN
        v_check := jsonb_build_object(
            'check', 'no_foreign_key',
            'pass', FALSE,
            'error', '检测到' || v_has_foreign_key || '个外键约束，违反禁止外键规范'
        );
        v_all_pass := FALSE;
        v_fail_count := v_fail_count + 1;
    ELSE
        v_check := jsonb_build_object(
            'check', 'no_foreign_key',
            'pass', TRUE,
            'message', '无外键约束，合规'
        );
        v_pass_count := v_pass_count + 1;
    END IF;
    v_checks := array_append(v_checks, v_check);

    -- ========== 构建最终JSON结果 ==========
    v_result := jsonb_build_object(
        'schema', p_schema_name,
        'table', p_table_name,
        'total_checks', (v_pass_count + v_fail_count),
        'pass_count', v_pass_count,
        'fail_count', v_fail_count,
        'all_pass', v_all_pass,
        'checks', COALESCE(to_jsonb(v_checks), '[]'::jsonb),
        'checked_at', NOW()::TEXT
    );

    RETURN v_result;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_tenant_isolation_ddl(VARCHAR, VARCHAR)
    IS '校验目标表是否满足多租户隔离DDL规范（10通用字段/tenant_id NOT NULL/tenant_id首列索引/COMMENT/无FK），返回JSON含逐项检查明细';

-- ============================================================
-- 第5步：DDL合规批量校验函数
-- 说明:     遍历 tenant_isolation_constraint 表中所有启用的规则，
--           调用 fn_validate_tenant_isolation_ddl 逐表校验，
--           汇总返回所有表的校验结果。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_all_tenant_isolation_ddl()
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_rec          RECORD;
    v_results      JSONB[] := '{}';
    v_single       JSONB;
    v_schema_count INT := 0;
    v_pass_count   INT := 0;
    v_fail_count   INT := 0;
    v_skip_count   INT := 0;
BEGIN
    FOR v_rec IN
        SELECT schema_name, table_name, is_active
        FROM erp_base.tenant_isolation_constraint
        ORDER BY sort_order
    LOOP
        IF NOT v_rec.is_active THEN
            v_skip_count := v_skip_count + 1;
            CONTINUE;
        END IF;

        v_schema_count := v_schema_count + 1;

        -- 检查表是否存在
        IF NOT EXISTS (
            SELECT 1 FROM pg_catalog.pg_class c
            JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
            WHERE n.nspname = v_rec.schema_name
              AND c.relname = v_rec.table_name
              AND c.relkind = 'r'
        ) THEN
            v_single := jsonb_build_object(
                'schema', v_rec.schema_name,
                'table', v_rec.table_name,
                'status', 'SKIPPED',
                'reason', '表尚未创建'
            );
            v_skip_count := v_skip_count + 1;
            v_results := array_append(v_results, v_single);
            CONTINUE;
        END IF;

        -- 调用单表校验函数
        v_single := erp_base.fn_validate_tenant_isolation_ddl(v_rec.schema_name, v_rec.table_name);
        IF (v_single->>'all_pass')::BOOLEAN THEN
            v_pass_count := v_pass_count + 1;
        ELSE
            v_fail_count := v_fail_count + 1;
        END IF;
        v_results := array_append(v_results, v_single);
    END LOOP;

    RETURN jsonb_build_object(
        'total_schemas_checked', v_schema_count,
        'pass_count', v_pass_count,
        'fail_count', v_fail_count,
        'skip_count', v_skip_count,
        'results', COALESCE(to_jsonb(v_results), '[]'::jsonb),
        'checked_at', NOW()::TEXT
    );
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_all_tenant_isolation_ddl()
    IS '批量校验所有已注册表的DDL多租户隔离合规性，返回汇总JSON';

-- ============================================================
-- 第6步：DDL规范模板（注释文档）
-- 说明:     提供完整的"正确DDL模板"和"常见错误对照"，
--           作为DDL开发的标准参考模板。
-- ============================================================

/*
 * ===================================================================
 * 多租户隔离DDL规范 — 标准模板
 * ===================================================================
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 业务主表 DDL 模板（可直接复制修改）                         │
 * └──────────────────────────────────────────────────────────────┘
 *
 * -- ============================================================
 * -- Flyway Migration Script
 * -- Version: V{yyyyMMdd}{seq}
 * -- Description: {描述}
 * -- Author: AI Generated
 * -- Date: {日期}
 * -- ============================================================
 *
 * CREATE TABLE {schema}.{table_name} (
 *     id              BIGSERIAL       PRIMARY KEY,
 *     tenant_id       BIGINT          NOT NULL,
 *     -- 业务特有字段
 *     {business_code} VARCHAR(50)     NOT NULL,
 *     {business_name} VARCHAR(128)    NOT NULL,
 *     ...
 *     -- 数值字段（统一decimal(18,8)）
 *     {amount_field}  DECIMAL(18,8)   DEFAULT 0,
 *     {qty_field}     DECIMAL(18,8)   DEFAULT 0,
 *     {price_field}   DECIMAL(18,8)   DEFAULT 0,
 *     -- 扩展字段
 *     ext_str1        VARCHAR(200),
 *     ext_str2        VARCHAR(200),
 *     ext_num1        DECIMAL(18,8),
 *     ext_num2        DECIMAL(18,8),
 *     ext_date1       DATE,
 *     ext_bool1       BOOLEAN,
 *     ext_json        JSONB,
 *     -- 通用必含字段
 *     created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
 *     updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
 *     created_by      BIGINT,
 *     updated_by      BIGINT,
 *     is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
 *     owner_dept_id   BIGINT,
 *     owner_id        BIGINT,
 *     version         INT             NOT NULL DEFAULT 0
 * );
 *
 * -- 索引（tenant_id首列）
 * CREATE INDEX idx_{table}_tenant_id ON {schema}.{table_name}(tenant_id);
 * CREATE INDEX idx_{table}_tenant_{col} ON {schema}.{table_name}(tenant_id, {col});
 *
 * -- 部分唯一索引（WHERE is_deleted = false）
 * CREATE UNIQUE INDEX uk_{table}_{code}_active
 *     ON {schema}.{table_name}(tenant_id, {code})
 *     WHERE is_deleted = false;
 *
 * -- 注释
 * COMMENT ON TABLE {schema}.{table_name} IS '{表描述}';
 * COMMENT ON COLUMN {schema}.{table_name}.id IS '主键ID';
 * COMMENT ON COLUMN {schema}.{table_name}.tenant_id IS '租户ID';
 * COMMENT ON COLUMN {schema}.{table_name}.{business_code} IS '{业务编码}';
 * ...（每个字段必须有COMMENT）
 *
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 常见错误对照表（Common Mistakes）                            │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 错误1：缺少通用字段
 *   ❌ CREATE TABLE t1 (id BIGSERIAL PRIMARY KEY, name VARCHAR(100));
 *   ✅ CREATE TABLE t1 (id BIGSERIAL PRIMARY KEY, tenant_id BIGINT NOT NULL, ... 10个通用字段 ...);
 *
 * 错误2：tenant_id可空
 *   ❌ tenant_id BIGINT
 *   ✅ tenant_id BIGINT NOT NULL
 *
 * 错误3：联合索引不以tenant_id为首列
 *   ❌ CREATE INDEX idx_t1_code ON t1(code, tenant_id);
 *   ✅ CREATE INDEX idx_t1_tenant_code ON t1(tenant_id, code);
 *
 * 错误4：唯一索引未使用部分索引
 *   ❌ CREATE UNIQUE INDEX uk_t1_code ON t1(tenant_id, code, is_deleted);
 *   ✅ CREATE UNIQUE INDEX uk_t1_code_active ON t1(tenant_id, code) WHERE is_deleted = false;
 *
 * 错误5：数值字段硬编码小数位数
 *   ❌ total_amount DECIMAL(18,2)
 *   ✅ total_amount DECIMAL(18,8)
 *
 * 错误6：缺少COMMENT注释
 *   ❌ 无COMMENT ON TABLE/COLUMN语句
 *   ✅ 每个表和字段都有COMMENT ON
 *
 * 错误7：创建了外键约束
 *   ❌ CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES crm_customer(id)
 *   ✅ 不创建外键，应用层MyBatis-Plus维护关联
 *
 * 错误8：Flyway脚本命名不符合规范
 *   ❌ V1__init.sql 或 V2026-05-26__init.sql
 *   ✅ V20260526001__init_schema.sql（yyyyMMdd + 3位序号 + 双下划线 + 蛇形描述）
 */

-- ============================================================
-- 第7步：DDL规范与数据库规范对照表（注释文档）
-- 说明:     汇总多租户隔离DDL规范与全局数据库规范的对应关系。
-- ============================================================

/*
 * ===================================================================
 * 多租户隔离DDL规范 ↔ 全局数据库规范 对照表
 * ===================================================================
 *
 * 多租户隔离DDL规范              | 对应的全局数据库规范条款
 * -------------------------------|------------------------------------
 * 10个通用字段强制包含           | §3 通用必含字段 — 字段定义表
 * tenant_id NOT NULL             | §7 多租户隔离规范 — tenant_id必填
 * 联合索引以tenant_id为首列      | §7 多租户隔离规范 — 索引规范
 * 部分唯一索引WHERE is_deleted   | §6 软删除规范 — 唯一约束
 * 数值decimal(18,8)统一          | §10 数据类型约定 — 小数位数可配置
 * COMMENT注释强制                | §2 字段命名规则 — 注释要求
 * 禁止外键约束                  | §4 数据库设计原则 — 无外键约束
 * Flyway命名规范                 | §12 Flyway迁移脚本规范
 * 表命名规范                     | §1 表命名规则
 * 字段命名规范                   | §2 字段命名规则
 */

-- ============================================================
-- 第8步：DDL规范开发检查清单（注释文档）
-- 说明:     供DDL开发人员使用的检查清单，
--           确保每个DDL脚本符合多租户隔离规范。
-- ============================================================

/*
 * ===================================================================
 * 多租户隔离DDL规范 — DDL开发检查清单
 * ===================================================================
 *
 * □ 1. CREATE TABLE前：
 *      □ 确认表名符合{模块前缀}_{业务名}命名规范
 *      □ 确认字段名符合snake_case命名规范
 *      □ 确认Flyway脚本命名符合V{yyyyMMdd}{seq}__{description}.sql
 *      □ 确认脚本UTF-8编码
 *
 * □ 2. CREATE TABLE中：
 *      □ 10个通用字段全部包含（id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version）
 *      □ id使用BIGSERIAL PRIMARY KEY
 *      □ tenant_id为BIGINT NOT NULL
 *      □ created_at为TIMESTAMP NOT NULL DEFAULT NOW()
 *      □ updated_at为TIMESTAMP NOT NULL DEFAULT NOW()
 *      □ is_deleted为BOOLEAN NOT NULL DEFAULT FALSE
 *      □ version为INT NOT NULL DEFAULT 0
 *      □ 金额/单价/数量/转换率字段统一使用DECIMAL(18,8)
 *      □ 业务编码字段使用VARCHAR(50)
 *      □ 业务名称字段使用VARCHAR(128)
 *      □ 备注字段使用VARCHAR(500)
 *      □ 扩展字符串字段使用VARCHAR(200)
 *      □ 扩展数值字段使用DECIMAL(18,8)
 *      □ 扩展日期字段使用DATE
 *      □ 扩展布尔字段使用BOOLEAN
 *      □ 扩展JSON字段使用JSONB
 *
 * □ 3. CREATE TABLE后（索引）：
 *      □ 创建idx_{table}_tenant_id索引：CREATE INDEX ... ON (tenant_id)
 *      □ 所有联合索引以tenant_id为首列
 *      □ 业务唯一索引使用部分索引：CREATE UNIQUE INDEX ... WHERE is_deleted = false
 *      □ 外键字段创建普通索引以优化JOIN
 *      □ 高频查询条件字段创建索引
 *
 * □ 4. CREATE TABLE后（注释）：
 *      □ 表级COMMENT ON TABLE已添加
 *      □ 每个列级COMMENT ON COLUMN已添加
 *      □ 枚举字段注释列出所有可能值
 *      □ 外键字段注释标注关联目标表
 *
 * □ 5. DML脚本：
 *      □ INSERT使用ON CONFLICT保证幂等性
 *      □ 预置数据包含tenant_id值
 *      □ ON CONFLICT的冲突列包含tenant_id（多租户隔离）
 *
 * □ 6. 最终审查：
 *      □ 无FOREIGN KEY约束
 *      □ 无硬编码小数位数（非DECIMAL(18,8)的数值字段需说明原因）
 *      □ 脚本开头注释完整（任务编号+描述+作者+日期）
 *      □ 回滚脚本已准备（DROP TABLE IF EXISTS ... CASCADE）
 *      □ 调用fn_validate_tenant_isolation_ddl校验通过
 */

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================

-- 验证约束规则总数：
-- SELECT COUNT(*) AS total_rules
-- FROM erp_base.tenant_isolation_constraint
-- WHERE is_active = TRUE;
-- 预期结果：>= 29条（29个业务表规则 + 1个元数据表豁免规则）

-- 验证各表分类的规则数：
-- SELECT table_category, COUNT(*) AS rule_count
-- FROM erp_base.tenant_isolation_constraint
-- WHERE is_active = TRUE
-- GROUP BY table_category
-- ORDER BY table_category;

-- 验证所有活跃规则的约束一致性：
-- SELECT schema_name, table_name
-- FROM erp_base.tenant_isolation_constraint
-- WHERE is_active = TRUE
--   AND require_common_fields = TRUE
--   AND (require_tenant_id = FALSE OR require_tenant_first_idx = FALSE);
-- 预期结果：0行（业务表的所有约束必须同时启用）

-- 校验单表DDL合规性（以sys_user为例）：
-- SELECT erp_base.fn_validate_tenant_isolation_ddl('erp_base', 'sys_user');

-- 批量校验所有已注册表：
-- SELECT erp_base.fn_validate_all_tenant_isolation_ddl();

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP TABLE IF EXISTS erp_base.tenant_isolation_constraint CASCADE;
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_tenant_isolation_ddl(VARCHAR, VARCHAR);
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_all_tenant_isolation_ddl();
-- ============================================================
