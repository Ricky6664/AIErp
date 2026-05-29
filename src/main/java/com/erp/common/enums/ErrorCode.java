package com.erp.common.enums;

import com.erp.common.exception.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码枚举.
 *
 * <p>实现 {@link IErrorCode} 接口, 按模块分段管理错误码, 全系统唯一, 不允许重复.</p>
 *
 * <p>错误码分段规则:
 * <ul>
 *   <li>0        - 成功</li>
 *   <li>10000~19999 - 系统级错误</li>
 *   <li>20000~29999 - 认证授权错误</li>
 *   <li>30000~39999 - 参数校验错误</li>
 *   <li>40000~49999 - 业务逻辑错误</li>
 *   <li>50000~59999 - 数据访问错误</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Getter
@AllArgsConstructor
public enum ErrorCode implements IErrorCode {

    // ========== 成功 ==========
    /** 操作成功 */
    SUCCESS(0, "操作成功"),

    // ========== 系统级错误 10000~19999 ==========
    /** 系统内部错误 */
    INTERNAL_ERROR(10500, "系统内部错误"),
    /** 请求资源不存在 */
    NOT_FOUND(10404, "请求资源不存在"),
    /** 系统请求超时 */
    SYS_TIMEOUT(10001, "系统请求超时"),
    /** 系统繁忙, 请稍后重试 */
    SYS_BUSY(10002, "系统繁忙, 请稍后重试"),
    /** 服务不可用 */
    SYS_SERVICE_UNAVAILABLE(10003, "服务不可用"),

    // ========== 认证授权错误 20000~29999 ==========
    /** 未登录或登录已过期 */
    UNAUTHORIZED(20001, "未登录或登录已过期"),
    /** Token已过期 */
    TOKEN_EXPIRED(20002, "Token已过期"),
    /** 无操作权限 */
    FORBIDDEN(20003, "无操作权限"),
    /** 无效的Token */
    TOKEN_INVALID(20004, "无效的Token"),
    /** 账号已被锁定 */
    ACCOUNT_LOCKED(20005, "账号已被锁定"),
    /** 密码错误 */
    PASSWORD_ERROR(20006, "密码错误"),
    /** 验证码错误或已过期 */
    CAPTCHA_ERROR(20007, "验证码错误或已过期"),

    // ========== 参数校验错误 30000~39999 ==========
    /** 参数校验失败 */
    PARAM_INVALID(30001, "参数校验失败"),
    /** 必填参数缺失 */
    PARAM_MISSING(30002, "必填参数缺失"),
    /** 参数格式不正确 */
    PARAM_FORMAT_ERROR(30003, "参数格式不正确"),
    /** 参数值超出允许范围 */
    PARAM_RANGE_ERROR(30004, "参数值超出允许范围"),
    /** 参数类型不匹配 */
    PARAM_TYPE_ERROR(30005, "参数类型不匹配"),
    /** 参数值重复 */
    PARAM_DUPLICATE(30006, "参数值重复"),

    // ========== 业务逻辑错误 40000~49999 ==========
    /** 业务处理异常 */
    BUSINESS_ERROR(40001, "业务处理异常"),
    /** 数据状态不允许此操作 */
    DATA_STATUS_INVALID(40002, "数据状态不允许此操作"),
    /** 操作过于频繁, 请稍后重试 */
    OPERATION_TOO_FREQUENT(40003, "操作过于频繁, 请稍后重试"),

    // ========== 数据访问错误 50000~59999 ==========
    /** 数据库操作异常 */
    DB_ERROR(50001, "数据库操作异常"),
    /** 数据不存在 */
    DATA_NOT_FOUND(50002, "数据不存在"),
    /** 数据已存在(重复) */
    DATA_ALREADY_EXISTS(50003, "数据已存在"),
    /** 缓存服务异常 */
    CACHE_ERROR(50004, "缓存服务异常"),
    ;

    /** 错误码 */
    private final int code;

    /** 错误消息 */
    private final String message;
}
