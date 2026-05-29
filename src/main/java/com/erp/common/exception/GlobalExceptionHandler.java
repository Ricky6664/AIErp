package com.erp.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.erp.common.enums.ErrorCode;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器.
 *
 * <p>统一捕获并处理所有异常, 转换为标准 {@link RT} 响应结构, 并记录日志.</p>
 *
 * <p>处理优先级(由具体到通用):
 * <ol>
 *   <li>{@link BusinessException} — 业务逻辑异常</li>
 *   <li>{@link ParamException} — 业务层参数校验异常</li>
 *   <li>{@link AuthException} — 认证异常</li>
 *   <li>{@link PermissionException} — 权限异常</li>
 *   <li>Spring 参数校验异常({@link MethodArgumentNotValidException}, {@link BindException}, {@link ConstraintViolationException} 等)</li>
 *   <li>Sa-Token 认证授权异常({@link NotLoginException}, {@link NotPermissionException} 等)</li>
 *   <li>{@link Exception} — 未知异常兜底</li>
 * </ol>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@RestControllerAdvice
@Slf4j
@Tag(name = "系统基础-全局异常处理", description = "统一捕获并处理所有异常, 转换为标准RT响应结构")
public class GlobalExceptionHandler {

    // ========== 自定义业务异常 ==========

    /**
     * 业务异常.
     *
     * <p>级别: WARN. 业务逻辑校验失败, 属于可预期异常, 不打印堆栈.</p>
     *
     * @param e       业务异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理业务异常", description = "捕获BusinessException, 业务逻辑校验失败场景")
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("[BusinessException] uri={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMsg());
        return RT.fail(e.getCode(), e.getMsg());
    }

    /**
     * 参数异常(业务层抛出).
     *
     * <p>级别: WARN. 业务层主动抛出的参数校验异常.</p>
     *
     * @param e       参数异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理参数异常", description = "捕获ParamException, 业务层主动抛出的参数校验异常")
    @ExceptionHandler(ParamException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleParamException(ParamException e, HttpServletRequest request) {
        log.warn("[ParamException] uri={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMsg());
        return RT.fail(e.getCode(), e.getMsg());
    }

    /**
     * 认证异常.
     *
     * <p>级别: WARN. 用户未登录/Token 失效等认证场景.</p>
     *
     * @param e       认证异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理认证异常", description = "捕获AuthException, 用户未登录或Token失效等认证场景")
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleAuthException(AuthException e, HttpServletRequest request) {
        log.warn("[AuthException] uri={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMsg());
        return RT.fail(e.getCode(), e.getMsg());
    }

    /**
     * 权限异常.
     *
     * <p>级别: WARN. 用户无操作权限/无数据权限等场景.</p>
     *
     * @param e       权限异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理权限异常", description = "捕获PermissionException, 用户无操作权限或无数据权限场景")
    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handlePermissionException(PermissionException e, HttpServletRequest request) {
        log.warn("[PermissionException] uri={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMsg());
        return RT.fail(e.getCode(), e.getMsg());
    }

    // ========== Spring 参数校验异常 ==========

    /**
     * 请求体参数校验异常(@Valid / @Validated + @RequestBody).
     *
     * @param e       校验异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理请求体校验异常", description = "捕获@Valid/@Validated + @RequestBody触发的参数校验失败")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[MethodArgumentNotValidException] uri={}, message={}", request.getRequestURI(), msg);
        return RT.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), msg);
    }

    /**
     * 表单绑定异常(@ModelAttribute / @Valid).
     *
     * @param e       绑定异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理表单绑定异常", description = "捕获@ModelAttribute/@Valid触发的表单绑定校验失败")
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleBindException(BindException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[BindException] uri={}, message={}", request.getRequestURI(), msg);
        return RT.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), msg);
    }

    /**
     * 参数约束违反异常(@Validated + @RequestParam / @PathVariable / 方法级校验).
     *
     * <p>级别: WARN. 由 {@code @Validated} 注解在方法参数或类级别触发,
     * 与 {@link MethodArgumentNotValidException} 互补.</p>
     *
     * @param e       约束违反异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理参数约束违反异常", description = "捕获@Validated + @RequestParam/@PathVariable触发的方法级校验失败")
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("[ConstraintViolationException] uri={}, message={}", request.getRequestURI(), msg);
        return RT.fail(ErrorCode.PARAM_INVALID.getCode(), msg);
    }

    /**
     * 必填参数缺失.
     *
     * @param e       缺失异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理必填参数缺失", description = "捕获MissingServletRequestParameterException, 缺少必填请求参数")
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String msg = "缺少必填参数: " + e.getParameterName();
        log.warn("[MissingServletRequestParameterException] uri={}, message={}", request.getRequestURI(), msg);
        return RT.fail(ErrorCode.PARAM_MISSING.getCode(), msg);
    }

    /**
     * 参数类型不匹配.
     *
     * @param e       类型不匹配异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理参数类型不匹配", description = "捕获MethodArgumentTypeMismatchException, 请求参数类型转换失败")
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String msg = "参数类型错误: " + e.getName() + ", 期望类型: "
                + (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        log.warn("[MethodArgumentTypeMismatchException] uri={}, message={}", request.getRequestURI(), msg);
        return RT.fail(ErrorCode.PARAM_TYPE_ERROR.getCode(), msg);
    }

    /**
     * 请求体 JSON 解析失败.
     *
     * @param e       解析异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理请求体解析异常", description = "捕获HttpMessageNotReadableException, 请求体JSON解析失败")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("[HttpMessageNotReadableException] uri={}, message={}", request.getRequestURI(), e.getMessage());
        return RT.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), "请求体格式错误");
    }

    // ========== Sa-Token 认证授权异常 ==========

    /**
     * Sa-Token 未登录异常.
     *
     * @param e       未登录异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理Sa-Token未登录异常", description = "捕获NotLoginException, Sa-Token框架触发的未登录异常")
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        log.warn("[NotLoginException] uri={}, type={}, message={}", request.getRequestURI(), e.getType(), e.getMessage());
        return RT.fail(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
    }

    /**
     * Sa-Token 无权限异常.
     *
     * @param e       无权限异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理Sa-Token无权限异常", description = "捕获NotPermissionException, Sa-Token框架触发的无权限异常")
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        log.warn("[NotPermissionException] uri={}, permission={}", request.getRequestURI(), e.getPermission());
        return RT.fail(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage());
    }

    /**
     * Sa-Token 无角色异常.
     *
     * @param e       无角色异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理Sa-Token无角色异常", description = "捕获NotRoleException, Sa-Token框架触发的无角色异常")
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        log.warn("[NotRoleException] uri={}, role={}", request.getRequestURI(), e.getRole());
        return RT.fail(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage());
    }

    // ========== HTTP 方法/资源异常 ==========

    /**
     * 请求方法不支持.
     *
     * @param e       方法不支持异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理请求方法不支持", description = "捕获HttpRequestMethodNotSupportedException, HTTP请求方法不被允许")
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public RT<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("[HttpRequestMethodNotSupportedException] uri={}, method={}", request.getRequestURI(), e.getMethod());
        return RT.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 媒体类型不支持.
     *
     * @param e       媒体类型不支持异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理媒体类型不支持", description = "捕获HttpMediaTypeNotSupportedException, 请求的Content-Type不被支持")
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public RT<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.warn("[HttpMediaTypeNotSupportedException] uri={}, contentType={}", request.getRequestURI(), e.getContentType());
        return RT.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), "不支持的媒体类型: " + e.getContentType());
    }

    /**
     * 资源不存在(404).
     *
     * @param e       资源不存在异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "处理资源不存在(404)", description = "捕获NoResourceFoundException, 请求的资源不存在")
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RT<Void> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("[NoResourceFoundException] uri={}", request.getRequestURI());
        return RT.fail(ErrorCode.NOT_FOUND.getCode(), ErrorCode.NOT_FOUND.getMessage());
    }

    // ========== 兜底异常 ==========

    /**
     * 未知异常兜底处理.
     *
     * <p>级别: ERROR. 必须打印完整堆栈, 便于问题排查.</p>
     *
     * @param e       未知异常
     * @param request 请求
     * @return 统一响应
     */
    @Operation(summary = "未知异常兜底处理", description = "捕获Exception, 所有未被特定处理的异常统一由此方法兜底")
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public RT<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("[Exception] uri={}, 系统未知异常", request.getRequestURI(), e);
        return RT.fail(ErrorCode.INTERNAL_ERROR.getCode(), ErrorCode.INTERNAL_ERROR.getMessage());
    }
}
