package com.erp.module.org.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "岗位查询参数")
public class PositionQueryDTO extends PageQuery {

    @Schema(description = "关键词（模糊搜索岗位名称/编码）")
    private String keyword;

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "启用状态")
    private Boolean enableFlag;
}
