package com.erp.module.crm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户查询参数")
public class CustomerQueryDTO extends PageQuery {

    @Schema(description = "客户编码（模糊搜索）")
    private String customerCode;

    @Schema(description = "客户名称（模糊搜索）")
    private String customerName;

    @Schema(description = "客户分类ID")
    private Long classId;

    @Schema(description = "业务员ID")
    private Long salesPersonId;

    @Schema(description = "业务部门ID")
    private Long salesDeptId;

    @Schema(description = "公司主体ID")
    private Long companyId;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "启用状态")
    private Integer isActive;
}
