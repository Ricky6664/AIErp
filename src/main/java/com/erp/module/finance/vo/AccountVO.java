package com.erp.module.finance.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会计科目VO.
 *
 * @author AI
 */
@Data
public class AccountVO {

    private Long id;

    private String accountCode;

    private String accountName;

    private Long parentId;

    private Integer level;

    private Integer accountType;

    private String category;

    private Integer balanceDirection;

    private Boolean isLeaf;

    private Boolean isCash;

    private Boolean isBank;

    private String isForeignCurrency;

    private String isAuxiliary;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
