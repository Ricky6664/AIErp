package com.erp.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 认证方式配置表实体.
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_method")
public class AuthMethod extends BaseEntity {

    private String methodName;

    private String methodType;

    private String configJson;

    private Integer priority;

    private Boolean isEnabled;

    private String description;
}
