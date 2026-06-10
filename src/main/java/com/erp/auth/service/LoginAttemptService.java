package com.erp.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.AuthPasswordPolicy;
import com.erp.auth.mapper.AuthPasswordPolicyMapper;
import com.erp.auth.vo.LockStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录失败计数与锁定服务.
 *
 * @author AI
 * @since 2026-06-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final String LOGIN_LOCK_PREFIX = "login:lock:";

    private final StringRedisTemplate redisTemplate;
    private final AuthPasswordPolicyMapper policyMapper;

    public void incrementFailCount(String username) {
        String failKey = LOGIN_FAIL_PREFIX + username;
        Long failCount = redisTemplate.opsForValue().increment(failKey);
        log.debug("登录失败计数递增: username={}, failCount={}", username, failCount);

        int maxAttempts = getMaxAttempts();
        if (failCount != null && failCount >= maxAttempts) {
            int lockMinutes = getLockMinutes();
            String lockKey = LOGIN_LOCK_PREFIX + username;
            redisTemplate.opsForValue().set(lockKey, "locked", lockMinutes, TimeUnit.MINUTES);
            redisTemplate.expire(failKey, lockMinutes, TimeUnit.MINUTES);
            log.warn("账号已锁定: username={}, 失败次数={}, 锁定{}分钟", username, failCount, lockMinutes);
        } else {
            Long ttl = redisTemplate.getExpire(failKey);
            if (ttl != null && ttl <= 0) {
                int lockMinutes = getLockMinutes();
                redisTemplate.expire(failKey, lockMinutes, TimeUnit.MINUTES);
            }
        }
    }

    public long getFailCount(String username) {
        String failKey = LOGIN_FAIL_PREFIX + username;
        String countStr = redisTemplate.opsForValue().get(failKey);
        if (countStr == null) {
            return 0;
        }
        try {
            return Long.parseLong(countStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isLocked(String username) {
        String lockKey = LOGIN_LOCK_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    public long getLockRemainingSeconds(String username) {
        String lockKey = LOGIN_LOCK_PREFIX + username;
        Long ttl = redisTemplate.getExpire(lockKey);
        return (ttl != null && ttl > 0) ? ttl : 0;
    }

    public void resetFailCount(String username) {
        String failKey = LOGIN_FAIL_PREFIX + username;
        String lockKey = LOGIN_LOCK_PREFIX + username;
        redisTemplate.delete(failKey);
        redisTemplate.delete(lockKey);
        log.debug("登录成功, 清除失败计数与锁定: username={}", username);
    }

    public LockStatusVO getLockStatus(String username) {
        String lockKey = LOGIN_LOCK_PREFIX + username;
        boolean locked = Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        long remainingSeconds = 0;
        long remainingMinutes = 0;
        if (locked) {
            Long ttl = redisTemplate.getExpire(lockKey);
            remainingSeconds = (ttl != null && ttl > 0) ? ttl : 0;
            remainingMinutes = remainingSeconds > 0 ? (remainingSeconds + 59) / 60 : 0;
        }
        return LockStatusVO.builder()
                .locked(locked)
                .remainingSeconds(remainingSeconds)
                .remainingMinutes(remainingMinutes)
                .build();
    }

    public void unlock(String username) {
        String failKey = LOGIN_FAIL_PREFIX + username;
        String lockKey = LOGIN_LOCK_PREFIX + username;
        redisTemplate.delete(failKey);
        redisTemplate.delete(lockKey);
        log.info("管理员手动解锁用户: username={}", username);
    }

    private int getMaxAttempts() {
        AuthPasswordPolicy policy = getActivePolicy();
        if (policy != null && policy.getMaxAttempts() != null && policy.getMaxAttempts() > 0) {
            return policy.getMaxAttempts();
        }
        return 5;
    }

    private int getLockMinutes() {
        AuthPasswordPolicy policy = getActivePolicy();
        if (policy != null && policy.getLockMinutes() != null && policy.getLockMinutes() > 0) {
            return policy.getLockMinutes();
        }
        return 15;
    }

    private AuthPasswordPolicy getActivePolicy() {
        return policyMapper.selectOne(
                new LambdaQueryWrapper<AuthPasswordPolicy>()
                        .eq(AuthPasswordPolicy::getIsEnabled, true)
                        .orderByDesc(AuthPasswordPolicy::getCreateTime)
                        .last("LIMIT 1")
        );
    }
}
