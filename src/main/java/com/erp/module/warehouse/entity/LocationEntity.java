package com.erp.module.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库位实体.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inv_location")
public class LocationEntity extends BaseEntity {

    private Long warehouseId;

    private String locationCode;

    private String locationName;

    private String locationType;

    private Integer sortOrder;

    private Integer status;
}
