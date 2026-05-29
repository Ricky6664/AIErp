package com.erp.system.param;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 参数缓存刷新器.
 *
 * <p>监听 {@link ParamChangedEvent} 自动清除 Redis 缓存.
 * 手动刷新接口已迁移至 {@link com.erp.system.controller.SysParamController#refresh()}.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Component
@RequiredArgsConstructor
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

    private void evictCache(String category, String key) {
        redisTemplate.delete("sys:param:" + category + ":" + key);
        redisTemplate.delete("sys:param:list:" + category);
    }
}
