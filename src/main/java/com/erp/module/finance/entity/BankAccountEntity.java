package com.erp.module.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 银行账户实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_bank_account")
public class BankAccountEntity extends BaseEntity {

    private String accountName;

    private String bankAccountNo;

    private String bankName;

    private String bankBranch;

    private Long currencyId;

    private String accountType;

    private Integer status;
}
