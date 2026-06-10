package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段权限方案实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_field_permission_scheme")
public class SysFieldPermissionScheme extends BaseEntity {

    @NotBlank(message = "方案名称不能为空")
    private String schemeName;

    @NotBlank(message = "方案编码不能为空")
    private String schemeCode;

    private String schemeDesc;

    private String tableName;
}
