package com.erp.system.service.impl;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.service.SysCodeRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 编码规则 Service 实现.
 *
 * @author AI
 * @since 2026-05-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysCodeRuleServiceImpl extends SysCodeRuleService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_KEY_PREFIX = "code:lock:";
    private static final String CACHE_KEY_PREFIX = "code:cache:";
    private static final long LOCK_TIMEOUT_SECONDS = 10;
    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 100;

    @Override
    public String preview(Long ruleId) {
        // TODO: 实现编码预览逻辑（后续任务实现）
        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码预览功能待实现");
    }

    @Override
    public String generate(String ruleCode) {
        String lockKey = LOCK_KEY_PREFIX + ruleCode;
        for (int i = 0; i < MAX_RETRY; i++) {
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(locked)) {
                try {
                    // TODO: 实现编码生成逻辑（后续任务实现）
                    throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码生成功能待实现");
                } finally {
                    redisTemplate.delete(lockKey);
                }
            }
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码生成被中断", e);
            }
        }
        throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT, "编码生成失败，请稍后重试");
    }

    @Override
    public void refreshCache(String ruleCode) {
        String cacheKey = CACHE_KEY_PREFIX + ruleCode;
        redisTemplate.delete(cacheKey);
        // TODO: 重新加载编码规则到缓存（后续任务实现）
        log.info("编码规则缓存已清除: ruleCode={}", ruleCode);
    }
}
