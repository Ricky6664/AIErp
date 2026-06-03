package com.erp.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.auth.entity.AuthMethod;
import com.erp.auth.mapper.AuthMethodMapper;
import com.erp.auth.service.AuthMethodService;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.service.ServiceImplX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 认证方式配置 Service 实现.
 *
 * @author AI
 * @since 2026-06-04
 */
@Slf4j
@Service
public class AuthMethodServiceImpl extends ServiceImplX<AuthMethodMapper, AuthMethod> implements AuthMethodService {

    @Override
    public List<AuthMethod> listEnabled() {
        LambdaQueryWrapper<AuthMethod> wrapper = new LambdaQueryWrapper<AuthMethod>()
                .eq(AuthMethod::getIsEnabled, true)
                .orderByAsc(AuthMethod::getPriority);
        return list(wrapper);
    }

    @Override
    public boolean isMethodNameUnique(String methodName, Long excludeId) {
        LambdaQueryWrapper<AuthMethod> wrapper = new LambdaQueryWrapper<AuthMethod>()
                .eq(AuthMethod::getMethodName, methodName);
        if (excludeId != null) {
            wrapper.ne(AuthMethod::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    public boolean isMethodTypeUnique(String methodType, Long excludeId) {
        LambdaQueryWrapper<AuthMethod> wrapper = new LambdaQueryWrapper<AuthMethod>()
                .eq(AuthMethod::getMethodType, methodType);
        if (excludeId != null) {
            wrapper.ne(AuthMethod::getId, excludeId);
        }
        return count(wrapper) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePriority(Long id, Integer priority) {
        AuthMethod method = getById(id);
        if (method == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "认证方式不存在: id=" + id);
        }

        if (priority == null || priority < 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "优先级不能为空或负数");
        }

        method.setPriority(priority);
        updateById(method);

        log.info("认证方式优先级更新: id={}, methodName={}, priority={}", id, method.getMethodName(), priority);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        AuthMethod method = getById(id);
        if (method == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "认证方式不存在: id=" + id);
        }

        if (Boolean.TRUE.equals(method.getIsEnabled())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "认证方式已启用");
        }

        method.setIsEnabled(true);
        updateById(method);

        log.info("认证方式已启用: id={}, methodName={}", id, method.getMethodName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        AuthMethod method = getById(id);
        if (method == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "认证方式不存在: id=" + id);
        }

        if (Boolean.FALSE.equals(method.getIsEnabled())) {
            throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "认证方式已禁用");
        }

        method.setIsEnabled(false);
        updateById(method);

        log.info("认证方式已禁用: id={}, methodName={}", id, method.getMethodName());
    }
}
