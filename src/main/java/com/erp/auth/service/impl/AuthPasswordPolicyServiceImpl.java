package com.erp.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.AuthPasswordPolicy;
import com.erp.auth.mapper.AuthPasswordPolicyMapper;
import com.erp.auth.service.AuthPasswordPolicyService;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 密码策略配置 Service 实现.
 *
 * @author AI
 * @since 2026-06-04
 */
@Slf4j
@Service
public class AuthPasswordPolicyServiceImpl extends ServiceImplX<AuthPasswordPolicyMapper, AuthPasswordPolicy> implements AuthPasswordPolicyService {

    @Override
    public AuthPasswordPolicy getCurrentPolicy() {
        LambdaQueryWrapper<AuthPasswordPolicy> wrapper = new LambdaQueryWrapper<AuthPasswordPolicy>()
                .eq(AuthPasswordPolicy::getIsEnabled, true)
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public boolean isPolicyNameUnique(String policyName, Long excludeId) {
        LambdaQueryWrapper<AuthPasswordPolicy> wrapper = new LambdaQueryWrapper<AuthPasswordPolicy>()
                .eq(AuthPasswordPolicy::getPolicyName, policyName);
        if (excludeId != null) {
            wrapper.ne(AuthPasswordPolicy::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        AuthPasswordPolicy policy = getById(id);
        if (policy == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "密码策略不存在: id=" + id);
        }

        if (Boolean.TRUE.equals(policy.getIsEnabled())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "密码策略已启用");
        }

        LambdaQueryWrapper<AuthPasswordPolicy> enabledWrapper = new LambdaQueryWrapper<AuthPasswordPolicy>()
                .eq(AuthPasswordPolicy::getIsEnabled, true);
        AuthPasswordPolicy currentEnabled = getOne(enabledWrapper);
        if (currentEnabled != null) {
            currentEnabled.setIsEnabled(false);
            updateById(currentEnabled);
        }

        policy.setIsEnabled(true);
        updateById(policy);

        log.info("密码策略已启用: id={}, policyName={}", id, policy.getPolicyName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        AuthPasswordPolicy policy = getById(id);
        if (policy == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "密码策略不存在: id=" + id);
        }

        if (Boolean.FALSE.equals(policy.getIsEnabled())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "密码策略已禁用");
        }

        policy.setIsEnabled(false);
        updateById(policy);

        log.info("密码策略已禁用: id={}, policyName={}", id, policy.getPolicyName());
    }

    @Override
    public boolean validatePassword(String password) {
        AuthPasswordPolicy policy = getCurrentPolicy();
        if (policy == null) {
            return true;
        }

        if (password == null || password.isEmpty()) {
            return false;
        }

        if (policy.getMinLength() != null && password.length() < policy.getMinLength()) {
            return false;
        }

        if (Boolean.TRUE.equals(policy.getRequireUppercase()) && !password.matches(".*[A-Z].*")) {
            return false;
        }

        if (Boolean.TRUE.equals(policy.getRequireLowercase()) && !password.matches(".*[a-z].*")) {
            return false;
        }

        if (Boolean.TRUE.equals(policy.getRequireNumber()) && !password.matches(".*\\d.*")) {
            return false;
        }

        if (Boolean.TRUE.equals(policy.getRequireSpecialChar()) && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }

        return true;
    }
}
