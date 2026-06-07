package com.erp.module.org.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PositionListVO {

    private Long id;

    private String positionName;

    private String positionCode;

    private Long departmentId;

    private String departmentName;

    private Integer sortNo;

    private Boolean enableFlag;

    private LocalDateTime createTime;
}
