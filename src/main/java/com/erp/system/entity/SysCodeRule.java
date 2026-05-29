package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编码规则配置表实体.
 *
 * @author AI
 * @since 2026-05-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_code_rule")
public class SysCodeRule extends BaseEntity {

    private String ruleCode;

    private String ruleName;

    private String moduleCode;

    private String description;

    private String separator;

    private Long currentValue;

    private Integer isEnabled;
}
