package com.erp.system.param;

import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 参数缓存刷新器.
 *
 * <p>监听 {@link ParamChangedEvent} 自动清除 Redis 缓存,
 * 并对外暴露手动刷新接口.
 * 管理后台修改参数时发布 ParamChangedEvent, 通知所有节点同步刷新.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@RestController
@RequestMapping("/api/system/params")
@RequiredArgsConstructor
@Tag(name = "系统参数缓存管理", description = "参数缓存刷新接口")
public class ParamCacheRefresher {

    private final StringRedisTemplate redisTemplate;

    /**
     * 监听参数变更事件, 清除对应 Redis 缓存.
     *
     * @param event 参数变更事件
     */
    @EventListener
    public void onParamChanged(ParamChangedEvent event) {
        String category = event.getCategory();
        String key = event.getKey();
        log.info("收到参数变更事件: category={}, key={}, 清除对应缓存", category, key);
        evictCache(category, key);
    }

    /**
     * 手动刷新全部参数缓存.
     *
     * <p>清除所有 sys:param 前缀的 Redis 缓存, 适用于缓存异常或批量参数修改后的场景.</p>
     *
     * @return 已清除的缓存键数量
     */
    @Operation(summary = "手动刷新参数缓存")
    @PostMapping("/refresh")
    public RT<Long> refresh() {
        Set<String> keys = redisTemplate.keys("sys:param:*");
        if (keys == null || keys.isEmpty()) {
            log.info("手动刷新参数缓存, 无可清除的缓存键");
            return RT.ok(0L);
        }
        long count = keys.size();
        redisTemplate.delete(keys);
        log.info("手动刷新参数缓存, 已清除 {} 个缓存键", count);
        return RT.ok(count);
    }

    private void evictCache(String category, String key) {
        redisTemplate.delete("sys:param:" + category + ":" + key);
        redisTemplate.delete("sys:param:list:" + category);
    }
}
