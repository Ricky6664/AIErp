package com.erp.module.warehouse.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库位查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库位查询参数")
public class LocationQueryDTO extends PageQuery {

    @Schema(description = "所属仓库ID")
    private Long warehouseId;

    @Schema(description = "库位编码（模糊搜索）")
    private String locationCode;

    @Schema(description = "库位名称（模糊搜索）")
    private String locationName;

    @Schema(description = "库位类型")
    private String locationType;

    @Schema(description = "启用状态")
    private Integer status;
}
