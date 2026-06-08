package com.erp.approval.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批定义详情VO.
 *
 * @author AI
 */
@Data
public class DefinitionDetailVO {

    private Long id;

    private String definitionName;

    private String definitionCode;

    private String businessType;

    private String flowConfig;

    private Boolean enableFlag;

    private Integer version;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<NodeVO> nodes;
}
