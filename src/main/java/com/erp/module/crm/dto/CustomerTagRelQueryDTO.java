package com.erp.module.crm.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户标签关联查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户标签关联查询参数")
public class CustomerTagRelQueryDTO extends PageQuery {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "标签ID")
    private Long tagId;
}
