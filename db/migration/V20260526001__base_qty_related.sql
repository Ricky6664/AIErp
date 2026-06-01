-- ============================================================
-- ERP AI 系统 - base_qty 核心规范 + 计算逻辑 + 校验规则
-- 任务编号: P0-003-002-011-001-001
-- 文件名:   V20260526001__base_qty_related.sql
-- 说明:     定义 base_qty（基础单位换算数量）的核心规范、
--           计算公式、校验规则。
--           base_qty 是库存增减的"唯一真值"：
--             base_qty = qty × conversion_rate
--           - qty: 按所选业务单位的数量
--           - conversion_rate: 1个业务单位 = N个基础单位
--           - base_qty: 换算为基础单位的数量
--           所有出入库/库存余额/成本核算/MRP运算
--           统一以 base_qty 为计算基准。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-06-01
-- ============================================================

-- ============================================================
-- 第1步：base_qty 核心规范（注释文档）
-- 说明:     以注释形式系统阐述 base_qty 的核心规范，
--           作为所有涉及多单位/库存/单据明细模块的基础约束。
-- ============================================================

/*
 * ===================================================================
 * base_qty 核心规范 — 基础单位换算数量
 * ===================================================================
 *
 * 一、术语定义
 * ────────────
 *
 *   ┌────────────────┬────────────────────────────────────────────────┐
 *   │ 术语            │ 定义                                           │
 *   ├────────────────┼────────────────────────────────────────────────┤
 *   │ 业务单位(UOM)   │ 单据明细行上实际选用的计量单位（箱/包/kg/个等）│
 *   │ 基础单位        │ 商品档案中统一维护的基础计量单位               │
 *   │ 转换比例        │ 1个业务单位 = N个基础单位                      │
 *   │ qty（业务数量） │ 按所选业务单位的数量（录入数）                  │
 *   │ base_qty        │ 换算为基础单位的数量（库存增减统一使用）        │
 *   └────────────────┴────────────────────────────────────────────────┘
 *
 *
 * 二、核心公式
 * ────────────
 *
 *   base_qty = qty × conversion_rate
 *
 *   示例：
 *     - 商品A基础单位="个"，每箱=24个
 *       → 录入 3箱 → qty=3, conversion_rate=24, base_qty=72个
 *       → 录入 50个 → qty=50, conversion_rate=1, base_qty=50个
 *
 *   正向计算（录入）: base_qty = qty × conversion_rate
 *   反向计算（拆分）: qty = base_qty / conversion_rate
 *
 *
 * 三、 base_qty 的核心地位
 * ───────────────────────────
 *
 *   base_qty 是库存增减的"唯一真值"：
 *
 *   1. 所有库存表（余额/流水/货位/批次/序列号）的 qty 字段，其值均为 base_qty
 *   2. 所有出入库计算、成本核算、MRP 运算，一律以 base_qty 为计算基准
 *   3. 单据明细行的 qty（业务数量）仅用于显示，不参与库存计算
 *   4. 转换比例变动时，已发生单据不受影响（快照机制：conversion_rate 冗余存储）
 *
 *
 * 四、数据库存储约定
 * ──────────────────
 *
 *  1. 物理精度
 *     - DDL 统一使用 decimal(18,8)
 *     - 显示精度由系统参数 system.decimal_places_qty 控制（默认6位）
 *
 *  2. 明细从表商品冗余字段模板（所有含 product_id 的明细表强制包含）：
 *
 *     product_code        VARCHAR(50),
 *     product_name        VARCHAR(100),
 *     model               VARCHAR(100),
 *     spec                VARCHAR(100),
 *     brand               VARCHAR(50),
 *     unit_id             BIGINT,
 *     unit                VARCHAR(30),
 *     qty                 DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     is_multi_unit       BOOLEAN         DEFAULT FALSE,
 *     conversion_rate     DECIMAL(18,8)   NOT NULL DEFAULT 1,
 *     base_unit_id        BIGINT,
 *     base_qty            DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *
 *  3. CHECK 约束模板：
 *
 *     ALTER TABLE {detail_table}
 *       ADD CONSTRAINT ck_{detail_table}_base_qty CHECK (base_qty >= 0);
 *     ALTER TABLE {detail_table}
 *       ADD CONSTRAINT ck_{detail_table}_qty CHECK (qty >= 0);
 *     ALTER TABLE {detail_table}
 *       ADD CONSTRAINT ck_{detail_table}_conversion_rate CHECK (conversion_rate > 0);
 *
 *
 * 五、校验规则
 * ────────────
 *
 *  必须通过校验（否则拒绝保存）：
 *
 *    1. qty ≠ null, qty ≥ 0
 *    2. conversion_rate ≠ null, conversion_rate > 0
 *    3. base_qty ≠ null, base_qty ≥ 0
 *    4. base_qty = qty × conversion_rate（精度误差 ≤ 1e-8 容差）
 *    5. base_qty ≤ 9999999999.99999999（Decimal(18,8) 上限）
 *
 *  辅助属性子表校验：
 *
 *    - 库位/批次：同 detail_id 的 qty 之和 = 明细行 base_qty
 *    - 序列号：序列号记录数 = base_qty（必须为正整数）
 *
 *
 * 六、涉及的库存表
 * ────────────────
 *
 *   ┌─────────────────────┬──────────────────────────────────┐
 *   │ 表名                 │ qty 字段含义                      │
 *   ├─────────────────────┼──────────────────────────────────┤
 *   │ inv_stock_balance   │ 当前库存余额（base_qty）          │
 *   │ inv_stock_flow      │ 库存流水变动量（base_qty）         │
 *   │ inv_location_stock  │ 货位库存（base_qty）              │
 *   │ inv_batch_stock     │ 批次库存（base_qty）              │
 *   └─────────────────────┴──────────────────────────────────┘
 *
 *
 * 七、常见错误对照
 * ───────────────
 *
 *   错误1：直接用 qty 做库存计算
 *     ❌ UPDATE inv_stock_balance SET qty = qty - detail.qty
 *     ✅ UPDATE inv_stock_balance SET qty = qty - detail.base_qty
 *
 *   错误2：conversion_rate 存为 0
 *     ❌ conversion_rate DECIMAL(18,8) DEFAULT 0
 *     ✅ conversion_rate DECIMAL(18,8) NOT NULL DEFAULT 1
 *        ALTER TABLE ... ADD CONSTRAINT ck_... CHECK (conversion_rate > 0)
 *
 *   错误3：前后端计算不一致
 *     ❌ 前端计算 base_qty 后直接保存，后端不验证
 *     ✅ 后端保存时重新计算：base_qty = qty × conversion_rate，覆盖前端值
 *
 *   错误4：单位变更覆盖历史单据
 *     ❌ JOIN prod_product_unit 获取当前 conversion_rate 计算历史 base_qty
 *     ✅ 历史单据明细行的 conversion_rate 为快照值，变更不回溯
 */

-- ============================================================
-- 第2步：base_qty 计算函数
-- 说明:     PL/pgSQL函数，根据业务数量和转换比例计算 base_qty。
--           返回 decimal(18,8) 类型，scale=8，四舍五入。
--           统一所有需要 base_qty 计算的场景，确保一致性。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_calc_base_qty(
    p_qty             DECIMAL,
    p_conversion_rate DECIMAL
)
RETURNS DECIMAL(18,8)
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    -- 校验参数非空
    IF p_qty IS NULL THEN
        RAISE EXCEPTION 'qty 不能为 NULL';
    END IF;
    IF p_conversion_rate IS NULL OR p_conversion_rate <= 0 THEN
        RAISE EXCEPTION 'conversion_rate 必须大于 0，当前值: %', p_conversion_rate;
    END IF;

    -- 计算 base_qty = qty × conversion_rate，精度 8 位小数四舍五入
    RETURN ROUND(p_qty * p_conversion_rate, 8);
END;
$$;

COMMENT ON FUNCTION erp_base.fn_calc_base_qty(DECIMAL, DECIMAL)
    IS 'base_qty 计算函数 - 根据业务数量(qty)和转换比例(conversion_rate)计算基础单位换算数量(base_qty)，公式：base_qty = qty × conversion_rate';

-- ============================================================
-- 第3步：base_qty 一致性校验函数
-- 说明:     PL/pgSQL函数，校验 base_qty 是否满足公式：
--           base_qty = qty × conversion_rate
--           容忍误差 1e-8，用于后端保存前校验。
--           返回 TRUE（一致）或 FALSE（不一致）。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_base_qty(
    p_qty             DECIMAL,
    p_conversion_rate DECIMAL,
    p_base_qty        DECIMAL
)
RETURNS BOOLEAN
LANGUAGE plpgsql
IMMUTABLE
AS $$
DECLARE
    v_expected DECIMAL(18,8);
    v_diff     DECIMAL;
BEGIN
    -- 校验参数非空
    IF p_qty IS NULL OR p_conversion_rate IS NULL OR p_base_qty IS NULL THEN
        RAISE EXCEPTION 'qty、conversion_rate、base_qty 均不能为 NULL';
    END IF;

    -- 校验 conversion_rate 为正
    IF p_conversion_rate <= 0 THEN
        RAISE EXCEPTION 'conversion_rate 必须大于 0，当前值: %', p_conversion_rate;
    END IF;

    -- 计算期望值
    v_expected := ROUND(p_qty * p_conversion_rate, 8);

    -- 计算差值绝对值
    v_diff := ABS(v_expected - p_base_qty);

    -- 容忍误差 ≤ 1e-8，视为一致
    RETURN v_diff <= 0.00000001;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_base_qty(DECIMAL, DECIMAL, DECIMAL)
    IS 'base_qty 一致性校验函数 - 校验 base_qty 是否等于 qty × conversion_rate（容忍误差 1e-8），用于保存前数据校验';

-- ============================================================
-- 第4步：base_qty 反向计算函数
-- 说明:     根据 base_qty 和 conversion_rate 反算业务数量 qty。
--           公式：qty = base_qty / conversion_rate
--           用于库存拆合、盘点差异调整等场景。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_reverse_calc_qty(
    p_base_qty        DECIMAL,
    p_conversion_rate DECIMAL
)
RETURNS DECIMAL(18,8)
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    -- 校验参数非空
    IF p_base_qty IS NULL THEN
        RAISE EXCEPTION 'base_qty 不能为 NULL';
    END IF;
    IF p_conversion_rate IS NULL OR p_conversion_rate <= 0 THEN
        RAISE EXCEPTION 'conversion_rate 必须大于 0，当前值: %', p_conversion_rate;
    END IF;

    -- 反向计算：qty = base_qty / conversion_rate，精度 8 位小数四舍五入
    RETURN ROUND(p_base_qty / p_conversion_rate, 8);
END;
$$;

COMMENT ON FUNCTION erp_base.fn_reverse_calc_qty(DECIMAL, DECIMAL)
    IS 'base_qty 反向计算函数 - 根据 base_qty 和 conversion_rate 反算业务数量 qty，公式：qty = base_qty / conversion_rate';

-- ============================================================
-- 第5步：base_qty DDL 规范对照表（注释文档）
-- 说明:     汇总 base_qty 规范与全局数据库规范的对应关系。
-- ============================================================

/*
 * ===================================================================
 * base_qty 规范 ↔ 全局数据库规范 对照表
 * ===================================================================
 *
 * base_qty 规范条款               | 对应的全局数据库规范
 * -------------------------------|------------------------------------
 * decimal(18,8) 物理精度          | 数据类型规范 §2 数值精度规范
 * 显示精度由系统参数控制          | 数据类型规范 §2.3 精度参数化架构
 * qty/base_qty CHECK >= 0         | 数据类型规范 §9 CHECK约束规范
 * conversion_rate CHECK > 0       | 数据类型规范 §9 CHECK约束规范
 * 明细行商品快照字段              | 数据库命名规范 §12 明细从表商品冗余字段
 * base_qty/qty 字段命名           | 数据库命名规范 §4.2 按字段角色细分
 * 库存表 qty 均为 base_qty        | 本规范（base_qty）核心约定
 *
 *
 * 规范冲突决策（当 base_qty 规范与全局规范有冲突时）：
 *
 *   本规范（base_qty）为更底层的基础约定，与全局数据库规范互补而非冲突。
 *   若出现矛盾：
 *     - decimal(18,8) 物理精度：以全局数据库规范 §2 为准
 *     - base_qty 业务语义：以本规范为准
 *     - 字段命名：以全局数据库命名规范 §4 为准
 */

-- ============================================================
-- 第6步：base_qty 开发检查清单（注释文档）
-- 说明:     供开发者使用的检查清单，确保每个涉及 base_qty
--           的模块正确遵循本规范。
-- ============================================================

/*
 * ===================================================================
 * base_qty 规范 — 开发检查清单
 * ===================================================================
 *
 * □ 1. 明细从表 DDL：
 *      □ 包含完整的商品快照字段（product_code/name/model/spec/brand）
 *      □ 包含 unit_id/unit/base_unit_id 单位字段
 *      □ qty 字段类型为 DECIMAL(18,8) NOT NULL DEFAULT 0
 *      □ conversion_rate 字段类型为 DECIMAL(18,8) NOT NULL DEFAULT 1
 *      □ base_qty 字段类型为 DECIMAL(18,8) NOT NULL DEFAULT 0
 *      □ 包含 CHECK 约束：qty >= 0, base_qty >= 0, conversion_rate > 0
 *
 * □ 2. 后端 Entity/DTO：
 *      □ qty、base_qty、conversion_rate 使用 BigDecimal 类型
 *      □ 不使用 Double/Float（精度丢失）
 *      □ @NotNull 或 @DecimalMin 校验注解
 *
 * □ 3. 后端 Service 层：
 *      □ 保存前调用 calcBaseQty() 重新计算 base_qty（不信任前端值）
 *      □ calcBaseQty 使用 BigDecimal.multiply() 而非 double *
 *      □ scale=8, RoundingMode.HALF_UP
 *      □ 辅助属性拆分表（库位/批次）qty 之和校验
 *      □ 序列号管理时 base_qty 必须为正整数校验
 *
 * □ 4. 库存操作：
 *      □ 出入库使用 base_qty 而非 qty
 *      □ UPDATE 库存表时 SET qty = qty ± detail.base_qty
 *      □ 库存操作必须在同一 @Transactional 事务内
 *
 * □ 5. 前端：
 *      □ 单位选择后自动查 conversion_rate 并计算展示 base_qty
 *      □ 数量输入框 precision 从 system.decimal_places_qty 系统参数读取
 *      □ base_qty 展示字段只读（不开放用户编辑）
 */

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================

-- 验证 fn_calc_base_qty 计算函数：
-- SELECT erp_base.fn_calc_base_qty(3, 24) AS expected_72;
-- 预期结果：72.00000000
-- SELECT erp_base.fn_calc_base_qty(50, 1) AS expected_50;
-- 预期结果：50.00000000

-- 验证 fn_validate_base_qty 校验函数：
-- SELECT erp_base.fn_validate_base_qty(3, 24, 72) AS should_be_true;
-- 预期结果：TRUE
-- SELECT erp_base.fn_validate_base_qty(3, 24, 71) AS should_be_false;
-- 预期结果：FALSE

-- 验证 fn_reverse_calc_qty 反向计算函数：
-- SELECT erp_base.fn_reverse_calc_qty(72, 24) AS expected_3;
-- 预期结果：3.00000000

-- 验证函数已正确注册：
-- SELECT p.proname, pg_get_function_result(p.oid), pg_get_function_arguments(p.oid)
-- FROM pg_proc p
-- JOIN pg_namespace n ON n.oid = p.pronamespace
-- WHERE n.nspname = 'erp_base'
--   AND p.proname IN ('fn_calc_base_qty', 'fn_validate_base_qty', 'fn_reverse_calc_qty')
-- ORDER BY p.proname;

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP FUNCTION IF EXISTS erp_base.fn_calc_base_qty(DECIMAL, DECIMAL);
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_base_qty(DECIMAL, DECIMAL, DECIMAL);
-- DROP FUNCTION IF EXISTS erp_base.fn_reverse_calc_qty(DECIMAL, DECIMAL);
-- ============================================================
