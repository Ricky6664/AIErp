package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色字段权限实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_field_permission")
public class SysRoleFieldPermission extends BaseEntity {

    private Long roleId;

    private String tableName;

    private String fieldName;

    private String permissionType;
}
