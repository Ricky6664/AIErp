package com.erp.approval.dto;

import lombok.Data;

/**
 * 我的审批查询DTO.
 *
 * @author AI
 */
@Data
public class MyApprovalQueryDTO {

    private Integer pageNum;

    private Integer pageSize;

    /** 查询标签: pending(待审), reviewed(已审), submitted(我的申请) */
    private String tab;
}
