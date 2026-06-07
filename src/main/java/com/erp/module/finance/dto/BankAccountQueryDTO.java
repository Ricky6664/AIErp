package com.erp.module.finance.dto;

import com.erp.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 银行账户查询DTO.
 *
 * @author AI
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "银行账户查询参数")
public class BankAccountQueryDTO extends PageQuery {

    @Schema(description = "账户名称（模糊搜索）")
    private String accountName;

    @Schema(description = "银行账号（模糊搜索）")
    private String bankAccountNo;

    @Schema(description = "开户银行")
    private String bankName;

    @Schema(description = "币种ID")
    private Long currencyId;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "启用状态")
    private Integer status;
}
