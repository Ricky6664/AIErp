package com.erp.module.product.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品查询参数")
public class ProductQueryDTO extends PageQuery {

    @Schema(description = "商品编码（模糊搜索）")
    private String productCode;

    @Schema(description = "商品名称（模糊搜索）")
    private String productName;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "分类ID")
    private Long classId;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "启用状态")
    private Boolean isActive;
}
