package com.erp.module.warehouse.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 仓库VO.
 *
 * @author AI
 */
@Data
public class WarehouseVO {

    private Long id;

    private String warehouseCode;

    private String warehouseName;

    private String warehouseType;

    private String address;

    private Long managerId;

    private String phone;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
