package com.erp.system.controller;

import com.erp.common.annotation.RequirePermission;
import com.erp.common.exception.BusinessException;
import com.erp.common.enums.ErrorCode;
import com.erp.common.result.RT;
import com.erp.system.service.SysParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Tag(name = "系统参数管理", description = "系统参数CRUD接口")
@RestController
@RequestMapping("/api/system/params")
@RequiredArgsConstructor
public class SysParamController {

    private final SysParamService sysParamService;
    private final StringRedisTemplate redisTemplate;

    @Operation(summary = "按分类查询参数列表")
    @RequirePermission("system:param:query")
    @GetMapping("/category/{category}")
    public RT<List<Map<String, Object>>> listByCategory(
            @Parameter(description = "参数分类") @PathVariable String category) {
        return RT.ok(sysParamService.listByCategory(category));
    }

    @Operation(summary = "获取参数值")
    @RequirePermission("system:param:query")
    @GetMapping("/{category}/{key}")
    public RT<String> getByCategoryAndKey(
            @Parameter(description = "参数分类") @PathVariable String category,
            @Parameter(description = "参数键") @PathVariable String key) {
        String value = sysParamService.getStr(category, key);
        if (value == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "参数不存在: " + category + "." + key);
        }
        return RT.ok(value);
    }

    @Operation(summary = "新增参数")
    @RequirePermission("system:param:create")
    @PostMapping
    public RT<Void> create(
            @Parameter(description = "参数信息(category/key/value)") @RequestBody Map<String, String> body) {
        String category = body.get("category");
        String key = body.get("key");
        String value = body.get("value");
        sysParamService.setParam(category, key, value);
        return RT.ok();
    }

    @Operation(summary = "修改参数")
    @RequirePermission("system:param:update")
    @PutMapping("/{category}/{key}")
    public RT<Void> update(
            @Parameter(description = "参数分类") @PathVariable String category,
            @Parameter(description = "参数键") @PathVariable String key,
            @Parameter(description = "参数信息(value)") @RequestBody Map<String, String> body) {
        String value = body.get("value");
        sysParamService.setParam(category, key, value);
        return RT.ok();
    }

    @Operation(summary = "删除参数")
    @RequirePermission("system:param:delete")
    @DeleteMapping("/{category}/{key}")
    public RT<Boolean> delete(
            @Parameter(description = "参数分类") @PathVariable String category,
            @Parameter(description = "参数键") @PathVariable String key) {
        sysParamService.deleteParam(category, key);
        return RT.ok(true);
    }

    @Operation(summary = "手动刷新参数缓存")
    @RequirePermission("system:param:manage")
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
}
