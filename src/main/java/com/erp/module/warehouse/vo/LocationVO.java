package com.erp.module.warehouse.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库位VO.
 *
 * @author AI
 */
@Data
public class LocationVO {

    private Long id;

    private Long warehouseId;

    private String locationCode;

    private String locationName;

    private String locationType;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
