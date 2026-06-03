package com.erp.auth.service;

import com.erp.auth.entity.SysLoginLog;
import com.erp.auth.mapper.SysLoginLogMapper;
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
     * 异步更新登出时间.
     */
    @Async
    public void updateLogoutTime(Long userId) {
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
    }

    /**
     * 异步写入登录成功日志.
     */
    @Async
    public void logSuccess(Long userId, String ip, String browser, String os, String loginMethod) {
        SysLoginLog logEntry = buildLog(userId, ip, browser, os, loginMethod, "SUCCESS", null);
        sysLoginLogMapper.insert(logEntry);
        log.debug("登录成功日志已写入: userId={}", userId);
    }

    /**
     * 异步写入登录失败日志.
     */
    @Async
    public void logFailure(Long userId, String username, String ip, String browser, String os,
                           String loginMethod, String failReason) {
        SysLoginLog logEntry = buildLog(userId, ip, browser, os, loginMethod, "FAIL", failReason);
        logEntry.setUserId(userId);
        sysLoginLogMapper.insert(logEntry);
        log.debug("登录失败日志已写入: username={}, reason={}", username, failReason);
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
