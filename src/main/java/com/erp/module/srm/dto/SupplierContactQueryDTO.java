package com.erp.module.srm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商联系人查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "供应商联系人查询参数")
public class SupplierContactQueryDTO extends PageQuery {

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "联系人姓名（模糊搜索）")
    private String contactName;

    @Schema(description = "性别")
    private String gender;
}
