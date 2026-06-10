package com.erp.system.param;

import cn.hutool.json.JSONUtil;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.ParamException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 参数类型转换器.
 *
 * <p>将字符串类型的参数值按指定的值类型转换为目标 Java 类型.
 * 支持 STRING / NUMBER / BOOLEAN / JSON / DATE 五种基础类型转换.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
public final class ParamTypeConverter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ParamTypeConverter() {
    }

    /**
     * 将字符串参数值按值类型转换为目标类型.
     *
     * @param value       参数值（字符串形式）
     * @param valueType   值类型：STRING / NUMBER / BOOLEAN / JSON / DATE
     * @param targetClass 目标 Java 类型
     * @param <T>         泛型类型
     * @return 转换后的对象，value 为 null 时返回 null
     * @throws ParamException 转换失败时抛出，错误码 30002
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(String value, String valueType, Class<T> targetClass) {
        if (value == null) {
            return null;
        }
        if (targetClass == null) {
            throw new ParamException(ErrorCode.PARAM_MISSING, "目标类型不能为空");
        }
        if (targetClass == String.class) {
            return (T) value;
        }

        String vt = valueType != null ? valueType.toUpperCase() : "STRING";
        try {
            switch (vt) {
                case "STRING":
                    return (T) value;
                case "NUMBER":
                    return convertNumber(value, targetClass);
                case "BOOLEAN":
                    return convertBoolean(value, targetClass);
                case "JSON":
                    return convertJson(value, targetClass);
                case "DATE":
                    return convertDate(value, targetClass);
                default:
                    throw new ParamException(ErrorCode.PARAM_MISSING, "不支持的值类型: " + valueType);
            }
        } catch (ParamException e) {
            throw e;
        } catch (Exception e) {
            throw new ParamException(ErrorCode.PARAM_MISSING,
                    "类型转换失败: value=" + value + ", valueType=" + vt + ", target=" + targetClass.getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertNumber(String value, Class<T> targetClass) {
        if (targetClass == Integer.class || targetClass == int.class) {
            return (T) Integer.valueOf(value);
        }
        if (targetClass == Long.class || targetClass == long.class) {
            return (T) Long.valueOf(value);
        }
        if (targetClass == Double.class || targetClass == double.class) {
            return (T) Double.valueOf(value);
        }
        if (targetClass == Float.class || targetClass == float.class) {
            return (T) Float.valueOf(value);
        }
        if (targetClass == BigDecimal.class) {
            return (T) new BigDecimal(value);
        }
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertBoolean(String value, Class<T> targetClass) {
        boolean b = "true".equalsIgnoreCase(value)
                || "1".equals(value)
                || "yes".equalsIgnoreCase(value);
        if (targetClass == Boolean.class || targetClass == boolean.class) {
            return (T) Boolean.valueOf(b);
        }
        return (T) value;
    }

    private static <T> T convertJson(String value, Class<T> targetClass) {
        return JSONUtil.toBean(value, targetClass);
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertDate(String value, Class<T> targetClass) {
        if (targetClass == LocalDate.class) {
            try {
                return (T) LocalDate.parse(value, DATE_FMT);
            } catch (DateTimeParseException e) {
                return (T) LocalDateTime.parse(value, DATETIME_FMT).toLocalDate();
            }
        }
        if (targetClass == LocalDateTime.class) {
            try {
                return (T) LocalDateTime.parse(value, DATETIME_FMT);
            } catch (DateTimeParseException e) {
                return (T) LocalDate.parse(value, DATE_FMT).atStartOfDay();
            }
        }
        return (T) value;
    }
}
