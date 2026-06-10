package com.erp.module.finance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计科目实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_account")
public class AccountEntity extends BaseEntity {

    private Long parentId;

    private String accountCode;

    private String accountName;

    private Integer accountType;

    private Integer balanceDirection;

    @TableField("ext_num1")
    private Integer level;

    @TableField("ext_str1")
    private String category;

    @TableField("ext_bool1")
    private Boolean isLeaf;

    @TableField("ext_bool2")
    private Boolean isCash;

    @TableField("ext_bool3")
    private Boolean isBank;

    @TableField("ext_str2")
    private String isForeignCurrency;

    @TableField("ext_str3")
    private String isAuxiliary;
}
