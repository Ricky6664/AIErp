package com.erp.system.cache.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.result.RT;
import com.erp.system.cache.service.ICacheManagerService;
import com.erp.system.cache.vo.CacheKeyVO;
import com.erp.system.cache.vo.CacheStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 缓存管理Controller.
 *
 * @author AI
 * @since 2026-06-08
 */
@Slf4j
@Tag(name = "缓存管理", description = "Redis缓存管理接口")
@RestController
@RequestMapping("/api/system/cache")
@RequiredArgsConstructor
public class CacheManagerController {

    private final ICacheManagerService cacheManagerService;

    @Operation(summary = "扫描缓存Key列表")
    @RequirePermission("system:cache:manage")
    @GetMapping("/list")
    public RT<List<CacheKeyVO>> list(
            @Parameter(description = "匹配模式") @RequestParam(defaultValue = "*") String pattern,
            @Parameter(description = "扫描数量") @RequestParam(defaultValue = "100") int count) {
        return RT.ok(cacheManagerService.scanKeys(pattern, count));
    }

    @Operation(summary = "获取缓存Value")
    @RequirePermission("system:cache:manage")
    @GetMapping("/{key}/value")
    public RT<String> getValue(
            @Parameter(description = "缓存键") @PathVariable String key) {
        return RT.ok(cacheManagerService.getKeyValue(key));
    }

    @Operation(summary = "获取缓存统计信息")
    @RequirePermission("system:cache:manage")
    @GetMapping("/stats")
    public RT<CacheStatsVO> stats() {
        return RT.ok(cacheManagerService.getCacheStats());
    }

    @Operation(summary = "删除单个缓存Key")
    @RequirePermission("system:cache:manage")
    @DeleteMapping("/{key}")
    public RT<Void> deleteByKey(
            @Parameter(description = "缓存键") @PathVariable String key) {
        cacheManagerService.deleteByKey(key);
        log.info("缓存Key已删除: {}", key);
        return RT.ok();
    }

    @Operation(summary = "按模式批量删除缓存")
    @RequirePermission("system:cache:manage")
    @DeleteMapping("/batch")
    public RT<Void> deleteByPattern(
            @Parameter(description = "匹配模式") @RequestParam String pattern) {
        cacheManagerService.deleteByPattern(pattern);
        log.info("缓存模式删除完成: pattern={}", pattern);
        return RT.ok();
    }
}
