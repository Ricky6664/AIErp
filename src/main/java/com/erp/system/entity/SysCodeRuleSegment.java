package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编码规则段配置表实体.
 *
 * @author AI
 * @since 2026-05-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_code_rule_segment")
public class SysCodeRuleSegment extends BaseEntity {

    private Long ruleId;

    private Integer segmentType;

    private Integer segmentOrder;

    private String segmentValue;

    private Integer segmentLength;

    private String segmentFormat;
}
