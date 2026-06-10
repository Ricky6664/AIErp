package com.erp.module.product.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品多单位查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品多单位查询参数")
public class ProductUnitQueryDTO extends PageQuery {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "单位ID")
    private Long unitId;
}
