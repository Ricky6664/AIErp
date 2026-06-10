package com.erp.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SSO配置表实体.
 *
 * @author AI
 * @since 2026-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_sso_config")
public class AuthSsoConfig extends BaseEntity {

    private String ssoName;

    private String type;

    private String idpUrl;

    private String spEntityId;

    private String ssoLoginUrl;

    private String ssoLogoutUrl;

    private String certificate;

    private Boolean enabled;
}
