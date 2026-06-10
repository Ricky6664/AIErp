package com.erp.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.time.LocalDate;
import java.util.List;

/**
 * 通用查询条件构造器(QueryHelper).
 *
 * <p>提供常见的查询条件构造方法, 支持链式调用返回 LambdaQueryWrapper.
 * 所有方法均为静态工厂方法, 每次调用创建一个新的 wrapper 实例.</p>
 *
 * <p>使用示例:
 * <pre>{@code
 * LambdaQueryWrapper<User> wrapper = QueryHelper.<User>buildLikeWrapper("张三", List.of(User::getName, User::getPhone));
 * List<User> users = userMapper.selectList(wrapper);
 * }</pre>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
public final class QueryHelper {

    private QueryHelper() {
        throw new UnsupportedOperationException("Utility class, do not instantiate");
    }

    /**
     * 构建 OR 模糊查询条件.
     *
     * <p>对 keyword 中的 LIKE 通配符({@code %}, {@code _})进行转义.
     * 多个字段之间以 OR 连接, 任一字段匹配即命中.</p>
     *
     * @param keyword 搜索关键词(为 null 或空时返回空 wrapper)
     * @param fields  要搜索的字段列表
     * @param <T>     实体类型
     * @return LambdaQueryWrapper 实例
     */
    public static <T> LambdaQueryWrapper<T> buildLikeWrapper(String keyword, List<SFunction<T, ?>> fields) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        if (keyword == null || keyword.isEmpty() || fields == null || fields.isEmpty()) {
            return wrapper;
        }
        String escaped = escapeLikeKeyword(keyword);
        return wrapper.and(w -> {
            boolean first = true;
            for (SFunction<T, ?> field : fields) {
                if (first) {
                    w.like(field, escaped);
                    first = false;
                } else {
                    w.or().like(field, escaped);
                }
            }
        });
    }

    /**
     * 构建日期范围查询条件(闭区间).
     *
     * <p>使用 BETWEEN 构造 startField BETWEEN range[0] AND range[1] 条件.
     * range 为 null 或元素为 null 时返回空 wrapper.</p>
     *
     * @param startField 开始日期字段(SFunction 引用)
     * @param endField   结束日期字段(SFunction 引用)
     * @param range      日期范围 [start, end], 闭区间
     * @param <T>        实体类型
     * @return LambdaQueryWrapper 实例
     */
    public static <T> LambdaQueryWrapper<T> buildDateRangeWrapper(
            SFunction<T, ?> startField,
            SFunction<T, ?> endField,
            LocalDate[] range) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        if (range == null || range.length < 2 || range[0] == null || range[1] == null) {
            return wrapper;
        }
        wrapper.between(startField, range[0], range[1])
               .between(endField, range[0], range[1]);
        return wrapper;
    }

    /**
     * 构建枚举/状态筛选条件.
     *
     * <p>使用 EQ 构造 field = value 精确匹配条件.
     * value 为 null 时返回空 wrapper.</p>
     *
     * @param field 枚举/状态字段(SFunction 引用)
     * @param value 筛选值(为 null 时返回空 wrapper)
     * @param <T>   实体类型
     * @return LambdaQueryWrapper 实例
     */
    public static <T> LambdaQueryWrapper<T> buildEnumWrapper(SFunction<T, ?> field, Integer value) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        if (value != null) {
            wrapper.eq(field, value);
        }
        return wrapper;
    }

    /**
     * 转义 LIKE 关键字中的通配符.
     *
     * <p>MySQL/PostgreSQL 中 LIKE 的通配符为 {@code %} 和 {@code _}.
     * 用户输入的关键字如果包含这些字符, 需要进行转义, 否则会导致非预期的模糊匹配.</p>
     *
     * @param keyword 原始关键字
     * @return 转义后的关键字
     */
    private static String escapeLikeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        return keyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * 创建空 wrapper.
     *
     * <p>便捷方法, 用于链式调用起点或需要手动拼装条件的场景.</p>
     *
     * @param <T> 实体类型
     * @return 空的 LambdaQueryWrapper 实例
     */
    public static <T> LambdaQueryWrapper<T> create() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 构建查询页组合搜索条件.
     *
     * <p>将关键词模糊搜索、日期范围筛选、枚举状态筛选三种常见搜索条件
     * 合并到单个 LambdaQueryWrapper 中, 适用于查询页搜索交互场景.
     * 各条件独立可空, 为 null 或空时跳过对应条件.</p>
     *
     * <p>使用示例:
     * <pre>{@code
     * LambdaQueryWrapper<User> wrapper = QueryHelper.<User>buildSearchWrapper(
     *     "张三", List.of(User::getName, User::getPhone),
     *     new LocalDate[]{start, end}, User::getCreateTime, User::getUpdateTime,
     *     User::getStatus, 1);
     * Page<User> page = userMapper.selectPage(new Page<>(1, 20), wrapper);
     * }</pre>
     * </p>
     *
     * @param keyword     搜索关键词(为 null 或空时跳过LIKE条件)
     * @param likeFields  模糊搜索字段列表(为 null 或空时跳过LIKE条件)
     * @param dateRange   日期范围 [start, end](为 null 或元素为 null 时跳过BETWEEN条件)
     * @param startField  开始日期字段
     * @param endField    结束日期字段
     * @param enumField   枚举/状态字段(为 null 或 value 为 null 时跳过EQ条件)
     * @param enumValue   枚举/状态筛选值
     * @param <T>         实体类型
     * @return 组合了所有非空条件的 LambdaQueryWrapper 实例
     */
    public static <T> LambdaQueryWrapper<T> buildSearchWrapper(
            String keyword, List<SFunction<T, ?>> likeFields,
            LocalDate[] dateRange, SFunction<T, ?> startField, SFunction<T, ?> endField,
            SFunction<T, ?> enumField, Integer enumValue) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        applyKeywordCondition(wrapper, keyword, likeFields);
        applyDateRangeCondition(wrapper, dateRange, startField, endField);
        applyEnumCondition(wrapper, enumField, enumValue);
        return wrapper;
    }

    /**
     * 向已有 wrapper 追加关键词模糊搜索条件.
     *
     * @param wrapper    目标 wrapper
     * @param keyword    搜索关键词
     * @param likeFields 模糊搜索字段列表
     * @param <T>        实体类型
     */
    public static <T> void applyKeywordCondition(
            LambdaQueryWrapper<T> wrapper,
            String keyword, List<SFunction<T, ?>> likeFields) {
        if (keyword == null || keyword.isEmpty() || likeFields == null || likeFields.isEmpty()) {
            return;
        }
        String escaped = escapeLikeKeyword(keyword);
        wrapper.and(w -> {
            boolean first = true;
            for (SFunction<T, ?> field : likeFields) {
                if (first) {
                    w.like(field, escaped);
                    first = false;
                } else {
                    w.or().like(field, escaped);
                }
            }
        });
    }

    /**
     * 向已有 wrapper 追加日期范围筛选条件.
     *
     * @param wrapper    目标 wrapper
     * @param dateRange  日期范围 [start, end]
     * @param startField 开始日期字段
     * @param endField   结束日期字段
     * @param <T>        实体类型
     */
    public static <T> void applyDateRangeCondition(
            LambdaQueryWrapper<T> wrapper,
            LocalDate[] dateRange,
            SFunction<T, ?> startField,
            SFunction<T, ?> endField) {
        if (dateRange == null || dateRange.length < 2 || dateRange[0] == null || dateRange[1] == null) {
            return;
        }
        wrapper.between(startField, dateRange[0], dateRange[1])
               .between(endField, dateRange[0], dateRange[1]);
    }

    /**
     * 向已有 wrapper 追加枚举/状态筛选条件.
     *
     * @param wrapper   目标 wrapper
     * @param enumField 枚举/状态字段
     * @param enumValue 筛选值(为 null 时跳过)
     * @param <T>       实体类型
     */
    public static <T> void applyEnumCondition(
            LambdaQueryWrapper<T> wrapper,
            SFunction<T, ?> enumField, Integer enumValue) {
        if (enumValue != null && enumField != null) {
            wrapper.eq(enumField, enumValue);
        }
    }
}
