package com.erp.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.annotation.RequirePermission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限校验切面.
 *
 * <p>拦截标注 {@link RequirePermission} 的方法, 根据注解配置的逻辑关系
 * 调用 Sa-Token 权限校验 API.
 * 校验失败时 Sa-Token 抛出 {@code NotPermissionException},
 * 由 {@link com.erp.common.exception.GlobalExceptionHandler} 统一处理返回 403.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Pointcut("@annotation(com.erp.common.annotation.RequirePermission)")
    public void requirePermissionPointcut() {
    }

    /**
     * 环绕通知: 在方法执行前校验权限.
     *
     * <p>优先从方法上获取注解, 方法无注解时从类上获取.
     * 支持 AND/OR 两种逻辑模式.</p>
     *
     * @param joinPoint 切点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常或权限校验失败异常
     */
    @Around("requirePermissionPointcut()")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequirePermission annotation = AnnotationUtils.findAnnotation(method, RequirePermission.class);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), RequirePermission.class);
        }

        if (annotation == null) {
            return joinPoint.proceed();
        }

        String[] permissions = annotation.value();
        if (permissions == null || permissions.length == 0) {
            return joinPoint.proceed();
        }

        if (annotation.logic() == com.erp.common.enums.LogicEnum.OR) {
            StpUtil.checkPermissionOr(permissions);
        } else {
            StpUtil.checkPermissionAnd(permissions);
        }

        return joinPoint.proceed();
    }
}
