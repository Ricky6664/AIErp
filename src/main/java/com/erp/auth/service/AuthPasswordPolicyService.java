package com.erp.auth.service;

import com.erp.auth.entity.AuthPasswordPolicy;
import com.erp.common.service.IServiceX;

/**
 * 密码策略配置 Service 接口.
 *
 * @author AI
 * @since 2026-06-04
 */
public interface AuthPasswordPolicyService extends IServiceX<AuthPasswordPolicy> {

    AuthPasswordPolicy getCurrentPolicy();

    boolean isPolicyNameUnique(String policyName, Long excludeId);

    void enable(Long id);

    void disable(Long id);

    boolean validatePassword(String password);
}
