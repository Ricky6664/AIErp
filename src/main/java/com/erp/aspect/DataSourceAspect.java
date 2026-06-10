package com.erp.aspect;

import com.erp.common.annotation.DS;
import com.erp.config.DataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 多数据源切换切面.
 *
 * <p>拦截标注了 @DS 注解的类或方法, 在方法执行前设置数据源 key,
 * 方法执行后清理, 避免线程复用时的数据源污染.
 * 方法级注解优先级高于类级注解.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Aspect
@Component
@Order(-1)
public class DataSourceAspect {

    private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    /**
     * 切入点: 匹配类或方法上标注了 @DS 注解.
     */
    @Pointcut("@within(com.erp.common.annotation.DS) || @annotation(com.erp.common.annotation.DS)")
    public void dsPointcut() {
    }

    /**
     * 环绕通知: 根据 @DS 注解切换数据源.
     */
    @Around("dsPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 优先取方法级注解, 其次取类级注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DS methodDs = signature.getMethod().getAnnotation(DS.class);
        DS classDs = joinPoint.getTarget().getClass().getAnnotation(DS.class);

        String dsKey = null;
        if (methodDs != null) {
            dsKey = methodDs.value();
        } else if (classDs != null) {
            dsKey = classDs.value();
        }

        if (dsKey != null) {
            log.debug("Switching data source to [{}] for {}.{}",
                    dsKey, joinPoint.getTarget().getClass().getSimpleName(), signature.getName());
            DataSourceContextHolder.setDataSource(dsKey);
        }

        try {
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}
