package com.erp.module.srm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商分类实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("srm_supplier_class")
public class SupplierClass extends BaseEntity {

    private Long parentId;

    private String classCode;

    private String className;

    private Integer sortOrder;

    private Boolean isActive;
}
