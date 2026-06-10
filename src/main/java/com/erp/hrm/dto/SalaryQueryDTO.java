package com.erp.hrm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资查询参数")
public class SalaryQueryDTO extends PageQuery {

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "薪资月份")
    private String salaryMonth;
}
