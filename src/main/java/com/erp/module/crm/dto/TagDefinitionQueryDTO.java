package com.erp.module.crm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签定义查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标签定义查询参数")
public class TagDefinitionQueryDTO extends PageQuery {

    @Schema(description = "标签名称（模糊搜索）")
    private String tagName;

    @Schema(description = "标签分组")
    private String tagGroup;

    @Schema(description = "启用状态")
    private Boolean isActive;
}
