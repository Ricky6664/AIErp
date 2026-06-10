package com.erp.module.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 银行账户创建DTO.
 *
 * @author AI
 */
@Data
public class BankAccountCreateDTO {

    @NotBlank(message = "账户名称不能为空")
    @Size(max = 100, message = "账户名称最长100个字符")
    private String accountName;

    @NotBlank(message = "银行账号不能为空")
    @Size(max = 50, message = "银行账号最长50个字符")
    private String bankAccountNo;

    @NotBlank(message = "开户银行不能为空")
    @Size(max = 100, message = "开户银行最长100个字符")
    private String bankName;

    @Size(max = 100, message = "开户支行最长100个字符")
    private String bankBranch;

    @NotNull(message = "币种ID不能为空")
    private Long currencyId;

    @NotBlank(message = "账户类型不能为空")
    @Size(max = 20, message = "账户类型最长20个字符")
    private String accountType;

    @NotNull(message = "启用状态不能为空")
    private Integer status;
}
