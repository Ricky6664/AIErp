package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据权限方案实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_data_permission_scheme")
public class SysDataPermissionScheme extends BaseEntity {

    private String schemeName;

    private String schemeCode;

    private String schemeDesc;

    private String dataScope;
}
