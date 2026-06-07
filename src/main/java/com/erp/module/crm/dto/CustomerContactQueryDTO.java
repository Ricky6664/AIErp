package com.erp.module.crm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户联系人查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户联系人查询参数")
public class CustomerContactQueryDTO extends PageQuery {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "联系人姓名（模糊搜索）")
    private String contactName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "是否默认联系人")
    private Boolean isDefault;
}
