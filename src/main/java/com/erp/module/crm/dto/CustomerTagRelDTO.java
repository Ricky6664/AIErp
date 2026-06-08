package com.erp.module.crm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户标签关联DTO.
 *
 * @author AI
 */
@Data
public class CustomerTagRelDTO {

    private Long id;

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotNull(message = "标签ID不能为空")
    private Long tagId;
}
