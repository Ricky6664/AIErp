package com.erp.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 凭证字实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_voucher_word")
public class VoucherWordEntity extends BaseEntity {

    private String wordName;

    private String wordCode;

    private Integer sortOrder;

    private Integer status;
}
