package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色互斥关系实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_exclusion")
public class SysRoleExclusion extends BaseEntity {

    private Long roleA;

    private Long roleB;
}
