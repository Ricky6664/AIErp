package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户组实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_group")
public class SysUserGroup extends BaseEntity {

    private String groupCode;

    private String groupName;

    private String groupDesc;

    private Boolean isEnabled;

    private Integer sortOrder;
}
