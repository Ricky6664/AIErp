package com.erp.module.org.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门查询参数")
public class DeptQueryDTO extends PageQuery {

    @Schema(description = "公司ID（必填）")
    private Long companyId;

    @Schema(description = "关键词（模糊搜索部门名称/部门编码）")
    private String keyword;

    @Schema(description = "启用状态")
    private Boolean enableFlag;
}
