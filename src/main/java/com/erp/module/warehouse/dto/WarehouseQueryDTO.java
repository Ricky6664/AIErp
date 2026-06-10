package com.erp.module.warehouse.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "仓库查询参数")
public class WarehouseQueryDTO extends PageQuery {

    @Schema(description = "仓库名称（模糊搜索）")
    private String warehouseName;

    @Schema(description = "仓库类型")
    private String warehouseType;

    @Schema(description = "启用状态")
    private Integer status;
}
