package com.erp.auth.service;

import com.erp.auth.entity.AuthMethod;
import com.erp.common.service.IServiceX;

import java.util.List;

/**
 * 认证方式配置 Service 接口.
 *
 * @author AI
 * @since 2026-06-04
 */
public interface AuthMethodService extends IServiceX<AuthMethod> {

    List<AuthMethod> listEnabled();

    boolean isMethodNameUnique(String methodName, Long excludeId);

    boolean isMethodTypeUnique(String methodType, Long excludeId);

    void updatePriority(Long id, Integer priority);

    void enable(Long id);

    void disable(Long id);
}
