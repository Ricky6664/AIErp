package com.erp.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字段校验错误项.
 *
 * <p>用于封装单个字段的校验失败信息, 由 {@code GlobalExceptionHandler}
 * 从 {@code MethodArgumentNotValidException} / {@code BindException} /
 * {@code ConstraintViolationException} 中提取并组装, 通过 {@code RT.data} 返回给前端.</p>
 *
 * <p>字段说明:
 * <ul>
 *   <li>{@code field} — 校验失败的字段名(嵌套路径使用点号分隔, 如 {@code user.email})</li>
 *   <li>{@code message} — 校验失败的提示信息(来自校验注解的 {@code message} 属性)</li>
 *   <li>{@code rejectedValue} — 客户端提交的非法值(便于前端回显表单)</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Schema(description = "字段校验错误项")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 校验失败的字段名(嵌套路径使用点号分隔, 如 user.email) */
    @Schema(description = "校验失败的字段名", example = "username")
    private String field;

    /** 校验失败的提示信息 */
    @Schema(description = "校验失败的提示信息", example = "用户名不能为空")
    private String message;

    /** 客户端提交的非法值(便于前端回显表单) */
    @Schema(description = "客户端提交的非法值")
    private Object rejectedValue;

    // ========== 静态工厂方法 ==========

    /**
     * 创建校验错误项(完整参数).
     *
     * @param field         字段名
     * @param message       校验消息
     * @param rejectedValue 非法值
     * @return 校验错误项
     */
    public static ValidationError of(String field, String message, Object rejectedValue) {
        return new ValidationError(field, message, rejectedValue);
    }

    /**
     * 创建校验错误项(无非法值).
     *
     * @param field   字段名
     * @param message 校验消息
     * @return 校验错误项
     */
    public static ValidationError of(String field, String message) {
        return new ValidationError(field, message, null);
    }
}
