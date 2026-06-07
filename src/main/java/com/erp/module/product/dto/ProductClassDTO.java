package com.erp.module.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商品分类DTO.
 *
 * @author AI
 */
@Data
public class ProductClassDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称最长100个字符")
    private String className;

    private Long parentId;

    @NotNull(message = "排序号不能为空")
    private Integer sortOrder;

    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}
