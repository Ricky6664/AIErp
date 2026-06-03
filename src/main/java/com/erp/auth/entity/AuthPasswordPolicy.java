package com.erp.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 密码策略配置表实体.
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_password_policy")
public class AuthPasswordPolicy extends BaseEntity {

    private String policyName;

    private Integer minLength;

    private Boolean requireUppercase;

    private Boolean requireLowercase;

    private Boolean requireNumber;

    private Boolean requireSpecialChar;

    private Integer expireDays;

    private Integer maxAttempts;

    private Integer lockMinutes;

    private Integer passwordHistoryCount;

    private Boolean isEnabled;

    private String description;
}
