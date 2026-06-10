package com.erp.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 标签定义DTO.
 *
 * @author AI
 */
@Data
public class TagDefinitionDTO {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称最长50个字符")
    private String tagName;

    @Size(max = 50, message = "标签分组最长50个字符")
    private String tagGroup;

    @Size(max = 20, message = "标签颜色最长20个字符")
    private String tagColor;

    private Integer sortOrder;

    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}
