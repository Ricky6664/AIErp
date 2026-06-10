package com.erp.module.crm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户分类查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户分类查询参数")
public class CustomerClassQueryDTO extends PageQuery {

    @Schema(description = "分类名称（模糊搜索）")
    private String className;

    @Schema(description = "上级分类ID")
    private Long parentId;

    @Schema(description = "启用状态")
    private Integer isActive;
}
