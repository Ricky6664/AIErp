package com.erp.system.codegen;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.mapper.SysCodeRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 序列号生成器 —— 使用 Redis INCR 原子自增, 不可用时降级到 DB 乐观锁.
 *
 * <p>Redis key = code:seq:{ruleCode}:{yyyyMMdd}, EX=86400 天过期, 实现日重置.
 * 分布式锁 key = code:lock:{ruleCode}, 超时 10 秒.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceGenerator {

    private final StringRedisTemplate redisTemplate;
    private final ApplicationContext applicationContext;

    private static final String SEQ_KEY_PREFIX = "code:seq:";
    private static final String LOCK_KEY_PREFIX = "code:lock:";
    private static final long SEQ_EXPIRE_SECONDS = 86400;
    private static final long LOCK_TIMEOUT_SECONDS = 10;
    private static final int DB_MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 50;

    /**
     * 获取下一个序列值（仅数值）.
     *
     * <p>优先 Redis INCR 原子自增, Redis 不可用时降级到 DB 乐观锁.</p>
     *
     * @param ruleCode 编码规则编码
     * @return 下一个序列值
     */
    public long next(String ruleCode) {
        try {
            return redisNext(ruleCode);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Redis INCR 不可用, 降级到 DB 乐观锁: ruleCode={}", ruleCode, e);
            return dbNext(ruleCode);
        }
    }

    /**
     * 获取下一个序列号（补零后字符串）.
     *
     * @param ruleCode 编码规则编码
     * @param length   补零长度
     * @return 补零后的序列号字符串
     */
    public String getNext(String ruleCode, int length) {
        return String.format("%0" + length + "d", next(ruleCode));
    }

    // ========== Redis INCR ==========

    private long redisNext(String ruleCode) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seqKey = SEQ_KEY_PREFIX + ruleCode + ":" + dateStr;
        String lockKey = LOCK_KEY_PREFIX + ruleCode;

        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(locked)) {
            try {
                Long seq = redisTemplate.opsForValue().increment(seqKey);
                if (seq != null && seq == 1) {
                    redisTemplate.expire(seqKey, SEQ_EXPIRE_SECONDS, TimeUnit.SECONDS);
                }
                return seq != null ? seq : 1;
            } finally {
                redisTemplate.delete(lockKey);
            }
        }
        throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT, "获取分布式锁失败: " + ruleCode);
    }

    // ========== DB 乐观锁降级 ==========

    private long dbNext(String ruleCode) {
        SysCodeRuleMapper mapper = applicationContext.getBean(SysCodeRuleMapper.class);
        SysCodeRule rule = mapper.selectByRuleCode(ruleCode);
        if (rule == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "编码规则不存在: " + ruleCode);
        }
        for (int i = 0; i < DB_MAX_RETRIES; i++) {
            Long oldValue = rule.getCurrentValue();
            long newValue = (oldValue != null ? oldValue : 0) + 1;
            int affected = mapper.updateCurrentVersion(rule.getId(), newValue, oldValue);
            if (affected > 0) {
                return newValue;
            }
            rule = mapper.selectByRuleCode(ruleCode);
            if (rule == null) {
                throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
            }
            try {
                Thread.sleep(RETRY_DELAY_MS * (i + 1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ErrorCode.CACHE_ERROR);
            }
        }
        throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT, "DB序列号生成失败, 请稍后重试");
    }
}
