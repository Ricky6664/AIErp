package com.erp.auth.service.impl;

import com.erp.auth.entity.AuthSsoConfig;
import com.erp.auth.mapper.AuthSsoConfigMapper;
import com.erp.auth.service.AuthSsoConfigService;
import com.erp.common.service.ServiceImplX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SSO配置 Service 实现.
 *
 * @author AI
 * @since 2026-06-05
 */
@Slf4j
@Service
public class AuthSsoConfigServiceImpl extends ServiceImplX<AuthSsoConfigMapper, AuthSsoConfig> implements AuthSsoConfigService {
}
