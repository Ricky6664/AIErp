package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段权限方案明细实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_field_permission_scheme_detail")
public class SysFieldPermissionSchemeDetail extends BaseEntity {

    private Long schemeId;

    private String fieldName;

    private String permissionType;

    private String pageState;
}
