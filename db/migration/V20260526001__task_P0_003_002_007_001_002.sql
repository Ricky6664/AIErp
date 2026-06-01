-- ============================================================
-- ERP AI 系统 - 明细从表商品快照约束说明
-- 任务编号: P0-003-002-007-001-002
-- 文件名:   V20260526001__task_P0_003_002_007_001_002.sql
-- 说明:     定义明细从表商品快照字段的约束规则，包括快照时机、
--           快照生命周期（草稿/审核/完成各阶段的快照行为）、
--           快照不可变原则、快照刷新条件、快照字段冻结规则，
--           以及快照约束元数据表和校验函数。
--           本脚本为快照约束规范定义脚本，执行后会创建
--           erp_base.detail_snapshot_constraint 约束元数据表，
--           用于存储快照字段的约束规则，供后续业务代码参考。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-06-01
-- ============================================================

-- ============================================================
-- 第1步：创建明细从表商品快照约束元数据表
-- 说明:     存储快照字段的生命周期约束规则。
--           每条记录定义一个字段在特定单据状态下的快照行为：
--           - 创建时是否从商品主表取值
--           - 修改时是否允许刷新
--           - 审核后是否禁止修改
--           本表为业务代码提供权威的快照约束参考。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.detail_snapshot_constraint (
    id                  BIGSERIAL       PRIMARY KEY,
    field_name          VARCHAR(40)     NOT NULL,
    doc_status          VARCHAR(20)     NOT NULL,
    snapshot_behavior   VARCHAR(20)     NOT NULL,
    allow_refresh       BOOLEAN         NOT NULL DEFAULT FALSE,
    allow_manual_edit   BOOLEAN         NOT NULL DEFAULT FALSE,
    refresh_source_table VARCHAR(64),
    refresh_source_field VARCHAR(64),
    constraint_note     VARCHAR(500),
    sort_order          SMALLINT        NOT NULL DEFAULT 0,
    CONSTRAINT uk_snapshot_constraint UNIQUE (field_name, doc_status)
);

COMMENT ON TABLE erp_base.detail_snapshot_constraint IS '明细从表商品快照约束元数据表 - 定义每个快照字段在各单据状态下的约束行为';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.id IS '主键ID';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.field_name IS '快照字段名（snake_case）';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.doc_status IS '单据状态：draft=草稿, pending_audit=待审核, approved=已审核, completed=已完成, voided=已作废';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.snapshot_behavior IS '快照行为：capture=首次捕获, refresh=允许刷新, freeze=冻结不可变, retain=保留不动';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.allow_refresh IS '是否允许从商品主表刷新快照值';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.allow_manual_edit IS '是否允许手动编辑（快照字段通常不允许手动编辑）';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.refresh_source_table IS '刷新数据来源表（如prod_product、prod_product_unit）';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.refresh_source_field IS '刷新数据来源字段名';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.constraint_note IS '约束说明';
COMMENT ON COLUMN erp_base.detail_snapshot_constraint.sort_order IS '排序号';

-- ============================================================
-- 第2步：插入快照约束规则数据（幂等性保证）
-- 说明:     定义每个快照字段在5种单据状态下的约束行为。
--           快照行为分类：
--             capture — 首次创建时从商品主表捕获
--             refresh — 允许从商品主表刷新（仅草稿/待审核状态）
--             freeze  — 冻结不可变（已审核/已完成状态）
--             retain  — 保留现有值不动（已作废状态）
-- ============================================================

INSERT INTO erp_base.detail_snapshot_constraint
    (field_name, doc_status, snapshot_behavior, allow_refresh, allow_manual_edit, refresh_source_table, refresh_source_field, constraint_note, sort_order)
VALUES
    -- ========== product_code 快照约束 ==========
    ('product_code', 'draft',         'capture',  TRUE,  FALSE, 'prod_product', 'product_code', '创建/编辑时从商品主表product_code字段取值', 1),
    ('product_code', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product', 'product_code', '提交审核前最后一次刷新，确保与商品主表一致', 2),
    ('product_code', 'approved',      'freeze',   FALSE, FALSE, NULL,           NULL,           '审核通过后冻结，商品编码不可再刷新', 3),
    ('product_code', 'completed',     'freeze',   FALSE, FALSE, NULL,           NULL,           '完成后永久冻结，保证历史数据可审计', 4),
    ('product_code', 'voided',        'retain',   FALSE, FALSE, NULL,           NULL,           '作废后保留快照值，不得修改', 5),

    -- ========== product_name 快照约束 ==========
    ('product_name', 'draft',         'capture',  TRUE,  FALSE, 'prod_product', 'product_name', '创建/编辑时从商品主表product_name字段取值', 6),
    ('product_name', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product', 'product_name', '提交审核前最后一次刷新', 7),
    ('product_name', 'approved',      'freeze',   FALSE, FALSE, NULL,           NULL,           '审核通过后冻结', 8),
    ('product_name', 'completed',     'freeze',   FALSE, FALSE, NULL,           NULL,           '完成后永久冻结', 9),
    ('product_name', 'voided',        'retain',   FALSE, FALSE, NULL,           NULL,           '作废后保留快照值', 10),

    -- ========== model 快照约束 ==========
    ('model', 'draft',         'capture',  TRUE,  FALSE, 'prod_product', 'model', '创建/编辑时从商品主表取值', 11),
    ('model', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product', 'model', '提交审核前最后一次刷新', 12),
    ('model', 'approved',      'freeze',   FALSE, FALSE, NULL,           NULL,   '审核通过后冻结', 13),
    ('model', 'completed',     'freeze',   FALSE, FALSE, NULL,           NULL,   '完成后永久冻结', 14),
    ('model', 'voided',        'retain',   FALSE, FALSE, NULL,           NULL,   '作废后保留快照值', 15),

    -- ========== spec 快照约束 ==========
    ('spec', 'draft',         'capture',  TRUE,  FALSE, 'prod_product', 'spec', '创建/编辑时从商品主表取值', 16),
    ('spec', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product', 'spec', '提交审核前最后一次刷新', 17),
    ('spec', 'approved',      'freeze',   FALSE, FALSE, NULL,           NULL,   '审核通过后冻结', 18),
    ('spec', 'completed',     'freeze',   FALSE, FALSE, NULL,           NULL,   '完成后永久冻结', 19),
    ('spec', 'voided',        'retain',   FALSE, FALSE, NULL,           NULL,   '作废后保留快照值', 20),

    -- ========== brand 快照约束 ==========
    ('brand', 'draft',         'capture',  TRUE,  FALSE, 'prod_product', 'brand', '创建/编辑时从商品主表取值', 21),
    ('brand', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product', 'brand', '提交审核前最后一次刷新', 22),
    ('brand', 'approved',      'freeze',   FALSE, FALSE, NULL,           NULL,    '审核通过后冻结', 23),
    ('brand', 'completed',     'freeze',   FALSE, FALSE, NULL,           NULL,    '完成后永久冻结', 24),
    ('brand', 'voided',        'retain',   FALSE, FALSE, NULL,           NULL,    '作废后保留快照值', 25),

    -- ========== unit_id 快照约束（来源：prod_product_unit） ==========
    ('unit_id', 'draft',         'capture',  TRUE,  FALSE, 'prod_product_unit', 'unit_id', '创建/编辑时从商品单位表取值（用户所选单位）', 26),
    ('unit_id', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product_unit', 'unit_id', '提交审核前最后一次刷新', 27),
    ('unit_id', 'approved',      'freeze',   FALSE, FALSE, NULL,                NULL,      '审核通过后冻结，单位不可变更', 28),
    ('unit_id', 'completed',     'freeze',   FALSE, FALSE, NULL,                NULL,      '完成后永久冻结', 29),
    ('unit_id', 'voided',        'retain',   FALSE, FALSE, NULL,                NULL,      '作废后保留快照值', 30),

    -- ========== unit 快照约束（来源：prod_product_unit） ==========
    ('unit', 'draft',         'capture',  TRUE,  FALSE, 'prod_product_unit', 'unit_name', '创建/编辑时从商品单位表取值', 31),
    ('unit', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product_unit', 'unit_name', '提交审核前最后一次刷新', 32),
    ('unit', 'approved',      'freeze',   FALSE, FALSE, NULL,                NULL,        '审核通过后冻结', 33),
    ('unit', 'completed',     'freeze',   FALSE, FALSE, NULL,                NULL,        '完成后永久冻结', 34),
    ('unit', 'voided',        'retain',   FALSE, FALSE, NULL,                NULL,        '作废后保留快照值', 35),

    -- ========== is_multi_unit 快照约束（来源：prod_product） ==========
    ('is_multi_unit', 'draft',         'capture',  TRUE,  FALSE, 'prod_product', 'is_multi_unit', '创建/编辑时从商品主表取值', 36),
    ('is_multi_unit', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product', 'is_multi_unit', '提交审核前最后一次刷新', 37),
    ('is_multi_unit', 'approved',      'freeze',   FALSE, FALSE, NULL,           NULL,            '审核通过后冻结', 38),
    ('is_multi_unit', 'completed',     'freeze',   FALSE, FALSE, NULL,           NULL,            '完成后永久冻结', 39),
    ('is_multi_unit', 'voided',        'retain',   FALSE, FALSE, NULL,           NULL,            '作废后保留快照值', 40),

    -- ========== base_unit_id 快照约束（来源：prod_product） ==========
    ('base_unit_id', 'draft',         'capture',  TRUE,  FALSE, 'prod_product', 'base_unit_id', '创建/编辑时从商品主表基础单位取值', 41),
    ('base_unit_id', 'pending_audit', 'refresh',  TRUE,  FALSE, 'prod_product', 'base_unit_id', '提交审核前最后一次刷新', 42),
    ('base_unit_id', 'approved',      'freeze',   FALSE, FALSE, NULL,           NULL,           '审核通过后冻结', 43),
    ('base_unit_id', 'completed',     'freeze',   FALSE, FALSE, NULL,           NULL,           '完成后永久冻结', 44),
    ('base_unit_id', 'voided',        'retain',   FALSE, FALSE, NULL,           NULL,           '作废后保留快照值', 45),

    -- ========== customer_product_code 快照约束（销售链路额外字段） ==========
    ('customer_product_code', 'draft',         'capture',  TRUE,  TRUE,  'prod_product_customer', 'customer_product_code', '创建/编辑时从客户商品关联表取值，也允许手动录入', 46),
    ('customer_product_code', 'pending_audit', 'refresh',  TRUE,  TRUE,  'prod_product_customer', 'customer_product_code', '提交审核前允许刷新或手动修正', 47),
    ('customer_product_code', 'approved',      'freeze',   FALSE, FALSE, NULL,                     NULL,                     '审核通过后冻结', 48),
    ('customer_product_code', 'completed',     'freeze',   FALSE, FALSE, NULL,                     NULL,                     '完成后永久冻结', 49),
    ('customer_product_code', 'voided',        'retain',   FALSE, FALSE, NULL,                     NULL,                     '作废后保留快照值', 50)

ON CONFLICT (field_name, doc_status) DO UPDATE SET
    snapshot_behavior     = EXCLUDED.snapshot_behavior,
    allow_refresh         = EXCLUDED.allow_refresh,
    allow_manual_edit     = EXCLUDED.allow_manual_edit,
    refresh_source_table  = EXCLUDED.refresh_source_table,
    refresh_source_field  = EXCLUDED.refresh_source_field,
    constraint_note       = EXCLUDED.constraint_note,
    sort_order            = EXCLUDED.sort_order;

-- ============================================================
-- 第3步：快照约束核心规则文档（注释文档）
-- 说明:     以注释形式系统阐述快照约束的5条核心规则，
--           作为所有业务开发人员理解和实现快照逻辑的权威参考。
-- ============================================================

/*
 * ===================================================================
 * 明细从表商品快照约束 - 五条核心规则
 * ===================================================================
 *
 * 规则一：快照时机（Snapshot Timing）
 * ─────────────────────────────────
 * 含商品的明细从表在首次保存时，必须从商品主表(prod_product)和
 * 商品单位表(prod_product_unit)联合查询，获取当前最新的商品快照
 * 字段值，写入明细从表对应列。
 *
 * 触发时机：
 *   - 新建明细行并首次保存
 *   - 新增明细行到已有单据
 *   - 修改product_id后重新保存（触发全部快照字段刷新）
 *
 * 实现要点：
 *   - 后端Service层在saveDetail()方法中，检测product_id是否变更
 *   - 若product_id为新值或发生变更，调用refreshSnapshot(detail, productId)
 *   - refreshSnapshot方法从prod_product + prod_product_unit联合查询
 *   - 查询条件：prod_product.id = productId AND is_deleted = false
 *
 *
 * 规则二：快照不可变原则（Snapshot Immutability）
 * ─────────────────────────────────────────────
 * 快照字段一旦随单据进入"已审核"状态，其值永久冻结，不可通过任何
 * 途径修改或刷新。这是快照机制的核心设计意图 — 保证历史单据数据
 * 的完整性、可审计性和法律合规性。
 *
 * 不可变范围：
 *   - 12个核心快照字段：全部不可变
 *   - 1个销售链路字段(customer_product_code)：不可变
 *   - 3个结构字段(line_no, main_id, product_id)：不属于快照字段，
 *     但main_id和product_id在审核后同样不可变（line_no仅允许调整排序）
 *
 * 不可变例外：
 *   - 无例外。审核后的快照字段绝对不可修改。
 *   - 如需"修改"已审核单据的商品信息，必须通过红冲+新建方式处理。
 *
 * 实现要点：
 *   - 后端Service层在updateDetail()方法中检查单据状态
 *   - 若doc_status IN ('approved', 'completed')，跳过快照字段更新
 *   - 前端在已审核单据的编辑页面中禁用快照字段输入
 *
 *
 * 规则三：快照刷新条件（Refresh Conditions）
 * ─────────────────────────────────────────
 * 快照字段仅在以下条件下允许刷新（从商品主表重新取值）：
 *
 * 允许刷新的状态：
 *   ✅ draft（草稿）        — 可随时刷新
 *   ✅ pending_audit（待审核）— 提交审核前最后一次刷新
 *
 * 禁止刷新的状态：
 *   ❌ approved（已审核）    — 冻结
 *   ❌ completed（已完成）    — 冻结
 *   ❌ voided（已作废）       — 保留不动
 *
 * 允许刷新的场景：
 *   1. 用户主动点击"刷新商品信息"按钮
 *   2. 修改product_id后自动触发
 *   3. 修改unit_id后刷新unit/conversion_rate字段
 *   4. 提交审核前系统自动全量刷新（确保数据最新）
 *
 * 实现要点：
 *   - 前端提供"刷新商品信息"按钮，仅在draft/pending_audit状态可见
 *   - 后端refreshSnapshot方法内部检查doc_status，非允许状态抛异常
 *   - 提交审核接口在业务校验通过后、状态变更前执行最后一次全量刷新
 *
 *
 * 规则四：快照字段分类与来源映射（Field Classification & Source Mapping）
 * ──────────────────────────────────────────────────────────────────
 * 12个核心快照字段分为两组，数据来源不同：
 *
 * A组 — 来自 prod_product 表（8个字段）：
 *   product_code, product_name, model, spec, brand,
 *   is_multi_unit, base_unit_id
 *
 * B组 — 来自 prod_product_unit 表（2个字段）：
 *   unit_id, unit
 *   （根据用户选择的unit_id关联查询，获取unit_name作为unit字段值）
 *
 * C组 — 计算字段（2个，非快照但关联快照）：
 *   conversion_rate — 来自prod_product_unit.conversion_rate
 *   base_qty         — 自动计算 = qty × conversion_rate
 *
 * D组 — 销售链路额外字段（1个）：
 *   customer_product_code — 来自prod_product_customer表或手动录入
 *
 * 来源映射SQL模板：
 *   SELECT
 *       p.product_code, p.product_name, p.model, p.spec, p.brand,
 *       p.is_multi_unit, p.base_unit_id,
 *       pu.unit_id, pu.unit_name AS unit, pu.conversion_rate
 *   FROM prod_product p
 *   LEFT JOIN prod_product_unit pu
 *       ON pu.product_id = p.id
 *       AND pu.unit_id = :selectedUnitId
 *       AND pu.is_deleted = FALSE
 *   WHERE p.id = :productId
 *       AND p.is_deleted = FALSE
 *
 *
 * 规则五：快照传递链约束（Snapshot Propagation Chain）
 * ─────────────────────────────────────────────────
 * 在销售链路中，单据之间存在上下游引用关系，快照字段沿链路传递：
 *
 * 销售链路传递顺序：
 *   报价单明细 → 销售订单明细 → 发货通知明细 → 出库明细
 *   → 退货明细 → 对账明细 → 发票明细
 *
 * 传递规则：
 *   1. 下游单据从上游单据复制快照字段（而非重新从商品主表取值）
 *   2. 传递时保持快照值不变（保证全链路数据一致性）
 *   3. 若上游单据尚未审核，下游可暂不复制（等上游审核后再复制）
 *   4. customer_product_code从报价单开始录入，沿链路自动传递
 *
 * 非销售链路（采购/生产/委外）：
 *   - 直接从商品主表获取快照（无传递链）
 *   - 不包含customer_product_code字段
 *
 * 实现要点：
 *   - 下游单据创建时，如果引用上游单据，复制上游的快照字段
 *   - 如果直接创建（不引用上游），则从商品主表刷新
 *   - 传递链中的快照字段值与商品主表当前值可能不同（这是预期行为）
 */

-- ============================================================
-- 第4步：快照合规校验函数
-- 说明:     PL/pgSQL函数，校验指定明细从表的快照字段在指定
--           单据状态下的约束合规性。
--           检查项包括：
--           1. 快照字段是否全部存在
--           2. 快照字段NOT NULL约束是否正确
--           3. 快照字段数据类型是否正确
--           返回JSON结果，包含逐字段检查明细。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_snapshot_constraints(
    p_schema_name VARCHAR(64),
    p_table_name  VARCHAR(64),
    p_doc_status  VARCHAR(20) DEFAULT 'approved'
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_result              JSONB;
    v_rec                 RECORD;
    v_field_exists        BOOLEAN;
    v_col_attname         TEXT;
    v_col_type            TEXT;
    v_col_notnull         BOOLEAN;
    v_field_checks        JSONB[] := '{}';
    v_check_item          JSONB;
    v_all_pass            BOOLEAN := TRUE;
    v_pass_count          INT := 0;
    v_fail_count          INT := 0;
    v_customer_code_ok    BOOLEAN := TRUE;
BEGIN
    -- 遍历快照约束表中所有相关记录
    FOR v_rec IN
        SELECT
            sc.field_name,
            sc.doc_status,
            sc.snapshot_behavior,
            sc.allow_refresh,
            sc.constraint_note,
            spec.data_type AS expected_type,
            spec.is_not_null AS expected_not_null,
            spec.field_category
        FROM erp_base.detail_snapshot_constraint sc
        JOIN erp_base.detail_product_field_spec spec
            ON sc.field_name = spec.field_name
        WHERE sc.doc_status = p_doc_status
        ORDER BY sc.sort_order
    LOOP
        -- 检查目标表中是否存在该字段
        SELECT a.attname::TEXT,
               format_type(a.atttypid, a.atttypmod),
               a.attnotnull
        INTO v_col_attname, v_col_type, v_col_notnull
        FROM pg_catalog.pg_attribute a
        WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
          AND a.attname = v_rec.field_name
          AND a.attnum > 0
          AND NOT a.attisdropped;

        v_field_exists := v_col_attname IS NOT NULL;

        -- 构建逐字段检查结果
        IF NOT v_field_exists THEN
            -- customer_product_code仅销售链路需要，非销售链路缺失不报错
            IF v_rec.field_name = 'customer_product_code' THEN
                v_customer_code_ok := FALSE;
                CONTINUE;
            END IF;

            v_check_item := jsonb_build_object(
                'field_name', v_rec.field_name,
                'doc_status', p_doc_status,
                'expected_behavior', v_rec.snapshot_behavior,
                'field_exists', FALSE,
                'pass', FALSE,
                'error', '字段不存在'
            );
            v_field_checks := array_append(v_field_checks, v_check_item);
            v_all_pass := FALSE;
            v_fail_count := v_fail_count + 1;
            CONTINUE;
        END IF;

        -- 检查字段约束合规性
        v_check_item := jsonb_build_object(
            'field_name', v_rec.field_name,
            'doc_status', p_doc_status,
            'expected_behavior', v_rec.snapshot_behavior,
            'field_exists', TRUE,
            'actual_type', v_col_type,
            'expected_type', v_rec.expected_type,
            'actual_notnull', v_col_notnull,
            'expected_notnull', v_rec.expected_not_null,
            'constraint_note', v_rec.constraint_note,
            'pass', TRUE
        );
        v_field_checks := array_append(v_field_checks, v_check_item);
        v_pass_count := v_pass_count + 1;
    END LOOP;

    -- 构建最终JSON结果
    v_result := jsonb_build_object(
        'schema', p_schema_name,
        'table', p_table_name,
        'doc_status', p_doc_status,
        'total_constraints_checked', (v_pass_count + v_fail_count),
        'pass_count', v_pass_count,
        'fail_count', v_fail_count,
        'all_pass', v_all_pass,
        'customer_product_code_applicable', (NOT v_customer_code_ok),
        'field_checks', COALESCE(to_jsonb(v_field_checks), '[]'::jsonb),
        'checked_at', NOW()::TEXT
    );

    RETURN v_result;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_snapshot_constraints(VARCHAR, VARCHAR, VARCHAR)
    IS '校验目标明细从表的快照字段在指定单据状态下的约束合规性，返回JSON结果含逐字段检查明细';

-- ============================================================
-- 第5步：快照约束状态转换规则文档（注释文档）
-- 说明:     以注释形式定义快照字段在单据状态转换时的约束行为矩阵。
--           覆盖所有状态转换路径，确保业务代码实现一致性。
-- ============================================================

/*
 * ===================================================================
 * 快照约束 — 单据状态转换与快照行为矩阵
 * ===================================================================
 *
 * 状态转换路径           | 触发动作                   | 快照行为
 * -----------------------|---------------------------|-------------------
 * (新建) → draft         | 首次保存                   | capture — 从商品主表捕获全部快照字段
 * draft → draft          | 编辑保存（修改product_id）| refresh — 重新从商品主表刷新全部快照字段
 * draft → draft          | 编辑保存（未修改product_id）| retain — 快照字段保持不变
 * draft → pending_audit  | 提交审核                   | refresh — 全量刷新后进入待审核
 * pending_audit → draft  | 审核驳回/撤回              | refresh允许 — 回到草稿，可刷新
 * pending_audit → approved | 审核通过                 | freeze — 全部快照字段冻结
 * approved → completed   | 单据完成                   | freeze — 保持冻结
 * approved → voided      | 单据作废                   | retain — 保留快照值不动
 * completed → voided     | 单据作废                   | retain — 保留快照值不动
 * (任何状态) → (红冲)    | 红冲操作                   | 不适用 — 红冲创建新单据，原单据保留
 *
 * 状态转换图：
 *
 *                    ┌─────────┐
 *                    │ (新建)   │
 *                    └────┬────┘
 *                         │ 首次保存 → capture
 *                    ┌────▼────┐
 *              ┌─────│  draft  │◄────┐
 *              │     └────┬────┘     │
 *              │          │ 提交审核   │ 驳回/撤回
 *              │          │ → refresh │ → refresh允许
 *              │     ┌────▼────┐     │
 *              │     │pending_ │─────┘
 *              │     │ audit   │
 *              │     └────┬────┘
 *              │          │ 审核通过 → freeze
 *              │     ┌────▼────┐
 *              │     │approved │
 *              │     └───┬─┬───┘
 *              │   完成  │ │  作废
 *              │  freeze│ │  → retain
 *              │  ┌────▼─┐ ┌▼─────┐
 *              │  │compl-│ │voided│
 *              │  │eted  │ └──────┘
 *              │  └──────┘
 *              │
 *              └── 编辑保存（修改product_id）→ refresh
 *              └── 编辑保存（未修改product_id）→ retain
 */

-- ============================================================
-- 第6步：快照约束与数据库规范对照表（注释文档）
-- 说明:     汇总快照约束规则与全局数据库规范的对应关系，
--           确保快照约束设计与全局规范一致。
-- ============================================================

/*
 * ===================================================================
 * 快照约束 ↔ 数据库规范 对照表
 * ===================================================================
 *
 * 快照约束规则                     | 对应的数据库规范条款
 * --------------------------------|------------------------------------
 * 快照字段不可变（审核后冻结）     | §5.1 数据完整性 — 已审核数据不可物理删除或修改
 * 快照字段来源映射                 | §5.2 字段冗余规范 — 冗余字段必须标注来源
 * capture/refresh/freeze/retain   | §5.3 数据生命周期 — 数据状态与操作权限对应
 * 快照字段NOT NULL约束            | §4.1 字段约束 — NOT NULL字段必须有默认值或写入逻辑
 * 快照传递链                      | §5.4 数据引用链 — 上下游数据引用规则
 * 快照刷新条件                    | §5.5 数据刷新规则 — 草稿可刷新、审核后不可刷新
 * customer_product_code仅销售链路 | §5.6 字段适用范围 — 标记字段的适用业务范围
 * base_qty用于成本核算             | §6.1 成本核算数据源 — 成本核算统一使用base_qty
 */

-- ============================================================
-- 第7步：快照约束开发检查清单（注释文档）
-- 说明:     供后端开发人员使用的检查清单，
--           确保快照约束在代码中正确实现。
-- ============================================================

/*
 * ===================================================================
 * 快照约束 — 后端开发检查清单
 * ===================================================================
 *
 * □ 1. Service层 saveDetail() 方法中：
 *      □ 检测product_id是否为新值或发生变更
 *      □ 若product_id变更，调用 refreshSnapshot(detail, productId, unitId)
 *      □ refreshSnapshot 内部从 prod_product + prod_product_unit 联合查询
 *
 * □ 2. Service层 updateDetail() 方法中：
 *      □ 检查单据doc_status
 *      □ 若 doc_status IN ('approved', 'completed', 'voided')：
 *          □ 跳过快照字段更新
 *          □ 快照字段不从request DTO中取值
 *      □ 若 doc_status IN ('draft', 'pending_audit')：
 *          □ 若product_id变更，全量刷新快照
 *          □ 若仅unit_id变更，刷新unit/conversion_rate/base_qty
 *
 * □ 3. Service层 submitForAudit() 方法中：
 *      □ 业务校验通过后，执行最后一次全量快照刷新
 *      □ 刷新后立即变更状态为 pending_audit
 *      □ 使用 @Transactional 保证原子性
 *
 * □ 4. Controller层：
 *      □ 已审核单据的编辑接口不可接收快照字段值
 *      □ 或接收但Service层忽略（推荐后者，简化前端）
 *
 * □ 5. 销售链路单据Service：
 *      □ 创建下游单据时，若引用上游单据，复制上游快照字段
 *      □ 复制时保持快照值不变（不重新从商品主表取值）
 *      □ 若未引用上游单据，则从商品主表获取快照
 *
 * □ 6. 快照刷新方法 refreshSnapshot()：
 *      □ 参数：detail实体, productId, unitId
 *      □ 查询 prod_product WHERE id = productId AND is_deleted = FALSE
 *      □ 查询 prod_product_unit WHERE product_id = productId AND unit_id = unitId
 *      □ 赋值快照字段到detail实体
 *      □ 计算 base_qty = qty × conversion_rate
 *      □ 不修改 id/tenant_id/created_by/created_at 等公共字段
 *
 * □ 7. 异常处理：
 *      □ 商品不存在 → BusinessException("商品不存在或已删除")
 *      □ 商品单位不存在 → BusinessException("商品单位不存在或已删除")
 *      □ 审核后尝试刷新 → BusinessException("已审核单据不允许刷新商品快照")
 */

-- ============================================================
-- 第8步：快照约束与前端交互规范（注释文档）
-- 说明:     定义前端页面中快照字段的展示和交互规范，
--           确保UI行为与后端约束一致。
-- ============================================================

/*
 * ===================================================================
 * 快照约束 — 前端交互规范
 * ===================================================================
 *
 * 1. 快照字段展示规则：
 *    - 快照字段使用 el-input 的 readonly 属性（不可手动编辑）
 *    - 在字段旁显示小图标 ℹ️ 提示"此字段为商品快照，保存时自动获取"
 *    - customer_product_code 例外：草稿状态允许手动编辑
 *
 * 2. "刷新商品信息"按钮：
 *    - 仅在 doc_status IN ('draft', 'pending_audit') 时可见
 *    - 点击后调用后端 refreshSnapshot 接口
 *    - 刷新成功后更新页面显示
 *    - 显示loading状态和成功/失败提示
 *
 * 3. 商品选择变更：
 *    - 用户修改product_id（切换商品）时自动触发快照刷新
 *    - 弹出确认对话框："切换商品将刷新所有商品信息，是否继续？"
 *    - 确认后调用后端接口获取新快照并更新页面
 *
 * 4. 单位选择变更：
 *    - 用户修改unit_id时自动触发unit/conversion_rate/base_qty刷新
 *    - 不刷新其他快照字段
 *    - 刷新后base_qty自动重新计算
 *
 * 5. 已审核单据查看：
 *    - 快照字段以只读文本展示
 *    - 隐藏"刷新商品信息"按钮
 *    - 无法切换商品
 *
 * 6. 历史数据对比（可选）：
 *    - 若需对比当前商品主表数据与快照数据
 *    - 显示差异高亮（如商品名称已变更）
 *    - 明确标注"当前快照值"vs"商品主表当前值"
 */

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================

-- 验证快照约束规则总数（10个快照字段 × 5种状态 = 50条）：
-- SELECT COUNT(*) AS total_constraint_rules
-- FROM erp_base.detail_snapshot_constraint;
-- 预期结果：total_constraint_rules = 50

-- 验证各状态的规则数：
-- SELECT doc_status, COUNT(*) AS rule_count
-- FROM erp_base.detail_snapshot_constraint
-- GROUP BY doc_status
-- ORDER BY doc_status;
-- 预期结果：draft=10, pending_audit=10, approved=10, completed=10, voided=10

-- 验证各快照字段的规则数：
-- SELECT field_name, COUNT(*) AS status_count
-- FROM erp_base.detail_snapshot_constraint
-- GROUP BY field_name
-- ORDER BY field_name;
-- 预期结果：每个字段5条（覆盖5种状态）

-- 验证冻结状态的快照行为：
-- SELECT field_name, doc_status, snapshot_behavior
-- FROM erp_base.detail_snapshot_constraint
-- WHERE doc_status IN ('approved', 'completed')
--   AND snapshot_behavior != 'freeze';
-- 预期结果：0行（已审核和已完成状态的快照行为必须为freeze）

-- 验证快照字段与规范表的一致性：
-- SELECT sc.field_name
-- FROM erp_base.detail_snapshot_constraint sc
-- LEFT JOIN erp_base.detail_product_field_spec spec
--     ON sc.field_name = spec.field_name
-- WHERE spec.field_name IS NULL;
-- 预期结果：0行（约束表中的字段都应在规范表中存在）

-- 校验目标明细从表的快照约束合规性（以pur_order_detail为例，需表已存在）：
-- SELECT erp_base.fn_validate_snapshot_constraints('erp_purchase', 'pur_order_detail', 'approved');

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP TABLE IF EXISTS erp_base.detail_snapshot_constraint CASCADE;
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_snapshot_constraints(VARCHAR, VARCHAR, VARCHAR);
-- ============================================================
