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
}
