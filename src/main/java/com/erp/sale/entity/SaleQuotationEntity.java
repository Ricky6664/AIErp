package com.erp.sale.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sale_quotation")
@EqualsAndHashCode(callSuper = true)
public class SaleQuotationEntity extends BaseEntity {

    @TableField("sale_no")
    private String saleNo;

    @TableField("sale_name")
    private String saleName;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;
}
