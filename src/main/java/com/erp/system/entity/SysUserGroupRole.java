package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户组角色关联实体.
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_group_role")
public class SysUserGroupRole extends BaseEntity {

    private Long groupId;

    private Long roleId;
}
