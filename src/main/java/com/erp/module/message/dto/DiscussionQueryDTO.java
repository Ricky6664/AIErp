package com.erp.module.message.dto;

import com.erp.common.query.PageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单据沟通查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiscussionQueryDTO extends PageQuery {

    @NotBlank(message = "单据类型不能为空")
    private String businessType;

    @NotNull(message = "单据ID不能为空")
    private Long businessId;
}
