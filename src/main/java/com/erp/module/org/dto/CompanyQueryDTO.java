package com.erp.module.org.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "公司查询参数")
public class CompanyQueryDTO extends PageQuery {

    @Schema(description = "关键词（模糊搜索公司名称/公司简称）")
    private String keyword;

    @Schema(description = "启用状态")
    private Boolean enabled;
}
