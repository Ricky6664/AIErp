package com.erp.auth.service;

import com.erp.auth.entity.SysLoginLog;
import com.erp.auth.mapper.SysLoginLogMapper;
import com.erp.util.IpAddressUtil;
import com.erp.util.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录日志异步写入服务.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final SysLoginLogMapper sysLoginLogMapper;

    /**
     * 异步写入登录日志(便捷方法).
     *
     * <p>自动从HttpServletRequest解析IP、浏览器、操作系统信息.</p>
     *
     * @param userId  用户ID(失败时可为null)
     * @param request HTTP请求
     * @param status  登录状态: "SUCCESS" 或 "FAIL"
     */
    @Async("logAsyncExecutor")
    public void asyncWriteLog(Long userId, HttpServletRequest request, String status) {
        try {
            String ip = IpAddressUtil.getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String browser = UserAgentUtil.parseBrowser(userAgent);
            String os = UserAgentUtil.parseOs(userAgent);
            if ("SUCCESS".equals(status)) {
                logSuccessInternal(userId, ip, browser, os, "PASSWORD");
            } else {
                logFailureInternal(userId, null, ip, browser, os, "PASSWORD", null);
            }
        } catch (Exception e) {
            log.error("登录日志异步写入失败: userId={}, status={}", userId, status, e);
        }
    }

    /**
     * 异步更新登出时间.
     */
    @Async("logAsyncExecutor")
    public void updateLogoutTime(Long userId) {
        try {
            SysLoginLog latest = sysLoginLogMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysLoginLog>()
                            .eq(SysLoginLog::getUserId, userId)
                            .eq(SysLoginLog::getStatus, "SUCCESS")
                            .orderByDesc(SysLoginLog::getCreatedAt)
                            .last("LIMIT 1")
            );
            if (latest != null) {
                latest.setLogoutAt(LocalDateTime.now());
                sysLoginLogMapper.updateById(latest);
                log.debug("登出时间已更新: userId={}", userId);
            }
        } catch (Exception e) {
            log.error("更新登出时间失败: userId={}", userId, e);
        }
    }

    /**
     * 异步写入登录成功日志.
     */
    @Async("logAsyncExecutor")
    public void logSuccess(Long userId, String ip, String browser, String os, String loginMethod) {
        logSuccessInternal(userId, ip, browser, os, loginMethod);
    }

    /**
     * 异步写入登录失败日志.
     */
    @Async("logAsyncExecutor")
    public void logFailure(Long userId, String username, String ip, String browser, String os,
                           String loginMethod, String failReason) {
        logFailureInternal(userId, username, ip, browser, os, loginMethod, failReason);
    }

    private void logSuccessInternal(Long userId, String ip, String browser, String os, String loginMethod) {
        try {
            SysLoginLog logEntry = buildLog(userId, ip, browser, os, loginMethod, "SUCCESS", null);
            sysLoginLogMapper.insert(logEntry);
            log.debug("登录成功日志已写入: userId={}", userId);
        } catch (Exception e) {
            log.error("登录成功日志写入失败: userId={}", userId, e);
        }
    }

    private void logFailureInternal(Long userId, String username, String ip, String browser, String os,
                                    String loginMethod, String failReason) {
        try {
            SysLoginLog logEntry = buildLog(userId, ip, browser, os, loginMethod, "FAIL", failReason);
            sysLoginLogMapper.insert(logEntry);
            log.debug("登录失败日志已写入: userId={}, reason={}", userId, failReason);
        } catch (Exception e) {
            log.error("登录失败日志写入失败: userId={}, reason={}", userId, failReason, e);
        }
    }

    private SysLoginLog buildLog(Long userId, String ip, String browser, String os,
                                  String loginMethod, String status, String failReason) {
        SysLoginLog logEntry = new SysLoginLog();
        logEntry.setUserId(userId);
        logEntry.setLoginTime(LocalDateTime.now());
        logEntry.setIpAddress(ip);
        logEntry.setBrowser(browser);
        logEntry.setOs(os);
        logEntry.setLoginMethod(loginMethod);
        logEntry.setStatus(status);
        logEntry.setFailReason(failReason);
        return logEntry;
    }
}
