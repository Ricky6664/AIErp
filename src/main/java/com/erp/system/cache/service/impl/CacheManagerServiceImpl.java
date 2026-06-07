package com.erp.system.cache.service.impl;

import com.erp.system.cache.service.ICacheManagerService;
import com.erp.system.cache.vo.CacheKeyVO;
import com.erp.system.cache.vo.CacheStatsVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存管理Service实现.
 *
 * <p>封装RedisTemplate操作, 支持SCAN遍历Key列表(禁止KEYS)、TTL查询、Value读取(JSON反序列化)、
 * 按Key/模式清除缓存, 并记录操作日志.</p>
 *
 * @author AI
 * @since 2026-06-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheManagerServiceImpl implements ICacheManagerService {

    private static final int MAX_DELETE_COUNT = 1000;
    private static final int DEFAULT_SCAN_COUNT = 100;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final StringRedisTemplate redisTemplate;

    @Override
    public List<CacheKeyVO> scanKeys(String pattern, int count) {
        String matchPattern = buildPattern(pattern);
        int scanCount = count > 0 ? count : DEFAULT_SCAN_COUNT;
        StopWatch sw = new StopWatch();
        sw.start();

        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> result = new LinkedHashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(matchPattern)
                    .count(scanCount)
                    .build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                cursor.forEachRemaining(k -> {
                    String key = new String(k, StandardCharsets.UTF_8);
                    result.add(key);
                });
            }
            return result;
        });

        sw.stop();
        log.info("SCAN pattern={}, count={}, 耗时={}ms, 匹配数={}",
                matchPattern, scanCount, sw.getTotalTimeMillis(),
                keys != null ? keys.size() : 0);

        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        List<CacheKeyVO> result = new ArrayList<>();
        for (String key : keys) {
            Long ttl = getKeyTTL(key);
            String type = getKeyType(key);
            Long size = getKeySize(key);
            result.add(CacheKeyVO.builder()
                    .key(key)
                    .type(type)
                    .ttl(ttl)
                    .size(size)
                    .build());
        }
        return result;
    }

    @Override
    public Long getKeyTTL(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl == null) {
            return -2L;
        }
        return ttl;
    }

    @Override
    public String getKeyValue(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        try {
            Object parsed = OBJECT_MAPPER.readTree(value);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
        } catch (Exception e) {
            log.debug("Key {} 的Value不是JSON格式, 返回原始字符串", key);
            return value;
        }
    }

    @Override
    public void deleteByKey(String key) {
        Boolean deleted = redisTemplate.delete(key);
        log.info("缓存删除操作: key={}, 结果={}, 操作时间={}", key, deleted, System.currentTimeMillis());
    }

    @Override
    public void deleteByPattern(String pattern) {
        String matchPattern = buildPattern(pattern);
        StopWatch sw = new StopWatch();
        sw.start();

        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> result = new LinkedHashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(matchPattern)
                    .count(100)
                    .build();
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                cursor.forEachRemaining(k -> {
                    String key = new String(k, StandardCharsets.UTF_8);
                    result.add(key);
                    if (result.size() >= MAX_DELETE_COUNT) {
                        return;
                    }
                });
            }
            return result;
        });

        if (keys == null || keys.isEmpty()) {
            log.info("模式删除: 未匹配到Key, pattern={}", matchPattern);
            return;
        }

        if (keys.size() >= MAX_DELETE_COUNT) {
            log.warn("模式删除: 匹配Key数已达上限={}, 仅删除前{}个, pattern={}",
                    MAX_DELETE_COUNT, MAX_DELETE_COUNT, matchPattern);
        }

        Long deletedCount = redisTemplate.delete(keys);
        sw.stop();
        log.info("模式删除: pattern={}, 删除数={}, 耗时={}ms, 操作时间={}",
                matchPattern, deletedCount, sw.getTotalTimeMillis(), System.currentTimeMillis());
    }

    @Override
    public CacheStatsVO getCacheStats() {
        Properties info = redisTemplate.execute((RedisCallback<Properties>) connection -> {
            Properties props = new Properties();
            try {
                Properties serverInfo = connection.serverCommands().info();
                props.putAll(serverInfo);
            } catch (Exception e) {
                log.warn("获取Redis INFO失败: {}", e.getMessage());
            }
            return props;
        });

        Long dbSize = redisTemplate.execute((RedisCallback<Long>) connection -> {
            try {
                return connection.serverCommands().dbSize();
            } catch (Exception e) {
                log.warn("获取Redis DBSize失败: {}", e.getMessage());
                return 0L;
            }
        });

        String serverVersion = info != null ? info.getProperty("redis_version", "unknown") : "unknown";
        Long usedMemory = info != null ? parseMemory(info.getProperty("used_memory")) : 0L;
        Long uptime = info != null ? parseLong(info.getProperty("uptime_in_seconds")) : 0L;

        return CacheStatsVO.builder()
                .keyCount(dbSize)
                .serverVersion(serverVersion)
                .usedMemory(usedMemory)
                .uptimeInSeconds(uptime)
                .build();
    }

    private String buildPattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return "*";
        }
        return pattern;
    }

    private String getKeyType(String key) {
        try {
            return redisTemplate.type(key).code();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private Long getKeySize(String key) {
        try {
            Long size = redisTemplate.execute((RedisCallback<Long>) connection -> {
                try {
                    return connection.stringCommands().strLen(key.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    return 0L;
                }
            });
            return size != null ? size : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private Long parseMemory(String usedMemoryStr) {
        if (usedMemoryStr == null) {
            return 0L;
        }
        try {
            return Long.parseLong(usedMemoryStr);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private Long parseLong(String value) {
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
