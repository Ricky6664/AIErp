package com.erp.system.cache.service;

import com.erp.system.cache.vo.CacheKeyVO;
import com.erp.system.cache.vo.CacheStatsVO;

import java.util.List;

/**
 * 缓存管理Service接口.
 *
 * @author AI
 * @since 2026-06-07
 */
public interface ICacheManagerService {

    List<CacheKeyVO> scanKeys(String pattern, int count);

    Long getKeyTTL(String key);

    String getKeyValue(String key);

    void deleteByKey(String key);

    void deleteByPattern(String pattern);

    CacheStatsVO getCacheStats();
}
