package com.erp.module.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 库位创建DTO.
 *
 * @author AI
 */
@Data
public class LocationCreateDTO {

    @NotNull(message = "所属仓库ID不能为空")
    private Long warehouseId;

    @NotBlank(message = "库位编码不能为空")
    @Size(max = 50, message = "库位编码最长50个字符")
    private String locationCode;

    @NotBlank(message = "库位名称不能为空")
    @Size(max = 100, message = "库位名称最长100个字符")
    private String locationName;

    @NotBlank(message = "库位类型不能为空")
    @Size(max = 20, message = "库位类型最长20个字符")
    private String locationType;

    private Integer sortOrder;

    @NotNull(message = "启用状态不能为空")
    private Integer status;
}
