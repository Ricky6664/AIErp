package com.erp.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.annotation.OperLog;
import com.erp.system.entity.SysOperLog;
import com.erp.system.service.SysOperLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * 操作日志AOP切面.
 *
 * <p>拦截标注 {@link OperLog} 的方法, 自动记录操作人、IP、请求方法、URL、耗时、成功/失败、异常堆栈等,
 * 并通过 {@link SysOperLogService} 异步保存到 sys_oper_log 表.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperLogAspect {

    private static final int MAX_TRACE_LENGTH = 2000;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SysOperLogService sysOperLogService;

    @Pointcut("@annotation(com.erp.common.annotation.OperLog)")
    public void operLogPointcut() {
    }

    /**
     * 环绕通知: 拦截 @OperLog 方法, 记录操作日志并异步保存.
     *
     * @param joinPoint 切点
     * @param operLog   操作日志注解
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("operLogPointcut() && @annotation(operLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperLog sysOperLog = new SysOperLog();
        sysOperLog.setModule(operLog.module());
        sysOperLog.setAction(operLog.action());
        sysOperLog.setDescription(operLog.description());
        sysOperLog.setCreateTime(LocalDateTime.now());

        try {
            // 记录请求信息
            fillRequestInfo(joinPoint, operLog, sysOperLog);

            Object result = joinPoint.proceed();

            // 成功
            sysOperLog.setStatus("SUCCESS");
            sysOperLog.setElapsedMs(System.currentTimeMillis() - startTime);
            if (operLog.saveResponseData() && result != null) {
                try {
                    sysOperLog.setResponseData(OBJECT_MAPPER.writeValueAsString(result));
                } catch (Exception e) {
                    log.warn("序列化响应数据失败", e);
                }
            }
            return result;
        } catch (Throwable e) {
            // 失败
            sysOperLog.setStatus("FAIL");
            sysOperLog.setElapsedMs(System.currentTimeMillis() - startTime);
            sysOperLog.setErrorMsg(e.getMessage());
            if (operLog.isSaveErrorTrace()) {
                sysOperLog.setErrorTrace(truncateStackTrace(e));
            }
            throw e;
        } finally {
            sysOperLogService.save(sysOperLog);
        }
    }

    private void fillRequestInfo(ProceedingJoinPoint joinPoint, OperLog operLog, SysOperLog sysOperLog) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                sysOperLog.setRequestMethod(request.getMethod());
                sysOperLog.setRequestUrl(request.getRequestURI());
                sysOperLog.setOperatorIp(extractClientIp(request));
            }
        } catch (Exception e) {
            log.debug("获取HttpServletRequest失败", e);
        }

        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                sysOperLog.setOperatorId(Long.valueOf(loginId.toString()));
            }
        } catch (Exception e) {
            log.debug("获取登录用户ID失败", e);
        }

        if (operLog.saveRequestData()) {
            try {
                String[] parameterNames = signature.getParameterNames();
                Object[] args = joinPoint.getArgs();
                if (parameterNames != null && args != null && args.length > 0) {
                    StringBuilder sb = new StringBuilder("{");
                    for (int i = 0; i < args.length; i++) {
                        if (i > 0) {
                            sb.append(", ");
                        }
                        // 跳过 HttpServletRequest/HttpServletResponse 等 servlet 类型
                        if (args[i] instanceof HttpServletRequest) {
                            continue;
                        }
                        sb.append("\"").append(parameterNames[i]).append("\": ");
                        try {
                            sb.append(OBJECT_MAPPER.writeValueAsString(args[i]));
                        } catch (Exception e) {
                            sb.append("\"").append(args[i].getClass().getSimpleName()).append("\"");
                        }
                    }
                    sb.append("}");
                    sysOperLog.setRequestData(sb.toString());
                }
            } catch (Exception e) {
                log.debug("序列化请求参数失败", e);
            }
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多层代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String truncateStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        String trace = sw.toString();
        if (trace.length() > MAX_TRACE_LENGTH) {
            trace = trace.substring(0, MAX_TRACE_LENGTH);
        }
        return trace;
    }
}
