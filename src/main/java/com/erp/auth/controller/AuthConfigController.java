package com.erp.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.AuthMethod;
import com.erp.auth.entity.AuthOnlineDevice;
import com.erp.auth.entity.AuthPasswordPolicy;
import com.erp.auth.entity.SysLoginLog;
import com.erp.auth.mapper.SysLoginLogMapper;
import com.erp.auth.service.AuthMethodService;
import com.erp.auth.service.AuthPasswordPolicyService;
import com.erp.auth.service.OnlineDeviceService;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.query.PageQuery;
import com.erp.common.result.PageResult;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证配置控制器 - 认证方式 + 密码策略管理.
 *
 * @author AI
 * @since 2026-06-04
 */
@Slf4j
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Tag(name = "认证配置管理", description = "认证方式配置、密码策略配置等系统认证配置接口")
public class AuthConfigController {

    private final AuthMethodService authMethodService;
    private final AuthPasswordPolicyService authPasswordPolicyService;
    private final OnlineDeviceService onlineDeviceService;
    private final SysLoginLogMapper sysLoginLogMapper;

    // ==================== 认证方式 ====================

    @Operation(summary = "分页查询认证方式列表")
    @RequirePermission("system:auth-method:query")
    @GetMapping("/auth-methods/page")
    public RT<PageResult<AuthMethod>> pageAuthMethods(@Parameter(description = "分页参数") PageQuery query) {
        LambdaQueryWrapper<AuthMethod> wrapper = new LambdaQueryWrapper<AuthMethod>()
                .orderByAsc(AuthMethod::getPriority);
        return RT.ok(authMethodService.pageList(query, wrapper));
    }

    @Operation(summary = "查询已启用的认证方式列表")
    @RequirePermission("system:auth-method:query")
    @GetMapping("/auth-methods/enabled")
    public RT<List<AuthMethod>> listEnabledAuthMethods() {
        return RT.ok(authMethodService.listEnabled());
    }

    @Operation(summary = "查询认证方式详情")
    @RequirePermission("system:auth-method:query")
    @GetMapping("/auth-methods/{id}")
    public RT<AuthMethod> getAuthMethod(@Parameter(description = "认证方式ID") @PathVariable Long id) {
        AuthMethod entity = authMethodService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(entity);
    }

    @Operation(summary = "新增认证方式")
    @RequirePermission("system:auth-method:create")
    @PostMapping("/auth-methods")
    public RT<AuthMethod> createAuthMethod(@Valid @RequestBody AuthMethod entity, BindingResult bindingResult) {
        authMethodService.save(entity);
        log.info("认证方式已新增: id={}, methodName={}", entity.getId(), entity.getMethodName());
        return RT.ok(entity);
    }

    @Operation(summary = "修改认证方式")
    @RequirePermission("system:auth-method:update")
    @PutMapping("/auth-methods/{id}")
    public RT<AuthMethod> updateAuthMethod(@Parameter(description = "认证方式ID") @PathVariable Long id,
                                           @Valid @RequestBody AuthMethod entity, BindingResult bindingResult) {
        AuthMethod existing = authMethodService.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setId(id);
        authMethodService.updateById(entity);
        log.info("认证方式已修改: id={}, methodName={}", id, entity.getMethodName());
        return RT.ok(entity);
    }

    @Operation(summary = "删除认证方式")
    @RequirePermission("system:auth-method:delete")
    @DeleteMapping("/auth-methods/{id}")
    public RT<Boolean> deleteAuthMethod(@Parameter(description = "认证方式ID") @PathVariable Long id) {
        boolean result = authMethodService.removeById(id);
        log.info("认证方式已删除: id={}, result={}", id, result);
        return RT.ok(result);
    }

    @Operation(summary = "更新认证方式优先级")
    @RequirePermission("system:auth-method:update")
    @PutMapping("/auth-methods/{id}/priority")
    public RT<Void> updateAuthMethodPriority(@Parameter(description = "认证方式ID") @PathVariable Long id,
                                              @Parameter(description = "优先级") @RequestParam Integer priority) {
        authMethodService.updatePriority(id, priority);
        return RT.ok();
    }

    @Operation(summary = "启用认证方式")
    @RequirePermission("system:auth-method:update")
    @PutMapping("/auth-methods/{id}/enable")
    public RT<Void> enableAuthMethod(@Parameter(description = "认证方式ID") @PathVariable Long id) {
        authMethodService.enable(id);
        return RT.ok("已启用", null);
    }

    @Operation(summary = "禁用认证方式")
    @RequirePermission("system:auth-method:update")
    @PutMapping("/auth-methods/{id}/disable")
    public RT<Void> disableAuthMethod(@Parameter(description = "认证方式ID") @PathVariable Long id) {
        authMethodService.disable(id);
        return RT.ok("已禁用", null);
    }

    // ==================== 密码策略 ====================

    @Operation(summary = "分页查询密码策略列表")
    @RequirePermission("system:password-policy:query")
    @GetMapping("/password-policies/page")
    public RT<PageResult<AuthPasswordPolicy>> pagePasswordPolicies(@Parameter(description = "分页参数") PageQuery query) {
        LambdaQueryWrapper<AuthPasswordPolicy> wrapper = new LambdaQueryWrapper<AuthPasswordPolicy>()
                .orderByDesc(AuthPasswordPolicy::getCreateTime);
        return RT.ok(authPasswordPolicyService.pageList(query, wrapper));
    }

    @Operation(summary = "查询当前启用的密码策略")
    @RequirePermission("system:password-policy:query")
    @GetMapping("/password-policies/current")
    public RT<AuthPasswordPolicy> getCurrentPasswordPolicy() {
        return RT.ok(authPasswordPolicyService.getCurrentPolicy());
    }

    @Operation(summary = "查询密码策略详情")
    @RequirePermission("system:password-policy:query")
    @GetMapping("/password-policies/{id}")
    public RT<AuthPasswordPolicy> getPasswordPolicy(@Parameter(description = "密码策略ID") @PathVariable Long id) {
        AuthPasswordPolicy entity = authPasswordPolicyService.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        return RT.ok(entity);
    }

    @Operation(summary = "新增密码策略")
    @RequirePermission("system:password-policy:create")
    @PostMapping("/password-policies")
    public RT<AuthPasswordPolicy> createPasswordPolicy(@Valid @RequestBody AuthPasswordPolicy entity, BindingResult bindingResult) {
        authPasswordPolicyService.save(entity);
        log.info("密码策略已新增: id={}, policyName={}", entity.getId(), entity.getPolicyName());
        return RT.ok(entity);
    }

    @Operation(summary = "修改密码策略")
    @RequirePermission("system:password-policy:update")
    @PutMapping("/password-policies/{id}")
    public RT<AuthPasswordPolicy> updatePasswordPolicy(@Parameter(description = "密码策略ID") @PathVariable Long id,
                                                        @Valid @RequestBody AuthPasswordPolicy entity,
                                                        BindingResult bindingResult) {
        AuthPasswordPolicy existing = authPasswordPolicyService.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        entity.setId(id);
        authPasswordPolicyService.updateById(entity);
        log.info("密码策略已修改: id={}, policyName={}", id, entity.getPolicyName());
        return RT.ok(entity);
    }

    @Operation(summary = "删除密码策略")
    @RequirePermission("system:password-policy:delete")
    @DeleteMapping("/password-policies/{id}")
    public RT<Boolean> deletePasswordPolicy(@Parameter(description = "密码策略ID") @PathVariable Long id) {
        boolean result = authPasswordPolicyService.removeById(id);
        log.info("密码策略已删除: id={}, result={}", id, result);
        return RT.ok(result);
    }

    @Operation(summary = "启用密码策略")
    @RequirePermission("system:password-policy:update")
    @PutMapping("/password-policies/{id}/enable")
    public RT<Void> enablePasswordPolicy(@Parameter(description = "密码策略ID") @PathVariable Long id) {
        authPasswordPolicyService.enable(id);
        return RT.ok("已启用", null);
    }

    @Operation(summary = "禁用密码策略")
    @RequirePermission("system:password-policy:update")
    @PutMapping("/password-policies/{id}/disable")
    public RT<Void> disablePasswordPolicy(@Parameter(description = "密码策略ID") @PathVariable Long id) {
        authPasswordPolicyService.disable(id);
        return RT.ok("已禁用", null);
    }

    @Operation(summary = "校验密码是否符合当前策略")
    @RequirePermission("system:password-policy:query")
    @PostMapping("/password-policies/validate")
    public RT<Boolean> validatePassword(@Parameter(description = "待校验的密码") @RequestParam String password) {
        return RT.ok(authPasswordPolicyService.validatePassword(password));
    }

    // ==================== 在线设备管理 ====================

    @Operation(summary = "分页查询在线设备列表")
    @RequirePermission("system:online-device:query")
    @GetMapping("/online-devices/page")
    public RT<PageResult<AuthOnlineDevice>> pageOnlineDevices(@Parameter(description = "分页参数") PageQuery query) {
        LambdaQueryWrapper<AuthOnlineDevice> wrapper = new LambdaQueryWrapper<AuthOnlineDevice>()
                .orderByDesc(AuthOnlineDevice::getLastActiveTime);
        return RT.ok(onlineDeviceService.pageList(query, wrapper));
    }

    @Operation(summary = "强制下线设备")
    @RequirePermission("system:online-device:kick")
    @PostMapping("/online-devices/{tokenId}/kick")
    public RT<Void> kickDevice(@Parameter(description = "会话Token标识") @PathVariable String tokenId) {
        onlineDeviceService.kickDevice(tokenId);
        return RT.ok();
    }

    // ==================== 认证配置工作台 ====================

    @Operation(summary = "认证配置工作台聚合数据")
    @RequirePermission("system:auth-config:query")
    @GetMapping("/auth-config/workbench")
    public RT<Map<String, Object>> workbench() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Map<String, Object> data = new HashMap<>();

        long onlineDeviceCount = onlineDeviceService.countOnline();
        data.put("onlineDeviceCount", onlineDeviceCount);

        long todayLoginCount = sysLoginLogMapper.selectCount(
                new LambdaQueryWrapper<SysLoginLog>()
                        .eq(SysLoginLog::getStatus, "SUCCESS")
                        .ge(SysLoginLog::getLoginTime, todayStart)
        );
        data.put("todayLoginCount", todayLoginCount);

        long todayFailCount = sysLoginLogMapper.selectCount(
                new LambdaQueryWrapper<SysLoginLog>()
                        .eq(SysLoginLog::getStatus, "FAIL")
                        .ge(SysLoginLog::getLoginTime, todayStart)
        );
        data.put("todayFailCount", todayFailCount);

        data.put("ssoConfigCount", 0);

        return RT.ok(data);
    }
}
