package com.erp.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 凭证字创建DTO.
 *
 * @author AI
 */
@Data
public class VoucherWordCreateDTO {

    @NotBlank(message = "凭证字名称不能为空")
    @Size(max = 100, message = "凭证字名称最长100个字符")
    private String wordName;

    @NotBlank(message = "凭证字编码不能为空")
    @Size(max = 50, message = "凭证字编码最长50个字符")
    private String wordCode;

    @NotNull(message = "排序号不能为空")
    private Integer sortOrder;

    @NotNull(message = "启用状态不能为空")
    private Integer status;
}
