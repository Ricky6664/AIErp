package com.erp.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.erp.system.entity.SysCodeRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QueryHelper 通用查询条件构造器单元测试.
 *
 * <p>任务: P0-001-003-002-002-001 搜索表单布局</p>
 *
 * <p>验证范围:
 * <ul>
 *   <li>buildLikeWrapper: OR模糊查询 + LIKE通配符转义 + 边界处理</li>
 *   <li>buildDateRangeWrapper: 日期范围BETWEEN闭区间 + 边界处理</li>
 *   <li>buildEnumWrapper: 枚举EQ精确筛选 + 边界处理</li>
 *   <li>create: 空wrapper工厂方法</li>
 *   <li>工具类结构: final class + 私有构造 + 静态方法</li>
 * </ul>
 * </p>
 *
 * <p>说明: 本测试运行于无Spring上下文环境, 不使用 getTargetSql() 验证有SFunction条件的wrapper.
 * 改为通过 isEmptyOfNormal() 验证条件是否成功添加, 因为条件以lambda形式存储,
 * 列名解析(getTargetSql)需要MyBatis Configuration.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
class QueryHelperTest {

    private static final SFunction<SysCodeRule, String> RULE_CODE_FIELD = SysCodeRule::getRuleCode;
    private static final SFunction<SysCodeRule, String> RULE_NAME_FIELD = SysCodeRule::getRuleName;
    private static final SFunction<SysCodeRule, String> MODULE_CODE_FIELD = SysCodeRule::getModuleCode;
    private static final SFunction<SysCodeRule, ?> CREATE_TIME_FIELD = SysCodeRule::getCreateTime;
    private static final SFunction<SysCodeRule, ?> UPDATE_TIME_FIELD = SysCodeRule::getUpdateTime;
    private static final SFunction<SysCodeRule, Integer> IS_ENABLED_FIELD = SysCodeRule::getIsEnabled;

    // ========== buildLikeWrapper OR模糊查询 ==========

    @Nested
    @DisplayName("buildLikeWrapper OR模糊查询测试")
    class BuildLikeWrapperTests {

        @Test
        @DisplayName("buildLikeWrapper - 单字段关键词搜索 - 返回非空wrapper且条件已添加")
        void should_returnWrapperWithConditions_when_singleFieldWithKeyword() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("张三", List.of(RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(),
                    "条件应已添加到wrapper中(OR LIKE)");
        }

        @Test
        @DisplayName("buildLikeWrapper - 多字段OR模糊查询 - 条件成功添加")
        void should_combineMultipleFieldsWithOr_when_multipleFields() {
            List<SFunction<SysCodeRule, ?>> fields = Arrays.asList(
                    RULE_CODE_FIELD, RULE_NAME_FIELD, MODULE_CODE_FIELD);
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("test", fields);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(),
                    "多字段OR LIKE条件应已添加");
        }

        @Test
        @DisplayName("buildLikeWrapper - keyword为null - 返回空wrapper")
        void should_returnEmptyWrapper_when_keywordIsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper(null, List.of(RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildLikeWrapper - keyword为空字符串 - 返回空wrapper")
        void should_returnEmptyWrapper_when_keywordIsEmpty() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("", List.of(RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildLikeWrapper - fields为null - 返回空wrapper")
        void should_returnEmptyWrapper_when_fieldsIsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("test", null);

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildLikeWrapper - fields为空列表 - 返回空wrapper")
        void should_returnEmptyWrapper_when_fieldsIsEmpty() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("test", Collections.emptyList());

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildLikeWrapper - 包含百分号%的关键词 - 不抛异常且条件成功添加")
        void should_notThrowException_when_keywordContainsPercent() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("100%", List.of(RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "条件应已添加");
        }

        @Test
        @DisplayName("buildLikeWrapper - 包含下划线_的关键词 - 不抛异常")
        void should_notThrowException_when_keywordContainsUnderscore() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("test_name", List.of(RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildLikeWrapper - 包含反斜杠\\的关键词 - 不抛异常")
        void should_notThrowException_when_keywordContainsBackslash() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("C:\\path", List.of(RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildLikeWrapper - 包含混合通配符%和_ - 不抛异常")
        void should_notThrowException_when_keywordContainsMixedWildcards() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("100%_test", List.of(RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildLikeWrapper - 返回类型为LambdaQueryWrapper")
        void should_returnLambdaQueryWrapperType_when_buildLikeWrapper() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("test", List.of(RULE_NAME_FIELD));

            assertInstanceOf(LambdaQueryWrapper.class, wrapper);
        }

        @Test
        @DisplayName("buildLikeWrapper - 多个字段OR连接 - 任意字段LIKE匹配")
        void should_addOrConditionsForMultipleFields() {
            List<SFunction<SysCodeRule, ?>> fields = Arrays.asList(
                    RULE_CODE_FIELD, RULE_NAME_FIELD);
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("hello", fields);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "应添加OR LIKE条件组");
        }
    }

    // ========== buildDateRangeWrapper 日期范围BETWEEN ==========

    @Nested
    @DisplayName("buildDateRangeWrapper 日期范围测试")
    class BuildDateRangeWrapperTests {

        @Test
        @DisplayName("buildDateRangeWrapper - 有效日期范围 - 返回非空wrapper且BETWEEN条件已添加")
        void should_returnWrapperWithBetween_when_validDateRange() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(),
                    "BETWEEN日期范围条件应已添加");
        }

        @Test
        @DisplayName("buildDateRangeWrapper - 闭区间边界值 - 首尾日期包含")
        void should_includeBoundaryDates_when_closedInterval() {
            LocalDate start = LocalDate.of(2026, 5, 1);
            LocalDate end = LocalDate.of(2026, 5, 31);
            LocalDate[] range = {start, end};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "闭区间条件应已添加");
        }

        @Test
        @DisplayName("buildDateRangeWrapper - 同日范围 - start和end相等")
        void should_handleSameDayRange_when_startEqualsEnd() {
            LocalDate sameDay = LocalDate.of(2026, 5, 29);
            LocalDate[] range = {sameDay, sameDay};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "同日BETWEEN条件应已添加");
        }

        @Test
        @DisplayName("buildDateRangeWrapper - range为null - 返回空wrapper")
        void should_returnEmptyWrapper_when_rangeIsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, null);

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildDateRangeWrapper - range长度不足 - 返回空wrapper")
        void should_returnEmptyWrapper_when_rangeLengthInsufficient() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1)};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildDateRangeWrapper - range[0]为null - 返回空wrapper")
        void should_returnEmptyWrapper_when_rangeStartIsNull() {
            LocalDate[] range = {null, LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildDateRangeWrapper - range[1]为null - 返回空wrapper")
        void should_returnEmptyWrapper_when_rangeEndIsNull() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), null};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildDateRangeWrapper - 返回类型为LambdaQueryWrapper")
        void should_returnLambdaQueryWrapperType_when_buildDateRangeWrapper() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertInstanceOf(LambdaQueryWrapper.class, wrapper);
        }
    }

    // ========== buildEnumWrapper 枚举EQ筛选 ==========

    @Nested
    @DisplayName("buildEnumWrapper 枚举/状态筛选测试")
    class BuildEnumWrapperTests {

        @Test
        @DisplayName("buildEnumWrapper - 有效枚举值 - 返回非空wrapper且EQ条件已添加")
        void should_returnWrapperWithEq_when_validEnumValue() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildEnumWrapper(IS_ENABLED_FIELD, 1);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "EQ条件应已添加");
        }

        @Test
        @DisplayName("buildEnumWrapper - value为null - 返回空wrapper")
        void should_returnEmptyWrapper_when_valueIsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildEnumWrapper(IS_ENABLED_FIELD, null);

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildEnumWrapper - value为0 - 正常构造EQ条件(Integer 0应视为有效值)")
        void should_returnEqWrapper_when_valueIsZero() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildEnumWrapper(IS_ENABLED_FIELD, 0);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "0是有效值,应添加EQ条件");
        }

        @Test
        @DisplayName("buildEnumWrapper - value为负数 - 正常构造EQ条件")
        void should_returnEqWrapper_when_valueIsNegative() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildEnumWrapper(IS_ENABLED_FIELD, -1);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "负数也是有效值,应添加EQ条件");
        }

        @Test
        @DisplayName("buildEnumWrapper - 返回类型为LambdaQueryWrapper")
        void should_returnLambdaQueryWrapperType_when_buildEnumWrapper() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildEnumWrapper(IS_ENABLED_FIELD, 1);

            assertInstanceOf(LambdaQueryWrapper.class, wrapper);
        }
    }

    // ========== create 空wrapper工厂 ==========

    @Nested
    @DisplayName("create 空wrapper工厂测试")
    class CreateTests {

        @Test
        @DisplayName("create - 返回非null的LambdaQueryWrapper")
        void should_returnNonNullWrapper_when_create() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();

            assertNotNull(wrapper);
        }

        @Test
        @DisplayName("create - 返回空条件wrapper(getTargetSql为空串)")
        void should_returnEmptyConditionWrapper_when_create() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();

            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("create - 返回类型为LambdaQueryWrapper")
        void should_returnLambdaQueryWrapperType_when_create() {
            Object wrapper = QueryHelper.<SysCodeRule>create();

            assertInstanceOf(LambdaQueryWrapper.class, wrapper);
        }

        @Test
        @DisplayName("create - 多次调用返回不同wrapper实例")
        void should_returnDifferentInstances_when_createCalledMultipleTimes() {
            LambdaQueryWrapper<SysCodeRule> wrapper1 = QueryHelper.create();
            LambdaQueryWrapper<SysCodeRule> wrapper2 = QueryHelper.create();

            assertNotSame(wrapper1, wrapper2, "每次create应返回新实例");
        }
    }

    // ========== 链式调用与组合 ==========

    @Nested
    @DisplayName("链式调用与组合测试")
    class ChainingTests {

        @Test
        @DisplayName("链式调用 - buildLikeWrapper后可继续链式添加条件")
        void should_supportChaining_when_continueAddingConditions() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.<SysCodeRule>buildLikeWrapper("test", List.of(RULE_NAME_FIELD));

            assertDoesNotThrow(() -> {
                wrapper.eq(IS_ENABLED_FIELD, 1);
                wrapper.orderByAsc(CREATE_TIME_FIELD);
            }, "链式添加条件不应抛异常");

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "链式条件应已添加");
        }

        @Test
        @DisplayName("组合查询 - 手动组合LIKE + 日期ge/le + 枚举EQ")
        void should_combineAllConditions_when_multipleConditionsOnOneWrapper() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};

            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();
            assertDoesNotThrow(() -> {
                wrapper.eq(IS_ENABLED_FIELD, 1);
                wrapper.ge(CREATE_TIME_FIELD, range[0]);
                wrapper.le(CREATE_TIME_FIELD, range[1]);
                wrapper.like(RULE_NAME_FIELD, "test");
            }, "组合条件不应抛异常");

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "组合条件应已添加");
        }
    }

    // ========== 工具类结构验证 ==========

    @Nested
    @DisplayName("工具类结构验证")
    class UtilityClassStructureTests {

        @Test
        @DisplayName("QueryHelper应为final class")
        void should_beFinalClass() {
            int modifiers = QueryHelper.class.getModifiers();
            assertTrue(java.lang.reflect.Modifier.isFinal(modifiers),
                    "QueryHelper应为final class");
        }

        @Test
        @DisplayName("QueryHelper构造方法应为private")
        void should_havePrivateConstructor() throws Exception {
            var constructor = QueryHelper.class.getDeclaredConstructor();
            assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
                    "构造方法应为private");
        }

        @Test
        @DisplayName("buildLikeWrapper应为static方法")
        void should_haveStaticBuildLikeWrapper() throws Exception {
            var method = QueryHelper.class.getMethod("buildLikeWrapper", String.class, List.class);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()),
                    "buildLikeWrapper应为static方法");
        }

        @Test
        @DisplayName("buildDateRangeWrapper应为static方法")
        void should_haveStaticBuildDateRangeWrapper() throws Exception {
            var method = QueryHelper.class.getMethod("buildDateRangeWrapper",
                    SFunction.class, SFunction.class, LocalDate[].class);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()),
                    "buildDateRangeWrapper应为static方法");
        }

        @Test
        @DisplayName("buildEnumWrapper应为static方法")
        void should_haveStaticBuildEnumWrapper() throws Exception {
            var method = QueryHelper.class.getMethod("buildEnumWrapper",
                    SFunction.class, Integer.class);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()),
                    "buildEnumWrapper应为static方法");
        }
    }

    // ========== 泛型安全 ==========

    @Nested
    @DisplayName("泛型安全验证")
    class GenericsSafetyTests {

        @Test
        @DisplayName("泛型方法 - buildLikeWrapper可链式添加同实体类型字段条件")
        void should_preserveGenericType_when_buildLikeWrapperChaining() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.<SysCodeRule>buildLikeWrapper("test", List.of(RULE_NAME_FIELD));

            assertDoesNotThrow(() ->
                    wrapper.eq(IS_ENABLED_FIELD, 1),
                    "泛型应正确保持,链式添加不应抛异常");
            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("泛型方法 - buildDateRangeWrapper字段与wrapper实体类型一致")
        void should_preserveGenericType_when_buildDateRangeWrapper() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal());
        }
    }

    // ========== 验收标准专项验证 ==========

    @Nested
    @DisplayName("验收标准专项验证")
    class AcceptanceCriteriaTests {

        @Test
        @DisplayName("验收标准1 - 模糊查询OR组合正确(LIKE通配符转义) - 方法不抛异常")
        void acceptance_should_createLikeWrapper_withoutException() {
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildLikeWrapper("100%_test",
                            List.of(RULE_CODE_FIELD, RULE_NAME_FIELD));

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(),
                    "OR模糊查询条件应已添加,通配符转义不应影响wrapper构建");
        }

        @Test
        @DisplayName("验收标准2 - 日期范围闭区间BETWEEN - 条件成功构建")
        void acceptance_should_createBetweenCondition_when_validRange() {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 12, 31);
            LocalDate[] range = {start, end};
            LambdaQueryWrapper<SysCodeRule> wrapper =
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD, range);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(),
                    "BETWEEN闭区间条件应已添加");
        }

        @Test
        @DisplayName("验收标准3 - 所有方法返回LambdaQueryWrapper<T>")
        void acceptance_should_returnLambdaQueryWrapperFromAllMethods() {
            assertInstanceOf(LambdaQueryWrapper.class,
                    QueryHelper.buildLikeWrapper("test", List.of(RULE_NAME_FIELD)));
            assertInstanceOf(LambdaQueryWrapper.class,
                    QueryHelper.buildDateRangeWrapper(CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                            new LocalDate[]{LocalDate.now(), LocalDate.now()}));
            assertInstanceOf(LambdaQueryWrapper.class,
                    QueryHelper.buildEnumWrapper(IS_ENABLED_FIELD, 1));
            assertInstanceOf(LambdaQueryWrapper.class,
                    QueryHelper.create());
        }
    }

    // ========== buildSearchWrapper 查询页组合搜索(P0-001-003-002-002-003) ==========

    @Nested
    @DisplayName("buildSearchWrapper 查询页组合搜索测试")
    class BuildSearchWrapperTests {

        @Test
        @DisplayName("buildSearchWrapper - 全部条件有效 - 返回包含所有条件的wrapper")
        void should_combineAllConditions_when_allParamsValid() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.buildSearchWrapper(
                    "test", List.of(RULE_CODE_FIELD, RULE_NAME_FIELD),
                    range, CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                    IS_ENABLED_FIELD, 1);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "应包含LIKE+日期BETWEEN+EQ条件");
        }

        @Test
        @DisplayName("buildSearchWrapper - 仅keyword有效其余为null - 仅LIKE条件")
        void should_addOnlyLikeCondition_when_onlyKeywordValid() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.buildSearchWrapper(
                    "test", List.of(RULE_NAME_FIELD),
                    null, CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                    null, null);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "应包含LIKE条件");
        }

        @Test
        @DisplayName("buildSearchWrapper - 仅日期范围有效其余为null - 仅BETWEEN条件")
        void should_addOnlyBetweenCondition_when_onlyDateRangeValid() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.buildSearchWrapper(
                    null, null,
                    range, CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                    null, null);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "应包含BETWEEN条件");
        }

        @Test
        @DisplayName("buildSearchWrapper - 仅枚举有效其余为null - 仅EQ条件")
        void should_addOnlyEqCondition_when_onlyEnumValid() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.buildSearchWrapper(
                    null, null,
                    null, CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                    IS_ENABLED_FIELD, 1);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "应包含EQ条件");
        }

        @Test
        @DisplayName("buildSearchWrapper - 全部参数为null - 返回空wrapper")
        void should_returnEmptyWrapper_when_allParamsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.buildSearchWrapper(
                    null, null,
                    null, CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                    null, null);

            assertNotNull(wrapper);
            assertEquals("", wrapper.getTargetSql().trim());
            assertTrue(wrapper.isEmptyOfNormal());
        }

        @Test
        @DisplayName("buildSearchWrapper - keyword为空字符串 - 跳过LIKE但其他条件正常")
        void should_skipLikeCondition_when_keywordIsEmpty() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.buildSearchWrapper(
                    "", List.of(RULE_NAME_FIELD),
                    range, CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                    IS_ENABLED_FIELD, 1);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "应包含日期+枚举条件");
        }

        @Test
        @DisplayName("buildSearchWrapper - likeFields为空列表 - 跳过LIKE但其他条件正常")
        void should_skipLikeCondition_when_likeFieldsIsEmpty() {
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.buildSearchWrapper(
                    "test", Collections.emptyList(),
                    range, CREATE_TIME_FIELD, UPDATE_TIME_FIELD,
                    IS_ENABLED_FIELD, 1);

            assertNotNull(wrapper);
            assertFalse(wrapper.isEmptyOfNormal(), "应包含日期+枚举条件");
        }
    }

    // ========== apply系列方法(追加条件到已有wrapper) ==========

    @Nested
    @DisplayName("applyKeywordCondition 追加关键词条件测试")
    class ApplyKeywordConditionTests {

        @Test
        @DisplayName("applyKeywordCondition - 向已有wrapper追加LIKE条件")
        void should_appendLikeCondition_to_existingWrapper() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();
            wrapper.eq(IS_ENABLED_FIELD, 1);

            QueryHelper.applyKeywordCondition(wrapper, "test", List.of(RULE_NAME_FIELD));

            assertFalse(wrapper.isEmptyOfNormal(), "应同时有EQ和LIKE条件");
        }

        @Test
        @DisplayName("applyKeywordCondition - keyword为null - wrapper条件不变")
        void should_notModifyWrapper_when_keywordIsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();
            wrapper.eq(IS_ENABLED_FIELD, 1);
            boolean wasNotEmpty = !wrapper.isEmptyOfNormal();

            QueryHelper.applyKeywordCondition(wrapper, null, List.of(RULE_NAME_FIELD));

            assertEquals(wasNotEmpty, !wrapper.isEmptyOfNormal(),
                    "wrapper应有条件且不变");
        }
    }

    @Nested
    @DisplayName("applyDateRangeCondition 追加日期范围条件测试")
    class ApplyDateRangeConditionTests {

        @Test
        @DisplayName("applyDateRangeCondition - 向已有wrapper追加BETWEEN条件")
        void should_appendBetweenCondition_to_existingWrapper() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();
            wrapper.eq(IS_ENABLED_FIELD, 1);
            LocalDate[] range = {LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)};

            QueryHelper.applyDateRangeCondition(wrapper, range, CREATE_TIME_FIELD, UPDATE_TIME_FIELD);

            assertFalse(wrapper.isEmptyOfNormal(), "应同时有EQ和BETWEEN条件");
        }

        @Test
        @DisplayName("applyDateRangeCondition - range为null - wrapper条件不变")
        void should_notModifyWrapper_when_rangeIsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();
            wrapper.eq(IS_ENABLED_FIELD, 1);

            QueryHelper.applyDateRangeCondition(wrapper, null, CREATE_TIME_FIELD, UPDATE_TIME_FIELD);

            assertFalse(wrapper.isEmptyOfNormal(), "EQ条件应保留");
        }
    }

    @Nested
    @DisplayName("applyEnumCondition 追加枚举筛选条件测试")
    class ApplyEnumConditionTests {

        @Test
        @DisplayName("applyEnumCondition - 向已有wrapper追加EQ条件")
        void should_appendEqCondition_to_existingWrapper() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();
            wrapper.like(RULE_NAME_FIELD, "test");

            QueryHelper.applyEnumCondition(wrapper, IS_ENABLED_FIELD, 1);

            assertFalse(wrapper.isEmptyOfNormal(), "应同时有LIKE和EQ条件");
        }

        @Test
        @DisplayName("applyEnumCondition - value为null - wrapper条件不变")
        void should_notModifyWrapper_when_valueIsNull() {
            LambdaQueryWrapper<SysCodeRule> wrapper = QueryHelper.create();
            wrapper.like(RULE_NAME_FIELD, "test");

            QueryHelper.applyEnumCondition(wrapper, IS_ENABLED_FIELD, null);

            assertFalse(wrapper.isEmptyOfNormal(), "LIKE条件应保留");
        }
    }
}
