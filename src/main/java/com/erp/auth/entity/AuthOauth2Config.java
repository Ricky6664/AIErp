package com.erp.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OAuth2配置表实体.
 *
 * @author AI
 * @since 2026-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_oauth2_config")
public class AuthOauth2Config extends BaseEntity {

    private String supplierName;

    private String type;

    private String clientId;

    private String clientSecret;

    private String authUrl;

    private String tokenUrl;

    private String userInfoUrl;

    private String scope;

    private Boolean enabled;
}
