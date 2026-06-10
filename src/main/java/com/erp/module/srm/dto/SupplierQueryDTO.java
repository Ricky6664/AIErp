package com.erp.module.srm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "供应商查询参数")
public class SupplierQueryDTO extends PageQuery {

    @Schema(description = "供应商编码")
    private String supplierCode;

    @Schema(description = "供应商名称（模糊搜索）")
    private String supplierName;

    @Schema(description = "供应商简称")
    private String shortName;

    @Schema(description = "供应商分类ID")
    private Long classId;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "启用状态")
    private Boolean isActive;
}
