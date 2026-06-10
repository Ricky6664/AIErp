package com.erp.auth.service.impl;

import com.erp.auth.entity.AuthOauth2Config;
import com.erp.auth.mapper.AuthOauth2ConfigMapper;
import com.erp.auth.service.AuthOauth2ConfigService;
import com.erp.common.service.ServiceImplX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OAuth2配置 Service 实现.
 *
 * @author AI
 * @since 2026-06-05
 */
@Slf4j
@Service
public class AuthOauth2ConfigServiceImpl extends ServiceImplX<AuthOauth2ConfigMapper, AuthOauth2Config> implements AuthOauth2ConfigService {
}
