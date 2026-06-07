package com.erp.module.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 仓库创建DTO.
 *
 * @author AI
 */
@Data
public class WarehouseCreateDTO {

    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 50, message = "仓库编码最长50个字符")
    private String warehouseCode;

    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 100, message = "仓库名称最长100个字符")
    private String warehouseName;

    @NotBlank(message = "仓库类型不能为空")
    @Size(max = 20, message = "仓库类型最长20个字符")
    private String warehouseType;

    @Size(max = 200, message = "地址最长200个字符")
    private String address;

    private Long managerId;

    @Size(max = 20, message = "联系电话最长20个字符")
    private String phone;

    @NotNull(message = "启用状态不能为空")
    private Integer status;
}
