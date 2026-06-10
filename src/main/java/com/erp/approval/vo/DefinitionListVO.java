package com.erp.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批定义列表VO.
 *
 * @author AI
 */
@Data
public class DefinitionListVO {

    private Long id;

    private String definitionName;

    private String definitionCode;

    private String businessType;

    private Boolean enableFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
