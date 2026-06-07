package com.erp.module.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_warehouse")
public class WarehouseEntity extends BaseEntity {

    private String warehouseCode;

    private String warehouseName;

    private String warehouseType;

    private String address;

    private Long managerId;

    private String phone;

    private Integer status;
}
