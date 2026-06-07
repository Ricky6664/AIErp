package com.erp.module.org.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeptTreeVO {

    private Long id;

    private String deptName;

    private String deptCode;

    private Long parentId;

    private List<DeptTreeVO> children;

    private Boolean enableFlag;
}
