-- ============================================================
-- ERP AI 系统 - 数值精度参数优先级规则 + 尾差处理规则 + 参数配置
-- 任务编号: P0-003-002-013-001-001
-- 文件名:   V20260526001__task_P0_003_002_013_001_001.sql
-- 说明:     定义数值精度的参数优先级体系、尾差处理规则、
--           精度参数初始化以及 PL/pgSQL 辅助函数。
--           核心原则：
--             1. 数据库物理字段统一 decimal(18,8)
--             2. 显示精度由系统参数动态控制
--             3. 精度优先级：字段级 > 表级 > 模块级 > 系统级 > 默认值
--             4. 尾差采用 HALF_UP 舍入 + 末行找平策略
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-06-01
-- ============================================================

-- ============================================================
-- 第1步：精度参数初始化（幂等性保证）
-- 说明:     向 sys_param 表插入精度相关的系统预置参数。
--           所有参数均使用 ON CONFLICT DO UPDATE 保证幂等性。
--           参数命名规范：{模块}.decimal_places_{类别}
-- ============================================================

INSERT INTO sys_param
    (tenant_id, param_code, param_value, param_group, is_preset, is_readonly, remark,
     created_at, updated_at, is_deleted, version)
VALUES
    -- === 系统级精度参数（system group） ===
    (0, 'system.decimal_places_amount',  '2', 'system', TRUE, FALSE,
     '金额类字段显示精度（小数位数），控制所有金额字段的前端展示与后端校验截断位数。默认2位',
     NOW(), NOW(), FALSE, 1),
    (0, 'system.decimal_places_qty',     '6', 'system', TRUE, FALSE,
     '数量/转换率类字段显示精度（小数位数），控制所有数量与转换率字段的前端展示与后端校验截断位数。默认6位',
     NOW(), NOW(), FALSE, 1),
    (0, 'system.decimal_places_price',   '4', 'system', TRUE, FALSE,
     '单价类字段显示精度（小数位数），控制所有单价字段的前端展示与后端校验截断位数。默认4位',
     NOW(), NOW(), FALSE, 1),
    (0, 'system.decimal_places_rate',    '4', 'system', TRUE, FALSE,
     '税率/百分比类字段显示精度（小数位数），控制税率与百分比字段的前端展示与后端校验截断位数。默认4位',
     NOW(), NOW(), FALSE, 1),

    -- === 尾差处理参数（system group） ===
    (0, 'system.tail_diff_tolerance',    '0.00000001', 'system', TRUE, FALSE,
     '尾差容差阈值（绝对值），当明细行base_qty合计与主表base_qty差异小于此值时视为可接受尾差，自动末行找平。取值1e-8',
     NOW(), NOW(), FALSE, 1),
    (0, 'system.tail_diff_strategy',     'LAST_LINE', 'system', TRUE, FALSE,
     '尾差处理策略：LAST_LINE=末行找平（将差额摊入最后一行）/FIRST_LINE=首行找平/AVG_SPREAD=均摊到所有行/NONE=不处理直接拒绝',
     NOW(), NOW(), FALSE, 1),

    -- === 销售模块精度参数（sale group） ===
    (0, 'sale.decimal_places_price',     '4', 'sale', TRUE, FALSE,
     '销售模块单价精度覆盖，优先级高于 system.decimal_places_price。默认4位',
     NOW(), NOW(), FALSE, 1),
    (0, 'sale.decimal_places_amount',    '2', 'sale', TRUE, FALSE,
     '销售模块金额精度覆盖，优先级高于 system.decimal_places_amount。默认2位',
     NOW(), NOW(), FALSE, 1),

    -- === 采购模块精度参数（purchase group） ===
    (0, 'purchase.decimal_places_price', '4', 'purchase', TRUE, FALSE,
     '采购模块单价精度覆盖，优先级高于 system.decimal_places_price。默认4位',
     NOW(), NOW(), FALSE, 1),
    (0, 'purchase.decimal_places_amount','2', 'purchase', TRUE, FALSE,
     '采购模块金额精度覆盖，优先级高于 system.decimal_places_amount。默认2位',
     NOW(), NOW(), FALSE, 1),

    -- === 库存模块精度参数（inventory group） ===
    (0, 'inventory.decimal_places_qty',  '6', 'inventory', TRUE, FALSE,
     '库存模块数量精度覆盖，优先级高于 system.decimal_places_qty。默认6位',
     NOW(), NOW(), FALSE, 1)
ON CONFLICT (tenant_id, param_code) WHERE is_deleted = FALSE
DO UPDATE SET
    param_value = EXCLUDED.param_value,
    remark      = EXCLUDED.remark,
    updated_at  = NOW(),
    version     = sys_param.version + 1;


-- ============================================================
-- 第2步：精度优先级规则（注释文档）
-- 说明:     以注释形式系统阐述精度优先级体系，作为所有
--           涉及数值精度处理的模块基础约束。
-- ============================================================

/*
 * ===================================================================
 * 数值精度参数优先级规则 — Precision Priority Specification
 * ===================================================================
 *
 * 一、核心原则
 * ────────────
 *
 *   数据库物理存储统一使用 decimal(18,8)，保证足够精度不丢失数据。
 *   显示精度（前端输入框小数位数、列表展示位数、后端校验截断位数）
 *   由参数体系动态控制，无需修改 DDL。
 *
 *
 * 二、精度优先级链（由高到低）
 * ────────────────────────────
 *
 *   Level 1（最高） — 字段级精度注解
 *     └─ 在 Entity/DTO 字段上使用 @DecimalPlaces(n) 注解
 *     └─ 例：@DecimalPlaces(2) private BigDecimal totalAmount;
 *     └─ 优先级最高，覆盖所有下级配置
 *
 *   Level 2 — 表级精度配置
 *     └─ 在 sys_table_config 或单据配置中按表设定精度
 *     └─ 例：sale_order_detail.unit_price → 6位
 *     └─ 覆盖模块级和系统级
 *
 *   Level 3 — 模块级精度参数
 *     └─ sys_param 中按模块设定的参数
 *     └─ 例：sale.decimal_places_price → 控制销售模块所有单价
 *     └─ 命名规范：{module}.decimal_places_{category}
 *     └─ 覆盖系统级
 *
 *   Level 4 — 系统级精度参数
 *     └─ sys_param 中 system.* 全局参数
 *     └─ 例：system.decimal_places_amount → 控制所有金额
 *     └─ 当模块级参数不存在时生效
 *
 *   Level 5（最低） — 默认值
 *     └─ 代码中硬编码的兜底默认值
 *     └─ 例：金额默认2位 / 数量默认6位 / 单价默认4位 / 税率默认4位
 *     └─ 仅在所有上级配置均不存在时使用
 *
 *   精度查找伪代码：
 *
 *     function getDecimalPlaces(fieldCategory, module, tableName, fieldName):
 *         // Level 1: 字段级
 *         annotation = getFieldAnnotation(fieldName)
 *         if annotation != null: return annotation.value()
 *
 *         // Level 2: 表级
 *         tableConfig = getTablePrecisionConfig(tableName, fieldName)
 *         if tableConfig != null: return tableConfig
 *
 *         // Level 3: 模块级
 *         moduleParam = getSysParam(module + '.decimal_places_' + fieldCategory)
 *         if moduleParam != null: return parseInt(moduleParam)
 *
 *         // Level 4: 系统级
 *         systemParam = getSysParam('system.decimal_places_' + fieldCategory)
 *         if systemParam != null: return parseInt(systemParam)
 *
 *         // Level 5: 默认值
 *         return DEFAULT_PRECISION[fieldCategory]
 *
 *
 * 三、字段类别与默认精度映射
 * ──────────────────────────
 *
 *   ┌──────────────────┬──────────┬────────────────────────────┐
 *   │ 字段类别          │ 默认精度  │ 适用范围                    │
 *   ├──────────────────┼──────────┼────────────────────────────┤
 *   │ amount（金额）    │ 2位      │ 单据金额、付款金额、成本金额│
 *   │ price（单价）     │ 4位      │ 采购单价、销售单价、成本价  │
 *   │ qty（数量）       │ 6位      │ 业务数量、基础数量、库存量  │
 *   │ rate（税率/百分比）│ 4位      │ 税率、折扣率、百分比字段    │
 *   │ conversion_rate  │ 6位      │ 单位转换率（同 qty）        │
 *   │ ext_num*（扩展）  │ 8位      │ 用户自定义扩展数值字段      │
 *   └──────────────────┴──────────┴────────────────────────────┘
 *
 *
 * 四、精度查找参数键映射
 * ─────────────────────
 *
 *   对于类别 amount：
 *     Level 3: {module}.decimal_places_amount
 *     Level 4: system.decimal_places_amount
 *     Level 5: 2
 *
 *   对于类别 price：
 *     Level 3: {module}.decimal_places_price
 *     Level 4: system.decimal_places_price
 *     Level 5: 4
 *
 *   对于类别 qty：
 *     Level 3: {module}.decimal_places_qty
 *     Level 4: system.decimal_places_qty
 *     Level 5: 6
 *
 *   对于类别 rate：
 *     Level 3: {module}.decimal_places_rate
 *     Level 4: system.decimal_places_rate
 *     Level 5: 4
 *
 *
 * 五、前端集成
 * ────────────
 *
 *   Vue 组件中从系统参数动态读取精度：
 *
 *     // 从全局 store 读取精度参数（登录时从 /api/sys-param/precision 加载）
 *     const { decimalPlaces } = usePrecisionStore()
 *
 *     // 使用方式
 *     <el-input-number :precision="decimalPlaces.amount" />
 *     <el-input-number :precision="decimalPlaces.price" />
 *     <el-input-number :precision="decimalPlaces.qty" />
 *
 *   API 接口：GET /api/sys-param/precision
 *     返回所有精度参数的键值对，前端缓存到 Pinia store。
 *
 *
 * 六、后端校验集成
 * ────────────────
 *
 *   Service 层保存前按参数截断：
 *
 *     BigDecimal rawValue = detail.getUnitPrice();
 *     int precision = precisionService.getDecimalPlaces("price", "sale", null, null);
 *     BigDecimal trimmed = rawValue.setScale(precision, RoundingMode.HALF_UP);
 *     detail.setUnitPrice(trimmed);
 *
 *   校验注解（字段级）：
 *
 *     @DecimalPlaces(4)  // Level 1 优先级，覆盖参数配置
 *     private BigDecimal unitPrice;
 */

-- ============================================================
-- 第3步：尾差处理规则（注释文档）
-- 说明:     定义多行单据场景下的尾差处理策略。
--           当明细行合计与主表金额存在舍入误差时，
--           按配置的策略自动找平。
-- ============================================================

/*
 * ===================================================================
 * 尾差处理规则 — Rounding Error Handling Rules
 * ===================================================================
 *
 * 一、尾差产生原因
 * ────────────────
 *
 *   多行明细各自按精度截断后，各行合计与原始总金额可能产生微小差异。
 *   例如（2位精度）：
 *     行1: 10.00 / 3 = 3.333... → 3.33
 *     行2: 10.00 / 3 = 3.333... → 3.33
 *     行3: 10.00 / 3 = 3.333... → 3.33
 *     合计: 3.33 + 3.33 + 3.33 = 9.99 ≠ 10.00
 *     尾差: 10.00 - 9.99 = 0.01
 *
 *
 * 二、尾差处理策略
 * ────────────────
 *
 *   策略由 sys_param 的 system.tail_diff_strategy 控制：
 *
 *   ┌──────────────┬────────────────────────────────────────────┐
 *   │ 策略          │ 说明                                       │
 *   ├──────────────┼────────────────────────────────────────────┤
 *   │ LAST_LINE    │ 末行找平：将差额摊入最后一行（默认策略）     │
 *   │ FIRST_LINE   │ 首行找平：将差额摊入第一行                  │
 *   │ AVG_SPREAD   │ 均摊：将差额均匀分摊到所有行（精度允许时）   │
 *   │ NONE         │ 不处理：差额超出容差时直接拒绝保存           │
 *   └──────────────┴────────────────────────────────────────────┘
 *
 *
 * 三、尾差容差阈值
 * ────────────────
 *
 *   system.tail_diff_tolerance 定义可接受的尾差范围（默认 1e-8）。
 *   若 |主表金额 - 明细合计| <= tolerance → 自动找平。
 *   若 |主表金额 - 明细合计| >  tolerance → 校验拒绝，提示用户调整数量/单价。
 *
 *
 * 四、LAST_LINE 策略伪代码
 * ────────────────────────
 *
 *   function handleTailDiff(headerAmount, detailLines, precision):
 *       detailsSum = sum(line.amount.setScale(precision, HALF_UP) for line in detailLines)
 *       diff = headerAmount.subtract(detailsSum)
 *
 *       if diff.abs() <= tolerance:
 *           return detailLines  // 尾差可接受
 *
 *       // 末行找平
 *       lastLine = detailLines.get(detailLines.size() - 1)
 *       lastLine.amount = lastLine.amount.add(diff)
 *       return detailLines
 *
 *
 * 五、不同字段类别的尾差处理
 * ──────────────────────────
 *
 *   ┌──────────────┬──────────────────────────────────────────┐
 *   │ 字段类别      │ 尾差处理方式                              │
 *   ├──────────────┼──────────────────────────────────────────┤
 *   │ 金额(amount) │ 末行找平（金额尾差必须消除）              │
 *   │ 数量(qty)    │ base_qty 合计必须严格一致，末行找平       │
 *   │ 单价(price)  │ 各行独立截断，不找平（单价尾差可接受）    │
 *   │ 税率(rate)   │ 各行独立截断，不找平                      │
 *   └──────────────┴──────────────────────────────────────────┘
 *
 *
 * 六、禁止行为
 * ────────────
 *
 *   - 禁止随意增加尾差容差阈值以"放过"不一致数据
 *   - 禁止在前端做尾差找平（前端仅展示，后端保存时统一处理）
 *   - 禁止对历史单据的尾差进行追溯调整
 */

-- ============================================================
-- 第4步：PL/pgSQL 精度辅助函数
-- 说明:     提供数据库层的精度计算函数，用于触发器或
--           存储过程中的精度截断与尾差计算。
--           应用层应优先使用 Java BigDecimal，
--           此函数作为数据库层的补充兜底。
-- ============================================================

-- -------------------------------------------------------
-- 4.1 round_to_precision — 按指定精度四舍五入
-- 参数:
--   p_value     numeric  待截断的数值
--   p_scale     int      目标小数位数
-- 返回:         numeric  截断后的值（HALF_UP 舍入）
-- -------------------------------------------------------

CREATE OR REPLACE FUNCTION erp_base.round_to_precision(
    p_value NUMERIC,
    p_scale INT
)
RETURNS NUMERIC
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    IF p_value IS NULL THEN
        RETURN NULL;
    END IF;
    IF p_scale < 0 OR p_scale > 8 THEN
        RAISE EXCEPTION '精度位数必须在 0~8 之间，当前值：%', p_scale;
    END IF;
    RETURN ROUND(p_value, p_scale);
END;
$$;

COMMENT ON FUNCTION erp_base.round_to_precision(NUMERIC, INT)
    IS '按指定精度四舍五入（HALF_UP）。p_value=待截断值, p_scale=小数位数(0~8)';


-- -------------------------------------------------------
-- 4.2 calc_tail_diff — 计算尾差
-- 参数:
--   p_total      numeric  主表/头部总金额
--   p_details    numeric[]  明细行数组
--   p_scale      int        目标小数位数
-- 返回:          numeric    尾差值 = p_total - SUM(p_details 截断后)
-- -------------------------------------------------------

CREATE OR REPLACE FUNCTION erp_base.calc_tail_diff(
    p_total   NUMERIC,
    p_details NUMERIC[],
    p_scale   INT
)
RETURNS NUMERIC
LANGUAGE plpgsql
IMMUTABLE
AS $$
DECLARE
    v_sum     NUMERIC := 0;
    v_detail  NUMERIC;
BEGIN
    IF p_details IS NULL OR array_length(p_details, 1) IS NULL THEN
        RETURN COALESCE(p_total, 0);
    END IF;

    FOREACH v_detail IN ARRAY p_details LOOP
        v_sum := v_sum + COALESCE(ROUND(v_detail, p_scale), 0);
    END LOOP;

    RETURN COALESCE(p_total, 0) - v_sum;
END;
$$;

COMMENT ON FUNCTION erp_base.calc_tail_diff(NUMERIC, NUMERIC[], INT)
    IS '计算总金额与明细行截断后合计的尾差。p_total=总金额, p_details=明细数组, p_scale=精度';


-- -------------------------------------------------------
-- 4.3 apply_tail_diff_last_line — 末行找平尾差
-- 参数:
--   p_total      numeric     主表总金额
--   p_details    numeric[]   明细行金额数组（将原地修改最后一行）
--   p_scale      int         目标小数位数
--   p_tolerance  numeric     尾差容差阈值（默认 1e-8）
-- 返回:          numeric[]   调整后的明细行数组
-- -------------------------------------------------------

CREATE OR REPLACE FUNCTION erp_base.apply_tail_diff_last_line(
    p_total     NUMERIC,
    p_details   NUMERIC[],
    p_scale     INT,
    p_tolerance NUMERIC DEFAULT 0.00000001
)
RETURNS NUMERIC[]
LANGUAGE plpgsql
IMMUTABLE
AS $$
DECLARE
    v_diff        NUMERIC;
    v_len         INT;
    v_result      NUMERIC[];
BEGIN
    IF p_details IS NULL OR array_length(p_details, 1) IS NULL THEN
        RETURN p_details;
    END IF;

    v_len := array_length(p_details, 1);

    -- 计算尾差
    v_diff := erp_base.calc_tail_diff(p_total, p_details, p_scale);

    -- 尾差在容差范围内，不做找平
    IF ABS(v_diff) <= p_tolerance THEN
        RETURN p_details;
    END IF;

    -- 复制数组，末行加尾差
    v_result := p_details;
    v_result[v_len] := ROUND(v_result[v_len] + v_diff, p_scale);

    RETURN v_result;
END;
$$;

COMMENT ON FUNCTION erp_base.apply_tail_diff_last_line(NUMERIC, NUMERIC[], INT, NUMERIC)
    IS '末行找平尾差。将总金额与明细合计的差额摊入最后一行';


-- -------------------------------------------------------
-- 4.4 get_decimal_places — 精度查找函数（模拟优先级链）
-- 参数:
--   p_category   VARCHAR  字段类别：amount/price/qty/rate
--   p_module     VARCHAR  模块名：sale/purchase/inventory（NULL=使用系统级）
-- 返回:          INT      精度值（0~8）
-- -------------------------------------------------------

CREATE OR REPLACE FUNCTION erp_base.get_decimal_places(
    p_category VARCHAR,
    p_module   VARCHAR DEFAULT NULL
)
RETURNS INT
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_param_code VARCHAR(200);
    v_value      TEXT;
    v_places     INT;
    -- 默认精度表
    v_defaults   JSONB := '{
        "amount": 2,
        "price": 4,
        "qty": 6,
        "rate": 4,
        "conversion_rate": 6
    }'::JSONB;
BEGIN
    -- 参数校验
    IF p_category IS NULL OR NOT v_defaults ? p_category THEN
        RAISE EXCEPTION '无效的字段类别：%，有效值为 amount/price/qty/rate/conversion_rate', p_category;
    END IF;

    -- Level 3: 模块级参数
    IF p_module IS NOT NULL THEN
        v_param_code := p_module || '.decimal_places_' || p_category;
        SELECT param_value INTO v_value
        FROM sys_param
        WHERE param_code = v_param_code
          AND is_deleted = FALSE
        LIMIT 1;

        IF v_value IS NOT NULL THEN
            v_places := v_value::INT;
            IF v_places >= 0 AND v_places <= 8 THEN
                RETURN v_places;
            END IF;
        END IF;
    END IF;

    -- Level 4: 系统级参数
    v_param_code := 'system.decimal_places_' || p_category;
    SELECT param_value INTO v_value
    FROM sys_param
    WHERE param_code = v_param_code
      AND is_deleted = FALSE
    LIMIT 1;

    IF v_value IS NOT NULL THEN
        v_places := v_value::INT;
        IF v_places >= 0 AND v_places <= 8 THEN
            RETURN v_places;
        END IF;
    END IF;

    -- Level 5: 默认值
    RETURN (v_defaults ->> p_category)::INT;
END;
$$;

COMMENT ON FUNCTION erp_base.get_decimal_places(VARCHAR, VARCHAR)
    IS '按优先级链查找数值精度。p_category=字段类别(amount/price/qty/rate), p_module=模块名';

-- ============================================================
-- 第5步：精度查找结果验证
-- 说明:     验证精度函数在不依赖外部数据时的默认行为。
--           以下查询用于开发环境验证，生产环境可注释掉。
-- ============================================================

-- 验证默认精度（不依赖 sys_param 数据时返回 Level 5 默认值）
-- SELECT erp_base.get_decimal_places('amount') AS default_amount;  -- 期望 2
-- SELECT erp_base.get_decimal_places('price')  AS default_price;   -- 期望 4
-- SELECT erp_base.get_decimal_places('qty')    AS default_qty;     -- 期望 6
-- SELECT erp_base.get_decimal_places('rate')   AS default_rate;    -- 期望 4

-- 验证 round_to_precision
-- SELECT erp_base.round_to_precision(3.14159265, 4);  -- 期望 3.1416
-- SELECT erp_base.round_to_precision(3.14159265, 2);  -- 期望 3.14
-- SELECT erp_base.round_to_precision(NULL, 2);        -- 期望 NULL

-- 验证尾差计算
-- SELECT erp_base.calc_tail_diff(10.00, ARRAY[3.333, 3.333, 3.333], 2);  -- 期望 ≈ 0.01

-- 验证末行找平
-- SELECT erp_base.apply_tail_diff_last_line(10.00, ARRAY[3.33, 3.33, 3.33], 2);
-- 期望: {3.33, 3.33, 3.34}（末行加 0.01）
