package com.erp.module.org.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeptListVO {

    private Long id;

    private String deptName;

    private String deptCode;

    private Long parentId;

    private String companyName;

    private String managerName;

    private String deptType;

    private Integer sortNo;

    private Boolean enableFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
