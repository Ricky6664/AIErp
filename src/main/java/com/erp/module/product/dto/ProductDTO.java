package com.erp.module.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商品DTO.
 *
 * @author AI
 */
@Data
public class ProductDTO {

    private Long id;

    @NotBlank(message = "商品编码不能为空")
    @Size(max = 50, message = "商品编码最长50个字符")
    private String productCode;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称最长200个字符")
    private String productName;

    @Size(max = 100, message = "型号最长100个字符")
    private String model;

    @Size(max = 200, message = "规格最长200个字符")
    private String spec;

    @Size(max = 100, message = "品牌最长100个字符")
    private String brand;

    private Long baseUnitId;

    private Boolean isMultiUnit;

    @NotNull(message = "分类ID不能为空")
    private Long classId;

    private Boolean isSaleable;

    private Boolean isPurchasable;

    private Boolean isProducible;

    private Boolean isOutsourceable;

    private Boolean isSubPart;

    @Size(max = 20, message = "审核状态最长20个字符")
    private String auditStatus;

    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;

    @Size(max = 500, message = "备注最长500个字符")
    private String remark;
}
