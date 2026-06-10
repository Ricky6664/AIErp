package com.erp.module.product.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品控制策略查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品控制策略查询参数")
public class ProductControlQueryDTO extends PageQuery {

    @Schema(description = "商品ID")
    private Long productId;
}
