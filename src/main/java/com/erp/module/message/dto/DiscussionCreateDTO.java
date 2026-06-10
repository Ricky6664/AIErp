package com.erp.module.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 单据沟通新增DTO.
 *
 * @author AI
 */
@Data
public class DiscussionCreateDTO {

    @NotBlank(message = "单据类型不能为空")
    private String businessType;

    @NotNull(message = "单据ID不能为空")
    private Long businessId;

    @NotBlank(message = "留言内容不能为空")
    private String content;

    private Long parentId;

    private String attachment;
}
