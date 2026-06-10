package com.erp.module.finance.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 银行账户VO.
 *
 * @author AI
 */
@Data
public class BankAccountVO {

    private Long id;

    private String accountName;

    private String bankAccountNo;

    private String bankName;

    private String bankBranch;

    private Long currencyId;

    private String accountType;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
