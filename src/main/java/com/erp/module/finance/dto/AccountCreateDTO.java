package com.erp.module.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 会计科目创建DTO.
 *
 * @author AI
 */
@Data
public class AccountCreateDTO {

    @NotBlank(message = "科目编码不能为空")
    @Size(max = 50, message = "科目编码最长50个字符")
    private String accountCode;

    @NotBlank(message = "科目名称不能为空")
    @Size(max = 200, message = "科目名称最长200个字符")
    private String accountName;

    private Long parentId;

    @NotNull(message = "科目级次不能为空")
    private Integer level;

    @NotNull(message = "科目类型不能为空")
    private Integer accountType;

    @NotBlank(message = "科目类别不能为空")
    @Size(max = 50, message = "科目类别最长50个字符")
    private String category;

    @NotNull(message = "余额方向不能为空")
    private Integer balanceDirection;

    @NotNull(message = "是否末级不能为空")
    private Boolean isLeaf;

    private Boolean isCash;

    private Boolean isBank;

    private String isForeignCurrency;

    private String isAuxiliary;
}
